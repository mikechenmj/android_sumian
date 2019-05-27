package com.sumian.devicedemo.sleepdata.ui

import android.annotation.SuppressLint
import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.JsonObject
import com.sumian.device.net.NetworkManager
import com.sumian.device.util.JsonUtil
import com.sumian.devicedemo.R
import com.sumian.devicedemo.base.BaseFragment
import com.sumian.devicedemo.sleepdata.data.SleepDurationReport
import com.sumian.devicedemo.sleepdata.data.WeeklyReportResponse
import com.sumian.devicedemo.sleepdata.util.TimeUtilV2
import kotlinx.android.synthetic.main.fragment_weekly_report_fragment_v2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeeklyReportFragmentV2 : BaseFragment() {
    private var mSelectedTime = TimeUtilV2.getWeekStartDayTime(System.currentTimeMillis())

    override fun getLayoutId(): Int {
        return R.layout.fragment_weekly_report_fragment_v2
    }

    companion object {

        private const val KEY_SCROLL_TIME = "scroll_time"

        fun newInstance(): WeeklyReportFragmentV2 {
            return WeeklyReportFragmentV2()
        }

        fun newInstance(time: Long): WeeklyReportFragmentV2 {
            val bundle = Bundle()
            bundle.putLong(KEY_SCROLL_TIME, time)
            val weeklyReportFragment = WeeklyReportFragmentV2()
            weeklyReportFragment.arguments = bundle
            return weeklyReportFragment
        }
    }

    override fun initWidget() {
        super.initWidget()
        refresh_layout.setOnRefreshListener { queryData(mSelectedTime) }
    }


    override fun initData() {
        super.initData()
        val initTime = getInitTime()
        updateCurrentTime(initTime)
        queryData(initTime)
    }

    private fun queryData(time: Long) {
        val call = NetworkManager.getApi().getWeeksSleepReportV2((time / 1000).toInt())
        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                ToastUtils.showShort(t.message)
                refresh_layout.isRefreshing = false
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                refresh_layout.isRefreshing = false
                if (response.isSuccessful) {
                    val wrr = JsonUtil.fromJson<WeeklyReportResponse>(
                            response.body().toString(),
                            WeeklyReportResponse::class.java
                    )
                    val data: SleepDurationReport =
                            if ((wrr == null) || (wrr.list.isEmpty())) {
                                createEmptyData(time)
                            } else {
                                wrr.list[0]
                            }
                    weekly_report_view.setData(data)
                    updateCurrentTime(time)
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateCurrentTime(time: Long) {
        mSelectedTime = TimeUtilV2.getWeekStartDayTime(time)
    }

    private fun createEmptyData(time: Long): SleepDurationReport {
        return SleepDurationReport.createFromTime(time)
    }

    private fun getInitTime(): Long {
        return arguments?.getLong(KEY_SCROLL_TIME) ?: System.currentTimeMillis()
    }

    fun getSelectedTime(): Long {
        return mSelectedTime
    }
}
