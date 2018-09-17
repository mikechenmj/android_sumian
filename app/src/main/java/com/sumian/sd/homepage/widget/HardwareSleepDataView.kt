package com.sumian.sd.homepage.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.blankj.utilcode.util.ToastUtils
import com.sumian.hw.base.HwBasePresenter
import com.sumian.hw.report.base.BaseResultResponse
import com.sumian.hw.report.bean.DailyMeta
import com.sumian.hw.report.bean.DailyReport
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.hw.report.ReportActivity
import kotlinx.android.synthetic.main.lay_no_sleep_data.view.*
import kotlinx.android.synthetic.main.view_hardware_sleep_data.view.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/9/13 14:30
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class HardwareSleepDataView(context: Context, attributeSet: AttributeSet? = null) : FrameLayout(context, attributeSet) {
    private var mOnRefreshClicked = false

    init {
        LayoutInflater.from(context).inflate(R.layout.view_hardware_sleep_data, this, true)
        ll_title_bar.setOnClickListener {
            ReportActivity.show()
        }
        btn_for_no_data.setOnClickListener {
            mOnRefreshClicked = true
            queryDailyReport()
        }
        btn_for_no_data.text = resources.getString(R.string.refresh)
        tv_no_sleep_data_desc.text = resources.getString(R.string.refresh_sleep_dairy_hint)
    }

    fun setDailyReport(dailyReport: DailyReport?) {
        val hasData = dailyReport != null && dailyReport.id != 0
        if (!hasData && mOnRefreshClicked) {
            ToastUtils.showShort(context.getString(R.string.is_the_latest_data_already))
            mOnRefreshClicked = false
            return
        }
        ll_no_sleep_record.visibility = if (hasData) GONE else View.VISIBLE
        ll_sleep_data_container.visibility = if (hasData) VISIBLE else View.GONE
        updateUI(dailyReport)
    }

    private fun updateUI(dailyReport: DailyReport?) {
        if (dailyReport == null) {
            return
        }
        tv_all_day_sleep_duration.text = formatTime(dailyReport.sleep_duration)
        tv_light_sleep_duration.text = formatTime(dailyReport.light_duration)
        tv_deep_sleep_duration.text = formatTime(dailyReport.deep_duration)
        progress_light_sleep.setProgress(dailyReport.light_duration_percent)
        progress_deep_sleep.setProgress(dailyReport.deep_duration_percent)
    }

    fun queryDailyReport() {
        val map = HashMap<String, Any>(0)
        map["date"] = System.currentTimeMillis() / 1000
        map["page_size"] = 1
        map["is_include"] = 1
        val call = AppManager.getHwV1HttpService().getTodaySleepReport(map)
        HwBasePresenter.mCalls.add(call)
        call.enqueue(object : com.sumian.hw.network.callback.BaseResponseCallback<BaseResultResponse<DailyReport, DailyMeta>>() {
            override fun onSuccess(response: BaseResultResponse<DailyReport, DailyMeta>) {
                val data = response.data
                val dailyReport = if (data != null && data.size > 0) data[0] else null
                setDailyReport(dailyReport)
            }

            override fun onFailure(code: Int, error: String) {
            }
        })
    }

    private fun formatTime(unixTime: Int): String? {
        return com.sumian.sd.utils.TimeUtil.getHourMinuteStringFromSecondInZh(unixTime)
    }
}