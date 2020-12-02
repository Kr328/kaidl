package com.github.kr328.kaidl.builder

import com.github.kr328.kaidl.IBINDER
import com.github.kr328.kaidl.Method
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

val Method.transactProperty: PropertySpec
    get() = PropertySpec.builder("TRANSACTION_$name", INT).build()

fun TypeSpec.Builder.addTransactProperty(method: Method): TypeSpec.Builder {
    return addProperty(
        method.transactProperty.toBuilder()
            .addAnnotation(JvmStatic::class)
            .initializer("%T.FIRST_CALL_TRANSACTION + %L", IBINDER, method.id)
            .build()
    )
}