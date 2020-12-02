package com.github.kr328.kaidl

import com.squareup.kotlinpoet.ClassName

internal val INTERFACE = ClassName("com.github.kr328.kaidl", "BinderInterface")
internal val CODE = ClassName("com.github.kr328.kaidl", "Code")
internal val BINDER = ClassName("android.os", "Binder")
internal val IBINDER = ClassName("android.os", "IBinder")
internal val PARCEL = ClassName("android.os", "Parcel")
internal val IINTERFACE = ClassName("android.os", "IInterface")
internal val PARCELABLE = ClassName("android.os", "Parcelable")