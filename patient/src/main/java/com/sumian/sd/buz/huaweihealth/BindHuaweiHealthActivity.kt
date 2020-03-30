package com.sumian.sd.buz.huaweihealth

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.ImageView
import com.sumian.common.base.BaseActivity
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.R
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.utils.TimeUtil
import kotlinx.android.synthetic.main.activity_bind_huawei_health.*
import kotlinx.android.synthetic.main.fragment_scan_device.ripple_view

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
        ll_permission_explain.setOnClickListener {
            showPermissionExplain()
        }
        bt_bind.setOnClickListener {
            if (HuaweiHealthUtil.isHuaweiHealthInstalled(this)) {
                HuaweiHealthUtil.requestAuthorization(this) { _, _ -> }
            } else {
                showHuaweiHealthNoInstalled()
            }
        }
        showPermissionExplain()
    }

    override fun onResume() {
        super.onResume()
        ripple_view.startAnimation()
        refreshUpdateTime()
        queryHuaweiHealthData()
    }

    override fun onPause() {
        super.onPause()
        ripple_view.stopAnimation()
    }

    private fun refreshUpdateTime() {
        tv_update_time.text = getString(R.string.bind_huawei_health_update_time_header) + TimeUtil.formatDate("yyyy.MM.dd  HH:mm", System.currentTimeMillis())
    }

    private fun showPermissionExplain() {
        SumianDialog(this)
                .setTitleText(getString(R.string.bind_huawei_health_tip_title))
                .setMessageText(getString(R.string.bind_huawei_health_tip_content))
                .setLeftBtn(R.string.confirm, null)
                .show()
    }

    private fun showHuaweiHealthNoInstalled() {
        SumianDialog(this)
                .setTitleText(getString(R.string.bind_huawei_health_dialog_title))
                .setMessageText(getString(R.string.bind_huawei_health_dialog_content))
                .setLeftBtn(R.string.confirm, null)
                .show()
    }

    private fun queryHuaweiHealthData(start: String = "2019-11-11", end: String = "2020-3-19") {
        HuaweiHealthUtil.queryHuaweiHealthData(this, start, end) { code, data ->
            if (data == null) {
                return@queryHuaweiHealthData
            }
            updateHealthDataTable(data)
//            Log.i("MCJ", "Gson().toJson(it): ${Gson().toJson(data)}")
        }
    }

    private fun updateHealthDataTable(data: HuaweiHealthData) {
        runOnUiThread {
            iv_step_sum_state.updateResult(data.stepSum.size > 0)
            iv_distance_state.updateResult(data.distanceSum.size > 0)
            iv_calories_state.updateResult(data.caloriesSum.size > 0)
            iv_sleep_state.updateResult(data.coreSleeps.size > 0)
            iv_personal_info_state.updateResult(data.let { it.birthday.isNotEmpty() && it.gender.isNotEmpty() })
            iv_personal_sign_state.updateResult(data.let { it.height.isNotEmpty() && it.weight.isNotEmpty() })
        }
    }

    fun ImageView.updateResult(result: Boolean) {
        if (result) setImageResource(R.drawable.ic_tick) else setImageResource(R.drawable.ic_cross)
    }
}