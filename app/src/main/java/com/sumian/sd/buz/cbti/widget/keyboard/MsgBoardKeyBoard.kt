@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package com.sumian.sd.buz.cbti.widget.keyboard

import android.content.Context
import android.text.InputFilter
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.qmuiteam.qmui.util.QMUISpanHelper
import com.sumian.common.helper.ToastHelper
import com.sumian.common.widget.adapter.EmptyTextWatcher
import com.sumian.common.widget.voice.IVisible
import com.sumian.sd.R
import com.sumian.sd.common.utils.UiUtil
import kotlinx.android.synthetic.main.lay_msg_board_keyboard.view.*

class MsgBoardKeyBoard : LinearLayout, View.OnClickListener, IVisible {

    companion object {
        private const val ANONYMOUS_TYPE = 0x01  //匿名
        private const val NONE_ANONYMOUS_TYPE = 0x00 //未匿名
    }

    private var mOnKeyBoardCallback: OnKeyBoardCallback? = null

    private var initBottom = -1
    private var initLeft = -1

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_msg_board_keyboard, this)
        et_msg_board_input.filters = arrayOf(InputFilter.LengthFilter(200))
        et_msg_board_input.addTextChangedListener(object : EmptyTextWatcher() {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                tv_send.isEnabled = !TextUtils.isEmpty(s)
            }
        })
        tv_send.setOnClickListener(this)
        invalidSpan()
        tv_is_anonymous.setOnClickListener(this)
        v_bg.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_send -> {
                if (TextUtils.isEmpty(getContent())) {
                    ToastHelper.show(context, "留言内容不能为空", Gravity.CENTER)
                    return
                }

                if (getContent().length > 200) {
                    ToastHelper.show(context, "留言内容不能超过200", Gravity.CENTER)
                    return
                }

                mOnKeyBoardCallback?.sendContent(getContent(), if (isAnonymous()) ANONYMOUS_TYPE else NONE_ANONYMOUS_TYPE)
            }
            R.id.tv_is_anonymous -> {
                if (tv_is_anonymous.tag == null) {
                    tv_is_anonymous.tag = true
                } else {
                    tv_is_anonymous.tag = null
                }
                invalidSpan()
            }
            R.id.v_bg -> {
                closeKeyBoard()
            }
        }
    }

    private fun invalidSpan() {
        val text = QMUISpanHelper.generateSideIconText(
                true,
                context.resources.getDimensionPixelOffset(R.dimen.space_10),
                context.getString(R.string.anonymous_submit),
                context.getDrawable(if (isAnonymous()) {
                    R.drawable.inputbox_icon_selected
                } else {
                    R.drawable.inputbox_icon_unselected
                }))
        tv_is_anonymous.text = text
    }

    fun setOnKeyBoardCallback(onKeyBoardCallback: OnKeyBoardCallback): MsgBoardKeyBoard {
        this.mOnKeyBoardCallback = onKeyBoardCallback
        return this
    }

    private fun getContent(): String = et_msg_board_input.text.toString().trim()

    private fun isAnonymous(): Boolean {
        return tv_is_anonymous.tag != null
    }

    override fun hide() {
        visibility = View.GONE
        et_msg_board_input.text = null
        initBottom = -1
        UiUtil.closeKeyboard(et_msg_board_input)
    }

    override fun show() {
        visibility = View.VISIBLE
        et_msg_board_input.text = null
        UiUtil.showSoftKeyboard(et_msg_board_input)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        //Log.e("TAG", " changed  $changed b $b")
        if (initBottom == -1 || initLeft == -1) {//软键盘第一次打开
            initBottom = b
            initLeft = l
            return
        }
        if (changed) {
            //Log.e("TAG", " height $b width $r initBottom $initBottom initLeft $initLeft")
            val height = b - initBottom //高度变化值（弹出输入法，布局变小，则为负值）
            val width = r - initLeft  // 当前屏幕宽度（对应输入法而言无影响）
            when {
                height < -200 -> {//打开软键盘
                    v_bg.visibility = View.VISIBLE
                }
                height == 0 -> {//隐藏软键盘
                    closeKeyBoard()
                    v_bg.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun closeKeyBoard() {
        hide()
        tv_is_anonymous.tag = null
        invalidSpan()
        mOnKeyBoardCallback?.close()
    }

    interface OnKeyBoardCallback {

        fun sendContent(content: String, anonymousType: Int)

        fun close()
    }

}