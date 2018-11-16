package com.sumian.sd.service.tel.bean

import android.os.Parcelable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.google.gson.annotations.SerializedName
import com.sumian.sd.R
import com.sumian.sd.app.App
import com.sumian.common.utils.TimeUtilV2
import kotlinx.android.parcel.Parcelize

/**
 * Created by sm
 *
 * on 2018/8/14
 *
 * desc:电话预约详情
 *
 */
@Suppress("DEPRECATION")
@Parcelize
data class TelBooking(var id: Int,
                      var booking_date_id: Int,
                      var user_id: Int, //用户id
                      var doctor_id: Int, //医生id
                      var admin_id: Int, //创建预约的管理员id
                      var type: Int,//预约类型 1：电话预约
                      var plan_start_at: Int, //计划开始时间 如果未使用，为0
                      var plan_end_at: Int,//计划结束时间 如果未使用，为0
                      var started_at: Int,//实际开始时间 如果未实际开始，为0
                      var ended_at: Int, //实际结束时间 如果未实际开始，为0
                      var status: Int,//状态 0:待确认，1：已确认 2：进行中 3：通话中 4：已完成 5：已关闭 6：已挂起 7：已取消 8：已结束 9：未使用
                      var consulting_question: String?,//咨询问题
                      var add: String,//补充说明
                      var package_id: Int,//服务包id
                      var traceable_id: Int,//电话预约机会获取来源的id 来源是购买或赠送
                      var traceable_type: String?,//获取来源 App\\Models\\GiftRecord 赠送 或 App\\Models\\Order 购买
                      var created_at: Int,//创建时间
                      var updated_at: Int,//最后更新时间
                      @SerializedName("package")
                      var p_package: TelBookingPackage  //服务包信息 include=package
) : Parcelable {

    companion object {

        const val UN_FINISHED_TYPE: Int = 0x00  //未完成的电话预约
        const val IS_FINISHED_TYPE: Int = 0x01    //已完成的电话预约
        const val STATUS_0_WAITING_CONFIRM = 0
        const val STATUS_1_CONFIRMED = 1
        const val STATUS_2_GOING = 2
        const val STATUS_3_ON_CALL = 3
        const val STATUS_4_COMPLETE = 4
        const val STATUS_5_CLOSED = 5
        const val STATUS_6_HANG_ON = 6
        const val STATUS_7_CANCELED = 7
        const val STATUS_8_FINISH = 8
        const val STATUS_9_UNUSED = 9
    }

    fun formatStatus(): CharSequence {
        return when (status) {
            STATUS_0_WAITING_CONFIRM -> {
                val formatStatus = "待确认"
                val spannableString = SpannableString(formatStatus)
                spannableString.setSpan(ForegroundColorSpan(App.getAppContext().resources.getColor(R.color.b3_color)), 0, formatStatus.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                return spannableString
            }
            STATUS_1_CONFIRMED, STATUS_2_GOING, STATUS_3_ON_CALL -> "已确认"
            STATUS_4_COMPLETE, STATUS_7_CANCELED -> "已完成"
            STATUS_5_CLOSED, STATUS_8_FINISH -> "已关闭"
            STATUS_6_HANG_ON -> "挂起中"
            //9 未使用,不显示
            else -> {
                ""
            }
        }

    }

    fun topMsg(): String {
        return when (status) {
            STATUS_0_WAITING_CONFIRM -> {
                "请耐心等待医生确认预约信息"
            }
            STATUS_1_CONFIRMED, STATUS_2_GOING, STATUS_3_ON_CALL -> {
                "预约已确认，请保持电话畅通"
            }
            else -> {
                ""
            }
        }
    }

    fun formatOrderContent(): String {
        return when (status) {
            STATUS_9_UNUSED -> "您的${p_package.servicePackage.formatPackageNameAndIntro()}还未使用，点击预约 >"
            STATUS_7_CANCELED -> {
                if (isNotUsed()) {
                    getCancelString()
                } else {
                    getOrderString()
                }
            }
            else -> getOrderString()
        }
    }

    private fun getOrderString() =
            "${formatOrderTime()}预约时长:  ${p_package.servicePackage.formatServiceLengthType()} \r\n咨询问题:  $consulting_question"

    private fun getCancelString() = "您的${p_package.servicePackage.formatPackageNameAndIntro()}已取消。"

    private fun formatOrderTime(): String {
        return if (plan_start_at <= 0) {
            "预约时间:  ${formatOrderCreateTimeYYYYMMDD()}\r\n"
        } else {
            "预约时间:  ${formatOrderPlanStartTimeYYYYMMDD()}\r\n"
        }
    }

    fun formatOrderTimeYYYYMMDDHHMM(): String {
        return if (plan_start_at <= 0) {
            formatOrderCreateTimeYYYYMMDDHHMM()
        } else {
            formatOrderPlanStartTimeYYYYMMDDHHMM()
        }
    }

    fun formatOrderCreateTime(): String {
        return TimeUtilV2.formatYYYYMMDDHHMM(updated_at)
    }

    private fun formatOrderCreateTimeYYYYMMDD(): String {
        return TimeUtilV2.formatYYYYMMDD(created_at)
    }

    private fun formatOrderCreateTimeYYYYMMDDHHMM(): String {
        return TimeUtilV2.formatYYYYMMDDHHMM(created_at)
    }

    private fun formatOrderPlanStartTimeYYYYMMDD(): String {
        return TimeUtilV2.formatYYYYMMDD(plan_start_at)
    }

    fun formatOrderPlanStartTimeYYYYMMDDHHMM(): String {
        return TimeUtilV2.formatYYYYMMDDHHMM(plan_start_at)
    }

    fun isNotUsed(): Boolean {
        return plan_start_at == 0
    }

    fun showTopTips(): Boolean {
        return when (status) {
            STATUS_0_WAITING_CONFIRM, STATUS_1_CONFIRMED, STATUS_2_GOING, STATUS_3_ON_CALL -> {
                return true
            }
            else -> {
                false
            }
        }
    }

    /**
     * 电话预约小包
     */
    @Parcelize
    data class TelBookingPackage(var id: Int,   // 服务包id
                                 var servicePackage: ServicePackage //服务包基础信息 include=package.servicePackage
    ) : Parcelable

    /**
     * 电话预约服务包信息
     */
    @Parcelize
    data class ServicePackage(var id: Int,
                              var service_id: Int,
                              var name: String,//服务包名
                              var introduction: String,//服务包介绍
                              var service_length: Int,//服务时长
                              var service_length_unit: Int, //服务时长类型 0：无 1：分钟 2：小时 3：天
                              var service: Service?
    ) : Parcelable {


        fun formatServiceLengthType(): String {
            return "$service_length" + when (service_length_unit) {
                2 -> {
                    "小时"
                }
                3 -> {
                    "天"
                }
                else -> {
                    "分钟"
                }
            }
        }

        fun formatPackageNameAndIntro(): String {
            return "【${service?.name}（${name}）】"
        }
    }

    @Parcelize
    data class Service(
            @SerializedName("id") val id: Int,
            @SerializedName("name") val name: String
    ) : Parcelable
}