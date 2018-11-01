@file:Suppress("MemberVisibilityCanBePrivate")

package com.sumian.sd.diary.sleeprecord.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.diary.sleeprecord.FillSleepRecordActivity
import com.sumian.sd.diary.sleeprecord.SleepRecordActivity
import com.sumian.sd.diary.sleeprecord.bean.SleepRecord
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.utils.TimeUtil
import kotlinx.android.synthetic.main.lay_no_sleep_data.view.*
import kotlinx.android.synthetic.main.view_simple_sleep_record_view.view.*


/**
 * <pre>
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/5/31 20:02
 * desc   :
 * version: 1.0
</pre> *
 */
class SimpleSleepRecordView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    init {
        init(context)
    }

    private fun init(context: Context) {
        View.inflate(context, R.layout.view_simple_sleep_record_view, this)
        setOnLabelClickListener(OnClickListener { SleepRecordActivity.launch(context) })
        setOnClickRightArrowListener(OnClickListener { SleepRecordActivity.launch(context) })
        setOnClickFillSleepRecordBtnListener(OnClickListener { FillSleepRecordActivity.launch(context, System.currentTimeMillis()) })
        ll_no_sleep_record.setOnClickListener { FillSleepRecordActivity.launch(context, System.currentTimeMillis()) }
    }

    fun setSleepRecord(sleepRecord: SleepRecord?) {
        val hasRecord = sleepRecord != null
        ll_sleep_record.visibility = if (hasRecord) View.VISIBLE else View.GONE
        ll_no_sleep_record.visibility = if (hasRecord) View.GONE else View.VISIBLE
        title_view_sleep_record.visibility = View.VISIBLE
        val showRefill = hasRecord && TextUtils.isEmpty(sleepRecord!!.doctor_evaluation)
        title_view_sleep_record.setTvMenuVisibility(if (showRefill) View.VISIBLE else View.GONE)
        if (hasRecord) {
            showSleepRecord(sleepRecord!!)
        }
    }

    private fun showSleepRecord(sleepRecord: SleepRecord) {
        tv_sleep_duration.text = TimeUtil.getHourMinuteStringFromSecondInZh(sleepRecord.sleep_duration)
        tv_fall_asleep_duration.text = TimeUtil.getHourMinuteStringFromSecondInZh(sleepRecord.fall_asleep_duration)
        progress_view_sleep.setProgress(sleepRecord.sleep_efficiency)
    }

    fun setOnClickRightArrowListener(listener: View.OnClickListener) {
        title_view_sleep_record.setOnRightArrowClickListener(listener)
    }

    fun setOnLabelClickListener(listener: View.OnClickListener) {
        title_view_sleep_record.setOnClickListener(listener)
    }

    fun setOnClickFillSleepRecordBtnListener(listener: View.OnClickListener) {
        btn_for_no_data.setOnClickListener(listener)
        ll_no_sleep_record.setOnClickListener(listener)
    }

    fun querySleepRecord() {
        val call = AppManager.getSdHttpService().getSleepDiaryDetail((System.currentTimeMillis() / 1000L).toInt())
        call.enqueue(object : BaseSdResponseCallback<SleepRecord>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                setSleepRecord(null)
            }
            override fun onSuccess(response: SleepRecord?) {
                setSleepRecord(response)
            }
        })
    }
}
