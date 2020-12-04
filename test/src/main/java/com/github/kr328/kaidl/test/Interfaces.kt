package com.github.kr328.kaidl.test

import com.github.kr328.kaidl.BinderInterface

@BinderInterface
interface ListenInterface {
    fun onCreated(id: Long)
    fun onDestroy(id: Long, reason: String?)
}

@BinderInterface
interface DataInterface {
    suspend fun queryAllIds(): List<Long>
    fun queryById(id: Long): String
    fun queryAll(): Map<Long, String>
    fun match(db: List<Map<Long, String>>): Map<Long, String>
}

@BinderInterface
interface MainInterface {
    fun register(l: ListenInterface)
    fun data(): DataInterface
}

