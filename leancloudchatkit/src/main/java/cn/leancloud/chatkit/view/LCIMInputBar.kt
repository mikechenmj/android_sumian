package cn.leancloud.chatkit.view

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import cn.leancloud.chatkit.R
import cn.leancloud.chatkit.event.LCIMInputBottomBarEvent
import cn.leancloud.chatkit.event.LCIMInputBottomBarRecordEvent
import cn.leancloud.chatkit.event.LCIMInputBottomBarTextEvent
import cn.leancloud.chatkit.utils.LCIMPathUtils
import com.blankj.utilcode.util.KeyboardUtils
import de.greenrobot.event.EventBus
import kotlinx.android.synthetic.main.lcim_input_box.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/8 10:37
 * desc   :
 * version: 1.0
 */
class LCIMInputBar(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet) {

    init {
        LayoutInflater.from(context).inflate(R.layout.lcim_input_box, this, true)
        initRecordPanel()
        iv_switch_voice.setOnClickListener { showRecordVoicePanel(!iv_switch_voice.isSelected) }
        bt_send.setOnClickListener { sendMessage() }
        et_message.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                bt_send.isEnabled = !TextUtils.isEmpty(s)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        et_message.setOnClickListener { showRecordVoicePanel(false) }
    }

    private fun initRecordPanel() {
        record_layout.setSavePath(LCIMPathUtils.getRecordPathByCurrentTime(context))
        record_layout.mRecordListener = object : LCIMRecordButton.RecordEventListener {
            override fun onFinishedRecord(audioPath: String, secs: Int) {
                if (secs > 0)
                    EventBus.getDefault().post(
                            LCIMInputBottomBarRecordEvent(
                                    LCIMInputBottomBarEvent.INPUTBOTTOMBAR_SEND_AUDIO_ACTION, audioPath, secs, tag))
                record_layout.setSavePath(LCIMPathUtils.getRecordPathByCurrentTime(context))
            }

            override fun onStartRecord() {}
        }
    }

    private fun sendMessage() {
        val content = et_message.text.toString()
        et_message.setText("")
        EventBus.getDefault().post(
                LCIMInputBottomBarTextEvent(LCIMInputBottomBarEvent.INPUTBOTTOMBAR_SEND_TEXT_ACTION, content, tag))
    }

    fun showRecordVoicePanel(show: Boolean) {
        iv_switch_voice.isSelected = show
        record_layout.visibility = if (show) View.VISIBLE else View.GONE
        if (show) {
            KeyboardUtils.hideSoftInput(this)
        }
    }
}