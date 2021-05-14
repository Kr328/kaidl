package com.github.kr328.kaidl.test

import android.os.Bundle
import android.os.IBinder
import android.util.SparseBooleanArray
import java.util.*

class BasicTypeImpl : BasicTypeInterface {
    override fun echoInt(v: Int) = v
    override fun echoLong(v: Long) = v
    override fun echoFloat(v: Float) = v
    override fun echoDouble(v: Double) = v
    override fun echoString(v: String) = v
    override fun echoByte(v: Byte) = v
    override fun echoBoolean(v: Boolean) = v
    override fun echoByteArray(v: ByteArray) = v
    override fun echoCharArray(v: CharArray) = v
    override fun echoBooleanArray(v: BooleanArray) = v
    override fun echoIntArray(v: IntArray) = v
    override fun echoLongArray(v: LongArray) = v
    override fun echoFloatArray(v: FloatArray) = v
    override fun echoDoubleArray(v: DoubleArray) = v
    override fun echoIBinder(v: IBinder) = v
    override fun echoBundle(v: Bundle) = v
    override fun echoSparseBooleanArray(v: SparseBooleanArray) = v
}

class ContainerImpl : ContainerInterface {
    override fun echoIntList(v: List<Int>): List<Int> = v
    override fun echoDoubleList(v: List<Double>): List<Double> = v
    override fun echoStringLongMap(v: Map<String, Long>): Map<String, Long> = v
    override fun echoLongSetList(v: List<Set<Long>>): List<Set<Long>> = v
}

class NullableImpl : NullableInterface {
    override fun echoInt(v: Int?): Int? = v
    override fun echoFloat(v: Float?): Float? = v
    override fun echoString(v: String?): String? = v
    override fun echoMap(v: Map<String, Long?>?): Map<String, Long?>? = v
}

class ComplexTypesImpl : ComplexTypeInterface {
    override fun echoParcelable(p: ExampleParcelable) = p
    override fun echoParcelableNullable(p: ExampleParcelable?) = p
    override fun echoParcelableList(l: List<ExampleParcelable>) = l
    override fun echoBasicInterface(b: BasicTypeInterface) = b
    override fun echoBasicInterfaceNullable(b: BasicTypeInterface?) = b
    override fun echoBasicInterfaceList(l: List<BasicTypeInterface>) = l
    override fun echoUUID(v: UUID): UUID = v
    override fun echoEnum(v: ExampleEnum): ExampleEnum = v
}

class SuspendImpl : SuspendInterface {
    override suspend fun echoInt(v: Int) = v
    override suspend fun echoIntList(v: List<Int>) = v

    override suspend fun throwException(msg: String) {
        throw Exception(msg)
    }
}