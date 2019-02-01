package com.sumian.sddoctor.util

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/9/29 14:21
 * desc   :
 * version: 1.0
 */
class PriceUtil {
    companion object {
        fun formatPrice(price: Int): String {
            return String.format("%d.%02d", price / 100, price % 100)
        }
    }
}