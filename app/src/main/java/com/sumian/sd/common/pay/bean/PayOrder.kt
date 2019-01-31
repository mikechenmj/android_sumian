package com.sumian.sd.common.pay.bean

import com.google.gson.annotations.SerializedName

/**
 *
 *Created by sm
 * on 2018/6/2 21:48
 * desc:用户创建的支付订单
 **/
data class PayOrder(
        @SerializedName("discount_code")
        val discountCode: String?,//优惠码
        @SerializedName("reseller_id")
        val resellerId: Int?,//分销 id
        val mobile: String?,//手机号
        val captcha: String?,//验证码
        val amount: Double = 0.00,//支付总金额（单位：分）
        val channel: String = "wx",//付款平台（wx:微信app，alipay：支付宝app，wx_pub:微信公众号）//允许值: {"wx", "alipay", "wx_pub"}
        val currency: String = "cny",//货币代码，目前仅支持人民币 cny 允许值: {"cny"}
        val subject: String,//商品标题
        val body: String,
        @SerializedName("open_id")
        val openId: String?,//open_id 微信公众号购买为必填
        @SerializedName("package_id")
        val packageId: Int,//套餐 id
        val quantity: Int = 0//购买数量
)