package cn.leancloud.chatkit.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import cn.leancloud.chatkit.R
import cn.leancloud.chatkit.utils.LCIMPathUtils
import com.avos.avoscloud.im.v2.audio.AVIMAudioRecorder
import com.blankj.utilcode.util.LogUtils
import com.qmuiteam.qmui.util.QMUISpanHelper
import kotlinx.android.synthetic.main.lcim_chat_record_layout_2.view.*
import java.io.File

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/12 14:56
 * desc   :
 * version: 1.0
 */
class LCIMRecordLayout(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {
    private var mTimer: CountDownTimer? = null
    private var mRecordTime = 0
    private var mAudioRecorder: AVIMAudioRecorder? = null
    private var mStatus = STATUS_NORMAL
    private var mOutputPath: String? = null
    var mRecordListener: LCIMRecordButton.RecordEventListener? = null

    companion object {
        private const val MIN_RECORD_TIME = 3
        private const val MAX_RECORD_TIME = 60
        private const val STATUS_NORMAL = 1
        private const val STATUS_CANCEL = 2
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.lcim_chat_record_layout_2, this, true)

        initRecordPanel()
    }

    private fun initRecordPanel() {
        record_voice_container.setOnTouchListener(object : OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                val action = event.action
                LogUtils.d("container - $action")
                val x = event.rawX.toInt()
                val y = event.rawY.toInt()
                val isInCancelView = isTouchPointInView(iv_cancel_record, x, y)
                when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (isInRecordView(x, y)) {
                            startRecord()
                            return true
                        } else {
                            return false
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        LogUtils.d("isInCancelView $isInCancelView")
                        tv_record_voice_hint.text = resources.getString(if (isInCancelView) R.string.release_finger_cancel_send else R.string.move_right_cancel_send)
                        tv_record_voice_hint.setTextColor(resources.getColor(if (isInCancelView) R.color.t4_color else R.color.t2_color))
                        iv_cancel_record.isSelected = isInCancelView
                    }
                    MotionEvent.ACTION_UP -> {
                        stopRecord(isInCancelView)
                    }
                }
                return true
            }
        })
    }

    private fun isInRecordView(x: Int, y: Int) = isTouchPointInView(iv_record_voice, x, y)

    private fun startRecord() {
        iv_cancel_record.isSelected = false
        tv_record_voice_warning.visibility = View.GONE
        tv_record_voice_hint.visibility = View.VISIBLE
        tv_record_voice_hint.text = resources.getString(R.string.move_right_cancel_send)
        tv_record_voice_hint.setTextColor(resources.getColor(R.color.t2_color))
        startTimer()
        mRecordTime = 0
        mStatus = STATUS_NORMAL
        startRecording()
        iv_record_voice_ripple.startAnimation()
    }

    private fun startRecording() {
        mOutputPath = LCIMPathUtils.getRecordPathByCurrentTime(context)
        try {
            if (null == mAudioRecorder) {
                val localFilePath = mOutputPath
                mAudioRecorder = AVIMAudioRecorder(localFilePath, object : AVIMAudioRecorder.RecordEventListener {
                    override fun onFinishedRecord(milliSeconds: Long, reason: String?) {
                        if (mStatus == STATUS_CANCEL) {
                            removeFile()
                        } else {
                            if (milliSeconds < MIN_RECORD_TIME * 1000) {
                                removeFile()
                            } else {
                                mRecordListener?.onFinishedRecord(localFilePath, Math.round((milliSeconds / 1000).toFloat()))
                            }
                        }
                    }

                    override fun onStartRecord() {
                        mRecordListener?.onStartRecord()
                    }
                })
            }
            mAudioRecorder?.start()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    private fun stopRecord(cancel: Boolean) {
        mTimer?.cancel()
        tv_record_voice_hint.visibility = View.GONE
        if (cancel) {
            LogUtils.d("cancel record")
            mStatus = STATUS_CANCEL
        } else {
            LogUtils.d("finish record")
            if (mRecordTime < MIN_RECORD_TIME) {
                showWarningHint(true)
                mStatus = STATUS_CANCEL
            } else if (mRecordTime == MAX_RECORD_TIME) {
                showWarningHint(false)
            }
        }
        mAudioRecorder?.stop()
        mAudioRecorder = null
        iv_record_voice_ripple.stopAnimation()
    }

    private fun removeFile() {
        val file = File(mOutputPath)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun startTimer() {
        mTimer?.cancel()
        mTimer = object : CountDownTimer(1000L * MAX_RECORD_TIME, 1000L) {
            override fun onFinish() {
                stopRecord(false)
            }

            override fun onTick(millisUntilFinished: Long) {
                mRecordTime++
            }
        }.start()
    }

    private fun showWarningHint(isTooShort: Boolean) {
        val hintText = resources.getString(if (isTooShort) R.string.record_too_short else R.string.record_too_long)
        val span = QMUISpanHelper.generateSideIconText(true,
                resources.getDimensionPixelOffset(R.dimen.space_5),
                hintText, resources.getDrawable(R.drawable.inputbox_icon_caveat))
        tv_record_voice_warning.text = span
        tv_record_voice_warning.visibility = View.VISIBLE
        tv_record_voice_warning.postDelayed({ tv_record_voice_warning.visibility = View.GONE }, 2000)
    }

    private fun isTouchPointInView(view: View, x: Int, y: Int): Boolean {
        val location = intArrayOf(0, 0)
        view.getLocationOnScreen(location)
        val left = location[0]
        val top = location[1]
        val right = left + view.width
        val bottom = top + view.height
        return x in left..right && y in top..bottom
    }

    fun setSavePath(recordPathByCurrentTime: String?) {
        mOutputPath = recordPathByCurrentTime
    }
}