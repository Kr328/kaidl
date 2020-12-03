package com.github.kr328.kaidl.resolver

import com.google.devtools.ksp.processing.Resolver

private val localResolver = ThreadLocal<Resolver>()

fun Resolver.store() {
    localResolver.set(this)
}

fun loadResolver(): Resolver {
    return localResolver.get() ?: throw IllegalStateException("local resolver not found")
}