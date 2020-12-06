package com.github.kr328.kaidl.builder

import com.github.kr328.kaidl.resolver.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

fun CodeBlock.Builder.addReadFromParcel(
    type: TypeName,
    parcelName: String
): CodeBlock.Builder {
    if (type.isNullable) {
        beginControlFlow("if (%N.readInt() != 0)", parcelName)
    }

    when (type.canonicalName) {
        // internal types
        "kotlin.Int" -> addStatement("%N.readInt()", parcelName)
        "kotlin.Long" -> addStatement("%N.readLong()", parcelName)
        "kotlin.Float" -> addStatement("%N.readFloat()", parcelName)
        "kotlin.Double" -> addStatement("%N.readDouble()", parcelName)
        "kotlin.String" -> addStatement("%N.readString()!!", parcelName)
        "kotlin.Byte" -> addStatement("%N.readByte()", parcelName)
        "kotlin.Unit" -> addStatement("Unit")
        "kotlin.Boolean" -> addStatement("%N.readInt() != 0", parcelName)
        "kotlin.ByteArray" -> addStatement("%N.createByteArray()!!", parcelName)
        "kotlin.CharArray" -> addStatement("%N.createCharArray()!!", parcelName)
        "kotlin.BooleanArray" -> addStatement("%N.createBooleanArray()!!", parcelName)
        "kotlin.IntArray" -> addStatement("%N.createIntArray()!!", parcelName)
        "kotlin.LongArray" -> addStatement("%N.createLongArray()!!", parcelName)
        "kotlin.FloatArray" -> addStatement("%N.createFloatArray()!!", parcelName)
        "kotlin.DoubleArray" -> addStatement("%N.createDoubleArray()!!", parcelName)
        "android.os.IBinder" -> addStatement("%N.readStrongBinder()!!", parcelName)
        "android.os.Bundle" -> addStatement("%N.readBundle()!!", parcelName)
        "android.util.SparseBooleanArray" -> addStatement("%N.readSparseBooleanArray()!!", parcelName)

        // collections
        "kotlin.Pair" -> {
            type as ParameterizedTypeName

            addReadFromParcel("first", type.typeArguments[0], parcelName)
            addReadFromParcel("second", type.typeArguments[1], parcelName)

            addStatement("first to second")
        }
        "kotlin.collections.List" -> {
            type as ParameterizedTypeName

            beginControlFlow(
                "%T(%N.readInt())",
                type,
                parcelName
            )

            addReadFromParcel(type.typeArguments[0], parcelName)

            endControlFlow()
        }
        "kotlin.collections.Array" -> {
            type as ParameterizedTypeName

            beginControlFlow(
                "%T(%N.readInt())",
                type,
                parcelName
            )

            addReadFromParcel(type.typeArguments[0], parcelName)

            endControlFlow()
        }
        "kotlin.collections.Set" -> {
            type as ParameterizedTypeName

            beginControlFlow(
                "%T(%N.readInt())",
                LIST.parameterizedBy(type.typeArguments[0]),
                parcelName
            )

            addReadFromParcel(type.typeArguments[0], parcelName)

            endControlFlow()

            add(".%M()", MemberName("kotlin.collections", "toSet"))
        }
        "kotlin.collections.Map" -> {
            type as ParameterizedTypeName

            beginControlFlow(
                "%T(%N.readInt())",
                LIST.parameterizedBy(
                    PAIR.parameterizedBy(
                        type.typeArguments[0],
                        type.typeArguments[1]
                    )
                ),
                parcelName
            )

            addReadFromParcel(
                PAIR.parameterizedBy(type.typeArguments[0], type.typeArguments[1]),
                parcelName
            )

            endControlFlow()

            addStatement(".%M()", MemberName("kotlin.collections", "toMap"))
        }

        // parcelables
        else -> {
            when (type.parcelableType) {
                ParcelableType.BinderInterface ->
                    addStatement(
                        "%N.readStrongBinder()!!.%M(%T::class)",
                        parcelName,
                        MemberName(type.packageName, "unwrap"),
                        type.copy(nullable = false)
                    )
                ParcelableType.AidlInterface -> {
                    addStatement(
                        "%T.asInterface(%N.readStrongBinder()!!)",
                        (type.copy(nullable = false) as ClassName).nestedClass("Stub"),
                        parcelName
                    )
                }
                ParcelableType.Parcelable -> {
                    addStatement("%T.CREATOR.createFromParcel(%N)!!", type.copy(nullable = false), parcelName)
                }
                ParcelableType.Serializable -> {
                    addStatement("%N.readSerializable() as %T", parcelName, type.copy(nullable = false))
                }
                ParcelableType.Enum -> {
                    addStatement("%T.values()[%N.readInt()]", type.copy(nullable = false), parcelName)
                }
            }
        }
    }

    if (type.isNullable) {
        nextControlFlow("else")

        addStatement("null")

        endControlFlow()
    }

    return this
}

