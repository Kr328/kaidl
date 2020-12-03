package com.github.kr328.kaidl

import com.squareup.kotlinpoet.ClassName

val INTERFACE = ClassName("com.github.kr328.kaidl", "BinderInterface")
val CODE = ClassName("com.github.kr328.kaidl", "Code")
val BINDER = ClassName("android.os", "Binder")
val IBINDER = ClassName("android.os", "IBinder")
val PARCEL = ClassName("android.os", "Parcel")
val IINTERFACE = ClassName("android.os", "IInterface")
val PARCELABLE = ClassName("android.os", "Parcelable")
val PAIR = ClassName(Pair::class.java.packageName, Pair::class.java.simpleName)
val LIST = ClassName(List::class.java.packageName, List::class.java.simpleName)