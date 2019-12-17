package com.sumian.sddoctor.login.login

import android.app.Activity
import android.content.Intent
import com.sumian.common.base.BaseActivity
import com.sumian.sddoctor.R

class ImageCaptchaDialogActivity : BaseActivity(), ImageCaptchaDialog.OnImageCaptchaResultListener {

    companion object {
        @JvmStatic
        fun startForResult(context: Activity, code: Int) {
            context.startActivityForResult(Intent(context, ImageCaptchaDialogActivity::class.java), code)
        }
    }

    override fun onSuccess(data: Intent) {
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onFail() {
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
        showDialog()
    }

    private fun showDialog() {
        ImageCaptchaDialog(this, this).show()
    }

}