package com.sumian.sd.buz.homepage.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.qmuiteam.qmui.util.QMUISpanHelper
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.homepage.bean.CbtiChapterData
import com.sumian.sd.buz.homepage.bean.GetCbtiChaptersResponse
import kotlinx.android.synthetic.main.view_cbti_progress.view.*
import java.util.*

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
        ll_progress.visibility = if (chaptersData != null && !chaptersData.meta.isLock) View.VISIBLE else View.GONE
        ll_not_buy.visibility = if (chaptersData == null || chaptersData.meta.isLock) View.VISIBLE else View.GONE
        if (chaptersData != null) {
            tv_cbti_title.text = resources.getString(if (AppManager.getAccountViewModel().isControlGroup()) R.string.sleep_health_education else R.string.cbti_ing)
            tv_cbti_subtitle.text = resources.getString(if (chaptersData.meta.allFinished) R.string.cbti_ing_all_finished_desc else R.string.cbti_ing_describe)
            val dataList = chaptersData.data
            updateProgressViewList(dataList)
            tv_progress.text = if (chaptersData.meta.allFinished) resources.getString(R.string.is_finished) else chaptersData.meta.currentStatus

            val oldBmp = BitmapFactory.decodeResource(resources, R.drawable.ic_home_people)
            val newBmp = Bitmap.createScaledBitmap(oldBmp, resources.getDimensionPixelOffset(R.dimen.space_16), resources.getDimensionPixelOffset(R.dimen.space_12), true)
            oldBmp.recycle()
            val bitmapDrawable = BitmapDrawable(resources, newBmp)
            val text = String.format(Locale.getDefault(), "%s%s", if (chaptersData.meta.joinedCount <= 0) "--" else String.format(Locale.getDefault(), "%d", chaptersData.meta.joinedCount), resources.getString(R.string.join))
            //drawable.setBounds(0, 0, resources.getDimensionPixelOffset(R.dimen.space_22), resources.getDimensionPixelOffset(R.dimen.space_18))
            tv_cbti_lesson_people_count.text = QMUISpanHelper.generateSideIconText(true, resources.getDimensionPixelOffset(R.dimen.space_5), text, bitmapDrawable)
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