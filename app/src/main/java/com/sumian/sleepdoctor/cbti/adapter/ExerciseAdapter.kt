package com.sumian.sleepdoctor.cbti.adapter

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
import com.sumian.sleepdoctor.base.holder.SdBaseViewHolder
import com.sumian.sleepdoctor.cbti.bean.Exercise

/**
 * Created by dq
 *
 * on 2018/7/13
 *
 * desc:
 */
class ExerciseAdapter(context: Context) : BaseRecyclerAdapter<Exercise>(context) {

    override fun onCreateDefaultViewHolder(parent: ViewGroup?, type: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.lay_cbti_item_practice, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, item: Exercise, position: Int) {
        (holder as ViewHolder).initView(item)
    }

    class ViewHolder(itemView: View) : SdBaseViewHolder<Exercise>(itemView) {

        @BindView(R.id.tv_title)
        lateinit var mTvTitle: TextView

        @BindView(R.id.iv_lock)
        lateinit var mIvLock: ImageView

        @BindView(R.id.v_divider)
        lateinit var mDivider: View

        override fun initView(item: Exercise) {
            super.initView(item)

            mTvTitle.run {
                text = item.title
            }

            mIvLock.run {
                visibility = if (item.is_lock) {
                    setImageResource(R.mipmap.ic_cbti_icon_exercise_lock)
                    View.VISIBLE
                } else {
                    if (item.done) {
                        setImageResource(R.mipmap.ic_cbti_icon_exercise_complete)
                        View.VISIBLE
                    } else {
                        View.INVISIBLE
                    }
                }
            }
        }
    }

}