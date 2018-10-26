package com.sumian.sd.anxiousandbelief

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseBackActivity
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.widget.adapter.EmptyTextWatcher
import com.sumian.sd.R
import com.sumian.sd.anxiousandbelief.bean.AnxietyData
import com.sumian.sd.anxiousandbelief.event.AnxietyChangeEvent
import com.sumian.sd.app.AppManager
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.activity_anxiety.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/26 13:52
 * desc   :
 * version: 1.0
 */
class AnxietyActivity : BaseBackActivity() {
    private var mAnxietyData: AnxietyData? = null

    override fun getChildContentId(): Int {
        return R.layout.activity_anxiety
    }

    companion object {
        private const val KEY_ANXIETY = "anxiety"
        fun launch(anxiety: AnxietyData? = null) {
            val intent = Intent(ActivityUtils.getTopActivity(), AnxietyActivity::class.java)
            intent.putExtra(KEY_ANXIETY, anxiety)
            ActivityUtils.startActivity(intent)
        }
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        mAnxietyData = bundle.getParcelable(KEY_ANXIETY)
    }

    @SuppressLint("SetTextI18n")
    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.add_anxious)
        et_anxiety.addTextChangedListener(object : EmptyTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                tv_anxiety_text_count.text = "${et_anxiety.text.toString().length}/200"
            }
        })
        et_anxiety_solution.addTextChangedListener(object : EmptyTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                tv_anxiety_solution_text_count.text = "${et_anxiety_solution.text.toString().length}/200"
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
        call.enqueue(object : BaseSdResponseCallback<AnxietyData>() {
            override fun onSuccess(response: AnxietyData?) {
                EventBusUtil.postStickyEvent(AnxietyChangeEvent(response!!))
                finish()
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
    }

    private fun checkEt(text: String): Boolean {
        return if (TextUtils.isEmpty(text)) {
            ToastUtils.showShort("请先完成题目内容")
            false
        } else {
            true
        }
    }
}