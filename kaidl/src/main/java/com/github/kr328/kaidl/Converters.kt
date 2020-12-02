package com.github.kr328.kaidl

import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.ClassName

fun KSDeclaration.toClassName(): ClassName {
    return ClassName(packageName.asString(), simpleName.asString())
}