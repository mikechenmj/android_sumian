package com.sumian.sd.service.cbti.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.sd.R
import com.sumian.sd.base.holder.SdBaseViewHolder
import com.sumian.sd.service.cbti.bean.Course
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

    class ViewHolder(itemView: View) : SdBaseViewHolder<Course>(itemView) {

        private val mTvTitle: TextView  by lazy {
            itemView.findViewById<TextView>(R.id.tv_title)
        }

        private val mTvDuration: TextView by lazy {
            itemView.findViewById<TextView>(R.id.tv_duration)
        }

        private val mIvLock: ImageView by lazy {
            itemView.findViewById<ImageView>(R.id.iv_lock)
        }
        private val mTvLearn: TextView  by lazy {
            itemView.findViewById<TextView>(R.id.bt_learn)
        }

        private val mDivider: View  by lazy {
            itemView.findViewById<View>(R.id.v_divider)
        }

        @SuppressLint("SetTextI18n")
        override fun initView(item: Course) {
            super.initView(item)

            mTvTitle.text = item.title

            val min = item.duration / 60

            val second = item.duration % 60

            mTvDuration.text = String.format(Locale.getDefault(), "%d%s%02d", min, ":", second)

            Gone(!item.is_lock, mIvLock)

            mTvLearn.run {
                isActivated = !item.regard_as_done
                text = if (!item.regard_as_done) getText(R.string.learn) else getText(R.string.review)
                Gone(item.is_lock, this@run)
            }

        }

    }

}