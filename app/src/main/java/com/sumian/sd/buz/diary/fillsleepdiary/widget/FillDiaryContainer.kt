package com.sumian.sd.buz.diary.fillsleepdiary.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.blankj.utilcode.util.KeyboardUtils
import com.sumian.sd.R
import kotlinx.android.synthetic.main.view_fill_diary_container.view.*


/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/12 16:04
 * desc   :
 * version: 1.0
 */
class  FillDiaryContainer(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_fill_diary_container, this, true)
        vg_root.setOnClickListener { KeyboardUtils.hideSoftInput(this) }
    }

    fun setContentView(layout: Int) {
        LayoutInflater.from(context).inflate(layout, vg_fill_diary_content, true)
    }

    fun setTitle(title: String) {
        tv_fill_diary_title.text = title
    }

    @SuppressLint("SetTextI18n")
    fun setProgress(cur: Int, total: Int = 9) {
        tv_fill_diary_progress.text = "${cur + 1}/$total"
        iv_fill_diary_pre.visibility = if (cur == 0) View.GONE else View.VISIBLE
        iv_fill_diary_next.visibility = if (cur == total - 1) View.GONE else View.VISIBLE
        tv_fill_diary_progress.visibility = if (cur == total - 1) View.GONE else View.VISIBLE
        bt_fill_diary_complete.visibility = if (cur == total - 1) View.VISIBLE else View.GONE
        tv_fill_diary_title.text = context.resources.getStringArray(R.array.fill_diary_title)[cur]
    }
}