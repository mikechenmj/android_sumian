package com.sumian.sd.buz.doctor.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 *
 *Created by sm
 * on 2018/5/30 10:44
 * desc:  医生服务功能的服务包
 **/
@Parcelize
data class DoctorServicePackage(var id: Int,//服务包模板id
                                var service_id: Int,//服务id
                                var name: String,//服务包名称
                                var introduction: String, //服务包简介
                                var default_price: Double, //默认价格
                                var service_length: Int,//服务时长
                                var service_length_unit: Int,//服务时长类型 0：无 1：分钟 2：小时 3：天
                                var enable: Int,//是否开启
                                var sold_by_doctor: Int,//是否通过医生售卖
                                var created_at: Int,//创建时间
                                var updated_at: Int,//最后更新时间
                                var packages: List<ServicePackage>
) : Parcelable, Serializable {

    /**
     * 医生服务包功能下属的各个级别的服务包 e.y.  比如通过不同价格划分了很多小包
     */
    @Parcelize
    data class ServicePackage(var id: Int,//医生的服务包
                              var unit_price: Double,//单价
                              var doctor_id: Int,//医生id
                              var service_package_id: Int,//服务包模板id
                              var enable: Int,//开启 1：开启 已过滤enable=0情况
                              var created_at: Int,//创建时间
                              var updated_at: Int)//最后更新时间
        : Parcelable, Serializable
}