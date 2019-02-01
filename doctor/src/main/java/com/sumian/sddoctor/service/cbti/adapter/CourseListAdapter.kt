package com.sumian.sddoctor.service.cbti.adapter

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
import kotlinx.android.synthetic.main.lay_item_cbti_lesson_item.*

@Suppress("DEPRECATION")
/**
 * Created by dq
 *
 * on 2018/7/17
 *
 * desc:  横向滑动,选择的 course list adapter
 */
class CourseListAdapter(context: Context) : BaseRecyclerAdapter<Course>(context) {

    override fun onCreateDefaultViewHolder(parent: ViewGroup?, type: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.lay_item_cbti_lesson_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Course, position: Int) {
        (holder as ViewHolder).initView(item)
    }

    class ViewHolder(itemView: View, override val containerView: View? = itemView) :
            SdBaseViewHolder<Course>(itemView), LayoutContainer {

        override fun initView(item: Course) {
            super.initView(item)
            tv_title.run {
                isActivated = if (item.current_course) {
                    setTextColor(resources.getColor(R.color.b3_color))
                    true
                } else {
                    setTextColor(resources.getColor(R.color.t2_color))
                    false
                }
                text = item.title
            }
        }
    }
}