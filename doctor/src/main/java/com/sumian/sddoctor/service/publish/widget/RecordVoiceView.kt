package com.sumian.sddoctor.service.publish.widget

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.blankj.utilcode.util.ActivityUtils
import com.qmuiteam.qmui.util.QMUISpanHelper
import com.sumian.common.helper.ToastHelper
import com.sumian.common.widget.voice.IVisible
import com.sumian.sddoctor.R
import com.sumian.sddoctor.service.publish.player.AudioRecorder
import com.sumian.sddoctor.service.publish.utils.FilePathUtil
import com.sumian.sddoctor.util.ResUtils
import kotlinx.android.synthetic.main.lay_record_voice_view.view.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by jzz
 * on 2018/1/5.
 * desc:
 */

class RecordVoiceView : LinearLayout, EasyPermissions.PermissionCallbacks, View.OnTouchListener, IVisible {

    companion object {

        private val TAG = RecordVoiceView::class.java.simpleName

        private const val RECORD_PERM = 0x01
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    private var mVoiceRecordCallback: VoiceRecordCallback? = null

    private var mDownX: Float = 0.0f

    private val mAudioRecorder: AudioRecorder  by lazy {
        AudioRecorder.init()
    }

    private var mAudioFilePath: String? = null
    private var mCountDownTimer: CountDownTimer? = null

    private var mDuration: Int = 0

    private var mRipplePaint: Paint? = null

    private var mIsDelete: Boolean = false
    private var mIsSend: Boolean = false
    private var mIsShowCountDown = false

    private var mActivityWeakReference: WeakReference<Activity>? = null

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_record_voice_view, this)
        iv_voice_anim?.setOnTouchListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun setUp(activity: Activity): RecordVoiceView {
        this.mActivityWeakReference = WeakReference(activity)
        return this
    }

    fun setOnKeyboardActionListener(VoiceRecordCallback: VoiceRecordCallback) {
        mVoiceRecordCallback = VoiceRecordCallback
    }

    fun onRequestPermissionsResultDelegate(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.e(TAG, "onPermissionsGranted: ----------->")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        val activity = mActivityWeakReference?.get()!!
        ToastHelper.show(context, "未授予录音权限,请正确授予录音权限", Gravity.CENTER)
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(activity, perms)) {
            AppSettingsDialog.Builder(activity).build().show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Forward results to EasyPermissions
        //  EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    override fun show() {
        visibility = View.VISIBLE
    }

