package com.sumian.sddoctor.account.delegate

import android.app.Activity
import android.content.DialogInterface
import android.text.TextUtils
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import com.sumian.sddoctor.R
import com.sumian.sddoctor.account.bean.Version
import com.sumian.sddoctor.account.contract.VersionContract
import com.sumian.sddoctor.account.presenter.VersionPresenter
import com.sumian.sddoctor.account.widget.AppVersionUpgradeAlertDialog
import com.sumian.sddoctor.app.App
import com.sumian.sddoctor.util.UiUtils

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
@Suppress("DEPRECATION")
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
            AppVersionUpgradeAlertDialog(mActivity)
                    .setTitle(R.string.version_upgrade_title)
                    .setVersionMsg(
                            if (TextUtils.isEmpty(versionMsg))
                                App.getAppContext().getString(R.string.force_upgrade_version)
                            else versionMsg)
                    .setMessageGravity(Gravity.CENTER_HORIZONTAL)
                    .setRightBtn(R.string.go_to_experience, this)
                    .setCancelable(false)
                    .setTopIconResource(R.drawable.ic_popups_update)
                    .setOnKeyListener(this)
                    .show()
            VersionDialogAlertUtils.saveAlertTime()
        } else {
            if (isHaveUpgrade && VersionDialogAlertUtils.isCanAlert()) {
                AppVersionUpgradeAlertDialog(mActivity)
                        .setTitle(R.string.version_upgrade_title)
                        .setVersionMsg(if (TextUtils.isEmpty(versionMsg))
                            App.getAppContext().getString(R.string.have_a_new_version)
                        else versionMsg)
                        .setMessageGravity(Gravity.CENTER_HORIZONTAL)
                        .setCloseIconVisible(true)
                        .setRightBtn(R.string.go_to_experience, this)
                        .setTopIconResource(R.drawable.ic_popups_update)
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

    private val mPresenter: VersionPresenter by lazy {
        VersionPresenter.init(this)
    }

    fun checkVersion(activity: Activity) {
        this.mActivity = activity
        this.mPresenter.getVersion()
    }


}