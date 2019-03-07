package com.sumian.sd.buz.sleepertalk

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.sumian.common.image.ImageLoader
import com.sumian.sd.R
import kotlinx.android.synthetic.main.list_item_sleeper_talk_homepage.view.*
import kotlinx.android.synthetic.main.view_sleeper_talk_homepage.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/7 09:12
 * desc   :
 * version: 1.0
 */
class SleeperTalkHomepageView(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet) {
    init {
        View.inflate(context, R.layout.view_sleeper_talk_homepage, this)
        tv_more_article.setOnClickListener { SleeperTalkListActivity.launch() }
    }

    fun setData(list: List<String>) {
        v_item_container.removeAllViews()
        for (s in list) {
            val itemView = View.inflate(context, R.layout.list_item_sleeper_talk_homepage, null)
            v_item_container.addView(itemView)
            itemView.setOnClickListener { }
            itemView.tv_title.text = s
            ImageLoader.loadImage(s, itemView.iv_bg)
        }
    }

}