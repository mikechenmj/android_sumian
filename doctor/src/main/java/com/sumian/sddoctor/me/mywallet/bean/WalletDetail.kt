package com.sumian.sddoctor.me.mywallet.bean

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/18 16:05
 * desc   :
 * version: 1.0
 */
data class WalletDetail(
        val id: Int,
        val doctor_id: Int,
        val sn: String,
        val type: Int, //0：收入；1：支出
        val amount: Long,
        val balance: Long,
        val content: String,
        val created_at: Int,
        val updated_at: Int
) {
    fun getCreateInMillis(): Long {
        return created_at * 1000L
    }

    fun getTypeString(): String {
        return if (type == 0) "收入" else "支出"
    }

    fun getSignedAmount(): Long {
        return amount * (if (type == 1) -1 else 1)
    }
}