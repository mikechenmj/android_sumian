package com.sumian.sd.service.cbti.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.R
import com.sumian.sd.service.cbti.bean.Questionnaire
import kotlinx.android.synthetic.main.lay_dialog_cbti_questionnaires.*

@Suppress("DEPRECATION")
/**
 * CBTI 调查问卷 弹窗
 */
@SuppressLint("InflateParams")
class CBTIQuestionDialog(context: Context) : AppCompatDialog(context, R.style.SumianDialog) {

    private var onSubmitQuestionCallback: OnSubmitQuestionCallback? = null
    private var mSelectPosition = -1
    private lateinit var mCurrentQuestionnaire: Questionnaire

    init {
        val rootView = LayoutInflater.from(context).inflate(R.layout.lay_dialog_cbti_questionnaires, null, false)
        setContentView(rootView)
        tv_submit.setOnClickListener {
            if (mSelectPosition == -1) {
                ToastHelper.show(it.context, "请选择您的上周睡眠限制执行情况")
                return@setOnClickListener
            }
            onSubmitQuestionCallback?.submitQuestionCallback(mSelectPosition)
        }
        setCancelable(false)
    }

    fun setOnSubmitQuestionCallback(onSubmitQuestionCallback: OnSubmitQuestionCallback): CBTIQuestionDialog {
        this.onSubmitQuestionCallback = onSubmitQuestionCallback
        return this
    }

    fun setQuestionnaire(questionnaire: Questionnaire): CBTIQuestionDialog {
        this.mCurrentQuestionnaire = questionnaire
        tv_title.text = questionnaire.question
        updateItem(-1, questionnaire)
        return this
    }

    fun updateQuestionResult() {
        val questionSelection = mCurrentQuestionnaire.selection[mSelectPosition]
        tv_title.text = questionSelection
        tv_submit.setText(R.string.fine)
        tv_submit.setOnClickListener {
            if (onSubmitQuestionCallback?.dismissQuestionDialog()!!) {
                doCancel()
            }
        }
        flow_layout.removeAllViewsInLayout()
        val tvSelect = TextView(context)
        val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val margin = context.resources.getDimensionPixelOffset(R.dimen.space_16)
        layoutParams.setMargins(margin, margin, margin, margin)
        tvSelect.layoutParams = layoutParams
        tvSelect.background = null
        tvSelect.textSize = 14.0f
        tvSelect.setLineSpacing(6.0f, 1.0f)
        tvSelect.setTextColor(tvSelect.resources.getColor(R.color.t2_color))
        val explanation = mCurrentQuestionnaire.explanations[mSelectPosition]
        tvSelect.text = explanation
        flow_layout.setHorizontalSpacing(0.0f)
        flow_layout.setVerticalSpacing(0.0f)
        flow_layout.addView(tvSelect)
    }

    private fun updateItem(position: Int, questionnaire: Questionnaire) {
        mSelectPosition = position
        flow_layout.removeAllViewsInLayout()
        questionnaire.selection.forEachIndexed { index, text ->
            val itemView = LayoutInflater.from(context).inflate(R.layout.lay_cbti_question_item, flow_layout, false)
            val tvSelect = itemView.findViewById<TextView>(R.id.tv_select)
            tvSelect.text = text
            tvSelect.tag = index
            tvSelect.isActivated = position == index
            if (position == index) {
                tvSelect.setTextColor(tvSelect.resources.getColor(R.color.b2_color))
            } else {
                tvSelect.setTextColor(tvSelect.resources.getColor(R.color.t2_color))
            }
            tvSelect.setOnClickListener {
                var tmpPosition: Int = it.tag as Int
                if (tmpPosition == mSelectPosition) {
                    tmpPosition = -1
                }
                updateItem(tmpPosition, questionnaire)
            }
            flow_layout.addView(itemView)
        }
    }

    private fun doCancel() {
        if (isShowing) {
            cancel()
        }
    }

    interface OnSubmitQuestionCallback {
        fun submitQuestionCallback(position: Int)
        fun dismissQuestionDialog(): Boolean
    }

}