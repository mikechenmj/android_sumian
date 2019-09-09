package com.sumian.sd.buz.anxiousandfaith

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.widget.adapter.EmptyTextWatcher
import com.sumian.sd.R
import com.sumian.sd.R.layout
import com.sumian.sd.R.string
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.anxiousandfaith.bean.AnxietyData
import com.sumian.sd.buz.anxiousandfaith.event.AnxietyChangeEvent
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.EventBusUtil
import kotlinx.android.synthetic.main.activity_anxiety.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/26 13:52
 * desc   :
 * version: 1.0
 */
class AnxietyActivity : WhileTitleNavBgActivity() {
    private var mAnxietyData: AnxietyData? = null

    override fun getLayoutId(): Int {
        return layout.activity_anxiety
    }

    companion object {
        private const val KEY_ANXIETY = "anxiety"
        fun launch(anxiety: AnxietyData? = null) {
            val intent = Intent(ActivityUtils.getTopActivity(), AnxietyActivity::class.java)
            intent.putExtra(KEY_ANXIETY, anxiety)
            ActivityUtils.startActivity(intent)
        }

        fun getLaunchIntent(): Intent {
            return Intent(ActivityUtils.getTopActivity(), AnxietyActivity::class.java)
        }
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        mAnxietyData = bundle.getParcelable(KEY_ANXIETY)
    }

    override fun getPageName(): String {
        return StatConstants.page_add_anxiety
    }

    @SuppressLint("SetTextI18n")
    override fun initWidget() {
        super.initWidget()
        setTitle(string.add_anxious)
        et_anxiety.addTextChangedListener(object : EmptyTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                val length = et_anxiety.text.toString().length
                tv_anxiety_text_count.text = "$length/200"
                tv_anxiety_text_count.setTextColor(resources.getColor(if (length > 200) R.color.t4_color else R.color.t1_color))
            }
        })
        et_anxiety_solution.addTextChangedListener(object : EmptyTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                val length = et_anxiety_solution.text.toString().length
                tv_anxiety_solution_text_count.text = "$length/200"
                tv_anxiety_solution_text_count.setTextColor(resources.getColor(if (length > 200) R.color.t4_color else R.color.t1_color))
            }
        })
        bt_save.setOnClickListener { saveAnxiety() }
        if (mAnxietyData != null) {
            et_anxiety.setText(mAnxietyData!!.anxiety)
            et_anxiety_solution.setText(mAnxietyData!!.solution)
        }
    }

    private fun saveAnxiety() {
        val anxiety = et_anxiety.text.toString()
        val anxietySolution = et_anxiety_solution.text.toString()
        if (!checkEt(anxiety) || !checkEt(anxietySolution)) {
            return
        }

        val call = if (mAnxietyData == null) {
            AppManager.getSdHttpService().addAnxiety(anxiety, anxietySolution)
        } else {
            AppManager.getSdHttpService().updateAnxiety(mAnxietyData!!.id, anxiety, anxietySolution)
        }
        addCall(call)
        bt_save.isEnabled = false
        call.enqueue(object : BaseSdResponseCallback<AnxietyData>() {
            override fun onSuccess(response: AnxietyData?) {
                EventBusUtil.postStickyEvent(AnxietyChangeEvent(response!!))
                finish()
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
                bt_save.isEnabled = true
            }
        })
    }

    private fun checkEt(text: String): Boolean {
        return if (TextUtils.isEmpty(text)) {
            ToastUtils.showShort(getString(string.please_finish_question_first))
            false
        } else if (text.length > 200) {
            ToastUtils.showShort(getString(string.input_is_too_long))
            false
        } else {
            true
        }
    }
}