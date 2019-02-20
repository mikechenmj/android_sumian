@file:Suppress("DEPRECATION")

package com.sumian.sd.buz.setting.version.delegate

import android.app.Activity
import android.content.DialogInterface
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import com.sumian.sd.R
import com.sumian.sd.app.App
import com.sumian.sd.buz.account.login.LoginActivity
import com.sumian.sd.buz.setting.version.bean.Version
import com.sumian.sd.buz.setting.version.contract.VersionContract
import com.sumian.sd.buz.setting.version.presenter.VersionPresenter
import com.sumian.sd.buz.setting.version.widget.AppVersionUpgradeDialog
import com.sumian.sd.common.utils.UiUtils
import com.sumian.sd.main.MainActivity
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

    private var showDotRunnable: Runnable? = null
    private var hideDotRunnable: Runnable? = null

    override fun onGetVersionSuccess(version: Version) {
        this.mVersion = version
    }

    override fun onGetVersionFailed(error: String) {
    }

    override fun onHaveUpgrade(isHaveUpgrade: Boolean, isHaveForce: Boolean, isShowDialog: Boolean, versionMsg: String?) {
        hideDotRunnable?.run()
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
                    .setRightBtn(R.string.go_to_experience, this)
                    .setCancelable(false)
                    .setOnKeyListener(this)
                    .show()
            VersionDialogAlertUtils.saveAlertTime()

            showDotRunnable?.run()
        } else {

            if (!isShowDialog) {
                showDotRunnable?.run()
                return
            }
            if (isHaveUpgrade) {
                showDotRunnable?.run()
            } else {
                hideDotRunnable?.run()
            }

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
                        .setRightBtn(R.string.go_to_experience, this)
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

    fun checkVersionCallback(activity: Activity, showDotRunnable: Runnable?, hideDotRunnable: Runnable?) {
        this.mActivity = activity
        this.mPresenter.getVersion()
        this.showDotRunnable = showDotRunnable
        this.hideDotRunnable = hideDotRunnable
    }

}