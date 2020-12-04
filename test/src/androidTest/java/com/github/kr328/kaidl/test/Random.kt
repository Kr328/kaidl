package com.github.kr328.kaidl.test

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