    override fun hide() {
        visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {

                if (!checkRecordPermission()) {
                    return false
                }

                mIsDelete = false
                mIsSend = false
                mIsShowCountDown = false
                mDuration = 0

                rollBackView()

                iv_voice_anim.isPressed = true
                iv_line.visibility = View.VISIBLE
                iv_garbage.visibility = View.VISIBLE

                // iv_voice_anim?.startAnimation(mAnimation)

                mAudioFilePath = FilePathUtil.makeFilePath(v.context, AudioRecorder.AUDIO_DIR_PATH, System.currentTimeMillis().toString() + AudioRecorder.AUDIO_SUFFIX_MP3)

                mDownX = event.x

                tv_voice_label.setText(R.string.turn_right_cancel_record)
                tv_voice_label.setTextColor(ResUtils.getColor(R.color.t2_color))

                if (mAudioRecorder.state != AudioRecorder.State.RECORDING) {

                    this.mCountDownTimer = object : CountDownTimer(180 * 1000L, 1000L) {

                        override fun onTick(millisUntilFinished: Long) {

                            val time = (millisUntilFinished / 1000L).toInt()

                            // Log.e(TAG, "onTick: --------->$time")

                            mDuration = 180 - time

                            iv_voice_anim.isPressed = true
                            voice_text_view.text = formatDuration(mDuration)

                            if (time <= 10) {
                                mIsShowCountDown = true
                                tv_voice_label.text = String.format(Locale.getDefault(), "%d%s", time, "s后停止录音")
                                tv_voice_label.setTextColor(ResUtils.getColor(R.color.t2_color))
                            }

                            if (mAudioRecorder.state != AudioRecorder.State.RECORDING) {
                                mAudioRecorder.reset()
                                mAudioRecorder.setOutputFile(mAudioFilePath)
                                mAudioRecorder.prepare()
                                mAudioRecorder.start()
                            }

                        }

                        override fun onFinish() {

                            // iv_voice_anim?.clearAnimation()

                            if (mAudioRecorder.state != AudioRecorder.State.STOPPED) {
                                mAudioRecorder.finishRecord()
                            }

                            if (mDuration < 2) {
                                tv_voice_label.text = QMUISpanHelper.generateSideIconText(true, resources.getDimensionPixelOffset(R.dimen.space_4), "说话时间过短", resources.getDrawable(R.drawable.ic_input_box_warning))
                                // ToastHelper.show(context, "说话时间过短", Gravity.CENTER)
                                tv_voice_label.handler.removeCallbacksAndMessages(null)
                                tv_voice_label.postDelayed({ rollBackView() }, 800L)
                                mAudioRecorder.deleteMixRecorderFile(mAudioFilePath)
                                return
                            }

                            mIsSend = true
                            mIsShowCountDown = false

//                            val retriever = MediaMetadataRetriever()
//
//                            retriever.setDataSource(mAudioFilePath)
//                            val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
//                            val duration = java.lang.Long.parseLong(durationString) / 1000L

                            mVoiceRecordCallback?.onRecordFinish(mAudioFilePath!!, mDuration)
                        }
                    }.start()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val moveX = event.x
                mIsDelete = if (moveX - mDownX > 40) {//向右滑动
                    if (!mIsShowCountDown) {
                        tv_voice_label.setText(R.string.up_cancel_record)
                        tv_voice_label.setTextColor(ResUtils.getColor(R.color.t4_color))
                    }
                    iv_garbage.isPressed = true
                    iv_voice_anim.isPressed = true
                    iv_garbage.visibility = View.VISIBLE
                    iv_line.visibility = View.VISIBLE
                    true
                } else {
                    if (!mIsShowCountDown) {
                        tv_voice_label.setText(R.string.turn_right_cancel_record)
                        tv_voice_label.setTextColor(ResUtils.getColor(R.color.t2_color))
                    }
                    iv_voice_anim.isPressed = true
                    iv_garbage.isPressed = false
                    iv_garbage.visibility = View.VISIBLE
                    iv_line.visibility = View.VISIBLE
                    false
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {

                //ripple_view.stopAnimation()

                //iv_voice_anim.clearAnimation()
                mIsShowCountDown = false

                rollBackView()

                mCountDownTimer?.cancel()
                mAudioRecorder.finishRecord()
                mCountDownTimer = if (mIsDelete) {
                    mAudioRecorder.deleteListRecord()
                    rollBackView()
                    null
                } else {
                    if (!mIsSend)
                        mCountDownTimer?.onFinish()
                    null
                }
            }
            else -> {
            }
        }
        return true
    }

    @AfterPermissionGranted(RECORD_PERM)
    private fun checkRecordPermission(): Boolean {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
        return if (EasyPermissions.hasPermissions(getActivity(), *perms)) {
            true
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(getActivity(), ResUtils.getString(R.string.str_request_record_message), RECORD_PERM, *perms)
            false
        }
    }

    fun onRelease() {
        mAudioRecorder.release()
    }

    private fun getActivity(): Activity {
        return this.mActivityWeakReference?.get() ?: ActivityUtils.getTopActivity()
    }

    private fun formatDuration(duration: Int): String {
        val min = duration / 60
        val sec = duration % 60
        return String.format(Locale.getDefault(), "%d:%02d", min, sec)
    }

    @SuppressLint("SetTextI18n")
    fun rollBackView() {
        voice_text_view.text = "0:00"
        tv_voice_label.setText(R.string.start_record)
        tv_voice_label.setTextColor(ResUtils.getColor(R.color.t2_color))
        iv_voice_anim.isPressed = false
        iv_garbage.isPressed = false
        iv_garbage.visibility = View.INVISIBLE
        iv_line.visibility = View.INVISIBLE
    }

    interface VoiceRecordCallback {

        fun onRecordFinish(path: String, duration: Int)

    }
}
