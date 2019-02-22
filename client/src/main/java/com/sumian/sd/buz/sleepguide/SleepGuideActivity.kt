package com.sumian.sd.buz.sleepguide

import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.h5.bean.H5PayloadData
import com.sumian.common.h5.bean.ShareData
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.anxiousandfaith.AnxiousAndFaithActivity
import com.sumian.sd.buz.cbti.activity.CBTIIntroductionActivity
import com.sumian.sd.buz.diary.fillsleepdiary.SleepDiaryActivity
import com.sumian.sd.buz.kefu.KefuManager
import com.sumian.sd.buz.relaxation.RelaxationListActivity
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.h5.H5Uri
import com.sumian.sd.common.h5.SimpleWebActivity
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

    override fun getPageName(): String {
        return StatConstants.page_sleep_guide_cover
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.openTopPadding(true)
        mTitleBar.visibility = View.GONE
    }

    override fun onGoToPage(page: String, rawData: String) {
        when (page) {
            "sleepDiary" -> ActivityUtils.startActivity(SleepDiaryActivity::class.java)
            "relaxation" -> ActivityUtils.startActivity(RelaxationListActivity::class.java)
            "cbti" -> CBTIIntroductionActivity.show()
            "youzan" -> MiniProgramHelper.launchYouZanOrWeb(this)
            "anxietyAndFaith" -> ActivityUtils.startActivity(AnxiousAndFaithActivity::class.java)
            "sleepHealth" -> SimpleWebActivity.launch(this, H5Uri.CBTI_SLEEP_HEALTH, StatConstants.page_sleep_health_list)
            "onlineConsult" -> KefuManager.launchKefuActivity()
        }
    }

    override fun onShare(shareData: ShareData) {
        super.onShare(shareData)
        AppManager.getOpenEngine().shareUrl(this, shareData, null)
    }

}
