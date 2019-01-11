package com.sumian.sd.service.cbti.activity

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseDialogPresenterActivity
import com.sumian.common.mvp.IPresenter
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.R
import com.sumian.sd.h5.SimpleWebActivity
import com.sumian.sd.homepage.bean.FinalReport
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