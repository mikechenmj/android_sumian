package com.sumian.sddoctor.service.publish.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.service.publish.bean.Publish
import com.sumian.sddoctor.service.publish.contract.PublishDocContract
import com.sumian.sddoctor.service.publish.presenter.PublishDocPresenter
import com.sumian.sddoctor.widget.adapter.TextWatcherAdapter
import kotlinx.android.synthetic.main.activity_main_reply_publish_doc.*

/**
 * Created by dq
 *
 * on 2018/8/29
 *
 * desc: 医生图文咨询/周日记评估  文本回复
 */
class ReplyDocActivity : SddBaseActivity<PublishDocContract.Presenter>(), PublishDocContract.View, View.OnClickListener {

    companion object {

        private const val EXTRAS_ADVISORY_ID = "com.sumian.sddoctor.extras.publish.id"

        fun show(publishId: Int, publishType: Int) {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, ReplyDocActivity::class.java).apply {
                    putExtra(EXTRAS_ADVISORY_ID, publishId)
                    putExtra(Publish.EXTRAS_PUBLISH_TYPE, publishType)
                })
            }
        }

    }

    private var mPublishId: Int = 0
    private var mPublishType: Int = Publish.PUBLISH_ADVISORY_TYPE


    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_reply_publish_doc
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.mPublishId = bundle.getInt(Publish.EXTRAS_PUBLISH_ID, 0)
        this.mPublishType = bundle.getInt(Publish.EXTRAS_PUBLISH_TYPE, Publish.PUBLISH_ADVISORY_TYPE)
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mPresenter = PublishDocPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.doc_reply)
        et_input.filters = arrayOf(InputFilter.LengthFilter(500))
        et_input.addTextChangedListener(object : TextWatcherAdapter() {

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                tv_content_count.text = "${s?.length ?: 0}/500"
            }
        })
        btn_send.setOnClickListener(this)
    }

    override fun onClick(v: View) {

        val input = et_input.text.toString().trim()

        if (TextUtils.isEmpty(input)) {
            ToastUtils.showShort("请输入您的回复内容")
            return
        }

        if (input.length < 10) {
            ToastUtils.showShort("您的回复少于10个字")
            return
        }

        mPresenter?.publishDoc(publishType = mPublishType, publishId = mPublishId, content = input)
    }

    override fun onPublishSuccess(success: String) {
        ToastUtils.showShort(success)
        finish()
    }

    override fun onPublishFailed(error: String) {
        ToastUtils.showShort(error)
    }
}