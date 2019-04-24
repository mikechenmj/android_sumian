package com.sumian.sddoctor.service.cbti.presenter

import android.content.Context
import androidx.core.content.edit
import com.sumian.common.base.BaseViewModel
import com.sumian.sddoctor.app.App
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.service.cbti.activity.CBTIIntroduction2WebActivity
import com.sumian.sddoctor.service.cbti.activity.CBTIIntroductionActivity

/**
 * Created by jzz
 *
 * on 2019/1/10
 *
 * desc:
 */
object CBTILauncherPresenter : BaseViewModel() {

    private const val CBTI_LAUNCHER_FILE = "cbti_launcher_file"
    private const val LAUNCHER_KEY = "launcherAction"
    private const val ACCOUNT_KEY = "account"

    fun launcherCBTI() {
        val sp = App.getAppContext().getSharedPreferences(CBTI_LAUNCHER_FILE, Context.MODE_PRIVATE)
        val accountId = sp.getInt(ACCOUNT_KEY, 0)
        if (accountId <= 0) {//可能是体验账号或未在该手机上使用过的账号
            onLauncherCBTIIntroductionWeb()
        } else {
            val doctorInfo = AppManager.getAccountViewModel().getDoctorInfo().value
            val id = doctorInfo?.id ?: 0
            val isLookAccount = doctorInfo?.reviewStatus != 2
            val isVisitorAccount = doctorInfo?.isVisitorAccount() ?: true
            if (isLookAccount || isVisitorAccount) {//未认证/正在认证中/体验账号
                onLauncherCBTIIntroductionWeb()
            } else {
                if (accountId == id) {//同一个账号
                    val isLauncherIntroduction = sp.getBoolean(LAUNCHER_KEY, false)
                    if (isLauncherIntroduction) {//之前使用过，直接进入CBTI 列表介绍页
                        onLauncherCBTIIntroduction()
                    } else {//未使用过，先进入 web介绍页
                        onLauncherCBTIIntroductionWeb()
                    }
                } else {//不是同一个账号，进入 CBTI web介绍页
                    onLauncherCBTIIntroductionWeb()
                }
            }
        }
    }

    private fun onLauncherCBTIIntroduction() {
        CBTIIntroductionActivity.show()
    }

    private fun onLauncherCBTIIntroductionWeb() {
        CBTIIntroduction2WebActivity.show()
    }

    fun saveLauncherAction() {
        val isVisitorAccount = AppManager.getAccountViewModel().isVisitorAccount()
        val doctorInfo = AppManager.getAccountViewModel().getDoctorInfo().value
        val accountId = doctorInfo?.id ?: 0
        App.getAppContext().getSharedPreferences(CBTI_LAUNCHER_FILE, Context.MODE_PRIVATE).edit {
            if (isVisitorAccount) {
                putBoolean(LAUNCHER_KEY, false)
                putInt(ACCOUNT_KEY, 0)
            } else {
                putBoolean(LAUNCHER_KEY, true)
                putInt(ACCOUNT_KEY, accountId)
            }
        }
    }
}