package com.sumian.sd.service.cbti.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.sd.R
import com.sumian.sd.base.holder.SdBaseViewHolder
import com.sumian.sd.service.cbti.bean.Course

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

    class ViewHolder(itemView: View) : SdBaseViewHolder<Course>(itemView) {

        private val mTvTitle: TextView  by lazy {
            itemView.findViewById<TextView>(R.id.tv_title)
        }

        override fun initView(item: Course) {
            super.initView(item)

            mTvTitle.run {
                isActivated = if (item.current_course) {
                    setTextColor(resources.getColor(R.color.b3_color))
                    true
                } else {
                    if (item.is_lock) {
                        setTextColor(resources.getColor(R.color.t2_alpha_40_color))
                    } else {
                        setTextColor(resources.getColor(R.color.t2_color))
                    }
                    false
                }
                text = item.title
            }
        }
    }
}