package com.sumian.sleepdoctor.widget.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.text.TextUtils
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.sumian.sleepdoctor.R
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
class SumianImageTextDialog(context: Context) : Dialog(context, R.style.SumianLoadingDialog_NotDim) {
    private val mContext = context
    private var mRotateImage = true
    private var mImageRes: Int? = null
    private var mText: String? = null
    private val mHandler = Handler()
    private var mType = TYPE_TEXT

    companion object {
        private const val ROTATE_DURATION = 1500L

        const val TYPE_LOADING = 0
        const val TYPE_SUCCESS = 1
        const val TYPE_FAIL = 2
        const val TYPE_WARNING = 3
        const val TYPE_TEXT = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_sumian_dialog)
        updateImageAndText()
    }

    private fun updateImageAndText() {
        ll_iv_tv_container.visibility = if (mType == TYPE_TEXT) GONE else VISIBLE
        tv_toast.visibility = if (mType != TYPE_TEXT) GONE else VISIBLE

        if (mType != TYPE_TEXT) {
            if (mImageRes != null) {
                iv.setImageResource(mImageRes!!)
                iv.visibility = View.VISIBLE
            } else {
                iv.visibility = GONE
            }
            if (!TextUtils.isEmpty(mText)) {
                tv_desc.text = mText
                tv_desc.visibility = VISIBLE
            } else {
                tv_desc.visibility = GONE
            }
        } else {
            tv_toast.text = mText
        }
    }

    fun setImage(@DrawableRes imageRes: Int) {
        mImageRes = imageRes
    }

    fun setText(@StringRes textRes: Int): SumianImageTextDialog {
        mText = mContext.getString(textRes)
        return this
    }

    fun setText(text: String): SumianImageTextDialog {
        mText = text
        return this
    }

    fun setType(type: Int): SumianImageTextDialog {
        mType = type
        when (type) {
            TYPE_LOADING -> {
                setImage(R.drawable.dialog_loading_animation)
                mRotateImage = true
            }

            TYPE_SUCCESS -> {
                setImage(R.drawable.ic_dialog_success)
                setText(R.string.operation_success)
                mRotateImage = false
            }
            TYPE_FAIL -> {
                setImage(R.drawable.ic_dialog_fail)
                setText(R.string.operation_fail)
                mRotateImage = false
            }
            TYPE_WARNING -> {
                setImage(R.drawable.ic_dialog_warning)
                setText(R.string.operation_warning)
                mRotateImage = false
            }
            TYPE_TEXT -> {
                setImage(R.drawable.ic_dialog_warning)
                setText(R.string.operation_warning)
                mRotateImage = false
            }
            else -> {
                setType(TYPE_TEXT)
            }
        }
        return this
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
        mHandler.postDelayed({
            dismiss()
        }, delay)
        return this
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener {
            mHandler.removeCallbacksAndMessages(null)
            listener?.onDismiss(it)
        }
    }
}