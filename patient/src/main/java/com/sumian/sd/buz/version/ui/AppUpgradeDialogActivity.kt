package com.sumian.sd.buz.version.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.format.DateUtils
import android.view.KeyEvent
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.R
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.common.utils.UiUtils

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/27 16:51
 * desc   :
 * version: 1.0
 */
class AppUpgradeDialogActivity : BaseActivity() {

    companion object {
        private const val KEY_FORCE = "KEY_FORCE"
        private const val KEY_MSG = "KEY_MSG"
        private const val SHOW_UPGRADE_DIALOG_TIME = "SHOW_UPGRADE_DIALOG_TIME"

        fun start(force: Boolean = true, msg: String?) {
            if (!DialogManager.canShow(DialogManager.DIALOG_TYPE_APP, force)) {
                return
            }
            val spKey = SHOW_UPGRADE_DIALOG_TIME
            if (!force) {
                if (System.currentTimeMillis() - SPUtils.getInstance().getLong(spKey) < DateUtils.DAY_IN_MILLIS) {
                    return
                }
            }
            SPUtils.getInstance().put(spKey, System.currentTimeMillis())
            val intent = Intent(ActivityUtils.getTopActivity(), AppUpgradeDialogActivity::class.java)
            intent.putExtra(KEY_FORCE, force)
            intent.putExtra(KEY_MSG, msg)
            ActivityUtils.startActivity(intent)
            DialogManager.isAppForceUpgrade = force
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_update
    }

    override fun portrait(): Boolean {
        return false
    }

    override fun initWidget() {
        super.initWidget()
        val force = isForceUpgrade()
        val msg = intent.getStringExtra(KEY_MSG)
        showDialog(force, msg)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DialogManager.isAppUpgradeDialogShowing = true
    }

    override fun onDestroy() {
        super.onDestroy()
        DialogManager.isAppUpgradeDialogShowing = false
    }

    private fun isForceUpgrade() = intent.getBooleanExtra(KEY_FORCE, false)

    private fun showDialog(force: Boolean, msg: String) {
        SumianDialog(this)
                .setTopIcon(R.drawable.ic_notification_alert)
                .showCloseIcon(!force)
                .setTitleText(R.string.version_upgrade_title)
                .setMessageText(if (TextUtils.isEmpty(msg)) App.getAppContext().getString(R.string.have_a_new_version) else msg)
                .setRightBtn(R.string.go_to_experience, View.OnClickListener { UiUtils.openAppInMarket(App.getAppContext()) }, !force)
                .setCanceledOnTouchOutsideWrap(false)
                .setOnDismissListenerWrap(DialogInterface.OnDismissListener { finish() })
                .setOnKeyListenerWrap(object : DialogInterface.OnKeyListener {
                    override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
                        if (force && keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                            AppManager.exitApp()
                            return true
                        }
                        return false
                    }
                })
                .show()
    }

    override fun onBackPressed() {
        if (!isForceUpgrade()) {
            super.onBackPressed()
        }
    }

}