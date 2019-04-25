package com.sumian.sddoctor.service.cbti.activity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.h5.bean.H5PayloadData
import com.sumian.common.helper.ToastHelper
import com.sumian.sddoctor.BuildConfig
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.constants.H5Uri
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.h5.SddBaseWebViewActivity
import com.sumian.sddoctor.main.MainActivity
import com.sumian.sddoctor.service.cbti.presenter.CBTILauncherPresenter
import com.sumian.sddoctor.service.cbti.sheet.CBTIShareBottomSheet
import kotlinx.android.synthetic.main.activity_cbti_main_introduction2_web.*
import kotlinx.android.synthetic.main.lay_visitor_tips.*

/**
 * Created by sm
 *
 * on 2018/7/11
 *
 * desc:CBTI  h5介绍页  即了解更多那里跳转
 *
 */
@Suppress("DEPRECATION")
class CBTIIntroduction2WebActivity : SddBaseWebViewActivity() {

    companion object {

        private const val IS_GO_TO_MORE_INFO = "com.sumian.sdd.extras.is.more.info"

        @JvmStatic
        fun show(isGoToMoreInfo: Boolean = false) {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, CBTIIntroduction2WebActivity::class.java).apply {
                    putExtra(IS_GO_TO_MORE_INFO, isGoToMoreInfo)
                })
            }
        }
    }

    private var isMoreInfo = false

    override fun getLayoutId(): Int {
        return R.layout.activity_cbti_main_introduction2_web
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        isMoreInfo = bundle.getBoolean(IS_GO_TO_MORE_INFO, false)
    }

    override fun getPageName(): String {
        return StatConstants.page_cbti_info
    }

    override fun initWidget() {
        super.initWidget()
        getTitleBar().showMoreIcon(R.drawable.ic_nav_share)
        getTitleBar().setOnMenuClickListener { CBTIShareBottomSheet.show(supportFragmentManager) }
        bt_go_to_cbti.setOnClickListener {
            val doctorInfo = AppManager.getAccountViewModel().getDoctorInfo().value
            val visitorAccount = doctorInfo?.isVisitorAccount() == true
            if (visitorAccount) {
                ToastHelper.show(this@CBTIIntroduction2WebActivity, getString(R.string
                        .visitor_to_get_more_service), Gravity.CENTER)
                return@setOnClickListener
            } else if (doctorInfo?.reviewStatus != 2) {
                ToastHelper.show(this@CBTIIntroduction2WebActivity, getString(R.string
                        .complete_authentication_to_explore_more_cbti), Gravity.CENTER)
                return@setOnClickListener
            }
            CBTILauncherPresenter.saveLauncherAction()
            CBTIIntroductionActivity.show()
        }
        vg_warning.setOnClickListener {
            ActivityUtils.finishAllActivities()
            MainActivity.show(2)
        }
    }

    override fun initData() {
        super.initData()
        if (isMoreInfo) return
        val doctorInfo = AppManager.getAccountViewModel().getDoctorInfo().value
        val visitorAccount = doctorInfo?.isVisitorAccount() == true
        vg_warning.visibility = when {
            visitorAccount -> {
                tv_warning.text = getString(R.string.visitor_to_get_more_service)
                bt_go_to_cbti.setTextColor(resources.getColor(R.color.t2_color_day))
                bt_go_to_cbti.setBackgroundColor(resources.getColor(R.color.l3_color_day))
                iv_nav.visibility = View.VISIBLE
                View.VISIBLE
            }
            doctorInfo?.reviewStatus != 2 -> {
                tv_warning.text = getString(R.string.complete_authentication_to_explore_more_cbti)
                bt_go_to_cbti.setTextColor(resources.getColor(R.color.t2_color_day))
                bt_go_to_cbti.setBackgroundColor(resources.getColor(R.color.l3_color_day))
                iv_nav.visibility = View.VISIBLE
                View.VISIBLE
            }
            else -> {
                bt_go_to_cbti.setTextColor(resources.getColor(R.color.b2_color_day))
                bt_go_to_cbti.setBackgroundColor(resources.getColor(R.color.b3_color_day))
                View.GONE
            }
        }
        bt_go_to_cbti.visibility = View.VISIBLE
    }

    override fun getCompleteUrl(): String {
        val urlContent = H5Uri.NATIVE_ROUTE.replace("{pageData}", H5PayloadData(H5Uri.CBTI_INTRODUCTION, mapOf()).toJson())
                .replace("{token}", AppManager.getAccountViewModel().getToken() ?: "")
        return BuildConfig.BASE_H5_URL + urlContent
    }
}