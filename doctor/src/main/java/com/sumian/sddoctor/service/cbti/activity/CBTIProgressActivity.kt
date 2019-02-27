package com.sumian.sddoctor.service.cbti.activity

import android.content.Intent
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.helper.ToastHelper
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseViewModelActivity
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.service.cbti.ITrigger
import com.sumian.sddoctor.service.cbti.adapter.CBTIProgressGroupAdapter
import com.sumian.sddoctor.service.cbti.bean.CBTIProgressGroup
import com.sumian.sddoctor.service.cbti.contract.CBTILauncherContract
import com.sumian.sddoctor.service.cbti.contract.CBTIProgressGroupContract
import com.sumian.sddoctor.service.cbti.presenter.CBTILauncherPresenter
import com.sumian.sddoctor.service.cbti.presenter.CBTIProgressGroupPresenter
import com.sumian.sddoctor.service.cbti.widget.CBTIGuidePopView
import kotlinx.android.synthetic.main.activity_main_cbti_progress.*

/**
 * CBTI  患者进度查询中心
 */
class CBTIProgressActivity : SddBaseViewModelActivity<CBTIProgressGroupPresenter>(), CBTIProgressGroupContract.View, ITrigger, SwipeRefreshLayout.OnRefreshListener, CBTILauncherContract.View {

    companion object {

        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, CBTIProgressActivity::class.java))
            }
        }
    }

    private val cbtiGroupAdapter by lazy {
        CBTIProgressGroupAdapter().setOnTrigger(this)
    }

    private val mCBTIGuiderPopView by lazy {
        CBTIGuidePopView.create()
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_cbti_progress
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mViewModel = CBTIProgressGroupPresenter.init(this)
    }

    override fun getPageName(): String {
        return StatConstants.page_cbti_patient_progress
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.cbti)
        mTitleBar.setTvAndIvColor(ColorCompatUtil.getColor(this@CBTIProgressActivity, R.color.t1_color))
        mTitleBar.setBgColor(ColorCompatUtil.getColor(this@CBTIProgressActivity, R.color.b2_color))
        mTitleBar.mIvBack.setColorFilter(ColorCompatUtil.getColor(this@CBTIProgressActivity, R.color.b3_color))
        iv_cbti_banner.setOnClickListener { CBTILauncherPresenter.create(this).launcherCBTI() }
        refresh.setOnRefreshListener(this)
        recycler.itemAnimator = null
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = cbtiGroupAdapter
        mCBTIGuiderPopView.showPop(iv_cbti_banner)
    }

    override fun initData() {
        super.initData()
        mViewModel?.initDefaultCBTIProgressGroups()
        mViewModel?.getCBTIProgressGroups()
    }

    override fun onRelease() {
        super.onRelease()
        mCBTIGuiderPopView.dismiss()
    }

    override fun showLoading() {
        //super.showLoading()
        refresh.showRefreshAnim()
    }

    override fun dismissLoading() {
        //super.dismissLoading()
        refresh.hideRefreshAnim()
    }

    override fun onRefresh() {
        mViewModel?.getCBTIProgressGroups()
    }

    override fun onTrigger(position: Int, group: CBTIProgressGroup) {
        mViewModel?.getCBTIProgressGroups(group.key, isHavePatient = true)
    }

    override fun onLauncherCBTIIntroduction() {
        super.onLauncherCBTIIntroduction()
        CBTIIntroductionActivity.show()
    }

    override fun onLauncherCBTIIntroductionWeb() {
        super.onLauncherCBTIIntroductionWeb()
        CBTIIntroduction2WebActivity.show()
    }

    override fun onGetCBTIProgressGroupsSuccess(groups: MutableList<CBTIProgressGroup>) {
        cbtiGroupAdapter.resetItems(groups)
    }

    override fun onGetCBTIProgressGroupsFailed(error: String) {
        ToastHelper.show(this, error, Gravity.CENTER)
    }
}