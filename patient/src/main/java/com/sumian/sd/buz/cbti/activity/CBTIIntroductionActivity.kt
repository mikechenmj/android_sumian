package com.sumian.sd.buz.cbti.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sumian.common.base.BaseActivity
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.helper.ToastHelper
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.statistic.StatUtil
import com.sumian.common.utils.MoneyUtil
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.cbti.adapter.CBTIIntroductionAdapter
import com.sumian.sd.buz.cbti.event.CBTIServiceBoughtEvent
import com.sumian.sd.buz.cbti.sheet.CBTIShareBottomSheet
import com.sumian.sd.buz.doctor.bean.DoctorService
import com.sumian.sd.buz.homepage.bean.CbtiChapterData
import com.sumian.sd.buz.homepage.bean.GetCbtiChaptersResponse
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.pay.activity.PaymentActivity
import com.sumian.sd.common.utils.EventBusUtil
import com.sumian.sd.common.utils.TimeUtil
import kotlinx.android.synthetic.main.activity_main_cbti_introduction.*
import kotlinx.android.synthetic.main.item_cbti_renew_package.view.*
import kotlinx.android.synthetic.main.lay_cbti_lesson_banner_home_view.view.*
import kotlinx.android.synthetic.main.lay_cbti_lesson_introduction_home_view.view.*
import kotlinx.android.synthetic.main.layout_cbti_renew_bottom_sheet.*

/**
 * Created by jzz
 *
 * on 2018-10-26.
 *
 * desc:CBTI 课程介绍页  (已购买/未购买状态) 包括课程列表、banner，学习进度，过期时间，了解更多 h5
 *
 */
class CBTIIntroductionActivity : BaseActivity(), BaseRecyclerAdapter.OnItemClickListener {

    companion object {
        private const val REQUEST_CODE_BUY_SERVICE = 100

        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, CBTIIntroductionActivity::class.java))
            }
        }

        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, CBTIIntroductionActivity::class.java)
        }
    }

    private val mAdapter: CBTIIntroductionAdapter by lazy {
        val adapter = CBTIIntroductionAdapter(this@CBTIIntroductionActivity)
        adapter.setOnItemClickListener(this@CBTIIntroductionActivity)
        adapter
    }

    private var mRenewService: DoctorService? = null

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_cbti_introduction
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.cbti_title_bar)
        mTitleBar.openTopPadding(true)
