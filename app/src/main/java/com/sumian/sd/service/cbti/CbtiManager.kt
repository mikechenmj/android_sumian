package com.sumian.sd.service.cbti

import android.text.format.DateUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.homepage.bean.FinalReport
import com.sumian.sd.homepage.bean.GetCbtiChaptersResponse
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.service.cbti.activity.CbtiFinalReportDialogActivity

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/1/11 16:09
 * desc   : 用户完成最后一个视频后的第8天（自然天）后打开首页，如果未完成终期评估就会有弹窗,
 *          若用户未完成填写，距离上一次弹窗隔3天（自然天）打开首页就弹一次；
 *          距离第一次弹窗超过45天，就不再提醒用户去填写；
 * version: 1.0
 */
object CbtiManager {
    /**
     *
     */
    private const val SP_KEY_FIRST_SHOW_TIME = "SP_KEY_FIRST_SHOW_TIME"
    private const val SP_KEY_LAST_SHOW_TIME = "SP_KEY_LAST_SHOW_TIME"
    private const val SP_KEY_FIRST_SHOW_GAP_DURATION = DateUtils.DAY_IN_MILLIS * 8
    private const val SP_KEY_ANOTHER_SHOW_GAP_DURATION = DateUtils.DAY_IN_MILLIS * 3
    private const val SP_KEY_VALID_SHOW_DURATION = DateUtils.DAY_IN_MILLIS * 45
    private fun getSp() = SPUtils.getInstance(javaClass.simpleName)

    fun showFinalReportDialogIfNeed() {
        AppManager.getSdHttpService().getCbtiChapters(null)
                .enqueue(object : BaseSdResponseCallback<GetCbtiChaptersResponse>() {
                    override fun onSuccess(response: GetCbtiChaptersResponse?) {
                        if (response == null) {
                            return
                        }
                        val finalReport = response.meta.finalReport

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
                && (firstShowTime == 0L || currentTimeMillis - firstShowTime < SP_KEY_VALID_SHOW_DURATION)) {
            sp.put(SP_KEY_LAST_SHOW_TIME, currentTimeMillis)
            if (firstShowTime == 0L) {
                sp.put(SP_KEY_FIRST_SHOW_TIME, currentTimeMillis)
            }
            CbtiFinalReportDialogActivity.start(finalReport)
        }
    }

}