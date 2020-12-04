package com.github.kr328.kaidl

import android.os.*
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
import kotlin.coroutines.resumeWithException

private abstract class CompletableBinder : Binder() {
    abstract fun onComplete(data: Parcel)
    abstract fun onCanceled()

    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        when (code) {
            TRANSACTION_complete -> {
                onComplete(data)
            }
            TRANSACTION_canceled -> {
                onCanceled()
            }
            else -> return super.onTransact(code, data, reply, flags)
        }

        return true
    }

    companion object {
        const val TRANSACTION_complete = IBinder.FIRST_CALL_TRANSACTION + 0
        const val TRANSACTION_canceled = IBinder.FIRST_CALL_TRANSACTION + 1
    }
}

private abstract class TransactionContext : Binder() {
    abstract fun requestCancel()

    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        when (code) {
            TRANSACTION_requestCancel ->
                requestCancel()
            else -> super.onTransact(code, data, reply, flags)
        }

        return true
    }

    companion object {
        const val TRANSACTION_requestCancel = IBinder.FIRST_CALL_TRANSACTION + 0
    }
}

suspend fun IBinder.suspendTransact(code: Int, data: Parcel, reply: Parcel): Boolean {
    var finalizer: () -> Unit = {}

    return try {
        suspendCancellableCoroutine {
            val completable = object : CompletableBinder() {
                override fun onComplete(data: Parcel) {
                    reply.appendFrom(data, 0, data.dataAvail())

                    it.resumeWith(Result.success(true))
                }

                override fun onCanceled() {
                    it.cancel()
                }
            }

            val r = Parcel.obtain()

            data.writeStrongBinder(completable)

            val context: IBinder = try {
                transact(code, data, r, 0)

                r.readException()

                r.readStrongBinder()
            } catch (e: Exception) {
                return@suspendCancellableCoroutine it.resumeWithException(e)
            } finally {
                r.recycle()
            }

            val link = IBinder.DeathRecipient {
                @Suppress("ThrowableNotThrown")
                it.resumeWithException(DeadObjectException())
            }

            finalizer = {
                try {
                    context.unlinkToDeath(link, 0)
                } catch (e: Exception) {
                    // ignore
                }
            }

            context.linkToDeath(link, 0)

            it.invokeOnCancellation {
                val stub = Parcel.obtain()

                try {
                    context.transact(TransactionContext.TRANSACTION_requestCancel, stub, null, 0)
                } catch (e: Exception) {
                    // ignore
                } finally {
                    stub.recycle()
                }
            }
        }
    } finally {
        withContext(NonCancellable) {
            finalizer()
        }
    }
}

fun suspendTransaction(
    data: Parcel,
    reply: Parcel,
    block: suspend (reply: Parcel) -> Unit
) {
    val completable = data.readStrongBinder()

    val job = GlobalScope.launch {
        val r = Parcel.obtain()

        try {
            block(r)
        } catch (e: Exception) {
            withContext(NonCancellable) {
                if (e is CancellationException) {
                    completable.transact(CompletableBinder.TRANSACTION_canceled, r, null, 0)
                } else {
                    r.setDataPosition(0)

                    r.writeException(IllegalArgumentException(e.message).apply {
                        stackTrace = e.stackTrace
                    })

                    completable.transact(CompletableBinder.TRANSACTION_complete, r, null, 0)
                }
            }

            throw e
        } finally {
            withContext(NonCancellable) {
                r.recycle()
            }
        }
    }

    val context = object : TransactionContext() {
        override fun requestCancel() {
            job.cancel()
        }
    }

    reply.writeNoException()
    reply.writeStrongBinder(context)
}