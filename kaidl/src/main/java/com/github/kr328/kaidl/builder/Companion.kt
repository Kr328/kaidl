package com.github.kr328.kaidl.builder

import com.github.kr328.kaidl.IBINDER
import com.github.kr328.kaidl.Method
import com.squareup.kotlinpoet.*

val Method.transactionProperty: PropertySpec
    get() = PropertySpec.builder("TRANSACTION_$name", INT).build()

val descriptorProperty: PropertySpec
    get() = PropertySpec.builder("DESCRIPTOR", STRING).build()

fun TypeSpec.Builder.addTransactProperty(method: Method): TypeSpec.Builder {
    return addProperty(
        method.transactionProperty.toBuilder()
            .addAnnotation(JvmStatic::class)
            .initializer("%T.FIRST_CALL_TRANSACTION + %L", IBINDER, method.id)
            .build()
    )
}

fun TypeSpec.Builder.addDescriptor(forClass: ClassName): TypeSpec.Builder {
    return addProperty(
        descriptorProperty.toBuilder()
            .addAnnotation(JvmStatic::class)
            .initializer("%S", forClass.canonicalName)
            .build()
    )
}