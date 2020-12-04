package com.github.kr328.kaidl.test

import android.os.Binder
import android.os.Bundle
import android.os.IInterface
import android.os.Parcel
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.util.*
import kotlin.random.Random

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class BinderTest {
    private fun <T>assertEchoEquals(value: T, func: (T) -> T) {
        val echo = func(value)

        assert(Objects.deepEquals(value, echo)) {
            "${Objects.toString(value)} != ${Objects.toString(echo)}"
        }
    }

    @Test
    fun assertion() {
        assert(!Objects.deepEquals(1, 2))
        assert(Objects.deepEquals(1, 1))
    }

    @Test
    fun parcelBasicTypes() {
        val impl = BasicTypeImpl().wrap()
        val loopback = LoopbackIBinder(impl)
        val proxy = loopback.unwrap(BasicTypeInterface::class)
        val random = Random(System.currentTimeMillis())

        assert(proxy !is BasicTypeImpl)

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
        assertEchoEquals(random.nextIntArray(64), proxy::echoIntArray)
        assertEchoEquals(random.nextLongArray(64), proxy::echoLongArray)
        assertEchoEquals(random.nextFloatArray(64), proxy::echoFloatArray)
        assertEchoEquals(random.nextDoubleArray(64), proxy::echoDoubleArray)
        assertEchoEquals(random.nextSparseBooleanArray(64), proxy::echoSparseBooleanArray)

        val bundle = Bundle().apply {
            putLong("key", random.nextLong())
        }

        assert(bundle.get("key")?.equals(proxy.echoBundle(bundle).get("key")) ?: false)

        val descriptor = random.nextString()

        val stubBinder = object: Binder() {
            override fun getInterfaceDescriptor(): String {
                return descriptor
            }
        }

        assertEquals(descriptor, proxy.echoIBinder(stubBinder).interfaceDescriptor)
    }


}