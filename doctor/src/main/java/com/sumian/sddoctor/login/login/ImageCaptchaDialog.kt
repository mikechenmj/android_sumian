package com.sumian.sddoctor.login.login

import android.app.Dialog
import android.content.Context
import android.content.Intent
import com.sumian.common.image.ImageLoader
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.dialog_image_captcha.*

class ImageCaptchaDialog(context: Context, private var onImageCaptchaResultListener: OnImageCaptchaResultListener) : Dialog(context, R.style.SumianDialog) {

    private val mContext = context
    private var mImageCaptcha: ImageCaptcha? = null

    companion object {
        const val EXTRA_CAPTCHA_ID = "captcha_id"
        const val EXTRA_CAPTCHA_PHRASE = "captcha_phrase"
    }

    init {
        setCancelable(false)
        setContentView(R.layout.dialog_image_captcha)
        refreshImageCaptcha()
        iv_image_captcha.setOnClickListener { refreshImageCaptcha() }
        iv_close.setOnClickListener { onImageCaptchaResultListener.onClose() }
        bt_image_captcha_confirm.setOnClickListener {
            var imageCaptcha = et_image_captcha_content.text
            if (imageCaptcha.isEmpty()) {
                return@setOnClickListener
            }
            if (mImageCaptcha == null) {
                return@setOnClickListener
            }
            var data = Intent()
            refreshImageCaptcha()
            data.putExtra(EXTRA_CAPTCHA_ID, mImageCaptcha!!.id)
            data.putExtra(EXTRA_CAPTCHA_PHRASE, imageCaptcha.toString())
            onImageCaptchaResultListener.onSend(data)
        }
    }

    private fun refreshImageCaptcha() {
        val call = AppManager.getHttpService().queryImageCaptcha()
        call.enqueue(object : BaseSdResponseCallback<ImageCaptcha>() {
            override fun onSuccess(response: ImageCaptcha?) {
                if (response == null) {
                    return
                }
                mImageCaptcha = response
                ImageLoader.loadImage(response.dataUrl, iv_image_captcha)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
            }
        })
    }

    interface OnImageCaptchaResultListener {
        fun onSend(data: Intent)
        fun onClose()
    }
}