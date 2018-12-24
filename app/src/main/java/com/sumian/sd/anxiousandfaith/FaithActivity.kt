package com.sumian.sd.anxiousandfaith

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.mvp.IPresenter
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.widget.adapter.EmptyTextWatcher
import com.sumian.sd.R
import com.sumian.sd.anxiousandfaith.bean.FaithData
import com.sumian.sd.anxiousandfaith.event.FaithChangeEvent
import com.sumian.sd.app.AppManager
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.activity_faith.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/26 17:38
 * desc   :
 * version: 1.0
 */
@SuppressLint("SetTextI18n")
class FaithActivity : BasePresenterActivity<IPresenter>() {
    private var mProgress = 0
    private var mEvent = ""
    private var mThought = ""
    private var mEmotion = -1
    private var mId = -1
    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_faith
    }

    companion object {
        private const val KEY_FAITH_DATA = "faith_data"

        fun launch(faithData: FaithData? = null) {
            val intent = Intent(ActivityUtils.getTopActivity(), FaithActivity::class.java)
            intent.putExtra(KEY_FAITH_DATA, faithData)
            ActivityUtils.startActivity(intent)
        }
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        val faithData = bundle.getParcelable<FaithData>(KEY_FAITH_DATA)
        if (faithData != null) {
            mId = faithData.id
            mEvent = faithData.scene
            mThought = faithData.idea
            mEmotion = faithData.emotion_type
        }
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.add_belief)
        progress_view.setTvs(R.string.event, R.string.thought, R.string.emotion)
        bt_next_step.setOnClickListener { onNextClick() }
        et_belief.addTextChangedListener(object : EmptyTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                val length = et_belief.text.toString().length
                tv_belief_text_count.text = "$length/200"
                tv_belief_text_count.setTextColor(resources.getColor(if (length > 200) R.color.t4_color else R.color.t1_color))
                when (mProgress) {
                    0 -> mEvent = getEtInput()
                    1 -> mThought = getEtInput()
                }
            }
        })
        et_belief.setText(mEvent)
        emotion_view.setSelectedEmotion(mEmotion)
    }

    private fun updateUIByProgress(progress: Int) {
        progress_view.setProgress(progress)
        et_belief.visibility = if (progress < 2) View.VISIBLE else View.GONE
        tv_belief_text_count.visibility = if (progress < 2) View.VISIBLE else View.GONE
        vg_emotion.visibility = if (progress == 2) View.VISIBLE else View.GONE
        et_belief.setText(if (progress == 0) mEvent else mThought)
        bt_next_step.text = getText(if (progress == 2) R.string.save else R.string.next_step)
        tv_progress_title.text = getString(when (progress) {
            0 -> R.string.belief_title_0
            1 -> R.string.belief_title_1
            else -> R.string.belief_title_2
        })
    }

    private fun onNextClick() {
        when (mProgress) {
            0 -> if (checkInput(mEvent)) updateUIByProgress(++mProgress)
            1 -> if (checkInput(mThought)) updateUIByProgress(++mProgress)
            2 -> {
                mEmotion = emotion_view.getSelectedEmotion()
                if (mEmotion == -1) {
                    ToastUtils.showShort(R.string.please_finish_question_first)
                    return
                }
                addOrUpdateBelief()
            }
        }
    }

    private fun checkInput(text: String): Boolean {
        return if (TextUtils.isEmpty(text)) {
            ToastUtils.showShort(R.string.please_finish_question_first)
            false
        } else if (text.length > 200) {
            ToastUtils.showShort(R.string.input_is_too_long)
            false
        } else {
            true
        }
    }

    private fun getEtInput(): String {
        return et_belief.text.toString()
    }

    private fun addOrUpdateBelief() {
        val call = if (mId == -1) {
            AppManager.getSdHttpService().addFaiths(mEvent, mThought, mEmotion)
        } else {
            AppManager.getSdHttpService().updateFaiths(mId, mEvent, mThought, mEmotion)
        }
        addCall(call)
        bt_next_step.isEnabled = false
        call.enqueue(object : BaseSdResponseCallback<FaithData>() {
            override fun onSuccess(response: FaithData?) {
                EventBusUtil.postStickyEvent(FaithChangeEvent(response!!))
                finish()
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
                bt_next_step.isEnabled = true
            }
        })
    }

    private fun preStep() {
        if (mProgress > 0) {
            mProgress--
            updateUIByProgress(mProgress)
        }
    }

    override fun onBackPressed() {
        if (mProgress > 0) {
            preStep()
        } else {
            super.onBackPressed()
        }
    }
}