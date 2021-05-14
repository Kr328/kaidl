package com.github.kr328.kaidl

import com.github.kr328.kaidl.builder.*
import com.github.kr328.kaidl.resolver.CodeValue
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.reflect.KClass

fun FileSpec.Builder.addStub(forClass: ClassName, functions: Sequence<FunSpec>): FileSpec.Builder {
    val stub =
        TypeSpec.classBuilder(ClassName(forClass.packageName, forClass.simpleName + "Delegate"))
            .primaryConstructor(FunSpec.constructorBuilder().addParameter("impl", forClass).build())
            .addModifiers(KModifier.OPEN)
            .superclass(com.github.kr328.kaidl.resolver.BINDER)
            .addSuperinterface(forClass, "impl")
            .addCompanion(forClass, functions)
            .addGetDescriptor()
            .addOnTransact(functions)

    return addType(stub.build())
}

fun FileSpec.Builder.addWrap(forClass: ClassName): FileSpec.Builder {
    val code = CodeBlock.builder()
        .beginControlFlow("if (this is %T)", com.github.kr328.kaidl.resolver.IBINDER)
        .addStatement("return this")
        .nextControlFlow("else")
        .addStatement("return %T(this)", forClass.delegate)
        .endControlFlow()

    return addFunction(
        FunSpec.builder("wrap")
            .receiver(forClass)
            .returns(com.github.kr328.kaidl.resolver.IBINDER)
            .addCode(code.build())
            .build()
    )
}

fun FileSpec.Builder.addProxyClass(
    forClass: ClassName,
    functions: Sequence<FunSpec>
): FileSpec.Builder {
    val clazz = TypeSpec.classBuilder(forClass.proxy)
        .addSuperinterface(forClass)
        .primaryConstructor(FunSpec.constructorBuilder().addParameter("remote", com.github.kr328.kaidl.resolver.IBINDER).build())
        .addProperty(PropertySpec.builder("remote", com.github.kr328.kaidl.resolver.IBINDER).initializer("remote").build())

    for (f in functions)
        clazz.addProxy(forClass, f)

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
                KClass::class.asClassName().parameterizedBy(forClass)
            )
            .receiver(com.github.kr328.kaidl.resolver.IBINDER)
            .returns(forClass)
            .addCode(code.build())
            .build()
    )
}

fun TypeSpec.Builder.addCompanion(
    forClass: ClassName,
    functions: Sequence<FunSpec>
): TypeSpec.Builder {
    val companion = TypeSpec.companionObjectBuilder().apply {
        val codes = functions.mapOfCodes()

        addDescriptor(forClass)

        functions.forEach {
            addTransactProperty(it, codes[it.name] ?: throw IllegalArgumentException())
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
    functions: Sequence<FunSpec>
): TypeSpec.Builder {
    val code = CodeBlock.builder().apply {
        beginControlFlow("when (code)")

        for (f in functions) {
            beginControlFlow("%N ->", f.transactionProperty.name)

            addStatement("reply ?: return false")

            addStatement("`data`.enforceInterface(%N)", descriptorProperty.name)

            for (p in f.parameters) {
                addReadFromParcel(p.name, p.type, "data")
            }

            if (f.modifiers.contains(KModifier.SUSPEND)) {
                beginControlFlow(
                    "%M(data, reply)",
                    MemberName(com.github.kr328.kaidl.resolver.INTERFACE.packageName, "suspendTransaction")
                )

                addStatement("reply -> ")
            }

            val args = f.parameters.joinToString(", ") { it.name }

            addStatement("val %N: %T = %N($args)", "_result", f.returnType ?: UNIT, f.name)

            addStatement("reply.writeNoException()")

            addWriteToParcel("_result", f.returnType ?: UNIT, "reply")

            if (f.modifiers.contains(KModifier.SUSPEND)) {
                endControlFlow()
            }

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
        .addParameter(ParameterSpec("data", com.github.kr328.kaidl.resolver.PARCEL))
        .addParameter(ParameterSpec("reply", com.github.kr328.kaidl.resolver.PARCEL.copy(nullable = true)))
        .addParameter(ParameterSpec("flags", INT))
        .returns(BOOLEAN)
        .addCode(code.build())

    return addFunction(func.build())
}

fun TypeSpec.Builder.addProxy(forClass: ClassName, function: FunSpec): TypeSpec.Builder {
    val code = CodeBlock.builder()
        .addStatement("val `data` = Parcel.obtain()")
        .addStatement("val `reply` = Parcel.obtain()")
    val func = function.toBuilder().addModifiers(KModifier.OVERRIDE)

    with(code) {
        beginControlFlow("return try")

        addStatement(
            "`data`.writeInterfaceToken(%T.%N)",
            forClass.delegate,
            descriptorProperty.name
        )

        for (p in function.parameters) {
            addWriteToParcel(p.name, p.type, "data")
        }

        if (function.modifiers.contains(KModifier.SUSPEND)) {
            addStatement(
                "remote.%M(%T.%N, `data`, reply)",
                MemberName(com.github.kr328.kaidl.resolver.INTERFACE.packageName, "suspendTransact"),
                forClass.delegate,
                function.transactionProperty.name
            )
        } else {
            addStatement(
                "remote.transact(%T.%N, `data`, reply, 0)",
                forClass.delegate,
                function.transactionProperty.name
            )
        }

        addStatement("reply.readException()")

        addReadFromParcel("_result", function.returnType ?: UNIT, "reply")

        addStatement("_result")

        nextControlFlow("finally")

        addStatement("data.recycle()")
        addStatement("reply.recycle()")

        endControlFlow()
    }

    return addFunction(func.addCode(code.build()).build())
}

fun Sequence<FunSpec>.mapOfCodes(): Map<String, Int> {
    val definedCodes = mapNotNull { it.tag(CodeValue::class)?.code }

    var generatedCode = -1

    val generateCode: () -> Int = {
        generatedCode++

        while (generatedCode in definedCodes)
            generatedCode++

        generatedCode
    }

    return map {
        it.name to (it.tag(CodeValue::class)?.code ?: generateCode())
    }.toMap()
}