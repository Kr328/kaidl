package com.github.kr328.kaidl.test

import com.github.kr328.kaidl.BinderInterface

@BinderInterface
interface ContainerInterface {
    fun echoIntList(v: List<Int>): List<Int>
    fun echoDoubleList(v: List<Double>): List<Double>
    fun echoStringLongMap(v: Map<String, Long>): Map<String, Long>
    fun echoLongSetList(v: List<Set<Long>>): List<Set<Long>>
}
