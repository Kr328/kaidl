package com.github.kr328.kaidl.test

import android.os.Bundle
import android.util.SparseBooleanArray
import kotlin.random.Random

fun Random.nextString(): String {
    return List(nextInt(64)) { nextInt(100) }.joinToString()
}

fun Random.nextCharArray(size: Int): CharArray {
    return Array(size) { nextInt().toChar() }.toCharArray()
}

fun Random.nextBooleanArray(size: Int): BooleanArray {
    return Array(size) { nextBoolean() }.toBooleanArray()
}

fun Random.nextIntArray(size: Int): IntArray {
    return Array(size) { nextInt() }.toIntArray()
}

fun Random.nextLongArray(size: Int): LongArray {
    return Array(size) { nextLong() }.toLongArray()
}

fun Random.nextFloatArray(size: Int): FloatArray {
    return Array(size) { nextFloat() }.toFloatArray()
}

fun Random.nextDoubleArray(size: Int): DoubleArray {
    return Array(size) { nextDouble() }.toDoubleArray()
}

fun Random.nextSparseBooleanArray(size: Int): SparseBooleanArray {
    return SparseBooleanArray(size).apply {
        repeat(size) {
            append(it, nextBoolean())
        }
    }
}
