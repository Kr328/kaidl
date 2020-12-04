package com.github.kr328.kaidl.resolver

import com.google.devtools.ksp.processing.Resolver

private val localResolver = ThreadLocal<Resolver>()

fun Resolver.store(block: () -> Unit) {
    localResolver.set(this)

    try {
        block()
    } finally {
        localResolver.remove()
    }
}

fun loadResolver(): Resolver {
    return localResolver.get() ?: throw IllegalStateException("local resolver not found")
}