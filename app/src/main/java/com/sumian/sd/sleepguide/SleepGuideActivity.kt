package com.sumian.sd.sleepguide

import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.h5.bean.H5PayloadData
import com.sumian.common.h5.bean.NativeRouteData
import com.sumian.common.h5.bean.ShareData
import com.sumian.sd.app.AppManager
import com.sumian.sd.h5.H5Uri
import com.sumian.sd.h5.SimpleWebActivity
import com.sumian.sd.homepage.SleepPrescriptionSettingActivity
import com.sumian.sd.kefu.KefuManager
import com.sumian.sd.relaxation.RelaxationListActivity
import com.sumian.sd.service.cbti.activity.CBTIIntroductionActivity
import com.sumian.sd.wxapi.MiniProgramHelper

class SleepGuideActivity : SimpleWebActivity() {

    companion object {
        fun start() {
            val intent = getLaunchIntentWithRouteData(
                    ActivityUtils.getTopActivity(),
                    H5PayloadData("sleepGuide", null).toJson(),
                    SleepGuideActivity::class.java)
            ActivityUtils.startActivity(intent)
        }
    }

    override fun onGoToPage(routeData: NativeRouteData) {
        super.onGoToPage(routeData)
        when (routeData.page) {
            "sleepPrescription" -> SleepPrescriptionSettingActivity.launch()
            "relaxation" -> ActivityUtils.startActivity(RelaxationListActivity::class.java)
            "cbti" -> CBTIIntroductionActivity.show()
            "youzan" -> MiniProgramHelper.launchYouZanOrWeb(this)
            "anxietyAndFaith" -> ActivityUtils.startActivity(RelaxationListActivity::class.java)
            "sleepHealth" -> SimpleWebActivity.launch(this, H5Uri.CBTI_SLEEP_HEALTH)
            "onlineConsult" -> KefuManager.launchKefuActivity()
        }
    }

    override fun onShare(shareData: ShareData) {
        super.onShare(shareData)
        AppManager.getOpenEngine().shareUrl(this, shareData, null)
    }

}
