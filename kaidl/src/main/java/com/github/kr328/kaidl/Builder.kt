package com.github.kr328.kaidl

import com.github.kr328.kaidl.builder.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.reflect.KClass

fun FileSpec.Builder.generateStub(forClass: ClassName, methods: List<Method>): FileSpec.Builder {
    val stub =
        TypeSpec.classBuilder(ClassName(forClass.packageName, forClass.simpleName + "Delegate"))
            .primaryConstructor(FunSpec.constructorBuilder().addParameter("impl", forClass).build())
            .addModifiers(KModifier.OPEN)
            .superclass(BINDER)
            .addSuperinterface(forClass, "impl")

    return addType(
        stub
            .generateCompanion(methods)
            .generateOnTransact(forClass, methods)
            .build()
    )
}

fun FileSpec.Builder.generateWrap(forClass: ClassName): FileSpec.Builder {
    return addFunction(
        FunSpec.builder("wrap")
            .receiver(forClass)
            .returns(IBINDER)
            .addCode(CodeBlock.of("return if ( this is ${IBINDER.canonicalName} ) this else ${forClass.simpleName}Delegate(this)"))
            .build()
    )
}

fun FileSpec.Builder.generateProxy(forClass: ClassName, methods: List<Method>): FileSpec.Builder {
    val clazz = TypeSpec.classBuilder(forClass.simpleName + "Proxy")
        .addSuperinterface(forClass)
        .primaryConstructor(FunSpec.constructorBuilder().addParameter("remote", IBINDER).build())
        .addProperty(PropertySpec.builder("remote", IBINDER).initializer("remote").build())

    for (m in methods)
        clazz.generateProxy(forClass, m)

    return addType(clazz.build())
}

fun FileSpec.Builder.generateUnwrap(forClass: ClassName): FileSpec.Builder {
    return addFunction(
        FunSpec.builder("unwrap")
            .addParameter(
                "c",
                ClassName(
                    KClass::class.java.packageName,
                    KClass::class.java.simpleName
                ).parameterizedBy(forClass)
            )
            .receiver(IBINDER)
            .returns(forClass)
            .addCode(CodeBlock.of("return if (this is ${forClass.simpleName}) this else ${forClass.simpleName}Proxy(this)"))
            .build()
    )
}

fun TypeSpec.Builder.generateCompanion(
    methods: List<Method>
): TypeSpec.Builder {
    val companion = TypeSpec.companionObjectBuilder().apply {
        methods.forEach {
            addTransactProperty(it)
        }
    }

    return addType(companion.build())
}

fun TypeSpec.Builder.generateOnTransact(
    forClass: ClassName,
    methods: List<Method>
): TypeSpec.Builder {
    val code = CodeBlock.builder().apply {
        beginControlFlow("when (code)")

        for (m in methods) {
            beginControlFlow("%T.%N ->", forClass.delegate, m.transactProperty.name)

            addStatement("requireNotNull(reply)")

            for (p in m.parameters) {
                addReadFromParcel(p.first, p.second, "data")
            }

            beginControlFlow("try") // try

            val args = m.parameters.joinToString(", ") { it.first }

            addStatement("val %N: %T = %N($args)", "_result", m.returnType.className, m.name)

            addStatement("reply.writeNoException()")

            addWriteToParcel("_result", m.returnType, "reply")

            nextControlFlow("catch (e: Exception)") // catch

            addStatement("reply.writeException(e)")

            endControlFlow() // try

            endControlFlow() // code ->
        }

        beginControlFlow("else ->")

        addStatement("return super.onTransact(code, data, reply, flags)")

        endControlFlow()

        endControlFlow()

        addStatement("return true")
    }


    val func = FunSpec.builder("onTransact")
        .addModifiers(KModifier.OVERRIDE)
        .addParameter(ParameterSpec("code", INT))
        .addParameter(ParameterSpec("data", PARCEL))
        .addParameter(ParameterSpec("reply", PARCEL.copy(nullable = true)))
        .addParameter(ParameterSpec("flags", INT))
        .returns(BOOLEAN)
        .addCode(code.build())

    return addFunction(func.build())
}

fun TypeSpec.Builder.generateProxy(forClass: ClassName, method: Method): TypeSpec.Builder {
    val code = CodeBlock.builder()
        .addStatement("val `data` = Parcel.obtain()")
        .addStatement("val `reply` = Parcel.obtain()")
    val func = FunSpec.builder(method.name)
        .addModifiers(KModifier.OVERRIDE)
        .returns(method.returnType.className)

    with(code) {
        beginControlFlow("return try")

        for (p in method.parameters) {
            func.addParameter(p.first, p.second.className)

            addWriteToParcel(p.first, p.second, "data")
        }

        code.addStatement(
            "remote.transact(%T.%N, `data`, reply, 0)",
            forClass.delegate,
            method.transactProperty.name
        )

        addStatement("reply.readException()")

        addReadFromParcel("_result", method.returnType, "reply")

        addStatement("_result")

        nextControlFlow("finally")

        addStatement("data.recycle()")
        addStatement("reply.recycle()")

        endControlFlow()
    }

    return addFunction(func.addCode(code.build()).build())
}