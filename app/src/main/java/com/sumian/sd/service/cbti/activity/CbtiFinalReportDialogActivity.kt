package com.sumian.sd.service.cbti.activity

import android.content.DialogInterface
import android.content.Intent
import android.text.format.DateUtils
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.sumian.common.base.BaseDialogPresenterActivity
import com.sumian.common.mvp.IPresenter
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.h5.SimpleWebActivity
import com.sumian.sd.homepage.bean.FinalReport
import com.sumian.sd.homepage.bean.GetCbtiChaptersResponse
import com.sumian.sd.network.callback.BaseSdResponseCallback
import java.util.HashMap

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/1/11 17:18
 * desc   :
 * version: 1.0
 */
class CbtiFinalReportDialogActivity : BaseDialogPresenterActivity<IPresenter>() {

    companion object {
        private const val KEY_FINAL_REPORT = "KEY_FINAL_REPORT"
        private const val SP_KEY_FIRST_SHOW_TIME = "SP_KEY_FIRST_SHOW_TIME"
        private const val SP_KEY_LAST_SHOW_TIME = "SP_KEY_LAST_SHOW_TIME"
        private const val SP_KEY_FIRST_SHOW_GAP_DURATION = DateUtils.DAY_IN_MILLIS * 8
        private const val SP_KEY_ANOTHER_SHOW_GAP_DURATION = DateUtils.DAY_IN_MILLIS * 3
        private const val SP_KEY_VALID_SHOW_DURATION = DateUtils.DAY_IN_MILLIS * 45
        private fun getSp() = SPUtils.getInstance(CbtiFinalReportDialogActivity::javaClass.name)

        fun showFinalReportDialogIfNeed() {
            AppManager.getSdHttpService().getCbtiChapters(null)
                    .enqueue(object : BaseSdResponseCallback<GetCbtiChaptersResponse>() {
                        override fun onSuccess(response: GetCbtiChaptersResponse?) {
                            if (response == null) {
                                return
                            }
                            val finalReport = response.meta.finalReport ?: return
                            showFinalReportDialogIfNeed(finalReport)
                        }

                        override fun onFailure(errorResponse: ErrorResponse) {
                            LogUtils.d(errorResponse.message)
                        }
                    })
        }

        private fun showFinalReportDialogIfNeed(finalReport: FinalReport) {
            if (finalReport.finishedAt == 0) {  // 未看完视频
                return
            }
            if (finalReport.finalOnlineReportId != 0) { // 已完成报告
                return
            }
            val currentTimeMillis = System.currentTimeMillis()
            val sp = getSp()
            val firstShowTime = sp.getLong(SP_KEY_FIRST_SHOW_TIME, 0L)
            val lastShowTime = sp.getLong(SP_KEY_LAST_SHOW_TIME, 0L)
            if (
                    currentTimeMillis - finalReport.finishedAt > SP_KEY_FIRST_SHOW_GAP_DURATION
                    && currentTimeMillis - lastShowTime > SP_KEY_ANOTHER_SHOW_GAP_DURATION
                    && (firstShowTime == 0L || currentTimeMillis - finalReport.finishedAt < SP_KEY_VALID_SHOW_DURATION)) {
                sp.put(SP_KEY_LAST_SHOW_TIME, currentTimeMillis)
                if (firstShowTime == 0L) {
                    sp.put(SP_KEY_FIRST_SHOW_TIME, currentTimeMillis)
                }
                CbtiFinalReportDialogActivity.start(finalReport)
            }
        }

        fun start(finalReport: FinalReport) {
            val intent = Intent(ActivityUtils.getTopActivity(), CbtiFinalReportDialogActivity::class.java)
            intent.putExtra(KEY_FINAL_REPORT, finalReport)
            ActivityUtils.startActivity(intent)
        }
    }

    override fun initWidget() {
        super.initWidget()
        SumianDialog(this)
                .showCloseIcon(true)
                .setTopIcon(R.drawable.popups_icon_report)
                .setTitleText(R.string.cbti_final_report)
                .setMessageText(R.string.cbti_final_report_dialog_message)
                .setRightBtn(R.string.go_to_evaluation, View.OnClickListener { launchFinalReportActivity() })
                .setOnDismissListenerWithReturn(DialogInterface.OnDismissListener { finish() })
                .show()
    }

    private fun launchFinalReportActivity() {
        val finalReport = intent.getParcelableExtra<FinalReport>(KEY_FINAL_REPORT)
        val payloadMap = HashMap<String, Any?>(3)
        payloadMap["scale_id"] = finalReport.scheme.scaleDistributionIds
        payloadMap["cbti_id"] = finalReport.scheme.cbtiId
        payloadMap["chapter_id"] = finalReport.scheme.chapterId
        ActivityUtils.startActivity(SimpleWebActivity.getLaunchIntentWithRouteData(this, "openCbtiScales", payloadMap))
    }
}