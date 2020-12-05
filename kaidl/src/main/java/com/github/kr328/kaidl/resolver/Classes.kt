package com.github.kr328.kaidl.resolver

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import java.io.Serializable

val INTERFACE = ClassName("com.github.kr328.kaidl", "BinderInterface")
val CODE = ClassName("com.github.kr328.kaidl", "Code")
val BINDER = ClassName("android.os", "Binder")
val IBINDER = ClassName("android.os", "IBinder")
val PARCEL = ClassName("android.os", "Parcel")
val IINTERFACE = ClassName("android.os", "IInterface")
val PARCELABLE = ClassName("android.os", "Parcelable")
val SERIALIZABLE = Serializable::class.asClassName()
val PAIR = Pair::class.asClassName()