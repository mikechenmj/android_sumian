package com.sumian.sddoctor.network.callback

import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.network.response.BaseResponseCallback
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.log.SddLogManager
import com.sumian.sddoctor.widget.SumianAlertDialog
import retrofit2.Call


/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/14 10:24
 *     desc   :
 *     version: 1.0
 * </pre>
 */
abstract class BaseSdResponseCallback<Data> : BaseResponseCallback<Data>() {

    override fun showSystemIsMaintainDialog() {
        SumianAlertDialog(ActivityUtils.getTopActivity())
                .setTitle(R.string.system_maintain)
                .setMessage(R.string.system_maintain_desc)
                .setRightBtn(R.string.confirm, null)
                .show()
    }

    override fun onUnauthorized() {
        AppManager.getAccountViewModel().updateTokenInvalidState(true)
    }

    override fun onFailure(call: Call<Data>?, t: Throwable?) {
        super.onFailure(call, t)
        val request = call?.request()
        val requestInfo = "${request?.url()} ${request?.body()}"
        SddLogManager.logHttp(requestInfo, "${t?.message}")
    }

}