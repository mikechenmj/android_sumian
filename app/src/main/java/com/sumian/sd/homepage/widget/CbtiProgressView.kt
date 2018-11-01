package com.sumian.sd.homepage.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.sumian.sd.R
import com.sumian.sd.homepage.bean.CbtiChapterData
import com.sumian.sd.homepage.bean.GetCbtiChaptersResponse
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

    fun setData(chaptersData: GetCbtiChaptersResponse?) {
        ll_progress.visibility = if (chaptersData != null) View.VISIBLE else View.GONE
        ll_not_buy.visibility = if (chaptersData == null) View.VISIBLE else View.GONE
        if (chaptersData != null) {
            tv_cbti_subtitle.text = resources.getString(if (chaptersData.meta.allFinished) R.string.cbti_ing_all_finished_desc else R.string.cbti_ing_describe)
            val dataList = chaptersData.data
            updateProgressViewList(dataList)

            tv_progress.text = if (chaptersData.meta.allFinished) resources.getString(R.string.is_finished) else chaptersData.meta.currentStatus
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
        ll_not_buy.setOnClickListener(onClickListener)
        ll_progress.setOnClickListener(onClickListener)
    }
}