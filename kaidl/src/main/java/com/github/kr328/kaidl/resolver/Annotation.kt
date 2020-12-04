package com.github.kr328.kaidl.resolver

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.ClassName

fun KSDeclaration.getAnnotationByName(name: ClassName): KSAnnotation? {
    return annotations
        .firstOrNull { it.annotationType.resolve().declaration.qualifiedName!!.asString() == name.canonicalName }
}

inline fun <reified T> KSAnnotation.getValue(key: String?): T? {
    return arguments.firstOrNull { it.name?.asString() == key } as T
}
