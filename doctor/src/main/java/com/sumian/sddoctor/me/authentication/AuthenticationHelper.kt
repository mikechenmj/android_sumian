package com.sumian.sddoctor.me.authentication

import android.content.Context
import android.view.View
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/30 15:33
 * desc   :
 * version: 1.0
 */
object AuthenticationHelper {
    fun checkAuthenticationStatusWithToast(context: Context, messageRes: Int): Boolean {
        val authenticateStatus = AppManager.getAccountViewModel().getAuthenticateStatus()
        when (authenticateStatus) {
            0 -> {
                SumianDialog(context)
                        .setTitleText(R.string.identity_authentication)
                        .setMessageText(context.getString(messageRes))
                        .setRightBtn(R.string.go_authenticate, View.OnClickListener { AuthenticationActivity.start() })
                        .setLeftBtn(R.string.next_time_talk_about, null)
                        .whitenLeft()
                        .show()
            }
            1 -> {
                SumianDialog(context)
                        .setTitleText(R.string.open_service)
                        .setMessageText(context.getString(R.string.authenticate_is_going_try_it_later))
                        .setRightBtn(R.string.confirm, null)
                        .show()
            }
        }
        return authenticateStatus == 2
    }
}