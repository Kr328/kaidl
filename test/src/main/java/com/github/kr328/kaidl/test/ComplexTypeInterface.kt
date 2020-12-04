package com.github.kr328.kaidl.test

import com.github.kr328.kaidl.BinderInterface

@BinderInterface
interface ComplexTypeInterface {
    fun echoParcelable(p: ExampleParcelable): ExampleParcelable
    fun echoParcelableNullable(p: ExampleParcelable?): ExampleParcelable?
    fun echoParcelableList(l: List<ExampleParcelable>): List<ExampleParcelable>
}
