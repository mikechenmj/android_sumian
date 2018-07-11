package com.sumian.sleepdoctor.doctor.bean

/**
 *
 *Created by sm
 * on 2018/6/2 21:48
 * desc:用户创建的支付订单
 **/
data class PayOrder(var amount: Double = 0.00,//支付总金额（单位：分）
                    var channel: String = "wx",//付款平台（wx:微信app，alipay：支付宝app，wx_pub:微信公众号）//允许值: {"wx", "alipay", "wx_pub"}
                    var currency: String = "cny",//货币代码，目前仅支持人民币 cny 允许值: {"cny"}
                    var subject: String,//商品标题
                    var body: String,
                    var package_id: Int,//套餐 id
                    var quantity: Int = 0//购买数量
)