package com.sumian.sleepdoctor.cbti.adapter

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
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.holder.BaseViewHolder
import com.sumian.sleepdoctor.cbti.bean.Lesson

/**
 * Created by dq
 *
 * on 2018/7/12
 *
 * desc:课程 adapter
 */
class LessonAdapter(context: Context) : BaseRecyclerAdapter<Lesson>(context) {

    override fun onCreateDefaultViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.lay_cbti_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Lesson, position: Int) {
        (holder as ViewHolder).initView(item)
    }

    class ViewHolder(itemView: View) : BaseViewHolder<Lesson>(itemView) {

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
        override fun initView(item: Lesson) {
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