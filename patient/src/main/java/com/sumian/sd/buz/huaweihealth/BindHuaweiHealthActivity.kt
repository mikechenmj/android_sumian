package com.sumian.sd.buz.huaweihealth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.preference.PreferenceManager
import com.sumian.common.base.BaseActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.log.SdLogManager
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.EventBusUtil
import com.sumian.sd.common.utils.TimeUtil
import kotlinx.android.synthetic.main.activity_bind_huawei_health.*
import kotlinx.android.synthetic.main.fragment_scan_device.ripple_view
import org.greenrobot.eventbus.Subscribe

class BindHuaweiHealthActivity : BaseActivity() {

    private var mIsHuaweiHealthInstalled: Boolean = false
    private var mMaxDate: Int = 0
    private lateinit var mSharedPreferences: SharedPreferences

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_bind_huawei_health
    }

    companion object {

        private const val HAD_SHOW_PERMISSION_EXPLAIN = "had_show_permission_explain"
        private const val MAX_DATE_DURATION = "max_date_duration"
        private const val LATEST_UPDATE_TIME = "latest_update_time"

        private const val ONE_DAY_MILLS = 24 * 60 * 60 * 1000L

        fun start(context: Context) {
            context.startActivity(Intent(context, BindHuaweiHealthActivity::class.java))
        }
    }

    override fun getPageName(): String {
        return StatConstants.page_bind_huawei_health
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.bind_huawei_health_title)
        EventBusUtil.register(this)
        mIsHuaweiHealthInstalled = HuaweiHealthUtil.isHuaweiHealthInstalled(this)
        SdLogManager.logHuaweiHealth("onCreate mIsHuaweiHealthInstalled: $mIsHuaweiHealthInstalled")
        SdLogManager.logHuaweiHealth("onCreate isHuaweiHealthConnectSuccess: ${HuaweiHealthUtil.isHuaweiHealthConnectSuccess}")

        ll_permission_explain.setOnClickListener {
            showPermissionExplain()
        }
        bt_bind.setOnClickListener {
            if (mIsHuaweiHealthInstalled) {
                if (HuaweiHealthUtil.isHuaweiHealthConnectSuccess) {
                    HuaweiHealthUtil.requestAuthorization(this) { code, message ->
                        Log.i("MCJ", "code: $code message: $code")
                        SdLogManager.logHuaweiHealth("requestAuthorization: code: $code")
                        if (code != 0) {
                            runOnUiThread {
                                if (!isFinishing) {
                                    showHuaweiHealthException()
                                }
                            }
                        }
                    }
                } else {
                    showHuaweiHealthException()
                }
            } else {
                showHuaweiHealthNoInstalled()
            }
        }
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        var hadShowPermissionExplain = mSharedPreferences.getBoolean(HAD_SHOW_PERMISSION_EXPLAIN, false)
        if (!hadShowPermissionExplain) {
            showPermissionExplain()
            mSharedPreferences.edit().putBoolean(HAD_SHOW_PERMISSION_EXPLAIN, true).commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusUtil.unregister(this)
    }

    @Subscribe
    fun onHuaweiHealthException(event: HuaweiHealthExceptionEvent) {
        HuaweiHealthUtil.isHuaweiHealthConnectSuccess = false
        SdLogManager.logHuaweiHealth("onHuaweiHealthException")
        Log.i("MCJ", "onHuaweiHealthException")
    }

    @Subscribe
    fun onHuaweiHealthConnectSuccess(event: HuaweiHealthConnectSuccessEvent) {
        HuaweiHealthUtil.isHuaweiHealthConnectSuccess = true
        SdLogManager.logHuaweiHealth("onHuaweiHealthConnectSuccess")
        Log.i("MCJ", "onHuaweiHealthConnectSuccess")
        getHuaweiHealthConfigInfo()
    }

    private fun getHuaweiHealthConfigInfo() {
        val call = AppManager.getSdHttpService().getHuaweiHealthConfigInfo()
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<HuaweiHealthConfigInfo>() {
            override fun onSuccess(response: HuaweiHealthConfigInfo?) {
                Log.i("MCJ", "getHuaweiHealthConfigInfo: $response")
                SdLogManager.logHuaweiHealth("getHuaweiHealthConfigInfo: $response")
                if (response == null) {
                    ToastHelper.show("未获取到配置信息")
                    return
                }
                refreshUpdateTime(mSharedPreferences.getLong(LATEST_UPDATE_TIME, 0))
                var localMaxDateDuration = mSharedPreferences.getInt(MAX_DATE_DURATION, 0)
                SdLogManager.logHuaweiHealth("localMaxDateDuration: $localMaxDateDuration")
                var startTime = 0L
                var endTime = 0L
                if (localMaxDateDuration != response.maxDateDuration) {
                    mSharedPreferences.edit().putInt(MAX_DATE_DURATION, response.maxDateDuration).commit()
                    startTime = System.currentTimeMillis() - response.maxDateDuration * ONE_DAY_MILLS
                    endTime = response.endDate
                } else {
                    startTime = response.startDate
                    endTime = response.endDate
                }
                mMaxDate = response.maxDateDuration
                queryHuaweiHealthData(TimeUtil.formatDate("yyyy-MM-dd", startTime), TimeUtil.formatDate("yyyy-MM-dd", endTime))
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastHelper.show("网络错误：${errorResponse.message}")
            }

            override fun onFinish() {
            }
        })
    }

    private fun updateHuaweiHealthData(data: HuaweiHealthData) {
        val call = AppManager.getSdHttpService().updateHuaweiHealthData(data)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<HuaweiHealthDataResponse>() {
            override fun onSuccess(response: HuaweiHealthDataResponse?) {
                Log.i("MCJ","updateHuaweiHealthData.onSuccess: $response")
                SdLogManager.logHuaweiHealth("updateHuaweiHealthData.onSuccess: $response")
                if (response == null) {
                    ToastHelper.show("未获取到更新日期")
                    return
                }
                updateHealthUi(response)
            }

            override fun onFinish() {
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                Log.i("MCJ","updateHuaweiHealthData.onFailure: $errorResponse")
                SdLogManager.logHuaweiHealth("updateHuaweiHealthData.onFailure: $errorResponse")
                ToastHelper.show("网络错误：${errorResponse.message}")
            }


        })
    }

    override fun onResume() {
        super.onResume()
        Log.i("MCJ", "isHuaweiHealthConnectSuccess: ${HuaweiHealthUtil.isHuaweiHealthConnectSuccess}")
        SdLogManager.logHuaweiHealth("onResume isHuaweiHealthConnectSuccess: ${HuaweiHealthUtil.isHuaweiHealthConnectSuccess}")
        SdLogManager.logHuaweiHealth("onResume mIsHuaweiHealthInstalled: $mIsHuaweiHealthInstalled")
        mIsHuaweiHealthInstalled = HuaweiHealthUtil.isHuaweiHealthInstalled(this)
        testHuaweiHealthService()
        if (HuaweiHealthUtil.isHuaweiHealthConnectSuccess) {
            getHuaweiHealthConfigInfo()
        }
        ripple_view.startAnimation()
    }

    override fun onPause() {
        super.onPause()
        ripple_view.stopAnimation()
    }

    private fun testHuaweiHealthService() {
        HuaweiHealthUtil.queryBirthday(this, { i, s -> })
    }

    private fun refreshUpdateTime(latestTime: Long) {
        if (latestTime <= 0) {
            tv_update_time.text = getString(R.string.bind_huawei_health_update_time_header)
        } else {
            tv_update_time.text = getString(R.string.bind_huawei_health_update_time_header) + TimeUtil.formatDate("yyyy.MM.dd  HH:mm", latestTime)
        }
    }

    private fun showPermissionExplain() {
        SumianDialog(this)
                .setTitleText(getString(R.string.bind_huawei_health_tip_title))
                .setMessageText(getString(R.string.bind_huawei_health_tip_content))
                .setLeftBtn(R.string.confirm, null)
                .setCanceledOnTouchOutsideWrap(false)
                .show()
    }

    private fun showHuaweiHealthNoInstalled() {
        SumianDialog(this)
                .setTitleText(getString(R.string.bind_huawei_health_dialog_title))
                .setMessageText(getString(R.string.bind_huawei_health_dialog_content))
                .setLeftBtn(R.string.confirm, null)
                .setCanceledOnTouchOutsideWrap(false)
                .show()
    }

    private fun showHuaweiHealthException() {
        SumianDialog(this)
                .setTitleText(getString(R.string.bind_huawei_health_exception_dialog_title))
                .setMessageText(getString(R.string.bind_huawei_health_exception_dialog_content))
                .setCanceledOnTouchOutsideWrap(false)
                .setLeftBtn(R.string.confirm, null)
                .show()
    }

    private fun queryHuaweiHealthData(start: String, end: String) {
        SdLogManager.logHuaweiHealth("queryHuaweiHealthData: $start $end")
        Log.i("MCJ","queryHuaweiHealthData: $start $end")
        if (mIsHuaweiHealthInstalled) {
            HuaweiHealthUtil.queryHuaweiHealthData(this, start, end) { code, data ->
                updateHuaweiHealthData(data)
            }
        }
    }

    private fun updateHealthUi(response: HuaweiHealthDataResponse) {
        var start = TimeUtil.formatDate("yyyy-MM-dd", System.currentTimeMillis() - ONE_DAY_MILLS * mMaxDate)
        var end = TimeUtil.formatDate("yyyy-MM-dd", System.currentTimeMillis())
        if (mIsHuaweiHealthInstalled) {
            HuaweiHealthUtil.queryHuaweiHealthData(this, start, end) { code, data ->
                SdLogManager.logHuaweiHealth("updateHealthUi: " +
                        "data.stepSum.size: ${data.stepSum.size}" +
                        "data.distanceSum.size: ${data.distanceSum.size}" +
                        "data.caloriesSum.size: ${data.caloriesSum.size}" +
                        "data.coreSleeps.size: ${data.coreSleeps.size}" +
                        "data.birthday: ${data.birthday}" +
                        "data.gender: ${data.gender}" +
                        "data.height: ${data.height}" +
                        "data.weight: ${data.weight}")
                runOnUiThread {
                    iv_step_sum_state.updateResult(data.stepSum.size > 0)
                    iv_distance_state.updateResult(data.distanceSum.size > 0)
                    iv_calories_state.updateResult(data.caloriesSum.size > 0)
                    iv_sleep_state.updateResult(data.coreSleeps.size > 0)
                    iv_personal_info_state.updateResult(data.let { it.birthday.isNotEmpty() && it.gender != 3 })
                    iv_personal_sign_state.updateResult(data.let { it.height != 0f && it.weight != 0f })
                    if (data.coreSleeps.size > 0) {
                        mSharedPreferences.edit().putLong(LATEST_UPDATE_TIME, response.latestTime).commit()
                        refreshUpdateTime(response.latestTime)
                    }
                }
            }
        } else {
            runOnUiThread { refreshUpdateTime(response.latestTime) }
        }
    }

    private fun ImageView.updateResult(result: Boolean) {
        if (result) setImageResource(R.drawable.ic_tick) else setImageResource(R.drawable.ic_cross)
    }
}