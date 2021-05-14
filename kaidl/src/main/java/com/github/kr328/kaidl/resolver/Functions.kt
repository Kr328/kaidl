package com.github.kr328.kaidl.resolver

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.UNIT

data class CodeValue(val code: Int)

fun KSClassDeclaration.resolveFunctions(): Sequence<FunSpec> {
    return declarations.filterIsInstance<KSFunctionDeclaration>()
        .map {
            FunSpec.builder(it.simpleName.asString()).apply {
                addModifiers(it.modifiers.mapNotNull(Modifier::toKModifier))

                it.parameters.forEach { p ->
                    addParameter(p.name?.asString() ?: "", p.type.resolve().resolveTypeName())
                }

                it.getAnnotationByName(CODE)?.getValue<Int>("value")?.apply {
                    tag(CodeValue::class, CodeValue(this))
                }

                returns(it.returnType?.resolve()?.resolveTypeName() ?: UNIT)
            }.build()
        }
}