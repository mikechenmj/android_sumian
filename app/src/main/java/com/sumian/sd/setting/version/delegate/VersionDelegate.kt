package com.sumian.sd.setting.version.delegate

import android.app.Activity
import android.content.DialogInterface
import android.view.KeyEvent
import android.view.View
import com.sumian.sd.R
import com.sumian.sd.account.login.LoginActivity
import com.sumian.sd.app.App
import com.sumian.sd.main.MainActivity
import com.sumian.sd.setting.version.bean.Version
import com.sumian.sd.setting.version.contract.VersionContract
import com.sumian.sd.setting.version.presenter.VersionPresenter
import com.sumian.sd.utils.UiUtils
import com.sumian.sd.widget.dialog.SumianAlertDialog
import com.sumian.sd.widget.dialog.theme.BlackTheme
import com.sumian.sd.widget.dialog.theme.ITheme
import com.sumian.sd.widget.dialog.theme.LightTheme
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

        fun init(): VersionDelegate {
            return VersionDelegate()
        }
    }

    private lateinit var mVersion: Version

    private lateinit var mActivity: Activity

    private var mIsHaveForce: Boolean = false

    override fun onGetVersionSuccess(version: Version) {
        this.mVersion = version
    }

    override fun onGetVersionFailed(error: String) {
    }

    override fun onHaveUpgrade(isHaveUpgrade: Boolean, isHaveForce: Boolean) {
        this.mIsHaveForce = isHaveForce
        if (isHaveForce) {
            SumianAlertDialog(mActivity)
                    .setTheme(createTheme())
                    .goneTopIcon(true)
                    .setTitle(R.string.version_upgrade)
                    .setMessage(R.string.force_upgrade_version)
                    .setRightBtn(R.string.sure, this)
                    .setCancelable(false)
                    .setOnKeyListener(this)
                    .show()
        } else {
//            if (isHaveUpgrade) {
//                SumianAlertDialog(mActivity)
//                        .setTheme(createTheme())
//                        .setTitle(R.string.version_upgrade)
//                        .setMessage(R.string.have_a_new_version)
//                        .setRightBtn(R.string.sure, this)
//                        .setCancelable(true)
//                        .setOnKeyListener(this)
//                        .show()
//            }
        }
    }

    private fun createTheme(): ITheme {
        return if (mActivity is MainActivity) {
            if ((mActivity as MainActivity).mIsBlackTheme) {
                ThemeFactory.create(BlackTheme::class.java)
            } else {
                ThemeFactory.create(LightTheme::class.java)
            }
        } else if (mActivity is LoginActivity) {
            ThemeFactory.create(BlackTheme::class.java)
        } else {
            ThemeFactory.create(LightTheme::class.java)
        }
    }

    private val mPresenter: VersionContract.Presenter by lazy {
        VersionPresenter.init(this)
    }

    fun checkVersion(activity: Activity) {
        this.mActivity = activity
        this.mPresenter.getVersion()
    }

    override fun onClick(v: View?) {
        UiUtils.openAppInMarket(App.getAppContext())
    }

    override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
            if (mIsHaveForce) {
                dialog.cancel()
                mActivity.finishAffinity()
            } else {
                dialog.cancel()
            }
            return true
        }
        return false
    }
}