fun CodeBlock.Builder.addReadFromParcel(
    valName: String,
    type: TypeName,
    parcelName: String,
): CodeBlock.Builder {
    return add("val %N: %T = ", valName, type).addReadFromParcel(type, parcelName)
}

fun CodeBlock.Builder.addWriteToParcel(
    valName: String,
    type: TypeName,
    parcelName: String
): CodeBlock.Builder {
    if (type.isNullable) {
        beginControlFlow("if (%N != null)", valName)

        addStatement("%N.writeInt(%L)", parcelName, 1)
    }

    when (type.canonicalName) {
        // internal types
        "kotlin.Int" -> addStatement("%N.writeInt(%N)", parcelName, valName)
        "kotlin.Long" -> addStatement("%N.writeLong(%N)", parcelName, valName)
        "kotlin.Float" -> addStatement("%N.writeFloat(%N)", parcelName, valName)
        "kotlin.Double" -> addStatement("%N.writeDouble(%N)", parcelName, valName)
        "kotlin.String" -> addStatement("%N.writeString(%N)", parcelName, valName)
        "kotlin.Byte" -> addStatement("%N.writeByte(%N)", parcelName, valName)
        "kotlin.Unit" -> Unit
        "kotlin.Boolean" -> addStatement("%N.writeInt(if (%N) 1 else 0)", parcelName, valName)
        "kotlin.ByteArray" -> addStatement("%N.writeByteArray(%N)", parcelName, valName)
        "kotlin.CharArray" -> addStatement("%N.writeCharArray(%N)", parcelName, valName)
        "kotlin.BooleanArray" -> addStatement("%N.writeBooleanArray(%N)", parcelName, valName)
        "kotlin.IntArray" -> addStatement("%N.writeIntArray(%N)", parcelName, valName)
        "kotlin.LongArray" -> addStatement("%N.writeLongArray(%N)", parcelName, valName)
        "kotlin.FloatArray" -> addStatement("%N.writeFloatArray(%N)", parcelName, valName)
        "kotlin.DoubleArray" -> addStatement("%N.writeDoubleArray(%N)", parcelName, valName)
        "android.os.IBinder" -> addStatement("%N.writeStrongBinder(%N)", parcelName, valName)
        "android.os.Bundle" -> addStatement("%N.writeBundle(%N)", parcelName, valName)
        "android.util.SparseBooleanArray" -> addStatement(
            "%N.writeSparseBooleanArray(%N)",
            parcelName,
            valName
        )

        // collections
        "kotlin.Pair" -> {
            type as ParameterizedTypeName

            addStatement("val first = %N.first", valName)
            addStatement("val second = %N.second", valName)

            addWriteToParcel("first", type.typeArguments[0], parcelName)
            addWriteToParcel("second", type.typeArguments[1], parcelName)
        }
        "kotlin.collections.List", "kotlin.collections.Set", "kotlin.collections.Array" -> {
            type as ParameterizedTypeName

            addStatement("%N.writeInt(%N.size)", parcelName, valName)

            beginControlFlow("%N.%M", valName, MemberName("kotlin.collections", "forEach"))

            addWriteToParcel("it", type.typeArguments[0], parcelName)

            endControlFlow()
        }
        "kotlin.collections.Map" -> {
            type as ParameterizedTypeName

            addStatement("val list = %N.%M()", valName, MemberName("kotlin.collections", "toList"))

            addWriteToParcel(
                "list",
                LIST.parameterizedBy(
                    PAIR.parameterizedBy(
                        type.typeArguments[0],
                        type.typeArguments[1]
                    )
                ),
                parcelName
            )
        }

        else -> {
            when (type.parcelableType) {
                ParcelableType.BinderInterface -> {
                    addStatement(
                        "%N.writeStrongBinder(%N.%M())",
                        parcelName,
                        valName,
                        MemberName(type.packageName, "wrap")
                    )
                }
                ParcelableType.AidlInterface -> {
                    addStatement("%N.writeStrongBinder(%N.asBinder())", parcelName, valName)
                }
                ParcelableType.Parcelable -> {
                    addStatement("%N.writeToParcel(%N, 0)", valName, parcelName)
                }
                ParcelableType.Serializable -> {
                    addStatement("%N.writeSerializable(%N)", parcelName, valName)
                }
                ParcelableType.Enum -> {
                    addStatement("%N.writeInt(%N.ordinal)", parcelName, valName)
                }
            }
        }
    }

    if (type.isNullable) {
        nextControlFlow("else")

        addStatement("%N.writeInt(%L)", parcelName, 0)

        endControlFlow()
    }

    return this
}
