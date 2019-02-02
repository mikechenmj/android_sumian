package com.sumian.sddoctor.service.publish.activity

import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.widget.voice.IVisible
import com.sumian.common.widget.voice.VoicePlayer
import com.sumian.common.widget.voice.VoicePlayerView
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseViewModelActivity
import com.sumian.sddoctor.service.advisory.bean.Record
import com.sumian.sddoctor.service.publish.bean.Publish
import com.sumian.sddoctor.service.publish.contract.PublishVoiceContact
import com.sumian.sddoctor.service.publish.presenter.PublishVoicePresenter
import com.sumian.sddoctor.service.publish.widget.RecordVoiceView
import com.sumian.sddoctor.service.publish.widget.VoiceAuditionView
import kotlinx.android.synthetic.main.activity_main_reply_publish_voice.*

/**
 * Created by dq
 *
 * on 2018/8/29
 *
 * desc: 医生图文咨询/周日记评估  语音回复
 */
class ReplyVoiceActivity : SddBaseViewModelActivity<PublishVoicePresenter>(),
        RecordVoiceView.VoiceRecordCallback, VoiceAuditionView.VoiceAuditionCallback, PublishVoiceContact.View,
        VoicePlayer.onPlayStatusListener, VoicePlayerView.OnVoiceViewListener {

    companion object {

        fun show(publishId: Int, publishType: Int) {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, ReplyVoiceActivity::class.java)
                        .apply {
                            putExtra(Publish.EXTRAS_PUBLISH_ID, publishId)
                            putExtra(Publish.EXTRAS_PUBLISH_TYPE, publishType)
                        })
            }
        }

    }

    private var mPublishId: Int = 0
    private var mPublishType: Int = Publish.PUBLISH_ADVISORY_TYPE

    private var mPath = ""
    private var mDuration = 0
    private var mProgress = 0

    private val mMediaPlayer: VoicePlayer by lazy {
        VoicePlayer().setStatusListener(this)
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_reply_publish_voice
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.mPublishId = bundle.getInt(Publish.EXTRAS_PUBLISH_ID, 0)
        this.mPublishType = bundle.getInt(Publish.EXTRAS_PUBLISH_TYPE, Publish.PUBLISH_ADVISORY_TYPE)
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mViewModel = PublishVoicePresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.voice_reply)
        record_voice_view.setUp(this@ReplyVoiceActivity).setOnKeyboardActionListener(this)
        voice_audition_view.setVoiceAuditionCallback(this)
        voice_audition_view.setOnVoiceViewListener(this)
    }

    override fun onStop() {
        super.onStop()
        mMediaPlayer.pause()
    }

    override fun onRelease() {
        super.onRelease()
        mMediaPlayer.release()
        record_voice_view.onRelease()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        record_voice_view?.onRequestPermissionsResultDelegate(requestCode, permissions, grantResults)
    }

    override fun onRecordFinish(path: String, duration: Int) {
        this.mPath = path
        this.mDuration = duration
        voice_audition_view.setVoice(path, duration, 0, true, Record.Sound.IDLE_STATUS)
        record_voice_view.rollBackView()
        invalidVisible(voice_audition_view, record_voice_view)
    }

    override fun onPausePreCallback(prePosition: Int, progress: Int) {

    }

    override fun onCompleteCallback(position: Int) {
        this.mProgress = 0
        voice_audition_view.setVoice(mPath, mDuration, 0, true, Record.Sound.IDLE_STATUS)
    }

    override fun onPrepareCallback(position: Int) {
        voice_audition_view.setVoice(mPath, mDuration, mProgress, false, Record.Sound.PLAYING_STATUS)
    }

    override fun onPlayCallback(position: Int) {
        voice_audition_view.setVoice(mPath, mDuration, mProgress, false, Record.Sound.PLAYING_STATUS)
    }

    override fun onPauseCallback(position: Int) {
        voice_audition_view.setVoice(mPath, mDuration, mProgress, false, Record.Sound.IDLE_STATUS)
    }

    override fun onStopCallback(position: Int) {
        super.onStopCallback(position)
        this.mProgress = 0
        voice_audition_view.setVoice(mPath, mDuration, 0, true, Record.Sound.IDLE_STATUS)
    }

    override fun onProgressCallback(position: Int, duration: Int, progress: Int) {
        this.mProgress = progress
        if (mMediaPlayer.isPlaying) {
            voice_audition_view.setVoice(mPath, duration, progress, false, Record.Sound.PLAYING_STATUS)
        } else {
            voice_audition_view.setVoice(mPath, duration, progress, false, Record.Sound.IDLE_STATUS)
        }
    }

    override fun doPlay() {
        mMediaPlayer.play(mPath, 0, mProgress)
    }

    override fun doPause() {
        mMediaPlayer.pause(0)
    }

    override fun doSeekTo(position: Int) {
        mMediaPlayer.seekTo(position)
    }

    override fun publishVoice() {
        mMediaPlayer.stop()
        voice_audition_view.setVoice(mPath, mDuration, 0, true, Record.Sound.IDLE_STATUS)
        mViewModel?.getPublishVoiceSts(publishType = mPublishType, publishId = mPublishId, voiceFilePath = mPath, duration = mDuration)
    }

    override fun reRecordVoice() {
        mMediaPlayer.stop()
        record_voice_view.rollBackView()
        voice_audition_view.setVoice(mPath, mDuration, 0, true, Record.Sound.IDLE_STATUS)
        invalidVisible(record_voice_view, voice_audition_view)
    }

    override fun onPublishVoiceSuccess() {
        finish()
    }

    override fun onPublishVoiceFailed(error: String) {
        ToastUtils.showShort(error)
    }

    override fun onGetPublishVoiceStsSuccess() {

    }

    override fun onGetPublishVoiceStsFailed(error: String) {
        ToastUtils.showShort(error)
    }

    private fun invalidVisible(showIVisible: IVisible, hideIVisible: IVisible) {
        showIVisible.show()
        hideIVisible.hide()
    }
}