package com.github.kr328.kaidl.test

import com.github.kr328.kaidl.BinderInterface

@BinderInterface
interface OtherInterface {
    fun queryActive(): String
}
