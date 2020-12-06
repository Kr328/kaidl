package com.github.kr328.kaidl.resolver

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName

enum class ParcelableType {
    BinderInterface, AidlInterface, Parcelable, Serializable, Enum
}

fun KSClassDeclaration.resolveParents(): List<KSClassDeclaration> {
    val parent = parentDeclaration

    return if (parent != null && parent is KSClassDeclaration) {
        parent.resolveParents() + parent
    } else {
        emptyList()
    }
}

fun KSDeclaration.toClassName(): ClassName {
    require(this is KSClassDeclaration) { throw IllegalArgumentException() }

    return ClassName(
        packageName.asString(),
        resolveParents().map { it.simpleName.asString() } + simpleName.asString())
}

fun KSType.resolveTypeName(): TypeName {
    val c = declaration.toClassName()

    return if (declaration.typeParameters.isNotEmpty()) {
        c.parameterizedBy(arguments.mapNotNull { it.type?.resolve()?.resolveTypeName() })
    } else {
        c
    }.copy(nullable = isMarkedNullable)
}

fun Modifier.toKModifier(): KModifier? {
    return when (this) {
        Modifier.PUBLIC -> KModifier.PUBLIC
        Modifier.PRIVATE -> KModifier.PRIVATE
        Modifier.INTERNAL -> KModifier.INTERNAL
        Modifier.PROTECTED -> KModifier.PROTECTED
        Modifier.IN -> KModifier.IN
        Modifier.OUT -> KModifier.OUT
        Modifier.OVERRIDE -> KModifier.OVERRIDE
        Modifier.LATEINIT -> KModifier.LATEINIT
        Modifier.ENUM -> KModifier.ENUM
        Modifier.SEALED -> KModifier.SEALED
        Modifier.ANNOTATION -> KModifier.ANNOTATION
        Modifier.DATA -> KModifier.DATA
        Modifier.INNER -> KModifier.INNER
        Modifier.SUSPEND -> KModifier.SUSPEND
        Modifier.TAILREC -> KModifier.TAILREC
        Modifier.OPERATOR -> KModifier.OPERATOR
        Modifier.INFIX -> KModifier.INFIX
        Modifier.INLINE -> KModifier.INLINE
        Modifier.EXTERNAL -> KModifier.EXTERNAL
        Modifier.ABSTRACT -> KModifier.ABSTRACT
        Modifier.FINAL -> KModifier.FINAL
        Modifier.OPEN -> KModifier.OPEN
        Modifier.VARARG -> KModifier.VARARG
        Modifier.NOINLINE -> KModifier.NOINLINE
        Modifier.CROSSINLINE -> KModifier.CROSSINLINE
        Modifier.REIFIED -> KModifier.REIFIED
        Modifier.EXPECT -> KModifier.EXPECT
        Modifier.ACTUAL -> KModifier.ACTUAL
        else -> null
    }
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