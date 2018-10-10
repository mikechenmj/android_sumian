package com.sumian.sd.service.advisory.adapter

import android.content.Context
import android.os.CountDownTimer
import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseViewHolder
import com.qmuiteam.qmui.util.QMUISpanHelper
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.sd.R
import com.sumian.sd.service.advisory.bean.Advisory
import com.sumian.sd.app.App
import com.sumian.sd.service.util.TimeUtilV2
import com.sumian.sd.utils.TimeUtil
import java.util.*

@Suppress("DEPRECATION")
/**
 *
 *Created by sm
 * on 2018/6/5 13:43
 * desc:咨询列表 adapter, 包含未使用,已使用2部分
 **/
class AdvisoryListAdapter(context: Context) : BaseRecyclerAdapter<Advisory>(context) {

    override fun onCreateDefaultViewHolder(parent: ViewGroup?, type: Int): RecyclerView.ViewHolder {
        val viewHolder = ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.lay_advisory_item, parent, false))
        viewHolder.itemView.tag = viewHolder
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, item: Advisory?, position: Int) {
        (holder as ViewHolder).init(item!!, mItems.size - 1 == position)
    }

    inner class ViewHolder constructor(itemView: View) : BaseViewHolder(itemView) {

        private var mCountDownTimer: CountDownTimer? = null

        fun init(item: Advisory, isGoneDivider: Boolean) {

            //咨询状态 0: 待回复 1：已回复 2：已结束 3：已关闭，4：已取消，5：待提问
            val advisoryTitle: String = when (item.status) {
                5 -> String.format(Locale.getDefault(), "%s", item.description)
                else ->
                    item.description
            }

            setText(R.id.tv_title, advisoryTitle)
                    .setText(R.id.tv_advisory_time, if (item.start_at > 0) {
                        TimeUtilV2.formatYYYYMMDDHHMM(item.start_at)
                    } else {
                        TimeUtilV2.formatYYYYMMDDHHMM(item.created_at)
                    })
                    .setText(R.id.tv_advisory_action_status, item.formatStatus()).setVisible(R.id.tv_advisory_action_status, true)

            mCountDownTimer?.cancel()

            setGone(R.id.tv_timer, false)
                    .setVisible(R.id.divider, false)

            if (item.last_second == null) {
                setGone(R.id.tv_timer, false)
                        .setVisible(R.id.divider, true)
            } else {
                if (item.last_second!! <= 0L) {
                    setGone(R.id.tv_timer, false)
                            .setVisible(R.id.divider, true)
                } else {

                    if (item.status == 1 && item.last_second!! <= 5 * 60 * 60) {

                        this.mCountDownTimer = object : CountDownTimer(item.last_second?.toLong()!! * 1000L, 1000L) {

                            override fun onTick(millisUntilFinished: Long) {

                                val iconText = QMUISpanHelper.generateSideIconText(false, itemView.resources.getDimensionPixelOffset(R.dimen.dp_4), "该订单还有 ", itemView.resources.getDrawable(R.drawable.graphic_icon_timing))

                                val day = millisUntilFinished / (1000 * 60 * 60 * 24)
                                val hour = (millisUntilFinished - day * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
                                val minute = (millisUntilFinished - day * (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60)) / (1000 * 60)
                                val second = (millisUntilFinished - day * (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60) - minute * (1000 * 60)) / 1000

                                val formatTimer = String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, second)

                                val colorText = SpannableString(formatTimer)

                                colorText.setSpan(ForegroundColorSpan(App.getAppContext().resources.getColor(R.color.t4_color)), 0, formatTimer.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                                val timerContent = TextUtils.concat(iconText, "  ", colorText, " 过期，请及时回复。")

                                setText(R.id.tv_timer, timerContent)
                                        .setVisible(R.id.tv_timer, true)
                                        .setGone(R.id.divider, false)
                            }

                            override fun onFinish() {
                                setVisible(R.id.tv_timer, true)
                                        .setGone(R.id.divider, true)
                            }

                        }.start()

                    } else {
                        setGone(R.id.tv_timer, false)
                                .setVisible(R.id.divider, true)
                    }
                }
            }

            setVisible(R.id.divider, !isGoneDivider)
        }


        fun getString(@StringRes textId: Int = 0): String {
            return itemView.resources.getString(textId)
        }
    }
}