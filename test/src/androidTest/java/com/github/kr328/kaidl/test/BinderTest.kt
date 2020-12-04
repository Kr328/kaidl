package com.github.kr328.kaidl.test

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import kotlin.random.Random

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class BinderTest {
    private fun <T>assertEchoEquals(value: T, func: (T) -> T) {
        assertEquals(value, func(value))
    }

    @Test
    fun parcelBasicTypes() {
        val impl = BasicTypeImpl().wrap()
        val loopback = LoopbackIBinder(impl)
        val proxy = loopback.unwrap(BasicTypeInterface::class)
        val random = Random(System.currentTimeMillis())

        assertEchoEquals(random.nextInt(), proxy::echoInt)
        assertEchoEquals(random.nextLong(), proxy::echoLong)
        assertEchoEquals(random.nextFloat(), proxy::echoFloat)
        assertEchoEquals(random.nextDouble(), proxy::echoDouble)
        assertEchoEquals(random.nextString(), proxy::echoString)
        assertEchoEquals(random.nextBytes(1)[0], proxy::echoByte)
        assertEchoEquals(random.nextBoolean(), proxy::echoBoolean)
        assertEchoEquals(random.nextBytes(64), proxy::echoByteArray)
        assertEchoEquals(random.nextCharArray(64), proxy::echoCharArray)
        assertEchoEquals(random.nextBooleanArray(64), proxy::echoBooleanArray)
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.github.kr328.kaidl.test.test", appContext.packageName)
    }
}