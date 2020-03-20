package com.sumian.sd.buz.huaweihealth

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import com.google.gson.Gson
import com.sumian.common.base.BaseActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.R
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.log.SdLogManager
import kotlinx.android.synthetic.main.activity_bind_huawei_health.*
import kotlinx.android.synthetic.main.fragment_scan_device.ripple_view

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/13 22:23
 * desc   :
 * version: 1.0
 */
class BindHuaweiHealthActivity : BaseActivity() {
    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_bind_huawei_health
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, BindHuaweiHealthActivity::class.java))
        }
    }

    override fun getPageName(): String {
        return StatConstants.page_bind_huawei_health
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.bind_huawei_health_title)
        var spannableString = SpannableString(getString(R.string.bind_huawei_health_tip_content))
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#FF475266")), 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        tv_bind_huawei_health_tip.text = spannableString
        bt_bind.setOnClickListener {
            if (HuaweiHealthUtil.isHuaweiHealthInstalled(this)) {
                HuaweiHealthUtil.requestAuthorization(this) { code, message ->
                    if (code == 0) {
                        HuaweiHealthUtil.queryHuaweiHealthData(this, "2019-11-11", "2020-3-19") { code, data ->
                            if (code == -1 || data == null) {
                                SdLogManager.logHuaweiHealth("绑定失败，请确保已完整授权。")
                                ToastHelper.show("绑定失败，请确保已完整授权。")
                            } else {
                                Log.i("MCJ", "Gson().toJson(it): ${Gson().toJson(data)}")
                            }
                        }
                    }
                }
            } else {
                SumianDialog(this)
                        .setTitleText(getString(R.string.bind_huawei_health_dialog_title))
                        .setMessageText(getString(R.string.bind_huawei_health_dialog_content))
                        .setLeftBtn(R.string.confirm, null)
                        .show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ripple_view.startAnimation()
    }

    override fun onPause() {
        super.onPause()
        ripple_view.stopAnimation()
    }
}