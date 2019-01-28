package com.sumian.sd.sleepguide

import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.h5.bean.H5PayloadData
import com.sumian.common.h5.bean.NativeRouteData
import com.sumian.common.h5.bean.ShareData
import com.sumian.sd.anxiousandfaith.AnxiousAndFaithActivity
import com.sumian.sd.app.AppManager
import com.sumian.sd.diary.fillsleepdiary.SleepDiaryActivity
import com.sumian.sd.h5.H5Uri
import com.sumian.sd.h5.SimpleWebActivity
import com.sumian.sd.kefu.KefuManager
import com.sumian.sd.relaxation.RelaxationListActivity
import com.sumian.sd.service.cbti.activity.CBTIIntroductionActivity
import com.sumian.sd.wxapi.MiniProgramHelper

class SleepGuideActivity : SimpleWebActivity() {

    companion object {
        fun start() {
            val intent = getLaunchIntentWithRouteData(
                    ActivityUtils.getTopActivity(),
                    H5PayloadData("sleepGuide", mapOf("analysisSource" to "mainPage")).toJson(),
                    SleepGuideActivity::class.java)
            ActivityUtils.startActivity(intent)
        }
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.openTopPadding(true)
        mTitleBar.visibility = View.GONE
    }

    override fun onGoToPage(routeData: NativeRouteData) {
        super.onGoToPage(routeData)
        when (routeData.page) {
            "sleepDiary" -> ActivityUtils.startActivity(SleepDiaryActivity::class.java)
            "relaxation" -> ActivityUtils.startActivity(RelaxationListActivity::class.java)
            "cbti" -> CBTIIntroductionActivity.show()
            "youzan" -> MiniProgramHelper.launchYouZanOrWeb(this)
            "anxietyAndFaith" -> ActivityUtils.startActivity(AnxiousAndFaithActivity::class.java)
            "sleepHealth" -> SimpleWebActivity.launch(this, H5Uri.CBTI_SLEEP_HEALTH)
            "onlineConsult" -> KefuManager.launchKefuActivity()
        }
    }

    override fun onShare(shareData: ShareData) {
        super.onShare(shareData)
        AppManager.getOpenEngine().shareUrl(this, shareData, null)
    }

}
