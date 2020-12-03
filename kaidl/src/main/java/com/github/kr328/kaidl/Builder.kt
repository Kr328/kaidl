package com.github.kr328.kaidl

import com.github.kr328.kaidl.builder.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.reflect.KClass

fun FileSpec.Builder.addStub(forClass: ClassName, methods: List<Method>): FileSpec.Builder {
    val stub =
        TypeSpec.classBuilder(ClassName(forClass.packageName, forClass.simpleName + "Delegate"))
            .primaryConstructor(FunSpec.constructorBuilder().addParameter("impl", forClass).build())
            .addModifiers(KModifier.OPEN)
            .superclass(BINDER)
            .addSuperinterface(forClass, "impl")

    return addType(
        stub
            .addCompanion(forClass, methods)
            .addGetDescriptor()
            .addOnTransact(methods)
            .build()
    )
}

fun FileSpec.Builder.addWrap(forClass: ClassName): FileSpec.Builder {
    val code = CodeBlock.builder()
        .beginControlFlow("if (this is %T)", IBINDER)
        .addStatement("return this")
        .nextControlFlow("else")
        .addStatement("return %T(this)", forClass.delegate)
        .endControlFlow()

    return addFunction(
        FunSpec.builder("wrap")
            .receiver(forClass)
            .returns(IBINDER)
            .addCode(code.build())
            .build()
    )
}

fun FileSpec.Builder.addProxyClass(forClass: ClassName, methods: List<Method>): FileSpec.Builder {
    val clazz = TypeSpec.classBuilder(forClass.proxy)
        .addSuperinterface(forClass)
        .primaryConstructor(FunSpec.constructorBuilder().addParameter("remote", IBINDER).build())
        .addProperty(PropertySpec.builder("remote", IBINDER).initializer("remote").build())

    for (m in methods)
        clazz.addProxy(forClass, m)

    return addType(clazz.build())
}

fun FileSpec.Builder.addUnwrap(forClass: ClassName): FileSpec.Builder {
    val code = CodeBlock.builder()
        .beginControlFlow("if (this is %T)", forClass)
        .addStatement("return this")
        .nextControlFlow("else")
        .addStatement("return %T(this)", forClass.proxy)
        .endControlFlow()

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
            .addCode(code.build())
            .build()
    )
}

fun TypeSpec.Builder.addCompanion(
    forClass: ClassName,
    methods: List<Method>
): TypeSpec.Builder {
    val companion = TypeSpec.companionObjectBuilder().apply {
        addDescriptor(forClass)

        methods.forEach {
            addTransactProperty(it)
        }
    }

    return addType(companion.build())
}

fun TypeSpec.Builder.addGetDescriptor(): TypeSpec.Builder {
    val func = FunSpec.builder("getInterfaceDescriptor")
        .addModifiers(KModifier.OVERRIDE)
        .returns(STRING.copy(nullable = true))
        .addCode("return DESCRIPTOR")

    return addFunction(func.build())
}

fun TypeSpec.Builder.addOnTransact(
    methods: List<Method>
): TypeSpec.Builder {
    val code = CodeBlock.builder().apply {
        beginControlFlow("when (code)")

        for (m in methods) {
            beginControlFlow("%N ->", m.transactionProperty.name)

            addStatement("reply ?: return false")

            addStatement("`data`.enforceInterface(%N)", descriptorProperty.name)

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

fun TypeSpec.Builder.addProxy(forClass: ClassName, method: Method): TypeSpec.Builder {
    val code = CodeBlock.builder()
        .addStatement("val `data` = Parcel.obtain()")
        .addStatement("val `reply` = Parcel.obtain()")
    val func = FunSpec.builder(method.name)
        .addModifiers(KModifier.OVERRIDE)
        .returns(method.returnType.className)

    with(code) {
        beginControlFlow("return try")

        addStatement(
            "`data`.writeInterfaceToken(%T.%N)",
            forClass.delegate,
            descriptorProperty.name
        )

        for (p in method.parameters) {
            func.addParameter(p.first, p.second.className)

            addWriteToParcel(p.first, p.second, "data")
        }

        code.addStatement(
            "remote.transact(%T.%N, `data`, reply, 0)",
            forClass.delegate,
            method.transactionProperty.name
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