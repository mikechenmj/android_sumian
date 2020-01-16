package com.sumian.sd.buz.huawei

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.sumian.common.base.BaseActivity
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.R
import com.sumian.sd.buz.stat.StatConstants
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
//            ToastUtils.showShort("绑定失败")
            SumianDialog(this)
                    .setTitleText(getString(R.string.bind_huawei_health_dialog_title))
                    .setMessageText(getString(R.string.bind_huawei_health_dialog_content))
                    .setLeftBtn(R.string.confirm, null)
                    .show()
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