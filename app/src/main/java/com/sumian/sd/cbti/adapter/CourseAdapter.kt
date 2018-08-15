package com.sumian.sd.cbti.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.sd.R
import com.sumian.sd.base.holder.SdBaseViewHolder
import com.sumian.sd.cbti.bean.Course

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

        @BindView(R.id.tv_title)
        lateinit var mTvTitle: TextView
        @BindView(R.id.tv_duration)
        lateinit var mTvDuration: TextView

        @BindView(R.id.iv_lock)
        lateinit var mIvLock: ImageView
        @BindView(R.id.bt_learn)
        lateinit var mTvLearn: TextView

        @BindView(R.id.v_divider)
        lateinit var mDivider: View

        @SuppressLint("SetTextI18n")
        override fun initView(item: Course) {
            super.initView(item)

            mTvTitle.text = item.title

            val min = item.duration / 60

            val second = item.duration % 60

            mTvDuration.text = "$min’$second”"

            Gone(!item.is_lock, mIvLock)

            mTvLearn.run {
                isActivated = !item.regard_as_done
                text = if (!item.regard_as_done) getText(R.string.learn) else getText(R.string.review)
                Gone(item.is_lock, this@run)
            }

        }

    }

}