package com.sumian.sd.buz.cbti.activity

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.common.statistic.StatUtil
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.R
import com.sumian.sd.buz.cbti.adapter.CBTIIntroductionAdapter
import com.sumian.sd.buz.cbti.contract.CBTIIntroductionContract
import com.sumian.sd.buz.cbti.event.CBTIServiceBoughtEvent
import com.sumian.sd.buz.cbti.presenter.CBTIIntroductionPresenter
import com.sumian.sd.buz.cbti.sheet.CBTIShareBottomSheet
import com.sumian.sd.buz.homepage.bean.CbtiChapterData
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.utils.EventBusUtil
import kotlinx.android.synthetic.main.activity_main_cbti_introduction.*

/**
 * Created by jzz
 *
 * on 2018-10-26.
 *
 * desc:CBTI 课程介绍页  (已购买/未购买状态) 包括课程列表、banner，学习进度，过期时间，了解更多 h5
 *
 */
class CBTIIntroductionActivity : BaseViewModelActivity<CBTIIntroductionPresenter>(), CBTIIntroductionContract.View,
        BaseRecyclerAdapter.OnItemClickListener {

    companion object {
        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, CBTIIntroductionActivity::class.java))
            }
        }
    }

    private val mAdapter: CBTIIntroductionAdapter by lazy {
        val adapter = CBTIIntroductionAdapter(this@CBTIIntroductionActivity)
        adapter.setOnItemClickListener(this@CBTIIntroductionActivity)
        adapter
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_cbti_introduction
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mViewModel = CBTIIntroductionPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.cbti_title_bar)
        mTitleBar.openTopPadding(true)
        mTitleBar.showMoreIcon(R.drawable.ic_nav_share)
        mTitleBar.setOnMenuClickListener {
            CBTIShareBottomSheet.show(fragmentManager = supportFragmentManager)
            StatUtil.event(StatConstants.click_cbti_main_page_share_btn)
        }
        recycler.adapter = mAdapter
        recycler.itemAnimator = null
        recycler.layoutManager = LinearLayoutManager(this)
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

    override fun getCBTIIntroductionListSuccess(cbtiChapterDataList: List<CbtiChapterData>) {
        mAdapter.resetItem(cbtiChapterDataList)
    }

    override fun getCBTIIntroductionListFailed(error: String) {
        ToastHelper.show(this, error, Gravity.CENTER)
    }

    override fun getBannerInfo(formatExpiredTime: String, formatTotalProgress: String) {
        cbti_introduction_list_home_banner.invalidateBanner(formatExpiredTime, formatTotalProgress)
    }

    override fun getCBTIServiceDetailSuccess(name: String, introduction: String, bannerUrl: String) {
        cbti_introduction_list_home_banner.invalidateBannerExtras(bannerUrl, name, introduction)
    }

    override fun getCBTIServiceDetailFailed(error: String) {
        getCBTIIntroductionListFailed(error)
    }

    override fun onCBTIServiceIsExpired(isExpired: Boolean) {
        // finish()
        if (isExpired) {
            showCBTIIntroductionWebView()
            StatUtil.trackBeginPage(this, StatConstants.page_cbti_introduction_from_banner)
            StatUtil.event(StatConstants.enter_cbti_introduction_page)
        } else {
            hideCBTIIntroductionWebView()
            StatUtil.trackBeginPage(this, StatConstants.page_cbti_chapter_list)
        }
        // CBTIIntroductionWebActivity.show()//已过期，跳转去购买服务
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            requestData()
            EventBusUtil.postStickyEvent(CBTIServiceBoughtEvent())
        }
    }

    private fun requestData() {
        mViewModel?.getCBTIServiceDetail()
        mViewModel?.getCBTIIntroductionList()
    }

    private fun initCBTIIntroductionWebView() {
        // if (mCbtiIntroductionWebView == null) {
        // val cbtiIntroductionWebView = CBTIIntroductionWebView(this)
        //  cbtiIntroductionWebView.post {
        cbti_introduction_webview?.setTitleBar(mTitleBar)
        cbti_introduction_webview?.requestCBTIIntroductionUrl()
        //  }
        //  val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        // layoutParams.gravity = Gravity.CENTER
        //cbti_introduction_container.addView(cbtiIntroductionWebView, layoutParams)
        // this.mCbtiIntroductionWebView = cbtiIntroductionWebView
        // Log.e("TAG", "initCBTIIntroductionWebView: --------init---->")
        //  } else {
        //     mCbtiIntroductionWebView?.requestCBTIIntroductionUrl()
        //   Log.e("TAG", "initCBTIIntroductionWebView: --------直接 request---->")
        // }
    }

    private fun showCBTIIntroductionWebView() {
        coordinator_cbti_info.visibility = View.GONE
        initCBTIIntroductionWebView()
        cbti_introduction_webview.visibility = View.VISIBLE
    }

    private fun hideCBTIIntroductionWebView() {
        coordinator_cbti_info.visibility = View.VISIBLE
        cbti_introduction_webview.visibility = View.GONE
        //mCbtiIntroductionWebView?.let {
        //    cbti_introduction_container.removeViewInLayout(it)
        //}
        // releaseWebView()
        //mCbtiIntroductionWebView = null
    }

    private fun releaseWebView() {
        cbti_introduction_webview?.destroyWebView()
    }
}