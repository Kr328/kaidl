package com.github.kr328.kaidl.test

import com.github.kr328.kaidl.BinderInterface
import java.util.*

@BinderInterface
interface ComplexTypeInterface {
    fun echoParcelable(p: ExampleParcelable): ExampleParcelable
    fun echoParcelableNullable(p: ExampleParcelable?): ExampleParcelable?
    fun echoParcelableList(l: List<ExampleParcelable>): List<ExampleParcelable>
    fun echoBasicInterface(b: BasicTypeInterface): BasicTypeInterface
    fun echoBasicInterfaceNullable(b: BasicTypeInterface?): BasicTypeInterface?
    fun echoBasicInterfaceList(l: List<BasicTypeInterface>): List<BasicTypeInterface>
    fun echoUUID(v: UUID): UUID
    fun echoEnum(v: ExampleEnum): ExampleEnum
}
