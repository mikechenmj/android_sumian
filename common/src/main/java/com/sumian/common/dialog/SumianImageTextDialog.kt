package com.sumian.common.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatDialog
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.R
import com.sumian.common.h5.bean.H5ShowToastData
import kotlinx.android.synthetic.main.layout_sm_toast.*


@Suppress("MemberVisibilityCanBePrivate")
/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/15 10:51
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SumianImageTextDialog(context: Context) : AppCompatDialog(context, R.style.SumianLoadingDialog_NotDim) {
    private val mContext = context
    private val mHandler = Handler()
    private var mType = TYPE_TEXT
    private var mText: String? = null // 图片下面的文本，不传时取TYPE 对应的文字

    companion object {
        private const val INVALID_RES_ID = 0
        // type
        const val TYPE_INVALID = "invalid"
        const val TYPE_LOADING = "loading"
        const val TYPE_SUCCESS = "success"
        const val TYPE_FAIL = "fail"
        const val TYPE_WARNING = "warning"
        const val TYPE_TEXT = "text"
        // duration
        const val SHOW_DURATION_SHORT = 2000L
        const val SHOW_DURATION_LONG = 3500L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_lay_dialog_common_sumian)
        updateImageAndText()
    }

    private fun updateImageAndText() {
        ll_iv_tv_container.visibility = if (mType == TYPE_TEXT) View.GONE else View.VISIBLE
        val imageRes = getImageRes(mType)
        if (imageRes != INVALID_RES_ID) {
            iv.setImageResource(imageRes)
            iv.visibility = View.VISIBLE
        } else {
            iv.visibility = View.GONE
        }
        val text = getText(mType, mText)
        tv_desc?.visibility = if (TextUtils.isEmpty(text)) View.GONE else View.VISIBLE
        tv_desc?.text = text
    }

    fun show(delay: Long, duration: Long): SumianImageTextDialog {
        mHandler.postDelayed({
            show()
            if (duration != 0L) {
                mHandler.postDelayed({ dismiss() }, duration)
            }
        }, delay)
        return this
    }

    fun dismiss(delay: Long): SumianImageTextDialog {
        if (!isShowing) {
            return this
        }
        mHandler.postDelayed({
            if (isShowing) {
                dismiss()
            }
        }, delay)
        return this
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener {
            mHandler.removeCallbacksAndMessages(null)
            listener?.onDismiss(it)
        }
    }

    fun show(toastData: H5ShowToastData) {
        val type = getType(toastData.type)
        if (type == TYPE_INVALID) {
            return
        }
        if (type == TYPE_TEXT) {
            ToastUtils.showShort(toastData.message)
            return
        }
        mType = type
        mText = toastData.message
        if (toastData.duration > 0) {
            val text = getText(type, mText)
            val imageRes = getImageRes(type)
            SumianImageTextToast.showToast(mContext, imageRes, text, toastData.duration > 2000)
            return
        }
        show(toastData.delay, toastData.duration)
    }

    fun show(type: String, text: String? = null, delay: Long = 0, duration: Long = 0) {
        show(H5ShowToastData(type, text, delay, duration))
    }

    private fun getType(typeString: String): String {
        return when (typeString) {
            "text" -> SumianImageTextDialog.TYPE_TEXT
            "success" -> TYPE_SUCCESS
            "error" -> TYPE_FAIL
            "loading" -> TYPE_LOADING
            "warning" -> TYPE_WARNING
            else -> {
                TYPE_INVALID
            }
        }
    }

    private fun getTextRes(type: String): Int {
        return when (type) {
            TYPE_TEXT -> INVALID_RES_ID
            TYPE_SUCCESS -> R.string.operation_success
            TYPE_FAIL -> R.string.operation_fail
            TYPE_WARNING -> R.string.operation_warning
            TYPE_LOADING -> INVALID_RES_ID
            else -> {
                INVALID_RES_ID
            }
        }
    }

    private fun getImageRes(type: String): Int {
        return when (type) {
            TYPE_SUCCESS -> R.drawable.ic_dialog_success
            TYPE_FAIL -> R.drawable.ic_dialog_fail
            TYPE_WARNING -> R.drawable.ic_dialog_warning
            TYPE_LOADING -> R.drawable.dialog_loading_animation
            else -> {
                INVALID_RES_ID
            }
        }
    }

    private fun getText(type: String, text: String?): String? {
        return if (TextUtils.isEmpty(text)) {
            val textRes = getTextRes(type)
            if (textRes != INVALID_RES_ID) {
                mContext.resources.getString(textRes)
            } else {
                null
            }
        } else {
            text
        }
    }

    /**
     * 清除所有延时任务
     */
    fun release() {
        mHandler.removeCallbacksAndMessages(null)
    }
}