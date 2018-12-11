@file:Suppress("DEPRECATION")

package com.sumian.sd.setting.version.delegate

import android.app.Activity
import android.content.DialogInterface
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import com.sumian.sd.R
import com.sumian.sd.account.login.LoginActivity
import com.sumian.sd.app.App
import com.sumian.sd.main.MainActivity
import com.sumian.sd.setting.version.bean.Version
import com.sumian.sd.setting.version.contract.VersionContract
import com.sumian.sd.setting.version.presenter.VersionPresenter
import com.sumian.sd.setting.version.widget.AppVersionUpgradeDialog
import com.sumian.sd.utils.UiUtils
import com.sumian.sd.widget.dialog.theme.ITheme
import com.sumian.sd.widget.dialog.theme.LightUpgradeTheme
import com.sumian.sd.widget.dialog.theme.ThemeFactory

/**
 * <pre>
 *     @author : sm

 *     e-mail : yaoqi.y@sumian.com
 *     time: 2018/6/29 16:36
 *
 *     version: 1.0
 *
 *     desc:
 *
 * </pre>
 */
open class VersionDelegate private constructor() : VersionContract.View, View.OnClickListener, DialogInterface.OnKeyListener {

    companion object {

        @JvmStatic
        fun init(): VersionDelegate {
            return VersionDelegate()
        }
    }

    private lateinit var mVersion: Version

    private lateinit var mActivity: Activity

    override fun onGetVersionSuccess(version: Version) {
        this.mVersion = version
    }

    override fun onGetVersionFailed(error: String) {
    }

    override fun onHaveUpgrade(isHaveUpgrade: Boolean, isHaveForce: Boolean, versionMsg: String?) {
        if (isHaveForce) {
            AppVersionUpgradeDialog(mActivity)
                    .setTopIconResource(R.drawable.ic_popups_update)
                    .hideTopIcon(false)
                    .setTitle(R.string.version_upgrade_title)
                    .setTheme(createTheme())
                    .setVersionMsg(
                            if (TextUtils.isEmpty(versionMsg))
                                App.getAppContext().getString(R.string.force_upgrade_version)
                            else versionMsg)
                    .setRightBtn(R.string.sure, this)
                    .setCancelable(false)
                    .setOnKeyListener(this)
                    .show()
            VersionDialogAlertUtils.saveAlertTime()
        } else {
            if (isHaveUpgrade && VersionDialogAlertUtils.isCanAlert()) {
                AppVersionUpgradeDialog(mActivity)
                        .setTopIconResource(R.drawable.ic_popups_update)
                        .hideTopIcon(false)
                        .setCloseIconVisible(true)
                        .setTitle(R.string.version_upgrade_title)
                        .setTheme(createTheme())
                        .setVersionMsg(if (TextUtils.isEmpty(versionMsg))
                            App.getAppContext().getString(R.string.have_a_new_version)
                        else versionMsg)
                        .setRightBtn(R.string.sure, this)
                        .setCancelable(true)
                        .setOnKeyListener(this)
                        .show()
                VersionDialogAlertUtils.saveAlertTime()
            }
        }
    }

    override fun onClick(v: View?) {
        UiUtils.openAppInMarket(App.getAppContext())
    }

    override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
            dialog.cancel()
            mActivity.finishAffinity()
            return true
        }
        return false
    }

    private val mPresenter: VersionContract.Presenter by lazy {
        VersionPresenter.init(this)
    }

    private fun createTheme(): ITheme {
        return when (mActivity) {
            is MainActivity -> ThemeFactory.create(LightUpgradeTheme::class.java)
            is LoginActivity -> ThemeFactory.create(LightUpgradeTheme::class.java)
            else -> ThemeFactory.create(LightUpgradeTheme::class.java)
        }
    }

    fun checkVersion(activity: Activity) {
        this.mActivity = activity
        this.mPresenter.getVersion()
    }

}