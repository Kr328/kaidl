package com.github.kr328.kaidl.resolver

import com.squareup.kotlinpoet.TypeName

private val parcelableTypeCache: MutableMap<String, ParcelableType> = mutableMapOf()

val TypeName.parcelableType: ParcelableType
    get() {
        return parcelableTypeCache.getOrPut(this.canonicalName) {
            loadResolver().run {
                val clazz = getClassDeclarationByName(getKSNameFromString(canonicalName))
                    ?: throw ClassNotFoundException("$canonicalName not found")

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