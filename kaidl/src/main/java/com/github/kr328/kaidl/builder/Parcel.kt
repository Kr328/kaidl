package com.github.kr328.kaidl.builder

import com.github.kr328.kaidl.ObjectType
import com.github.kr328.kaidl.Type
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName

fun CodeBlock.Builder.addReadFromParcel(
    valName: String,
    type: ObjectType,
    parcelName: String
): CodeBlock.Builder {
    add("val %N: %T = ", valName, type.className)

    if (type.className.isNullable) {
        beginControlFlow("if (%N.readInt() != 0)", parcelName)
    }

    when (type.type) {
        Type.Internal -> when (type.className.canonicalName) {
            "kotlin.Int" -> addStatement("%N.readInt()", parcelName)
            "kotlin.Long" -> addStatement("%N.readLong()", parcelName)
            "kotlin.Float" -> addStatement("%N.readFloat()", parcelName)
            "kotlin.Double" -> addStatement("%N.readDouble()", parcelName)
            "kotlin.String" -> addStatement("%N.readString()!!", parcelName)
            "kotlin.Byte" -> addStatement("%N.readByte()", parcelName)
            "kotlin.Unit" -> addStatement("Unit")
            "kotlin.Boolean" -> addStatement("%N.readInt() != 0", parcelName)
            "android.os.IBinder" -> addStatement("%N.readStrongBinder()!!", parcelName)
            else -> throw IllegalArgumentException("unsupported type ${type.className}")
        }
        Type.BinderInterface ->
            addStatement(
                "%N.readStrongBinder()!!.%M(%T::class)",
                parcelName,
                MemberName(type.className.packageName, "unwrap"),
                type.className
            )
        Type.IInterface ->
            addStatement(
                "%T.asInterface(%N.readStrongBinder()!!)",
                type.className.nestedClass("Stub"),
                parcelName
            )
        Type.Parcelable ->
            addStatement("%T.CREATOR.createFromParcel(%N)!!", type.className, parcelName)
    }

    if (type.className.isNullable) {
        nextControlFlow("else")

        addStatement("null")

        endControlFlow()
    }

    return this
}

fun CodeBlock.Builder.addWriteToParcel(
    valName: String,
    type: ObjectType,
    parcelName: String
): CodeBlock.Builder {
    if (type.className.isNullable) {
        beginControlFlow("if (%N != null)", valName)

        addStatement("%N.writeInt(%L)", parcelName, 1)
    }

    when (type.type) {
        Type.Internal -> when (type.className.canonicalName) {
            "kotlin.Int" -> addStatement("%N.writeInt(%N)", parcelName, valName)
            "kotlin.Long" -> addStatement("%N.writeLong(%N)", parcelName, valName)
            "kotlin.Float" -> addStatement("%N.writeFloat(%N)", parcelName, valName)
            "kotlin.Double" -> addStatement("%N.writeDouble(%N)", parcelName, valName)
            "kotlin.String" -> addStatement("%N.writeString(%N)", parcelName, valName)
            "kotlin.Byte" -> addStatement("%N.writeByte(%N)", parcelName, valName)
            "kotlin.Boolean" -> addStatement("%N.writeInt(if (%N) 1 else 0)", parcelName, valName)
            "android.os.IBinder" -> addStatement("%N.writeStrongBinder(%N)", parcelName, valName)
            "kotlin.Unit" -> Unit
            else -> throw IllegalArgumentException("unsupported type ${type.className}")
        }
        Type.BinderInterface ->
            addStatement(
                "%N.writeStrongBinder(%N.%M())",
                parcelName,
                valName,
                MemberName(type.className.packageName, "wrap")
            )
        Type.IInterface ->
            addStatement("%N.writeStrongBinder(%N.asBinder())", parcelName, valName)
        Type.Parcelable ->
            addStatement("%N.writeToParcel(%N)", valName, parcelName)
    }

    if (type.className.isNullable) {
        nextControlFlow("else")

        addStatement("%N.writeInt(%L)", parcelName, 0)

        endControlFlow()
    }

    return this
}