//        mTitleBar.showMoreIcon(R.drawable.ic_nav_share)
//        mTitleBar.setOnMenuClickListener {
//            CBTIShareBottomSheet.show(fragmentManager = supportFragmentManager)
//            StatUtil.event(StatConstants.click_cbti_main_page_share_btn)
//        }
        recycler.adapter = mAdapter
        recycler.itemAnimator = null
        recycler.layoutManager = LinearLayoutManager(this)
        cbti_introduction_list_home_banner.cbti_lesson_plan_view.tv_renew.setOnClickListener { showRenewDialog() }
    }

    override fun onResume() {
        super.onResume()
        requestData()
        cbti_introduction_webview?.resumeWebView()
    }

    override fun onPause() {
        super.onPause()
        cbti_introduction_webview?.pauseWebView()
    }

    override fun onBackPressed() {
        if (!cbti_introduction_webview.webViewCanGoBack()) {
            super.onBackPressed()
        }
    }

    override fun onRelease() {
        releaseWebView()
        super.onRelease()
    }

    override fun showLoading() {
        //super.showLoading()
    }

    override fun dismissLoading() {
        //super.dismissLoading()
    }

    override fun onItemClick(position: Int, itemId: Long) {
        val cbtiChapterData: CbtiChapterData = mAdapter.getItem(position)

        if (cbtiChapterData.isLock) {
            //ToastHelper.show(this, getString(R.string.cbti_chapter_no_lock), Gravity.CENTER)
            return
        }

        if (TextUtils.isEmpty(cbtiChapterData.scale_distribution_ids)) {//为 null 需要评估
            CBTIWeekCoursePartActivity.show(this, cbtiChapterData.id)
        } else {//需要做评估
            SumianDialog(this)
                    .setTitleText(R.string.go_to_evaluation_title)
                    .setMessageText(R.string.go_to_evaluation_message)
                    .setRightBtn(R.string.go_to_evaluation, View.OnClickListener { CBTIEvaluationWebActivity.show(cbtiChapterData.scale_distribution_ids, cbtiChapterData.id) })
                    .show()
        }
    }

    fun getCBTIIntroductionListFailed(error: String) {
        ToastHelper.show(this, error, Gravity.CENTER)
    }

    fun getCBTIServiceDetailSuccess(name: String, introduction: String, bannerUrl: String) {
        cbti_introduction_list_home_banner.invalidateBannerExtras(bannerUrl, name, introduction)
    }

    fun getCBTIServiceDetailFailed(error: String) {
        getCBTIIntroductionListFailed(error)
    }

    fun onCBTIServiceIsExpired(isExpired: Boolean) {
        if (isExpired) {
            showCBTIIntroductionWebView()
            StatUtil.trackBeginPage(this, StatConstants.page_cbti_introduction_from_banner)
            StatUtil.event(StatConstants.page_cbti_introduction)
        } else {
            hideCBTIIntroductionWebView()
            StatUtil.trackBeginPage(this, StatConstants.page_cbti_chapter_list)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            requestData()
            EventBusUtil.postStickyEvent(CBTIServiceBoughtEvent())
        }
    }

    private fun requestData() {
        getCBTIServiceDetail()
        getRenewCBTIService()
        getCBTIIntroductionList()
    }

    private fun initCBTIIntroductionWebView() {
        cbti_introduction_webview?.setTitleBar(mTitleBar)
        cbti_introduction_webview?.requestCBTIIntroductionUrl()
    }

    private fun showCBTIIntroductionWebView() {
        coordinator_cbti_info.visibility = View.GONE
        if (cbti_introduction_webview.visibility == View.GONE) {
            cbti_introduction_webview.visibility = View.VISIBLE
            initCBTIIntroductionWebView()
        }
    }

    private fun hideCBTIIntroductionWebView() {
        coordinator_cbti_info.visibility = View.VISIBLE
        cbti_introduction_webview.visibility = View.GONE
    }

    private fun releaseWebView() {
        cbti_introduction_webview?.destroyWebView()
    }

    fun getCBTIServiceDetail() {
        showLoading()
        val call = AppManager.getSdHttpService().getServiceByType(DoctorService.SERVICE_TYPE_CBTI)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<DoctorService>() {
            override fun onSuccess(response: DoctorService?) {
                response?.let {
                    getCBTIServiceDetailSuccess(it.name, it.introduction, it.picture)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                getCBTIServiceDetailFailed(errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                dismissLoading()
            }
        })
    }

    fun getRenewCBTIService() {
        showLoading()
        val call = AppManager.getSdHttpService().getServiceByType(DoctorService.SERVICE_TYPE_CBTI, 1)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<DoctorService>() {
            override fun onSuccess(response: DoctorService?) {
                if (response == null) {
                    return
                }
                mRenewService = response
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                getCBTIServiceDetailFailed(errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                dismissLoading()
            }
        })
    }

    fun getCBTIIntroductionList() {
        showLoading()
        val call = AppManager.getSdHttpService().getCbtiChapters("courses")
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<GetCbtiChaptersResponse>() {
            override fun onSuccess(response: GetCbtiChaptersResponse?) {
                response?.let {
                    val data = it.data
                    updateIntroduction(data, it)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                getCBTIIntroductionListFailed(errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                dismissLoading()
            }

        })
    }

    private fun updateIntroduction(data: List<CbtiChapterData>, it: GetCbtiChaptersResponse) {
        mAdapter.resetItem(data)
        onCBTIServiceIsExpired(it.meta.isLock)
        val formatExpiredDate = TimeUtil.formatDate("yyyy.MM.dd 到期", it.meta.expiredAt * 1000L)
        cbti_introduction_list_home_banner.invalidateBanner(formatExpiredDate, it.meta.totalProgressText)
//        cbti_introduction_list_home_banner.cbti_lesson_plan_view.tv_renew.isVisible = true
    }

    private fun showRenewDialog() {
        if (mRenewService == null) {
            ToastUtils.showShort("no data")
            return
        }
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.layout_cbti_renew_bottom_sheet)
        dialog.vg_package_container.setOnClickListener { dialog.dismiss() }
        for (pkg in mRenewService!!.service_packages) {
            val item = LayoutInflater.from(this).inflate(R.layout.item_cbti_renew_package, dialog.vg_package_container, false)
            item.tv_package_title.text = pkg.name
            val detail = pkg.packages[0]
            item.tv_package_price.text = MoneyUtil.fenToYuanString(detail.unit_price, includeSign = false, includeYuanMark = true)
            item.setOnClickListener {
                PaymentActivity.startForResult(ActivityUtils.getTopActivity(), mRenewService!!, pkg.id, REQUEST_CODE_BUY_SERVICE)
                dialog.dismiss()
            }
            dialog.vg_package_container.addView(item)
        }
        dialog.tv_cancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}