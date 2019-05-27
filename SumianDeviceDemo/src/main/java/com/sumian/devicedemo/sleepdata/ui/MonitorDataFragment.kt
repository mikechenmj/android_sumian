package com.sumian.devicedemo.sleepdata

import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.sumian.device.net.NetworkManager
import com.sumian.device.util.JsonUtil
import com.sumian.devicedemo.R
import com.sumian.devicedemo.base.BaseFragment
import com.sumian.devicedemo.sleepdata.data.BaseResultResponse
import com.sumian.devicedemo.sleepdata.data.DailyMeta
import com.sumian.devicedemo.sleepdata.data.DailyReport
import com.sumian.devicedemo.sleepdata.event.UpdateMonitorDataEvent
import com.sumian.devicedemo.sleepdata.event.UploadSleepDataFinishedEvent
import com.sumian.devicedemo.util.EventBusUtil
import kotlinx.android.synthetic.main.fragment_monitor_data.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/11 19:57
 * desc   :
 * version: 1.0
 */
class MonitorDataFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private var mSelectedTime = System.currentTimeMillis()

    override fun getLayoutId(): Int {
        return R.layout.fragment_monitor_data
    }

    companion object {
        private val KEY_TIME = "key_time"

        fun newInstance(time: Long): MonitorDataFragment {
            val bundle = Bundle()
            bundle.putLong(KEY_TIME, time)
            val fragment = MonitorDataFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initData() {
        super.initData()
        mSelectedTime = arguments!!.getLong(KEY_TIME)
        updateCurrentTimeData()
    }

    override fun onStart() {
        super.onStart()
        EventBusUtil.register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBusUtil.unregister(this)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onUploadSleepDataFinishedEvent(event: UploadSleepDataFinishedEvent) {
        LogUtils.d(event)
        if (event.success) {
            updateCurrentTimeData() // refresh
        }
        EventBusUtil.removeStickyEvent(event)
    }

    @Suppress("unused", "UNUSED_PARAMETER")
    @Subscribe
    fun onUploadMonitorDataEvent(event: UpdateMonitorDataEvent) {
        updateCurrentTimeData()
    }

    override fun onRefresh() {
        updateCurrentTimeData()
    }

    fun updateCurrentTimeData() {
        queryDiary(mSelectedTime)
    }

    private fun queryDiary(time: Long) {
        val call = NetworkManager.getApi().getSleepReport((time / 1000).toInt(), 1, 1)
        addCall(call)
        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                refresh?.isRefreshing = false
                ToastUtils.showShort(t.message)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                refresh?.isRefreshing = false
                if (response.isSuccessful) {
                    val body = response.body() ?: return
                    LogUtils.d(body)
                    val resp = JsonUtil.fromJson<BaseResultResponse<DailyReport, DailyMeta>>(
                            body,
                            object : TypeToken<BaseResultResponse<DailyReport, DailyMeta>>() {}.type
                    )
                    updateDailyReport(resp?.data?.get(0))
                }
            }
        })
    }

    fun getSelectedTime(): Long {
        return mSelectedTime
    }

    fun updateDailyReport(dailyReport: DailyReport?) {
        if (refresh == null) {
            return
        }
        refresh.isRefreshing = false
        refresh.setOnRefreshListener(this)
        if (dailyReport == null) {
            return
        }
        day_sleep_histogram_view.setData(dailyReport)
        report_sleep_duration_view.setSleepTodayDuration(dailyReport.sleep_duration)
        report_sleep_duration_view.setLightSleepData(
                dailyReport.light_duration,
                dailyReport.light_duration_percent
        )
        report_sleep_duration_view.setDeepSleepData(
                dailyReport.deep_duration,
                dailyReport.deep_duration_percent
        )
    }

}