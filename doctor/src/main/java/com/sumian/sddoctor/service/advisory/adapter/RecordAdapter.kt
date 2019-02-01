package com.sumian.sddoctor.service.advisory.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.qmuiteam.qmui.widget.QMUIRadiusImageView
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.media.LargeImageActivity
import com.sumian.common.widget.FlowLayout
import com.sumian.common.widget.voice.VoicePlayer
import com.sumian.common.widget.voice.VoicePlayerView
import com.sumian.sddoctor.R
import com.sumian.sddoctor.patient.bean.Patient
import com.sumian.sddoctor.service.advisory.base.holder.BaseViewHolder
import com.sumian.sddoctor.service.advisory.bean.Doctor
import com.sumian.sddoctor.service.advisory.bean.Record
import com.sumian.sddoctor.service.advisory.onlinereport.OnlineReportDetailActivity
import com.sumian.sddoctor.service.advisory.onlinereport.OnlineReportListActivity
import com.sumian.sddoctor.util.TimeUtil
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

    private lateinit var mPatient: Patient

    private lateinit var mMediaPlayer: VoicePlayer

    // private var mCurrentPosition: Int = -1

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

    fun setUser(patient: Patient) {
        this.mPatient = patient
    }

    fun registerMediaPlayer(voicePlayer: VoicePlayer): RecordAdapter {
        this.mMediaPlayer = voicePlayer
        return this
    }

    /**
     * mPatient question record
     */
    inner class QuestionViewHolder(itemView: View) : BaseViewHolder<Record>(itemView) {

        private val mAvatar: CircleImageView  by lazy {
            getView<CircleImageView>(R.id.civ_avatar)
        }
        private val mTvName: TextView  by lazy {
            getView<TextView>(R.id.tv_name)
        }

        private val mTvQuestionTime: TextView  by lazy {
            getView<TextView>(R.id.tv_time)
        }
        private val mTvContent: TextView  by lazy {
            getView<TextView>(R.id.tv_content)
        }
        private val mFlowLayout: FlowLayout  by lazy {
            getView<FlowLayout>(R.id.flow_layout)
        }
        private val mDividerTwo: View  by lazy {
            getView<View>(R.id.divide_two)
        }
        private val mReportLayout: FrameLayout  by lazy {
            getView<FrameLayout>(R.id.record_layout)
        }

        override fun initView(item: Record) {
            super.initView(item)

            load(mPatient.avatar, R.mipmap.ic_info_avatar_patient, mAvatar)
            this.mTvName.text = mPatient.getNameOrNickname()

            mTvQuestionTime.text = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(item.created_at * 1000L))

            mTvContent.text = item.content

            if (item.images.isEmpty()) {
                mFlowLayout.removeAllViewsInLayout()
                mFlowLayout.visibility = View.GONE
            } else {//初始化图片列表
                mFlowLayout.removeAllViews()
                item.images.forEach { img ->

                    val rootView = LayoutInflater.from(itemView.context).inflate(R.layout.lay_item_advisory_imag, mFlowLayout, false)

                    val image = rootView.findViewById<QMUIRadiusImageView>(R.id.iv)
                    image.setOnClickListener { v -> LargeImageActivity.show(v?.context, img) }

                    load("$img?x-oss-process=image/resize,m_lfit,h_80,w_80", RequestOptions.errorOf(R.mipmap.ic_preview_split_graph).fitCenter(), image)

                    mFlowLayout.addView(rootView)

                }

                mFlowLayout.visibility = View.VISIBLE
            }

            if (item.reports.isEmpty()) {
                mDividerTwo.visibility = View.GONE
                mReportLayout.visibility = View.GONE
                mReportLayout.setOnClickListener(null)
            } else {
                mDividerTwo.visibility = View.VISIBLE
                mReportLayout.visibility = View.VISIBLE
                mReportLayout.setOnClickListener {
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
    inner class ReplyViewHolder(itemView: View) : BaseViewHolder<Record>(itemView), VoicePlayerView.OnVoiceViewListener, VoicePlayer.onPlayStatusListener {

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
            load(mDoctor.avatar, R.drawable.ic_info_avatar_doctor, mAvatar)
            this.mTvName.text = mDoctor.getDoctorReplyName()
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
            //item.sound.progress = progress
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