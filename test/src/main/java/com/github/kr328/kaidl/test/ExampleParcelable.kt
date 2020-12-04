package com.github.kr328.kaidl.test

import android.os.Parcel
import android.os.Parcelable

data class ExampleParcelable(val int: Int, val long: Long, val string: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(int)
        parcel.writeLong(long)
        parcel.writeString(string)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ExampleParcelable> {
        override fun createFromParcel(parcel: Parcel): ExampleParcelable {
            return ExampleParcelable(parcel)
        }

        override fun newArray(size: Int): Array<ExampleParcelable?> {
            return arrayOfNulls(size)
        }
    }
}