package com.sumian.sd.buz.cbti.activity

import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.h5.bean.H5PayloadData
import com.sumian.common.statistic.StatUtil
import com.sumian.sd.BuildConfig
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBaseWebViewActivity
import com.sumian.sd.buz.cbti.sheet.CBTIShareBottomSheet
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.h5.H5Uri
import com.sumian.sd.common.utils.StatusBarUtil

/**
 * Created by sm
 *
 * on 2018/7/11
 *
 * desc:CBTI  h5介绍页  即了解更多那里跳转
 *
 */
class CBTIIntroduction2WebActivity : SdBaseWebViewActivity() {

    companion object {
        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, CBTIIntroduction2WebActivity::class.java))
            }
        }
    }

    override fun initWidget() {
        super.initWidget()
        StatUtil.event(StatConstants.enter_cbti_introduction_page)
        StatUtil.trackBeginPage(this, StatConstants.page_cbti_introduction_from_learn_more)
        getTitleBar().openTopPadding(true)
        getTitleBar().showMoreIcon(R.drawable.ic_nav_share)
        getTitleBar().setOnMenuClickListener {
            StatUtil.event(StatConstants.click_cbti_introduction_share)
            StatUtil.event(StatConstants.click_cbti_introduction_page_share_btn)
            CBTIShareBottomSheet.show(fragmentManager = supportFragmentManager)
        }
    }

    override fun getCompleteUrl(): String {
        val urlContent = H5Uri.NATIVE_ROUTE.replace("{pageData}", H5PayloadData(H5Uri.CBTI_INTRODUCTION, mapOf()).toJson())
                .replace("{token}", AppManager.getAccountViewModel().token.token) + "&analysisSource=mainPage_cbtiBanner"
        return BuildConfig.BASE_H5_URL + urlContent
    }
}