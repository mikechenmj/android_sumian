package com.sumian.common.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatDialog
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.R
import com.sumian.common.webview.bean.H5ShowToastData
import kotlinx.android.synthetic.main.layout_sumian_dialog.*


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

    companion object {
        const val TYPE_INVALID = -1
        const val TYPE_LOADING = 0
        const val TYPE_SUCCESS = 1
        const val TYPE_FAIL = 2
        const val TYPE_WARNING = 3
        const val TYPE_TEXT = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_lay_dialog_common_sumian)
        updateImageAndText()
    }

    private fun updateImageAndText() {
        ll_iv_tv_container.visibility = if (mType == TYPE_TEXT) View.GONE else View.VISIBLE
        val imageRes = getImageRes(mType)
        val textRes = getTextRes(mType)
        if (imageRes != 0) {
            iv.setImageResource(imageRes)
            iv.visibility = View.VISIBLE
        } else {
            iv.visibility = View.GONE
        }
        if (textRes != 0) {
            tv_desc.setText(textRes)
            tv_desc.visibility = View.VISIBLE
        } else {
            tv_desc.visibility = View.GONE
        }
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
        if (toastData.duration > 0) {
            val textRes = getTextRes(type)
            val imageRes = getImageRes(type)
            SumianImageTextToast.showToast(mContext, imageRes, textRes, toastData.duration > 2000)
            return
        }
        mType = type
        show(toastData.delay, toastData.duration)
    }

    private fun getType(typeString: String): Int {
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

    private fun getTextRes(type: Int): Int {
        return when (type) {
            TYPE_TEXT -> 0
            TYPE_SUCCESS -> R.string.operation_success
            TYPE_FAIL -> R.string.operation_fail
            TYPE_WARNING -> R.string.operation_warning
            TYPE_LOADING -> 0
            else -> {
                0
            }
        }
    }

    private fun getImageRes(type: Int): Int {
        return when (type) {
            TYPE_SUCCESS -> R.drawable.ic_dialog_success
            TYPE_FAIL -> R.drawable.ic_dialog_fail
            TYPE_WARNING -> R.drawable.ic_dialog_warning
            TYPE_LOADING -> R.drawable.dialog_loading_animation
            else -> {
                0
            }
        }
    }

    /**
     * 清除所有延时任务
     */
    fun release() {
        mHandler.removeCallbacksAndMessages(null)
    }
}