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
import com.sumian.sleepdoctor.base.holder.BaseViewHolder
import com.sumian.sleepdoctor.cbti.bean.Lesson

@Suppress("DEPRECATION")
/**
 * Created by dq
 *
 * on 2018/7/17
 *
 * desc:
 */
class LessonListAdapter(context: Context) : BaseRecyclerAdapter<Lesson>(context) {

    override fun onCreateDefaultViewHolder(parent: ViewGroup?, type: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.lay_item_cbti_lesson_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, item: Lesson, position: Int) {
        (holder as ViewHolder).initView(item)
    }


    class ViewHolder(itemView: View) : BaseViewHolder<Lesson>(itemView) {

        @BindView(R.id.tv_title)
        lateinit var mTvTitle: TextView

        override fun initView(item: Lesson) {
            super.initView(item)

            mTvTitle.run {
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

        override fun onItemClick(v: View?) {
            super.onItemClick(v)

        }

    }
}