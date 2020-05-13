package com.sumian.sd.buz.account.bean

import android.os.Parcel
import android.os.Parcelable

data class Ethnicities(var data: ArrayList<Ethnicity>) {
    companion object {
        const val SP_ETHNICITIES = "ethnicities"
    }

    data class Ethnicity(var id: Int, var name: String) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readInt(),
                parcel.readString() ?: "")

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(id)
            parcel.writeString(name)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Ethnicity> {
            override fun createFromParcel(parcel: Parcel): Ethnicity {
                return Ethnicity(parcel)
            }

            override fun newArray(size: Int): Array<Ethnicity?> {
                return arrayOfNulls(size)
            }
        }
    }
}