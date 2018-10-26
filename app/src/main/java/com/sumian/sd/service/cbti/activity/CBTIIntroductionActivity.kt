package com.sumian.sd.service.cbti.activity

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseBackPresenterActivity
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.helper.ToastHelper
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.R
import com.sumian.sd.homepage.bean.CbtiChapterData
import com.sumian.sd.service.cbti.adapter.CBTIIntroductionAdapter
import com.sumian.sd.service.cbti.contract.CBTIIntroductionContract
import com.sumian.sd.service.cbti.presenter.CBTIIntroductionPresenter
import kotlinx.android.synthetic.main.activity_main_cbti_introduction.*

/**
 * Created by jzz
 *
 * on 2018-10-26.
 *
 * desc:CBTI 课程介绍页   包括课程列表、banner，学习进度，过期时间，了解更多 h5
 *
 */
class CBTIIntroductionActivity : BaseBackPresenterActivity<CBTIIntroductionContract.Presenter>(), CBTIIntroductionContract.View,
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

    override fun getChildContentId(): Int {
        return R.layout.activity_main_cbti_introduction
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mPresenter = CBTIIntroductionPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.cbti_title_bar)
        mTitleBar.openTopPadding(true)
        recycler.adapter = mAdapter
        recycler.itemAnimator = null
        recycler.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        mPresenter?.getCBTIIntroductionList()
    }

    override fun showLoading() {
        //super.showLoading()
    }

    override fun dismissLoading() {
        //super.dismissLoading()
    }

    override fun onItemClick(position: Int, itemId: Long) {
        val cbtiChapterData: CbtiChapterData = mAdapter.getItem(position)
        if (TextUtils.isEmpty(cbtiChapterData.scale_distribution_ids)) {//为 null 需要评估
            if (cbtiChapterData.isLock) {
                ToastHelper.show(this, getString(R.string.cbti_chapter_no_lock), Gravity.CENTER)
            } else {
                CBTIWeekCoursePartActivity.show(this, cbtiChapterData.id)
            }
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
}