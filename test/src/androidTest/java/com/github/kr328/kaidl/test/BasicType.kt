package com.github.kr328.kaidl.test

import android.os.Bundle
import android.os.IBinder
import android.util.SparseBooleanArray

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
