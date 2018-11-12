@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package com.sumian.sd.setting

import android.content.Intent
import android.text.InputFilter
import android.text.TextUtils
import com.sumian.common.base.BaseBackPresenterActivity
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseBackPresenterActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.common.widget.adapter.EmptyTextWatcher
import com.sumian.sd.R
import com.sumian.sd.setting.contract.FeedbackContract
import com.sumian.sd.setting.presenter.FeedbackPresenter
import kotlinx.android.synthetic.main.activity_main_feedback.*
import java.util.*

/**
 * Created by sm
 * on 2018/3/22.
 * desc:
 */

class FeedbackActivity : BaseBackPresenterActivity<FeedbackContract.Presenter>(), FeedbackContract.View {

    companion object {
        private const val MAX_LENGTH = 500

        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, FeedbackActivity::class.java))
            }
        }
    }

    override fun getChildContentId(): Int {
        return R.layout.activity_main_feedback
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mPresenter = FeedbackPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.feedback)
        et_input.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_LENGTH))
        et_input.addTextChangedListener(object : EmptyTextWatcher() {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                tv_content_length.text = String.format(Locale.getDefault(), "%d%s%d", s?.length
                        ?: 0, "/", MAX_LENGTH)
                //if (s?.isEmpty()!!) {
                //    tv_content_length.visibility = View.GONE
                //  } else {
                // if (s.length >= MAX_LENGTH) {
                //     tv_content_length.setTextColor(ColorCompatUtil.getColor(this@FeedbackActivity, R.color.t4_color_day))
                // } else {
                tv_content_length.setTextColor(ColorCompatUtil.getColor(this@FeedbackActivity, R.color.full_general_color))
                // }
                //tv_content_length.visibility = View.VISIBLE
                // }
                et_input.requestLayout()
            }
        })

        val tvSubmit = findViewById<TextView>(R.id.tv_submit)
        tvSubmit.setOnClickListener { v ->
            val feedback = et_input.text.toString().trim()
            tv_error.text = null
            tv_error.visibility = View.GONE
            if (TextUtils.isEmpty(feedback)) {
                tv_error.text = getString(R.string.none_feedback_hint)
                tv_error.visibility = View.VISIBLE
                showToast(getString(R.string.none_feedback_hint))
                return@setOnClickListener
            }

            mPresenter?.feedback(feedback)
        }
    }

    override fun onFeedbackSuccess(success: String) {
        finish()
    }

    override fun onFeedbackFailed(error: String) {
        tv_error.text = error
        tv_error.visibility = View.VISIBLE
        //showCenterToast(error)
        showToast(error)
    }

    private fun showToast(error: String) {
        ToastHelper.show(error)
    }
}
