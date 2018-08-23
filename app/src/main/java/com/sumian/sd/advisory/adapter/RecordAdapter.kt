package com.sumian.sd.advisory.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.bumptech.glide.request.RequestOptions
import com.qmuiteam.qmui.widget.QMUIRadiusImageView
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.media.LargeImageActivity
import com.sumian.common.widget.FlowLayout
import com.sumian.sd.R
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.advisory.bean.Record
import com.sumian.sd.base.holder.SdBaseViewHolder
import com.sumian.sd.doctor.bean.Doctor
import com.sumian.sd.onlinereport.OnlineReportDetailActivity
import com.sumian.sd.onlinereport.OnlineReportListActivity
import com.sumian.sd.utils.TimeUtil
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 *Created by sm
 * on 2018/6/6 13:57
 * desc:
 **/
class RecordAdapter(context: Context) : BaseRecyclerAdapter<Record>(context) {

    private lateinit var mDoctor: Doctor

    private lateinit var mUser: UserInfo

    override fun onCreateDefaultViewHolder(parent: ViewGroup?, type: Int): RecyclerView.ViewHolder {
        return when (type) {
            Record.RECORD_QUESTION_TYPE -> {
                QuestionViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.lay_advisory_record_question_item, parent, false))
            }
            Record.RECORD_REPLY_TYPE -> {
                ReplyViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.lay_advisory_record_reply_item, parent, false))
            }
            else -> {
                throw NullPointerException("invalid advisory type")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, item: Record, position: Int) {
        when (getItemViewType(position)) {
            Record.RECORD_QUESTION_TYPE -> {
                (holder as QuestionViewHolder).initView(item)
            }
            Record.RECORD_REPLY_TYPE -> {
                (holder as ReplyViewHolder).initView(item)
            }
            else -> {
                (holder as QuestionViewHolder).initView(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (mItems[position].type) {
            Record.RECORD_QUESTION_TYPE -> Record.RECORD_QUESTION_TYPE
            Record.RECORD_REPLY_TYPE -> Record.RECORD_REPLY_TYPE
            else -> Record.RECORD_QUESTION_TYPE
        }
    }

    fun setDoctor(doctor: Doctor) {
        this.mDoctor = doctor
    }

    fun setUser(userProfile: UserInfo) {
        this.mUser = userProfile
    }


    /**
     * patient question record
     */
    inner class QuestionViewHolder(itemView: View) : SdBaseViewHolder<Record>(itemView) {

        private var mTvQuestionIndex: TextView? = null
        private var mTvQuestionTime: TextView? = null
        private var mTvContent: TextView? = null
        private var mFlowLayout: FlowLayout? = null
        private var mDividerTwo: View? = null
        private var mReportLayout: FrameLayout? = null

        init {
            this.mTvQuestionIndex = getView(R.id.tv_question_index)
            this.mTvQuestionTime = getView(R.id.tv_time)
            this.mTvContent = getView(R.id.tv_content)
            this.mFlowLayout = getView(R.id.flow_layout)
            this.mDividerTwo = getView(R.id.divide_two)
            this.mReportLayout = getView(R.id.record_layout)
        }

        override fun initView(item: Record) {
            super.initView(item)
            mTvQuestionIndex?.text = when (item.question_index) {
                1 -> {
                    itemView.resources.getString(R.string.first_question)
                }
                2 -> {
                    itemView.resources.getString(R.string.second_question)
                }
                3 -> {
                    itemView.resources.getString(R.string.third_question)
                }
                else -> {
                    itemView.resources.getString(R.string.first_question)
                }
            }

            mTvQuestionTime?.text = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(item.created_at * 1000L))

            mTvContent?.text = item.content

            if (item.images.isEmpty()) {
                mFlowLayout?.removeAllViewsInLayout()
                mFlowLayout?.visibility = View.GONE
            } else {//初始化图片列表
                mFlowLayout?.removeAllViews()
                item.images.forEach { img ->

                    val rootView = LayoutInflater.from(itemView.context).inflate(R.layout.lay_item_advisory_imag, mFlowLayout, false)

                    val image = rootView.findViewById<QMUIRadiusImageView>(R.id.iv)
                    image.setOnClickListener { v -> LargeImageActivity.show(v?.context, img) }

                    load("$img?x-oss-process=image/resize,m_lfit,h_80,w_80", RequestOptions.errorOf(R.mipmap.ic_preview_split_graph).fitCenter(), image)

                    mFlowLayout?.addView(rootView)

                }

                mFlowLayout?.visibility = View.VISIBLE
            }

            if (item.reports.isEmpty()) {
                mDividerTwo?.visibility = View.GONE
                mReportLayout?.visibility = View.GONE
                mReportLayout?.setOnClickListener(null)
            } else {
                mDividerTwo?.visibility = View.VISIBLE
                mReportLayout?.visibility = View.VISIBLE
                mReportLayout?.setOnClickListener {
                    if (item.reports.size > 1) {
                        OnlineReportListActivity.launchForShowList(itemView.context, item.reports)
                    } else {
                        OnlineReportDetailActivity.launch(itemView.context, item.reports[0].title, item.reports[0].report_url)
                    }
                }
            }
        }

    }

    /**
     * doctor replay record
     */
    inner class ReplyViewHolder(itemView: View) : SdBaseViewHolder<Record>(itemView) {

        private var mAvatar: CircleImageView? = null
        private var mTvName: TextView? = null
        private var mTvReplyTime: TextView? = null
        private var mTvContent: TextView? = null

        init {
            this.mAvatar = getView(R.id.civ_avatar)
            this.mTvName = getView(R.id.tv_name)
            this.mTvReplyTime = getView(R.id.tv_time)
            this.mTvContent = getView(R.id.tv_content)
        }

        override fun initView(item: Record) {
            super.initView(item)
            load(mDoctor.avatar, R.mipmap.ic_info_avatar_doctor, mAvatar)
            this.mTvName?.text = if (TextUtils.isEmpty(mDoctor.name)) getText(R.string.sleep_doctor) else mDoctor.name
            this.mTvReplyTime?.text = TimeUtil.formatYYYYMMDDHHMM(item.created_at)
            this.mTvContent?.text = item.content
        }

    }
}