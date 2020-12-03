package com.github.kr328.kaidl.resolver

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName

enum class ParcelableType {
    BinderInterface, AidlInterface, Parcelable
}

fun KSDeclaration.toClassName(): ClassName {
    require(this is KSClassDeclaration) { throw IllegalArgumentException() }

    return ClassName(packageName.asString(), simpleName.asString())
}

fun ClassName.markNullable(nullable: Boolean): ClassName {
    canonicalName

    return copy(nullable = nullable) as ClassName
}

fun KSType.resolveTypeName(): TypeName {
    val c = declaration.toClassName().copy(nullable = isMarkedNullable) as ClassName

    if (declaration.typeParameters.isNotEmpty())
        return c.parameterizedBy(arguments.mapNotNull { it.type?.resolve()?.resolveTypeName() })

    return c
}

val TypeName.simpleName: String
    get() {
        return when (this) {
            is ClassName -> this.simpleName
            is ParameterizedTypeName -> this.rawType.simpleName
            else -> throw IllegalArgumentException("unsupported type $this")
        }
    }

val TypeName.packageName: String
    get() {
        return when (this) {
            is ClassName -> this.packageName
            is ParameterizedTypeName -> this.rawType.packageName
            else -> throw IllegalArgumentException("unsupported type $this")
        }
    }

val TypeName.canonicalName: String
    get() {
        return when (this) {
            is ClassName -> this.canonicalName
            is ParameterizedTypeName -> this.rawType.canonicalName
            else -> throw IllegalArgumentException("unsupported type $this")
        }
    }