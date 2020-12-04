package com.github.kr328.kaidl.resolver

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName

fun KSClassDeclaration.getSuperByName(name: ClassName): KSClassDeclaration? {
    return superTypes.map { it.resolve().declaration }
        .filterIsInstance<KSClassDeclaration>()
        .firstOrNull { it.qualifiedName!!.asString() == name.canonicalName }
}