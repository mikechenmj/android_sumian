package com.sumian.sddoctor.service.cbti.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.sddoctor.R
import com.sumian.sddoctor.service.cbti.bean.Course
import com.sumian.sddoctor.service.cbti.holder.SdBaseViewHolder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.lay_cbti_item_lesson.*
import java.util.*

/**
 * Created by dq
 *
 * on 2018/7/12
 *
 * desc:课程 adapter
 */
class CourseAdapter(context: Context) : BaseRecyclerAdapter<Course>(context) {

    override fun onCreateDefaultViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.lay_cbti_item_lesson, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Course, position: Int) {
        (holder as ViewHolder).initView(item)
    }

    class ViewHolder(itemView: View, override val containerView: View? = itemView) :
            SdBaseViewHolder<Course>(itemView), LayoutContainer {

        @SuppressLint("SetTextI18n")
        override fun initView(item: Course) {
            super.initView(item)
            tv_title.text = item.title
            val min = item.duration / 60
            val second = item.duration % 60
            tv_duration.text = String.format(Locale.getDefault(), "%d%s%02d", min, ":", second)
            val progress = if (item.regardAsDone) 100 else 0
            cbti_progress_view.setProgress(progress, false)
            if (progress == 0) {
                cbti_progress_view.visibility = View.INVISIBLE
                tv_progress.text = "未完成"
                tv_progress.visibility = View.INVISIBLE
                v_divider_line.visibility = View.GONE
            } else {
                cbti_progress_view.visibility = View.VISIBLE
                tv_progress.text = "已完成"
                tv_progress.visibility = View.VISIBLE
                v_divider_line.visibility = View.VISIBLE
            }
        }
    }
}