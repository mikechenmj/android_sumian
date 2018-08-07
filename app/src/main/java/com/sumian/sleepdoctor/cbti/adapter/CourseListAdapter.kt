package com.sumian.sleepdoctor.cbti.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.holder.SdBaseViewHolder
import com.sumian.sleepdoctor.cbti.bean.Course

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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, item: Course, position: Int) {
        (holder as ViewHolder).initView(item)
    }

    class ViewHolder(itemView: View) : SdBaseViewHolder<Course>(itemView) {

        @BindView(R.id.tv_title)
        lateinit var mTvTitle: TextView

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