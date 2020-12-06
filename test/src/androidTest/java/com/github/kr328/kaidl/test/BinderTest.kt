package com.github.kr328.kaidl.test

import android.os.Binder
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import kotlin.random.Random

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class BinderTest {
    private fun <T> assertEchoEquals(value: T, func: (T) -> T) {
        val echo = func(value)

        assert(Objects.deepEquals(value, echo)) {
            "${Objects.toString(value)} != ${Objects.toString(echo)}"
        }
    }

    private suspend fun <T> assertEchoEqualsSuspend(value: T, func: suspend (T) -> T) {
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

        val stubBinder = object : Binder() {
            override fun getInterfaceDescriptor(): String {
                return descriptor
            }
        }

        assertEquals(descriptor, proxy.echoIBinder(stubBinder).interfaceDescriptor)
    }

    @Test
    fun parcelContainers() {
        val impl = ContainerImpl().wrap()
        val loopback = LoopbackIBinder(impl)
        val proxy = loopback.unwrap(ContainerInterface::class)
        val random = Random(System.currentTimeMillis())

        assertEchoEquals(List(32) { random.nextInt() }, proxy::echoIntList)
        assertEchoEquals(List(32) { random.nextDouble() }, proxy::echoDoubleList)
        assertEchoEquals(List(32) { random.nextString() to random.nextLong() }.toMap(), proxy::echoStringLongMap)
        assertEchoEquals(List(32) { List(8) { random.nextLong() }.toSet() }, proxy::echoLongSetList)
    }

    @Test
    fun parcelNullable() {
        val impl = NullableImpl().wrap()
        val loopback = LoopbackIBinder(impl)
        val proxy = loopback.unwrap(NullableInterface::class)
        val random = Random(System.currentTimeMillis())

        assertEchoEquals(random.nextInt(), proxy::echoInt)
        assertEchoEquals(null, proxy::echoInt)
        assertEchoEquals(random.nextFloat(), proxy::echoFloat)
        assertEchoEquals(null, proxy::echoFloat)
        assertEchoEquals(random.nextString(), proxy::echoString)
        assertEchoEquals(null, proxy::echoString)

        val m = List(10) {
            it.toString() to random.nextLong().takeIf { random.nextBoolean() }
        }.toMap()

        assertEchoEquals(m, proxy::echoMap)
        assertEchoEquals(null, proxy::echoMap)
    }

    @Test
    fun parcelComplexTypes() {
        val impl = ComplexTypesImpl().wrap()
        val loopback = LoopbackIBinder(impl)
        val proxy = loopback.unwrap(ComplexTypeInterface::class)
        val random = Random(System.currentTimeMillis())

        val basic = BasicTypeImpl()

        assertEchoEquals(random.nextParcelable(), proxy::echoParcelable)
        assertEchoEquals(random.nextParcelable(), proxy::echoParcelableNullable)
        assertEchoEquals(null, proxy::echoParcelableNullable)
        assertEchoEquals(List(10) { random.nextParcelable() }, proxy::echoParcelableList)

        assertEchoEquals(random.nextInt()) { proxy.echoBasicInterface(basic).echoInt(it) }
        assertEchoEquals(random.nextInt()) { proxy.echoBasicInterfaceNullable(basic)?.echoInt(it) ?: 0 }
        assertEchoEquals<Int?>(null) { proxy.echoBasicInterfaceNullable(null)?.echoInt(10) }
        assertEchoEquals(List(10) { random.nextInt() }) { l -> l.map { proxy.echoBasicInterface(basic).echoInt(it) } }
        assertEchoEquals(UUID.randomUUID(), proxy::echoUUID)
        assertEchoEquals(ExampleEnum.values()[random.nextInt(ExampleEnum.values().size)], proxy::echoEnum)
    }

    @Test
    fun suspendInterface() {
        runBlocking {
            val impl = SuspendImpl().wrap()
            val loopback = LoopbackIBinder(impl)
            val proxy = loopback.unwrap(SuspendInterface::class)
            val random = Random(System.currentTimeMillis())

            assertEchoEqualsSuspend(random.nextInt(), proxy::echoInt)
            assertEchoEqualsSuspend(List(10) { random.nextInt() }, proxy::echoIntList)

            val msg = random.nextString()

            try {
                proxy.throwException(msg)
            } catch (e: Exception) {
                assertEquals(msg, e.message)
            }
        }
    }
}