package com.github.kr328.kaidl.test

import android.os.Bundle
import android.os.IBinder
import android.util.SparseBooleanArray
import com.github.kr328.kaidl.BinderInterface

@BinderInterface
interface BasicTypeInterface {
    fun echoInt(v: Int): Int
    fun echoLong(v: Long): Long
    fun echoFloat(v: Float): Float
    fun echoDouble(v: Double): Double
    fun echoString(v: String): String
    fun echoByte(v: Byte): Byte
    fun echoBoolean(v: Boolean): Boolean
    fun echoByteArray(v: ByteArray): ByteArray
    fun echoCharArray(v: CharArray): CharArray
    fun echoBooleanArray(v: BooleanArray): BooleanArray
    fun echoIntArray(v: IntArray): IntArray
    fun echoLongArray(v: LongArray): LongArray
    fun echoFloatArray(v: FloatArray): FloatArray
    fun echoDoubleArray(v: DoubleArray): DoubleArray
    fun echoIBinder(v: IBinder): IBinder
    fun echoBundle(v: Bundle): Bundle
    fun echoSparseBooleanArray(v: SparseBooleanArray): SparseBooleanArray
}
