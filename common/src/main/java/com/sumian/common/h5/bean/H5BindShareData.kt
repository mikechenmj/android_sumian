package com.sumian.common.h5.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sumian.common.utils.JsonUtil

data class H5BindShareData(
        @SerializedName("platform") var platform: ArrayList<String> = ArrayList(),   //1.text 2.success 3.error 4.loading 5.warning
        @SerializedName("WEIXIN") var weixin: WEIXIN? = null,
        @SerializedName("WEIXIN_CIRCLE") var weixinCircle: WEIXIN_CIRCLE? = null
) {
    companion object {
        fun fromJson(json: String?): H5BindShareData {
            var toastData = JsonUtil.fromJson(json, H5BindShareData::class.java)
            if (toastData == null) {
                toastData = H5BindShareData()
            }
            return toastData
        }
    }
}

data class WEIXIN(var title: String, var desc: String, var link: String, var imgUrl: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(desc)
        parcel.writeString(link)
        parcel.writeString(imgUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WEIXIN> {
        override fun createFromParcel(parcel: Parcel): WEIXIN {
            return WEIXIN(parcel)
        }

        override fun newArray(size: Int): Array<WEIXIN?> {
            return arrayOfNulls(size)
        }
    }
}

data class WEIXIN_CIRCLE(var title: String, var link: String, var imgUrl: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(link)
        parcel.writeString(imgUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WEIXIN_CIRCLE> {
        override fun createFromParcel(parcel: Parcel): WEIXIN_CIRCLE {
            return WEIXIN_CIRCLE(parcel)
        }

        override fun newArray(size: Int): Array<WEIXIN_CIRCLE?> {
            return arrayOfNulls(size)
        }
    }
}