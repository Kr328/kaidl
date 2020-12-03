package com.github.kr328.kaidl.resolver

import com.github.kr328.kaidl.IINTERFACE
import com.github.kr328.kaidl.INTERFACE
import com.github.kr328.kaidl.PARCELABLE
import com.squareup.kotlinpoet.TypeName

private val parcelableTypeCache: MutableMap<String, ParcelableType> = mutableMapOf()

val TypeName.parcelableType: ParcelableType
    get() {
        return parcelableTypeCache.getOrPut(this.canonicalName) {
            loadResolver().run {
                val clazz = getClassDeclarationByName(getKSNameFromString(canonicalName))
                        ?: throw ClassNotFoundException()

                when (clazz.qualifiedName!!.asString()) {
                    else -> when {
                        clazz.getAnnotationByName(INTERFACE) != null ->
                            ParcelableType.BinderInterface
                        clazz.getSuperByName(IINTERFACE) != null ->
                            ParcelableType.AidlInterface
                        clazz.getSuperByName(PARCELABLE) != null ->
                            ParcelableType.Parcelable
                        else ->
                            throw IllegalArgumentException("unsupported type $canonicalName")
                    }
                }
            }
        }
    }