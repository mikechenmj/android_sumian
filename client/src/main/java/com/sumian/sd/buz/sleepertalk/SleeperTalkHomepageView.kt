package com.sumian.sd.buz.sleepertalk

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.common.statistic.StatUtil
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.sleepertalk.bean.SleeperTalkData
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import io.alterac.blurkit.BlurKit
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
        tv_more_article.setOnClickListener {
            SleeperTalkListActivity.launch()
            StatUtil.event(StatConstants.click_homepage_more_sumian_story_btn)
        }
        for (i in 0..5) {
            val itemView = LayoutInflater.from(context).inflate(R.layout.list_item_sleeper_talk_homepage, v_item_container, false)
            v_item_container.addView(itemView)
        }
        BlurKit.init(context)
    }

    fun queryData() {
        val call = AppManager.getSdHttpService().getSleeperTalkList(1, 5)
        call.enqueue(object : BaseSdResponseCallback<PaginationResponseV2<SleeperTalkData>>() {
            override fun onSuccess(response: PaginationResponseV2<SleeperTalkData>?) {
                val list = response?.data ?: return
                setData(SleeperTalkDataUtil.sortData(list))
            }

            override fun onFailure(errorResponse: ErrorResponse) {
            }
        })
    }

    fun setData(list: List<SleeperTalkData>) {
        v_item_container?.removeAllViews() ?: return
        v_item_container?.removeAllViews()
        for (item in list) {
            val itemView = LayoutInflater.from(context).inflate(R.layout.list_item_sleeper_talk_homepage, v_item_container, false)
            v_item_container.addView(itemView)
            itemView.setOnClickListener { SleeperTalkActivity.launch(item.id) }
            itemView.tv_title.text = item.title
            itemView.tv_title.post { itemView.iv_bg.loadUrl(item.coverUrl, itemView.tv_title.height) }
        }
    }

}