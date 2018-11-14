package com.sumian.sd.network.callback

import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.network.response.BaseResponseCallback
import com.sumian.sd.R
import com.sumian.sd.account.login.LoginActivity
import com.sumian.sd.widget.dialog.SumianAlertDialog

/**
 * Created by  sm
 *
 * on 2018/9/29
 *
 *desc:默认为 BaseResponseCallback   可根据自身业务特殊处理相关 http_code  e.g. onFailure(errorCode)
 *
 */
abstract class BaseSdResponseCallback<Data> : BaseResponseCallback<Data>() {

    override fun onUnauthorized() {
        LoginActivity.show()
    }

    override fun showSystemIsMaintainDialog() {
        super.showSystemIsMaintainDialog()
        SumianAlertDialog(ActivityUtils.getTopActivity())
                .setTitle(R.string.system_maintain)
                .setMessage(R.string.system_maintain_desc)
                .setRightBtn(R.string.confirm, null)
                .show()
    }
}