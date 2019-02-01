package com.sumian.sddoctor.service.evaluation.adapter

import android.content.Context
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.qmuiteam.qmui.util.QMUISpanHelper
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.App
import com.sumian.sddoctor.patient.bean.Patient
import com.sumian.sddoctor.service.evaluation.bean.WeekEvaluation
import com.sumian.sddoctor.util.TimeUtil
import com.sumian.sddoctor.widget.adapter.BaseRecyclerAdapter
import java.util.*

@Suppress("DEPRECATION")
/**
 *
 *Created by sm
 * on 2018/6/5 13:43
 * desc:周日记评估列表 adapter
 **/
class EvaluationListAdapter(context: Context) : BaseRecyclerAdapter<WeekEvaluation>(context, ONLY_FOOTER) {

    override fun onCreateDefaultViewHolder(parent: ViewGroup?, type: Int): RecyclerView.ViewHolder {
        val viewHolder = ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.lay_advisory_item, parent, false))
        viewHolder.itemView.tag = viewHolder
        return viewHolder
    }

    override fun onBindDefaultViewHolder(holder: RecyclerView.ViewHolder?, item: WeekEvaluation?, position: Int) {
        (holder as ViewHolder).init(item!!, mItems.size - 1 == position)
    }

    inner class ViewHolder constructor(itemView: View) : BaseViewHolder(itemView) {

        private var mCountDownTimer: CountDownTimer? = null

        fun init(item: WeekEvaluation, isGoneDivider: Boolean) {

            setText(R.id.tv_patient_name, item.user.getNameOrNickname())

            if (item.user.tag == 0) {
                setVisible(R.id.tv_patient_label, false)
            } else {
                setText(R.id.tv_patient_label, item.user.formatTag())
                        .setBackgroundRes(R.id.tv_patient_label, when (item.user.tag) {
                            Patient.VIP_LEVEL -> {
                                R.drawable.bg_vip
                            }
                            Patient.SVIP_LEVEL -> {
                                R.drawable.bg_tab_dot
                            }
                            else -> {
                                R.drawable.bg_normal
                            }
                        })
                        .setVisible(R.id.tv_patient_label, true)
            }

            //日记评估状态 0: 未回复/待回复 1：已完成 2：已关闭，3：已取消
            setText(R.id.tv_title, item.description)
                    .setText(R.id.tv_advisory_time, if (item.status == 0) {
                        TimeUtil.formatYYYYMMDDHHMM(item.createdAt)
                    } else {
                        TimeUtil.formatYYYYMMDDHHMM(item.updatedAt)
                    })
                    .setText(R.id.tv_advisory_action_status, item.formatStatus()).setVisible(R.id.tv_advisory_action_status, true)

            mCountDownTimer?.cancel()

            setGone(R.id.tv_timer, false)
                    .setVisible(R.id.divider, false)

            //if (item.traceable_type == "App\\Models\\Advisory") {
            if (item.secondLast == 0) {
                setGone(R.id.tv_timer, false)
                        .setVisible(R.id.divider, true)
            } else {

                if (item.status == 0 && item.secondLast <= 5 * 60 * 60) {
                    initCountDownTimer(item)
                } else {
                    setGone(R.id.tv_timer, false)
                            .setVisible(R.id.divider, true)
                }
            }
            // } else {
            //    setVisible(R.id.divider, true)
            //}

            //   setVisible(R.id.divider, !isGoneDivider)
        }

        private fun initCountDownTimer(item: WeekEvaluation) {
            this.mCountDownTimer = object : CountDownTimer(item.secondLast.toLong() * 1000L, 1000L) {

                override fun onTick(millisUntilFinished: Long) {

                    val iconText = QMUISpanHelper.generateSideIconText(false, itemView.resources.getDimensionPixelOffset(R.dimen.dp_4), "该订单还有 ", itemView.resources.getDrawable(R.drawable.ic_graphic_timing))

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
        }


        fun getString(@StringRes textId: Int = 0): String {
            return itemView.resources.getString(textId)
        }
    }
}