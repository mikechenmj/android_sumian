package com.sumian.sddoctor.login.login

import android.app.Activity
import android.content.Intent
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
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
        call = AppManager.getHttpService().requestLoginCaptcha(number, captchaId, captchaPhrase)
        mSending = true
        call.enqueue(object : BaseSdResponseCallback<Any>() {
            override fun onSuccess(response: Any?) {
                setResult(Activity.RESULT_OK, data)
                finish()
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
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