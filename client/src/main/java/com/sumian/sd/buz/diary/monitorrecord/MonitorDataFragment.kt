package com.sumian.sd.buz.diary.monitorrecord

import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseFragment
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.diary.event.UpdateMonitorDataEvent
import com.sumian.sd.buz.job.UploadSleepDataFinishedEvent
import com.sumian.sd.buz.report.base.BaseResultResponse
import com.sumian.sd.buz.report.bean.DailyMeta
import com.sumian.sd.buz.report.bean.DailyReport
import com.sumian.sd.common.log.LogManager
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.EventBusUtil
import kotlinx.android.synthetic.main.fragment_monitor_data.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

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
        LogManager.appendUserOperationLog("'检测数据' 下拉刷新")
        updateCurrentTimeData()
    }

    fun updateCurrentTimeData() {
        LogManager.appendUserOperationLog("'检测数据' 刷新")
        queryDiary(mSelectedTime)
    }

    override fun showLoading() {
        //super.showLoading()
    }

    override fun dismissLoading() {
        //super.dismissLoading()
    }

    private fun queryDiary(time: Long) {
        showLoading()
        val call = AppManager.getSdHttpService().getSleepReport((time / 1000).toInt(), 1, 1)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<BaseResultResponse<DailyReport, DailyMeta>>() {
            override fun onSuccess(response: BaseResultResponse<DailyReport, DailyMeta>?) {
                val data = response?.data
                if (data != null && data.size > 0) updateDailyReport(data[0])
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                refresh?.isRefreshing = false
            }
        })
    }

    fun getSelectedTime(): Long {
        return mSelectedTime
    }

    fun updateDailyReport(dailyReport: DailyReport) {
        if (refresh == null) {
            return
        }
        refresh.isRefreshing = false
        refresh.setOnRefreshListener(this)
        day_sleep_histogram_view.setData(dailyReport)
        report_sleep_duration_view.setSleepTodayDuration(dailyReport.sleep_duration)
        report_sleep_duration_view.setLightSleepData(dailyReport.light_duration, dailyReport.light_duration_percent)
        report_sleep_duration_view.setDeepSleepData(dailyReport.deep_duration, dailyReport.deep_duration_percent)
    }

}