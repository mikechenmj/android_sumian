package com.sumian.sddoctor.service.advisory.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sumian.common.base.BaseViewModelFragment
import com.sumian.common.helper.ToastHelper
import com.sumian.common.widget.voice.VoicePlayer
import com.sumian.sddoctor.R
import com.sumian.sddoctor.service.advisory.adapter.RecordAdapter
import com.sumian.sddoctor.service.advisory.bean.Advisory
import com.sumian.sddoctor.service.advisory.contract.RecordContract
import com.sumian.sddoctor.service.advisory.presenter.RecordPresenter
import com.sumian.sddoctor.service.publish.activity.ReplyDocActivity
import com.sumian.sddoctor.service.publish.activity.ReplyVoiceActivity
import com.sumian.sddoctor.service.publish.bean.Publish
import kotlinx.android.synthetic.main.fragment_main_advisory_detail.*

/**
 *
 *Created by sm
 * on 2018/6/4 18:28
 * desc:咨询详情,包含了提问或者回复的记录列表(语音,文字,图片...),在线报告列表
 **/
class AdvisoryDetailFragment : BaseViewModelFragment<RecordPresenter>(), RecordContract.View, View.OnClickListener {

    companion object {

        private const val ARGS_ADVISORY_ID = "com.sumian.app.extras.advisory.id"

        @JvmStatic
        fun newInstance(advisoryId: Int): Fragment {
            return AdvisoryDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARGS_ADVISORY_ID, advisoryId)
                }
            }
        }
    }

    private var mAdvisoryId: Int = 0

    private val mAdapter: RecordAdapter by lazy {
        RecordAdapter(activity!!).registerMediaPlayer(mMediaPlayer)
    }

    private val mMediaPlayer: VoicePlayer by lazy {
        VoicePlayer()
    }

    private var mAdvisory: Advisory? = null

    override fun initBundle(bundle: Bundle) {
        this.mAdvisoryId = bundle.getInt(ARGS_ADVISORY_ID, 0)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_advisory_detail
    }

    override fun onInitWidgetBefore() {
        super.onInitWidgetBefore()
        this.mViewModel = RecordPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.itemAnimator = null
        recycler.adapter = mAdapter
        tv_voice_reply.setOnClickListener(this)
        tv_doc_reply.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        mViewModel?.getAdvisoryDetail(mAdvisoryId)
    }

    override fun onStop() {
        super.onStop()
        mMediaPlayer.pause()
    }

    override fun showLoading() {
        //super.showLoading()
    }

    override fun dismissLoading() {
        //super.dismissLoading()
    }

    override fun onRelease() {
        super.onRelease()
        mMediaPlayer.release()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    override fun onGetAdvisoryDetailSuccess(advisory: Advisory) {
        this.mAdvisory = advisory
        mAdvisory?.let { it ->
            //咨询状态 0: 待回复 1：已回复 2：已结束 3：已关闭，4：已取消，5：待提问
            when (it.status) {
                2, 3, 4 -> {
                    lay_record_container.visibility = View.GONE
                }
                1 -> {
                    lay_record_container.visibility = View.VISIBLE
                }
                0 -> {
                    lay_record_container.visibility = View.VISIBLE
                }
                else -> {
                    lay_record_container.visibility = View.GONE
                }
            }
            this.mAdapter.setDoctor(it.traceable.doctor)

            this.mAdapter.setUser(it.traceable.user)
            this.mAdapter.resetItem(advisory.traceable.records)
        }
    }

    override fun onGetAdvisoryDetailFailed(error: String) {
        showCenterToast(error)
    }

    private fun showCenterToast(error: String) {
        ToastHelper.show(activity, error, Gravity.CENTER)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_voice_reply -> {
                ReplyVoiceActivity.show(mAdvisoryId, Publish.PUBLISH_ADVISORY_TYPE)
            }
            R.id.tv_doc_reply -> {
                ReplyDocActivity.show(mAdvisoryId, Publish.PUBLISH_ADVISORY_TYPE)
            }
        }
    }

}