package com.sumian.sleepdoctor.advisory.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *
 *Created by sm
 * on 2018/6/8 10:20
 * desc:图片上传的 oss sts 凭证信息
 **/
@Parcelize
data class PictureOssSts(var access_key_id: String,
                         var access_key_secret: String,
                         var security_token: String,
                         var expiration: String,
                         var bucket: String,
                         var region: String,
                         var endpoint: String,
                         var callback_url: String,
                         var callback_body: String,
                         var objects: ArrayList<String>
) : Parcelable