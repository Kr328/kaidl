package com.github.kr328.kaidl.test

import com.github.kr328.kaidl.BinderInterface

@BinderInterface
interface NullableInterface {
    fun echoInt(v: Int?): Int?
    fun echoFloat(v: Float?): Float?
    fun echoString(v: String?): String?
    fun echoMap(v: Map<String, Long?>?): Map<String, Long?>?
}

