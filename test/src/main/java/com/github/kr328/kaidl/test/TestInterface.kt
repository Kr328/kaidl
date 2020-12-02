package com.github.kr328.kaidl.test

import com.github.kr328.kaidl.AndroidInterface

@AndroidInterface
interface TestInterface {
    fun ping(id: Int?, name: String): String?
    fun pong(): Boolean
}
