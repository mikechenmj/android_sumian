package com.sumian.sleepdoctor.advisory.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.advisory.adapter.RecordAdapter
import com.sumian.sleepdoctor.advisory.bean.Advisory
import com.sumian.sleepdoctor.advisory.contract.RecordContract
import com.sumian.sleepdoctor.advisory.presenter.RecordPresenter
import com.sumian.sleepdoctor.base.SdBaseActivity
import com.sumian.sleepdoctor.h5.H5Uri
import com.sumian.sleepdoctor.main.SdMainActivity
import com.sumian.sleepdoctor.widget.TitleBar
import com.sumian.sleepdoctor.widget.dialog.SumianWebDialog
import kotlinx.android.synthetic.main.activity_main_advisory_detail.*

/**
 *
 *Created by sm
 * on 2018/6/4 18:28
 * desc:咨询详情,包含了提问或者回复的记录列表,在线报告列表
 **/
class AdvisoryDetailActivity : SdBaseActivity<RecordContract.Presenter>(), RecordContract.View, SwipeRefreshLayout.OnRefreshListener, TitleBar.OnBackClickListener, TitleBar.OnMenuClickListener, View.OnClickListener {

    companion object {
        private const val ARGS_ADVISORY_ID = "com.sumian.app.extras.advisory.id"
        private const val ARGS_ADVISORY = "com.sumian.sleepdoctor.extras.advisory"

        fun launch(context: Context, advisory: Advisory) {
            val extras = Bundle().apply { putParcelable(ARGS_ADVISORY, advisory) }
            show(context, AdvisoryDetailActivity::class.java, extras)
        }

        fun getLaunchIntent(context: Context, advisoryId: Int): Intent {
            val intent = Intent(context, AdvisoryDetailActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            val bundle = Bundle()
            bundle.putInt(ARGS_ADVISORY_ID, advisoryId)
            intent.putExtras(bundle)
            return intent
        }
    }

    private var mAdvisoryId: Int = 0

    private lateinit var mAdapter: RecordAdapter
    private var mAdvisory: Advisory? = null

    override fun initBundle(bundle: Bundle?): Boolean {
        bundle?.let {
            this.mAdvisory = it.getParcelable(ARGS_ADVISORY)
            this.mAdvisoryId = it.getInt(ARGS_ADVISORY_ID, 0)
        }
        return super.initBundle(bundle)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_advisory_detail
    }

    override fun initPresenter() {
        super.initPresenter()
        RecordPresenter.init(this)
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        title_bar.setOnBackClickListener(this)
        title_bar.setOnMenuClickListener(this)
        refresh.setOnRefreshListener(this)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.itemAnimator = DefaultItemAnimator()
        this.mAdapter = RecordAdapter(this)
        recycler.adapter = mAdapter
        tv_bottom_notification.setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
        mAdvisory?.let {
            mPresenter.getAdvisoryDetail(it.id)
        }

        if (mAdvisory == null) {
            mPresenter.getAdvisoryDetail(mAdvisoryId)
        }

    }

    override fun onBegin() {
        super.onBegin()
        refresh.showRefreshAnim()
    }

    override fun onFinish() {
        super.onFinish()
        refresh.hideRefreshAnim()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    override fun onGetAdvisoryDetailSuccess(advisory: Advisory) {
        this.mAdvisory = advisory
        mAdvisory?.let {
            //咨询状态 0: 待回复 1：已回复 2：已结束 3：已关闭，4：已取消，5：待提问
            when (it.status) {
                2, 3, 4 -> {
                    tv_top_notification.setBackgroundColor(resources.getColor(R.color.b4_color))
                    tv_bottom_notification.text = getString(R.string.continue_ask_question)
                }
                else -> {
                    if (it.last_count == 0) {
                        tv_top_notification.setBackgroundColor(resources.getColor(R.color.b4_color))
                        tv_bottom_notification.text = getString(R.string.continue_ask_question)
                    } else {
                        tv_top_notification.setBackgroundColor(resources.getColor(R.color.b5_color))
                        tv_bottom_notification.text = "追问 (剩余${it.last_count}机会)"
                    }
                }
            }
            tv_top_notification.text = it.remind_description
            tv_top_notification.visibility = View.VISIBLE
            tv_bottom_notification.visibility = View.VISIBLE
            it.doctor?.let {
                this.mAdapter.setDoctor(it)
            }

            it.user?.let {
                this.mAdapter.setUser(it)
            }
            this.mAdapter.resetItem(advisory.records)
        }
    }

    override fun onGetAdvisoryDetailFailed(error: String) {
        showCenterToast(error)
    }

    override fun setPresenter(presenter: RecordContract.Presenter?) {
        this.mPresenter = presenter
    }

    override fun onRefresh() {
        mPresenter.getAdvisoryDetail(mAdvisoryId)
    }

    override fun onClick(v: View?) {
        mAdvisory?.let {
            if (it.last_count == 0 || it.status == 2 || it.status == 3 || it.status == 4) {
                SdMainActivity.launch(this, 1)
            } else {
                PublishAdvisoryRecordActivity.launch(this, mAdvisory)
                finish()
            }
        }
    }

    override fun onMenuClick(v: View?) {
        SumianWebDialog.createWithPartUrl(H5Uri.ADVISORY_GUIDE).show(supportFragmentManager)
    }

    override fun onBack(v: View?) {
        finish()
    }

}