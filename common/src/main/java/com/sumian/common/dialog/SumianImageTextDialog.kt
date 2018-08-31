package com.sumian.common.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatDialog
import android.text.TextUtils
import android.view.View
import com.sumian.common.R
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
    private var mRotateImage = true
    private var mImageRes: Int? = null
    private var mText: String? = null
    private val mHandler = Handler()
    private var mType = TYPE_TEXT

    private var mDuration: Long = 0

    private val mDismissRunnable: Runnable by lazy {
        Runnable {
            dismiss()
        }
    }

    private val mShowRunnable: Runnable by lazy {
        Runnable {
            show()
            if (mDuration != 0L) {
                mHandler.removeCallbacks(mDismissRunnable)
                mHandler.postDelayed(mDismissRunnable, mDuration)
            }
        }
    }

    companion object {
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
        ll_iv_tv_container.visibility = if (mType == TYPE_TEXT) View.GONE else View.VISIBLE
        tv_toast.visibility = if (mType != TYPE_TEXT) View.GONE else View.VISIBLE

        if (mType != TYPE_TEXT) {
            if (mImageRes != null) {
                iv.setImageResource(mImageRes!!)
                iv.visibility = View.VISIBLE
            } else {
                iv.visibility = View.GONE
            }
            if (!TextUtils.isEmpty(mText)) {
                tv_desc.text = mText
                tv_desc.visibility = View.VISIBLE
            } else {
                tv_desc.visibility = View.GONE
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
        this.mDuration = duration
        mHandler.removeCallbacks(mShowRunnable)
        mHandler.removeCallbacks(mDismissRunnable)
        mHandler.postDelayed(mShowRunnable, delay)
        return this
    }

    fun dismiss(delay: Long): SumianImageTextDialog {
        mHandler.removeCallbacks(mDismissRunnable)
        mHandler.postDelayed(mDismissRunnable, delay)
        return this
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener {
            mHandler.removeCallbacks(mShowRunnable)
            mHandler.removeCallbacks(mDismissRunnable)
            mHandler.removeCallbacksAndMessages(null)
            listener?.onDismiss(it)
        }
    }
}