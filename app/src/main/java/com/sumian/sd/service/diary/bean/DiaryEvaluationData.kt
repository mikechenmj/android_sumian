package com.sumian.sd.service.diary.bean

import com.google.gson.annotations.SerializedName

/**
 * {
"data": [
{
"id": 1,
"user_id": 2040,
"doctor_id": 2,
"package_id": 17,
"traceable_id": 1,
"traceable_type": "App\\Models\\Order",
"status": 4,
"start_at": 1536743737,                              //服务开始时间 0：未使用过
"end_at": 1536743737,                                //预计服务结束时间 0：未使用过
"diary_start_at": 1536422400,                        //评估开始时间 0：未使用过
"diary_end_at": 1536681600,                          //评估开始时间 0：未使用过
"remark": "",                                        //用户备注
"evaluation_type": 0,                                //回复类型 0：文字 1：音频 默认为0
"evaluation_content": {                              //evaluation_type = 0 如果未评价 此字段未null
"content": "xxxxxxxxxxx",                        //文字内容
},
"evaluation_content": {                              //evaluation_type = 1 如果未评价 此字段未null
"url": "www.ssss.com",                           //音频url
"duration": 20,                                  //音频时长 秒
},
"evaluated_at": 0,                                   //医生评价时间 用来判断医生是否评价 0：未评价
"diaries": [],                                       //医生评价时的日记 如果未评价, 空数组
"created_at": 1536737371,                            //购买或赠送时间
"updated_at": 1536737371,                            //最后更新时间
"description": "您的周睡眠日记评估还未使用，点击去使用 >", //描述
"remind_description": "服务已取消",                   //顶部提示条文案
"user": {
"id": 2040,                                     //用户id
"nickname": "xxq",                              //昵称
"name": "张si"                                  //用户名字
},
"doctor": {
"id": 2,                                        //医生id
"name": "潘教授",                                //医生姓名
},
"package": {                                        //include=package
"id": 17,
"service_package_id": 1,
"servicePackage": {                             //include=package.servicePackage
"id": 1,
"service_id": 1,
"name": "",                                 //服务包名称
"introduction": "",
"service_length": 0,
"service_length_unit": 3,
"service": {                                //include=package.servicePackage.service
"id": 1,
"name": "睡眠日记跟踪服务"                 //服务名称
}
}
}
},
],
"meta": {
"pagination": {
"total": 6,
"count": 6,
"per_page": 15,
"current_page": 1,
"total_pages": 1,
"links": []
}
}
}
 */
data class DiaryEvaluationData(
        @SerializedName("id") val id: Int,
        @SerializedName("user_id") val userId: Int,
        @SerializedName("doctor_id") val doctorId: Int,
        @SerializedName("package_id") val packageId: Int,
        @SerializedName("traceable_id") val traceableId: Int,
        @SerializedName("traceable_type") val traceableType: String,
        @SerializedName("status") val status: Int,  //状态 0：待回复 1：已完成 2：已关闭 3：已取消 4：未使用
        @SerializedName("start_at") val startAt: Int,
        @SerializedName("end_at") val endAt: Int,
        @SerializedName("diary_start_at") val diaryStartAt: Int,
        @SerializedName("diary_end_at") val diaryEndAt: Int,
        @SerializedName("remark") val remark: String,
        @SerializedName("evaluation_type") val evaluationType: Int,
        @SerializedName("evaluation_content") val evaluationContent: Any,
        @SerializedName("evaluated_at") val evaluatedAt: Int,
        @SerializedName("diaries") val diaries: List<Any>,
        @SerializedName("created_at") val createdAt: Int,
        @SerializedName("updated_at") val updatedAt: Int,
        @SerializedName("description") val description: String,
        @SerializedName("remind_description") val remindDescription: String
) {
    companion object {
        const val STATUS_0_WAITING_RESPONSE = 0
        const val STATUS_1_FINISHED = 1
        const val STATUS_2_CLOSED = 2
        const val STATUS_3_CANCELED = 3
        const val STATUS_4_UNUSED = 4
    }

    fun getUpdateAtInMillis(): Long {
        return updatedAt * 1000L
    }

    fun getDiaryStartAtInMillis(): Long {
        return diaryStartAt * 1000L
    }

    fun getDiaryEndAtInMillis(): Long {
        return diaryEndAt * 1000L
    }
}