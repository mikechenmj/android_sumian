package com.sumian.sd.advisory.bean

import android.os.Parcelable
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.doctor.bean.Doctor
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 *
 *Created by sm
 * on 2018/6/4 14:51
 * desc:  咨询详情
 **/
@Parcelize
data class Advisory(var id: Int, //咨询 ID
                    var status: Int,//咨询状态 0: 待回复 1：已回复 2：已结束 3：已关闭，4：已取消，5：待提问
                    var package_id: Int,//服务包 id
                    var order_id: Int,//订单 id
                    var user_id: Int,//用户 id
                    var doctor_id: Int,//提供咨询服务的医生 id
                    var start_at: Int,//开始提问时间，unix 时间戳
                    var end_at: Int,//结束提问时间，unix 时间戳
                    var created_at: Int,//咨询创建时间,unix 时间戳
                    var updated_at: Int,//咨询更新时间,unix 时间戳
                    var description: String,//描述
                    var service_id: Int,//服务 ID
                    var remind_description: String,//顶部提示条文案
                    var last_count: Int,//剩余次数
                    var records: ArrayList<Record>?,//提问或回复记录
                    var user: UserInfo?,//用户信息
                    var doctor: Doctor?//医生信息
) : Parcelable, Serializable {
    companion object {

        const val UNUSED_TYPE: Int = 0x00  //未使用的咨询
        const val USED_TYPE: Int = 0x01    //已使用过的咨询
    }
}
