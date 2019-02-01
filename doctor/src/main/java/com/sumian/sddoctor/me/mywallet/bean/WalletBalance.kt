package com.sumian.sddoctor.me.mywallet.bean

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/18 15:31
 * desc   :
 * version: 1.0
 */

data class WalletBalance(
        val balance: Long,
        val pending_income: Long,
        val total_income: Long,
        val total_cash: Long
)