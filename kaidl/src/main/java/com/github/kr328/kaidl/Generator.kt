package com.github.kr328.kaidl

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.util.*
import kotlin.reflect.KClass

fun FileSpec.Builder.generateStub(forClass: ClassName, methods: List<Method>): FileSpec.Builder {
    val stub =
            TypeSpec.classBuilder(ClassName(forClass.packageName, forClass.simpleName + "Delegate"))
                    .primaryConstructor(FunSpec.constructorBuilder().addParameter("impl", forClass).build())
                    .addModifiers(KModifier.OPEN)
                    .superclass(BINDER)
                    .addSuperinterface(forClass, "impl")

    return addType(stub.generateCompanion(methods).generateOnTransact(methods).build())
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
                    .addParameter("c", ClassName(KClass::class.java.packageName, KClass::class.java.simpleName).parameterizedBy(forClass))
                    .receiver(IBINDER)
                    .returns(forClass)
                    .addCode(CodeBlock.of("return if (this is ${forClass.simpleName}) this else ${forClass.simpleName}Proxy(this)"))
                    .build()
    )
}

fun TypeSpec.Builder.generateCompanion(forClass: ClassName, methods: List<Method>): TypeSpec.Builder {
    val companion = TypeSpec.companionObjectBuilder().apply {
        methods.forEach {
            addProperty(
                    PropertySpec.builder(it.transactProperty(), INT, KModifier.CONST)
                            .initializer("IBinder.FIRST_CALL_TRANSACTION + ${it.id}").build()
            )
        }
    }

    return addType(companion.build())
}

fun TypeSpec.Builder.generateOnTransact(methods: List<Method>): TypeSpec.Builder {
    val code = CodeBlock.builder().apply {
        beginControlFlow("when (code)")

        for (m in methods) {
            beginControlFlow("${m.transactProperty()} ->")

            addStatement("requireNotNull(reply)")

            for (p in m.parameters) {
                add("val ${p.first}: ${p.second.className} = ")
                addStatement(p.second.readFromParcel("data"))
            }

            beginControlFlow("try")

            val args = m.parameters.map { it.first }.joinToString(", ")

            val call = "${m.name}($args)"

            if (m.returnType.className == UNIT) {
                addStatement(call)

                addStatement("reply.writeNoException()")
            } else {
                addStatement("val _result = $call")

                addStatement("reply.writeNoException()")

                add(m.returnType.writeToParcel("reply", "_result"))
            }

            nextControlFlow("catch (e: Exception)")

            addStatement("reply.writeException(e)")

            endControlFlow()

            endControlFlow()
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
            .addStatement("val data = Parcel.obtain()")
            .addStatement("val reply = Parcel.obtain()")
    val func = FunSpec.builder(method.name)
            .addModifiers(KModifier.OVERRIDE)
            .returns(method.returnType.className)

    code.beginControlFlow("return try")

    for (p in method.parameters) {
        func.addParameter(p.first, p.second.className)

        code.add(p.second.writeToParcel("data", p.first))
    }

    val c = forClass.simpleName + "Delegate" + "." + method.transactProperty()

    code.addStatement("remote.transact($c, data, reply, 0)")

    code.addStatement("reply.readException()")

    code.addStatement(method.returnType.readFromParcel("reply"))

    code.nextControlFlow("finally")

    code.addStatement("data.recycle()")
    code.addStatement("reply.recycle()")

    code.endControlFlow()

    return addFunction(func.addCode(code.build()).build())
}

fun Method.transactProperty(): String {
    return "TRANSACT_CODE_${this.name.toUpperCase(Locale.ENGLISH)}"
}

fun ObjectType.readFromParcel(parcelName: String): String {
    val r = when (type) {
        Type.Internal -> when (className.canonicalName) {
            "kotlin.Int" -> "$parcelName.readInt()"
            "kotlin.Long" -> "$parcelName.readLong()"
            "kotlin.Float" -> "$parcelName.readFloat()"
            "kotlin.Double" -> "$parcelName.readDouble()"
            "kotlin.String" -> "$parcelName.readString()!!"
            "kotlin.Byte" -> "$parcelName.readByte()"
            "kotlin.Unit" -> "Unit"
            "kotlin.Boolean" -> "$parcelName.readInt() != 0"
            "android.os.IBinder" -> "$parcelName.readStrongBinder()!!"
            else -> throw IllegalArgumentException("unsupported type $className")
        }
        Type.AndroidInterface ->
            "$className.unwrap($parcelName.readStrongBinder()!!)"
        Type.IInterface ->
            "$className.Stub.asInterface($parcelName.readStrongBinder()!!)"
        Type.Parcelable ->
            "$className.CREATOR.createFromParcel($parcelName)!!"
    }

    return if (className.isNullable)
        "if ($parcelName.readInt() != 0) $r else null"
    else
        r
}

fun ObjectType.writeToParcel(parcelName: String, varName: String): String {
    val r = when (type) {
        Type.Internal -> when (className.canonicalName) {
            "kotlin.Int" -> "$parcelName.writeInt($varName)"
            "kotlin.Long" -> "$parcelName.writeLong($varName)"
            "kotlin.Float" -> "$parcelName.writeFloat($varName)"
            "kotlin.Double" -> "$parcelName.writeDouble($varName)"
            "kotlin.String" -> "$parcelName.writeString($varName)"
            "kotlin.Byte" -> "$parcelName.writeByte($varName)"
            "kotlin.Unit" -> ""
            "kotlin.Boolean" -> "$parcelName.writeInt(if ($varName) 1 else 0)"
            "android.os.IBinder" -> "$parcelName.writeStrongBinder($varName)"
            else -> throw IllegalArgumentException("unsupported type $className")
        }
        Type.AndroidInterface ->
            "$parcelName.writeStrongBinder($varName.wrap())"
        Type.IInterface ->
            "$parcelName.writeStrongBinder($varName.asBinder())"
        Type.Parcelable ->
            "$varName.writeToParcel($parcelName)"
    }

    return if (className.isNullable) {
        """
            if ($varName == null) {
                $parcelName.writeInt(0)
            } else {
                $parcelName.writeInt(1)
                $r
            }
            
        """.trimIndent()
    } else {
        "$r\n"
    }
}