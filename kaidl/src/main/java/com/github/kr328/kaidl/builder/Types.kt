package com.github.kr328.kaidl.builder

import com.squareup.kotlinpoet.ClassName

val ClassName.delegate: ClassName
    get() = ClassName(packageName, simpleName + "Delegate")

val ClassName.proxy: ClassName
    get() = ClassName(packageName, simpleName + "Proxy")