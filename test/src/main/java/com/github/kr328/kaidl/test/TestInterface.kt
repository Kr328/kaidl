package com.github.kr328.kaidl.test

import com.github.kr328.kaidl.BinderInterface

@BinderInterface
interface TestInterface {
    fun ping(id: Int?, name: String): String?
    fun pong(): Boolean

    fun register(o: OtherInterface)
}
