package com.github.kr328.kaidl.builder

import com.github.kr328.kaidl.resolver.IBINDER
import com.squareup.kotlinpoet.*

val FunSpec.transactionProperty: PropertySpec
    get() = PropertySpec.builder("TRANSACTION_$name", INT).build()

val descriptorProperty: PropertySpec
    get() = PropertySpec.builder("DESCRIPTOR", STRING).build()

fun TypeSpec.Builder.addTransactProperty(funSpec: FunSpec, code: Int): TypeSpec.Builder {
    return addProperty(
        funSpec.transactionProperty.toBuilder()
            .addAnnotation(JvmStatic::class)
            .initializer("%T.FIRST_CALL_TRANSACTION + %L", IBINDER, code)
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