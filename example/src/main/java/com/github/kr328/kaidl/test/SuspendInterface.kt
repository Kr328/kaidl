package com.github.kr328.kaidl.test

import com.github.kr328.kaidl.BinderInterface

@BinderInterface
interface SuspendInterface {
    suspend fun echoInt(v: Int): Int
    suspend fun echoIntList(v: List<Int>): List<Int>
    suspend fun throwException(msg: String)
}