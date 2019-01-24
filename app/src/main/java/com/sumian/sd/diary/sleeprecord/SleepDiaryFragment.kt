package com.sumian.sd.diary.sleeprecord

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.TimeUtilV2
import com.sumian.common.widget.refresh.SumianSwipeRefreshLayout
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBaseFragment
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.diary.fillsleepdiary.FillSleepDiaryActivity
import com.sumian.sd.diary.sleeprecord.bean.ShareInfo
import com.sumian.sd.diary.sleeprecord.bean.SleepRecord
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.widget.dialog.SumianAlertDialogV2
import kotlinx.android.synthetic.main.fragment_sleep_diary.*

class SleepDiaryFragment : SdBaseFragment<SdBasePresenter<*>>() {

    var selectedTime = System.currentTimeMillis()
    private var mRefreshLayout: SumianSwipeRefreshLayout? = null

    private val isRefillable: Boolean
        get() = TimeUtilV2.getDayDistance(System.currentTimeMillis(), selectedTime) < 3

    override fun getLayoutId(): Int {
        return R.layout.fragment_sleep_diary
    }

    override fun initWidget(root: View) {
        super.initWidget(root)
        sleep_record?.setOnClickRefillSleepRecordListener(View.OnClickListener {
            if (isRefillable) {
                launchFillSleepRecordActivity(selectedTime)
            } else {
                showRefillNotEnableDialog()
            }
        })
        sleep_record?.setOnClickFillSleepRecordBtnListener(View.OnClickListener {
            if (isRefillable) {
                launchFillSleepRecordActivity(selectedTime)
            }
        })
        mRefreshLayout = root.findViewById(R.id.refresh_layout)
        mRefreshLayout!!.setOnRefreshListener { queryAndShowSleepReportAtTime(selectedTime) }
    }

    private fun showRefillNotEnableDialog() {
        SumianAlertDialogV2(activity!!)
                .setMessageText(R.string.only_last_3_days_can_refill_sleep_diary)
                .setTopIcon(R.mipmap.ic_msg_icon_abnormal)
                .setOnBtnClickListener(R.string.hao_de, null)
                .show()
    }

    override fun initData() {
        super.initData()
        changeSelectTime(arguments!!.getLong(KEY_TIME))
    }

    private fun launchFillSleepRecordActivity(time: Long) {
        FillSleepDiaryActivity.startForResult(this, time, REQUEST_CODE_FILL_SLEEP_RECORD)
    }

    private fun changeSelectTime(time: Long) {
        selectedTime = time
        queryAndShowSleepReportAtTime(time)
    }

    private fun queryAndShowSleepReportAtTime(time: Long) {
        sleep_record.setTime(time)
        val call = AppManager.getSdHttpService().getSleepDiaryDetail((time / 1000L).toInt())
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<SleepRecord>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                updateSleepRecordView(null)
            }

            override fun onSuccess(response: SleepRecord?) {
                updateSleepRecordView(response)
            }

            override fun onFinish() {
                super.onFinish()
                mRefreshLayout!!.hideRefreshAnim()
            }
        })
    }

    private fun updateSleepRecordView(response: SleepRecord?) {
        sleep_record.setSleepRecord(response)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_FILL_SLEEP_RECORD) {
            if (resultCode == Activity.RESULT_OK) {
                val sleepRecord = FillSleepDiaryActivity.getResponseData(data!!) ?: return
                updateSleepRecordView(sleepRecord)
                showShareDialog(sleepRecord.sharedInfo ?: return)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showShareDialog(shareInfo: ShareInfo) {
        ShareSleepDiaryDialogActivity.start(shareInfo)
    }

    companion object {

        private val KEY_TIME = "key_time"
        val REQUEST_CODE_FILL_SLEEP_RECORD = 1

        fun newInstance(time: Long): SleepDiaryFragment {
            val bundle = Bundle()
            bundle.putLong(KEY_TIME, time)
            val sleepDiaryFragment = SleepDiaryFragment()
            sleepDiaryFragment.arguments = bundle
            return sleepDiaryFragment
        }
    }

}
