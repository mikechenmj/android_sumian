package com.sumian.sd.main

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sumian.sd.R
import com.umeng.commonsdk.internal.utils.InfoPreferenceAgent
import kotlinx.android.synthetic.main.dailog_homepage_user_guide.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/27 9:20
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class HomepageUserGuidDialog(context: Context, onClickListener: View.OnClickListener) : Dialog(context, R.style.FullScreenDialog) {
    init {
        val inflate = LayoutInflater.from(context).inflate(R.layout.dailog_homepage_user_guide, null)
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        inflate.layoutParams = layoutParams
        setContentView(inflate)
        iv_switch.setOnClickListener(onClickListener)
        inflate.setOnClickListener { dismiss() }
    }
}