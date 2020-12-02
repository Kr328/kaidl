package com.github.kr328.kaidl

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.UNIT

enum class Type {
    Internal, AndroidInterface, IInterface, Parcelable
}

data class ObjectType(
        val className: ClassName,
        val type: Type,
)

data class Method(
        val id: Int,
        val name: String,
        val returnType: ObjectType,
        val parameters: List<Pair<String, ObjectType>>
)

fun KSDeclaration.getAnnotation(type: ClassName, name: String?): Pair<KSAnnotation, Any?>? {
    for (a in annotations) {
        val t = a.annotationType.resolve().declaration

        if (t is KSClassDeclaration) {
            if (t.qualifiedName?.asString() == type.canonicalName) {
                return if (name != null)
                    a to a.arguments.find { it.name?.asString() == name }?.value
                else
                    a to null
            }
        }
    }

    return null
}

fun KSClassDeclaration.parseMethods(): List<Method> {
    val functions = declarations
            .filterIsInstance<KSFunctionDeclaration>()
            .filter { it.functionKind == FunctionKind.MEMBER }
            .map { it to (it.getAnnotation(CODE, "value")?.second as Int?) }

    val definedCodes = functions
            .mapNotNull { it.second }

    var autoCode = 0

    val getAutoCode: () -> Int = {
        autoCode++

        while (autoCode in definedCodes)
            autoCode++

        autoCode
    }

    return functions.map {
        val code = it.second ?: getAutoCode()
        val name = it.first.simpleName.asString()
        val returnType = it.first.returnType?.resolve()?.parseObjectType()
                ?: ObjectType(UNIT, Type.Internal)
        val params = it.first.parameters.map { p ->
            p.name!!.asString() to p.type.resolve().parseObjectType()
        }

        Method(code, name, returnType, params)
    }
}

fun KSType.parseObjectType(): ObjectType {
    val supers = (declaration as KSClassDeclaration).getAllSuperTypes()
    val type = when ((declaration as KSClassDeclaration).qualifiedName!!.asString()) {
        "kotlin.Int", "kotlin.Long",
        "kotlin.Float", "kotlin.Double",
        "kotlin.String",
        "kotlin.Byte",
        "kotlin.Unit",
        "kotlin.Boolean",
        "android.os.IBinder" -> Type.Internal
        else -> when {
            declaration.getAnnotation(INTERFACE, null) != null ->
                Type.AndroidInterface
            supers.any { s -> s.declaration.qualifiedName?.asString() == IINTERFACE.canonicalName } ->
                Type.IInterface
            supers.any { s -> s.declaration.qualifiedName?.asString() == PARCELABLE.canonicalName } ->
                Type.Parcelable
            else ->
                throw IllegalArgumentException("unsupported type $declaration")
        }
    }

    return ObjectType(
            declaration.toClassName().copy(nullable = isMarkedNullable) as ClassName,
            type
    )
}

