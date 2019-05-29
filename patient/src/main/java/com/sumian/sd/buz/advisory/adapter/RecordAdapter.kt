package com.sumian.sd.buz.advisory.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.qmuiteam.qmui.widget.QMUIRadiusImageView
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.base.holder.SdBaseViewHolder
import com.sumian.common.media.LargeImageActivity
import com.sumian.common.widget.FlowLayout
import com.sumian.common.widget.voice.VoicePlayer
import com.sumian.common.widget.voice.VoicePlayerView
import com.sumian.sd.R
import com.sumian.sd.buz.account.bean.UserInfo
import com.sumian.sd.buz.advisory.bean.Record
import com.sumian.sd.buz.doctor.bean.Doctor
import com.sumian.sd.buz.onlinereport.OnlineReportDetailActivity
import com.sumian.sd.buz.onlinereport.OnlineReportListActivity
import com.sumian.sd.common.utils.TimeUtil
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

    private lateinit var mMediaPlayer: VoicePlayer

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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Record, position: Int) {
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

    fun registerMediaPlayer(voicePlayer: VoicePlayer): RecordAdapter {
        this.mMediaPlayer = voicePlayer
        return this
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

            mTvQuestionTime?.text = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(Date(item.created_at * 1000L))

            mTvContent?.text = item.content

            if (item.images.isEmpty()) {
                mFlowLayout?.removeAllViewsInLayout()
                mFlowLayout?.visibility = View.GONE
            } else {//初始化图片列表
                mFlowLayout?.removeAllViews()
                item.images.forEach { img ->

                    val rootView = LayoutInflater.from(itemView.context).inflate(R.layout.lay_item_advisory_imag, mFlowLayout, false)

                    val image = rootView.findViewById<QMUIRadiusImageView>(R.id.iv_avatar)
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
                        OnlineReportDetailActivity.launch(itemView.context, item.reports[0])
                    }
                }
            }
        }

    }

    /**
     * doctor replay record
     */
    inner class ReplyViewHolder(itemView: View) : SdBaseViewHolder<Record>(itemView), VoicePlayerView.OnVoiceViewListener, VoicePlayer.onPlayStatusListener {

        private val mAvatar: CircleImageView  by lazy {
            getView<CircleImageView>(R.id.civ_avatar)
        }
        private val mTvName: TextView  by lazy {
            getView<TextView>(R.id.tv_name)
        }
        private val mTvReplyTime: TextView  by lazy {
            getView<TextView>(R.id.tv_time)
        }
        private val mTvContent: TextView  by lazy {
            getView<TextView>(R.id.tv_content)
        }
        private val mVoicePlayerView: VoicePlayerView  by lazy {
            getView<VoicePlayerView>(R.id.voice_player_view)
        }

        override fun initView(item: Record) {
            super.initView(item)
            load(mDoctor.avatar, R.mipmap.ic_info_avatar_doctor, mAvatar)
            this.mTvName.text = if (TextUtils.isEmpty(mDoctor.name)) getText(R.string.sleep_doctor) else mDoctor.name
            this.mTvReplyTime.text = TimeUtil.formatYYYYMMDDHHMM(item.created_at)
            this.mTvContent.visibility = View.GONE
            this.mVoicePlayerView.hide()
            if (item.content_type == 1) {
                this.mVoicePlayerView.setOnVoiceViewListener(this).invalid(item.sound.url, item.sound.duration, item.sound.progress, item.sound.status).show()
            } else {
                this.mTvContent.text = item.content
                this.mTvContent.visibility = View.VISIBLE
            }
        }

        override fun doPlay() {
            mMediaPlayer.setStatusListener(this).play(mItem.sound.url, adapterPosition, mItem.sound.progress)
        }

        override fun doPause() {
            mMediaPlayer.setStatusListener(this).pause(adapterPosition)
        }

        override fun doSeekTo(position: Int) {
            mItem.sound.progress = position
            mItems[adapterPosition] = mItem
            mMediaPlayer.seekTo(position)
        }

        override fun onPrepareCallback(position: Int) {
            val item = mItems[position]
            item.sound.status = Record.Sound.PREPARE_STATUS
            mItems[position] = item
            mVoicePlayerView.invalid(item.sound.url, item.sound.duration, item.sound.progress, item.sound.status)
        }

        override fun onPlayCallback(position: Int) {
            val item = mItems[position]
            item.sound.status = Record.Sound.PLAYING_STATUS
            mItems[position] = item
            mVoicePlayerView.invalid(item.sound.url, item.sound.duration, item.sound.progress, item.sound.status)
        }

        override fun onPausePreCallback(prePosition: Int, progress: Int) {
            val item = mItems[prePosition]
            item.sound.status = Record.Sound.IDLE_STATUS
            mItems[prePosition] = item
            notifyItemChanged(prePosition)
            // mVoicePlayerView.invalid(item.sound.url, item.sound.duration, item.sound.progress, item.sound.status)
        }

        override fun onPauseCallback(position: Int) {
            val item = mItems[position]
            item.sound.status = Record.Sound.IDLE_STATUS
            mItems[position] = item
            //notifyItemChanged(position)
            mVoicePlayerView.invalid(item.sound.url, item.sound.duration, item.sound.progress, item.sound.status)
        }

        override fun onProgressCallback(position: Int, duration: Int, progress: Int) {
            val item = mItems[position]
            item.sound.status = Record.Sound.PLAYING_STATUS
            item.sound.progress = progress
            item.sound.duration = duration
            mItems[position] = item
            //notifyItemChanged(position)
            mVoicePlayerView.invalid(item.sound.url, item.sound.duration, item.sound.progress, item.sound.status)
        }

        override fun onCompleteCallback(position: Int) {
            val item = mItems[position]
            item.sound.status = Record.Sound.IDLE_STATUS
            item.sound.progress = 0
            mItems[position] = item
            mVoicePlayerView.invalid(item.sound.url, item.sound.duration, item.sound.progress, item.sound.status)
        }

    }
}