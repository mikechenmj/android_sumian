package com.sumian.sleepdoctor.homepage.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.homepage.bean.CbtiChapterData
import com.sumian.sleepdoctor.homepage.bean.GetCbtiChaptersResponse
import com.sumian.sleepdoctor.utils.getString
import kotlinx.android.synthetic.main.view_cbti_progress.view.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/11 14:43
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class CbtiProgressView(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {
    private val mContext = context

    init {
        LayoutInflater.from(context).inflate(R.layout.view_cbti_progress, this, true)
    }

    fun initData(chaptersData: GetCbtiChaptersResponse?) {
        if (chaptersData == null) {
            return
        }
        val dataList = chaptersData.data
        updateProgressViewList(dataList)
        for ((index, data) in dataList.withIndex()) {
            if (data.chapterProgress != 100) {
                tv_progress.text = mContext.getString(R.string.cbti_chapters_progress, index + 1, data.chapterProgress)
                break
            }
        }
    }

    private fun updateProgressViewList(dataList: List<CbtiChapterData>) {
        ll_progress_view_container.removeAllViews()
        for ((index, data) in dataList.withIndex()) {
            val progressView = createWithLineProgressView(index == 0)
            progressView.setProgress(data.chapterProgress)
            ll_progress_view_container.addView(progressView)
        }
    }

    private fun createWithLineProgressView(isFirst: Boolean): WithLineProgressView {
        val progressView = WithLineProgressView(mContext, null)
        val layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT)
        progressView.layoutParams = layoutParams
        layoutParams.width = if (isFirst) LayoutParams.WRAP_CONTENT else 0
        layoutParams.weight = if (isFirst) 0f else 1.0f
        progressView.showLeftLine(!isFirst)
        return progressView
    }

    fun setOnEnterLearnBtnClickListener(onClickListener: OnClickListener) {
        tv_enter_learn.setOnClickListener(onClickListener)
    }
}