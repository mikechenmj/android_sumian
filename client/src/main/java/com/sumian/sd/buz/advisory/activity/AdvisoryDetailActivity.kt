package com.sumian.sd.buz.advisory.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.common.widget.voice.VoicePlayer
import com.sumian.sd.R
import com.sumian.sd.buz.advisory.adapter.RecordAdapter
import com.sumian.sd.buz.advisory.bean.Advisory
import com.sumian.sd.buz.advisory.presenter.RecordPresenter
import com.sumian.sd.common.h5.H5Uri
import com.sumian.sd.main.MainActivity
import com.sumian.sd.widget.TitleBar
import com.sumian.sd.widget.dialog.SumianWebDialog
import kotlinx.android.synthetic.main.activity_main_advisory_detail.*

/**
 *
 *Created by sm
 * on 2018/6/4 18:28
 * desc:咨询详情,包含了提问或者回复的记录列表,在线报告列表
 **/
class AdvisoryDetailActivity : BaseViewModelActivity<RecordPresenter>(), TitleBar.OnBackClickListener, TitleBar.OnMenuClickListener, View.OnClickListener {

    companion object {

        private const val ARGS_ADVISORY_ID = "com.sumian.app.extras.advisory.id"

        @JvmStatic
        fun show(context: Context, advisoryId: Int) {
            context.startActivity(getLaunchIntent(context, advisoryId))
        }

        @JvmStatic
        fun getLaunchIntent(context: Context, advisoryId: Int): Intent {
            val intent = Intent(context, AdvisoryDetailActivity::class.java)
            intent.putExtra(ARGS_ADVISORY_ID, advisoryId)
            return intent
        }
    }

    private var mAdvisoryId: Int = 0

    private val mAdapter: RecordAdapter by lazy {
        RecordAdapter(this).registerMediaPlayer(mMediaPlayer)
    }

    private var mAdvisory: Advisory? = null

    private val mMediaPlayer: VoicePlayer by lazy {
        VoicePlayer()
    }

    override fun initBundle(bundle: Bundle) {

        this.mAdvisoryId = bundle.getInt(ARGS_ADVISORY_ID, 0)!!

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_advisory_detail
    }

    override fun initWidget() {
        super.initWidget()
        RecordPresenter.init(this)
        title_bar.setOnBackClickListener(this)
        title_bar.setOnMenuClickListener(this)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.itemAnimator = null //避免 update 数据 item 闪烁
        recycler.adapter = mAdapter
        tv_bottom_notification.setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
        mViewModel?.getAdvisoryDetail(mAdvisoryId)
    }

    override fun onStop() {
        super.onStop()
        mMediaPlayer.pause()
    }

    override fun onRelease() {
        super.onRelease()
        mMediaPlayer.release()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    fun onGetAdvisoryDetailSuccess(advisory: Advisory) {
        this.mAdvisory = advisory
        if (mAdvisory == null) {
            empty_error_view.visibility = View.VISIBLE
        } else {

            //咨询状态 0: 待回复 1：已回复 2：已结束 3：已关闭，4：已取消，5：待提问
            when (advisory.status) {
                2, 3, 4 -> {
                    tv_top_notification.setBackgroundColor(resources.getColor(R.color.b4_color))
                    tv_bottom_notification.text = getString(R.string.continue_ask_question)
                }
                else -> {
                    if (advisory.last_count == 0) {
                        tv_top_notification.setBackgroundColor(resources.getColor(R.color.b4_color))
                        tv_bottom_notification.text = getString(R.string.continue_ask_question)
                    } else {
                        tv_top_notification.setBackgroundColor(resources.getColor(R.color.b5_color))
                        tv_bottom_notification.text = "追问 (剩余${advisory.last_count}机会)"
                    }
                }
            }
            tv_top_notification.text = advisory.remind_description
            tv_top_notification.visibility = View.VISIBLE
            tv_bottom_notification.visibility = View.VISIBLE
            advisory.doctor?.let {
                this.mAdapter.setDoctor(it)
            }
            advisory.user?.let {
                this.mAdapter.setUser(it)
            }
            this.mAdapter.resetItem(advisory.records)
            val isRecordEmpty = advisory.records.isNullOrEmpty()
            empty_error_view.visibility = if (isRecordEmpty) View.VISIBLE else View.GONE
            recycler.visibility = if (isRecordEmpty) View.GONE else View.VISIBLE

        }
    }

    fun onGetAdvisoryDetailFailed(error: String) {
        ToastUtils.showShort(error)
        empty_error_view.visibility = if (mAdvisory == null) View.VISIBLE else View.GONE
    }

    fun setPresenter(presenter: RecordPresenter?) {
        this.mViewModel = presenter
    }

    override fun onClick(v: View) {
        mAdvisory?.let {
            if (it.last_count == 0 || it.status == 2 || it.status == 3 || it.status == 4) {
                MainActivity.launch(MainActivity.TAB_2)
            } else {
                PublishAdvisoryRecordActivity.show(this, mAdvisory?.id!!, true)
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