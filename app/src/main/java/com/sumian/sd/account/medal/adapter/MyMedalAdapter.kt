package com.sumian.sd.account.medal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.image.loadImage
import com.sumian.sd.R
import com.sumian.sd.account.medal.bean.AchievementX
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.lay_item_my_medal.*

/**
 * Created by jzz
 *
 * on 2019/1/22
 *
 * desc: 我的勋章 adapter
 */
class MyMedalAdapter(context: Context) : BaseRecyclerAdapter<AchievementX>(context) {

    override fun onCreateDefaultViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.lay_item_my_medal, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: AchievementX, position: Int) {
        (holder as ViewHolder).initView(item)
    }


    inner class ViewHolder(itemView: View, override val containerView: View? = itemView) : RecyclerView.ViewHolder(itemView), LayoutContainer {

        fun initView(item: AchievementX) {
            val iconUrl = if (item.isHave()) {
                item.gainMedalPicture
            } else {
                item.notGainMedalPicture
            }
            iv_medal.loadImage(iconUrl, R.drawable.ic_popups_days, R.drawable.ic_popups_days)
            tv_medal.text = item.title
        }

    }
}