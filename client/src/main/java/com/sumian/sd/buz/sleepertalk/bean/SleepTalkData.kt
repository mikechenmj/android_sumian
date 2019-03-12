package com.sumian.sd.buz.sleepertalk.bean

import com.google.gson.annotations.SerializedName

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/12 21:12
 * desc   :
 * version: 1.0
 */
data class SleepTalkData(
        @SerializedName("author")
        val author: String, // aaa
        @SerializedName("content")
        val content: String, // <p><span style="font-size: 16px;font-family: 微软雅黑;">dfbslbsbsd</span></p><p><span style="font-size: 12px;font-family: 微软雅黑;">dfskhbskbgeruwghqeg</span></p><p><span style="font-size: 12px;font-family: 微软雅黑;">wegwegewgwe</span></p><p><span style="font-size: 12px;font-family: 微软雅黑;">wegwegewewgewgewgwe</span></p><p><span style="font-size: 12px;font-family: 微软雅黑;">wegewtrejhtewhergwrag</span></p><p></p><img src="https://sd-dev-oss-cdn.sumian.com/admin/pictures/62dbbf0e-23d2-408b-ac99-1ec413b39476.jpg" alt="" style="float:none;height: auto;width: auto"/><p></p><p></p>
        @SerializedName("cover_url")
        val coverUrl: String, // https://sd-dev-oss-cdn.sumian.com/admin/pictures/fb924834-b073-46bb-9461-771935de8903.jpg
        @SerializedName("created_at")
        val createdAt: Int, // 1552298185
        @SerializedName("id")
        val id: Int, // 1
        @SerializedName("introduction")
        val introduction: String, // dsbgsbgwegbev
        @SerializedName("is_like")
        val isLike: Boolean, // false
        @SerializedName("is_top")
        val isTop: Int, // 0
        @SerializedName("title")
        val title: String, // dbdbdsfbfbweb
        @SerializedName("updated_at")
        val updatedAt: Int // 1552371963
)