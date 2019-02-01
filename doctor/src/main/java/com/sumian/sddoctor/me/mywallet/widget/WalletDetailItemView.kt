package com.sumian.sddoctor.me.mywallet.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.sumian.sddoctor.R
import kotlinx.android.synthetic.main.view_wallet_detail_item.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/1/25 14:24
 * desc   :
 * version: 1.0
 */
class WalletDetailItemView(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_wallet_detail_item, this, true)
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.WalletDetailItemView)
        tv_start.text = typedArray.getString(R.styleable.WalletDetailItemView_tv_start)
        tv_end.text = typedArray.getString(R.styleable.WalletDetailItemView_tv_end)
        typedArray.recycle()
    }
}