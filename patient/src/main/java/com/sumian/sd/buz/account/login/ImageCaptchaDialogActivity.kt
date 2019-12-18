package com.sumian.sd.buz.account.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.sumian.common.base.BaseActivity
import com.sumian.sd.R
import retrofit2.Call

class ImageCaptchaDialogActivity : BaseActivity(), ImageCaptchaDialog.OnImageCaptchaResultListener {

    companion object {
        private const val EXTRA_NUMBER = "extra_number"
        @JvmStatic
        fun startForResult(context: Activity, number: String, code: Int) {
            context.startActivityForResult(Intent(context, ImageCaptchaDialogActivity::class.java).apply { putExtra(EXTRA_NUMBER, number) }, code)
        }
    }

    private var mNumber: String = ""
    private var mSending = false

    override fun onSend(data: Intent) {
        if (data == null) {
            return
        }
        requestCaptcha(mNumber, data)
    }

    private fun requestCaptcha(number: String, data: Intent) {
        if (mSending) {
            return
        }
        var captchaId = data.getStringExtra(ImageCaptchaDialog.EXTRA_CAPTCHA_ID)
        var captchaPhrase = data.getStringExtra(ImageCaptchaDialog.EXTRA_CAPTCHA_PHRASE)
        var call: Call<*>? = null
        call = CaptchaHelper.requestCaptcha(number, captchaId, captchaPhrase, object : CaptchaHelper.RequestCaptchaListener {
            override fun onFail(code: Int) {
            }

            override fun onStart() {
                mSending = true
            }

            override fun onSuccess() {
                setResult(Activity.RESULT_OK, data)
                finish()
            }

            override fun onFinish() {
                mSending = false
            }
        })
        addCall(call)
    }

    override fun onClose() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_activity_empty_layout
    }

    override fun portrait(): Boolean {
        return false
    }

    override fun initWidget() {
        super.initWidget()
        mNumber = intent.getStringExtra(EXTRA_NUMBER)
        showDialog()
    }

    private fun showDialog() {
        ImageCaptchaDialog(this, this).show()
    }
}