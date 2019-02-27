package com.sumian.sddoctor.service.cbti.activity

import android.app.Activity
import android.content.Intent
import android.view.Gravity
import android.view.View
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.common.utils.SpUtil
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.App
import com.sumian.sddoctor.base.StatusBarHelper
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.service.cbti.adapter.CBTIIntroductionAdapter
import com.sumian.sddoctor.service.cbti.bean.CbtiChapterData
import com.sumian.sddoctor.service.cbti.contract.CBTIIntroductionContract
import com.sumian.sddoctor.service.cbti.presenter.CBTIIntroductionPresenter
import com.sumian.sddoctor.service.cbti.sheet.CBTIShareBottomSheet
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

    override fun getConfigsSuccess() {

    }

    override fun getConfigsFailed(error: String) {
    }

    companion object {

        private const val IS_SHOW_POP_GUIDE_FILE = "com.sumian.sdd.is.show.guide.file"
        private const val IS_SHOW_POP = "com.sumian.sdd.is.show.pop"

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

    override fun getPageName(): String {
        return StatConstants.page_cbti_course
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mViewModel = CBTIIntroductionPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        StatusBarHelper.initTitleBarUI(this, mTitleBar)
        mTitleBar.setTitle(R.string.cbti_title_bar)
        mTitleBar.openTopPadding(true)
        mTitleBar.showMoreIcon(R.drawable.ic_nav_share)
        mTitleBar.setOnMenuClickListener {
            CBTIShareBottomSheet.show(fragmentManager = supportFragmentManager)
        }
        recycler.itemAnimator = null
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = mAdapter
        iv_oh_i_see.setOnClickListener {
            commitPopSp()
            lay_oh_i_see_container.visibility = View.GONE
        }
    }

    override fun initData() {
        super.initData()
        val sp = SpUtil.initSp(App.getAppContext(), IS_SHOW_POP_GUIDE_FILE)
        val isPop = sp.getBoolean(IS_SHOW_POP, false)
        if (isPop) return
        lay_oh_i_see_container.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        requestData()
    }

    override fun showLoading() {
        //super.showLoading()
    }

    override fun dismissLoading() {
        //super.dismissLoading()
    }

    override fun onItemClick(position: Int, itemId: Long) {
        CBTIWeekCoursePartActivity.show(this@CBTIIntroductionActivity, mAdapter.getItem(position).id)
    }

    override fun getCBTIIntroductionListSuccess(cbtiChapterDataList: List<CbtiChapterData>) {
        mAdapter.resetItem(cbtiChapterDataList)
    }

    override fun getCBTIIntroductionListFailed(error: String) {
        ToastHelper.show(this, error, Gravity.CENTER)
    }

    override fun getBannerInfo(formatTotalProgress: String) {
        cbti_introduction_list_home_banner.invalidateBanner(formatTotalProgress)
    }

    override fun getCBTIServiceDetailSuccess(name: String, introduction: String, bannerUrl: String) {
        cbti_introduction_list_home_banner.invalidateBannerExtras(bannerUrl, name, introduction)
    }

    override fun getCBTIServiceDetailFailed(error: String) {
        getCBTIIntroductionListFailed(error)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            requestData()
        }
    }

    private fun requestData() {
        // mViewModel?.getCBTIServiceDetail()
        mViewModel?.getCBTIIntroductionList()
        mViewModel?.getConfigs()
    }

    private fun commitPopSp() {
        val sharedPreferences = SpUtil.initSp(App.getAppContext(), IS_SHOW_POP_GUIDE_FILE)
        sharedPreferences.edit {
            putBoolean(IS_SHOW_POP, true)
        }
    }
}