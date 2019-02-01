package com.sumian.sddoctor.me.mywallet.bean

import com.sumian.sddoctor.network.response.Meta

data class WalletDetailResponse(
        val data: List<WalletDetail>,
        val meta: Meta
)