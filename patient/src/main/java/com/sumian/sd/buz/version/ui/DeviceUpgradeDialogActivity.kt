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
import com.sumian.device.manager.DeviceManager
import com.sumian.sd.R
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.upgrade.activity.DeviceVersionNoticeActivity
import com.sumian.sd.common.utils.EventBusUtil
import com.sumian.sd.common.utils.UiUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/27 16:51
 * desc   :
 * version: 1.0
 */
class DeviceUpgradeDialogActivity : BaseActivity() {

    companion object {
        private const val KEY_TYPE = "KEY_TYPE"
        private const val KEY_FORCE = "KEY_FORCE"
        private const val KEY_MSG = "KEY_MSG"
        const val TYPE_MONITOR = 0
        const val TYPE_SLEEP_MASTER = 1
        const val TYPE_APP = 2
        private const val SHOW_UPGRADE_DIALOG_TIME = "DeviceUpgradeDialogActivity.SHOW_UPGRADE_DIALOG_TIME"
        private const val SHOW_UPGRADE_DIALOG_TIME_FORCE = "DeviceUpgradeDialogActivity.SHOW_UPGRADE_DIALOG_TIME_FORCE"

        /**
         * @param type
         * @see TYPE_MONITOR,
         * @see TYPE_SLEEP_MASTER
         * @see TYPE_APP
         *
         * @param force 固件强制升级，每次连接都弹，普通升级 每天弹一次
         */
        fun start(type: Int, force: Boolean = false, msg: String? = null) {
            if (!DialogManager.canShow(DialogManager.DIALOG_TYPE_DEVICE, force)) {
                return
            }
            val spKey = if (force) SHOW_UPGRADE_DIALOG_TIME_FORCE else SHOW_UPGRADE_DIALOG_TIME
            val showDialogInterval = if (force) DateUtils.SECOND_IN_MILLIS * 2 else DateUtils.DAY_IN_MILLIS
            if (System.currentTimeMillis() - SPUtils.getInstance().getLong(spKey) < showDialogInterval) {
                return
            }
            SPUtils.getInstance().put(spKey, System.currentTimeMillis())
            val intent = Intent(ActivityUtils.getTopActivity(), DeviceUpgradeDialogActivity::class.java)
            intent.putExtra(KEY_TYPE, type)
            intent.putExtra(KEY_FORCE, force)
            intent.putExtra(KEY_MSG, msg)
            ActivityUtils.startActivity(intent)
            DialogManager.isDeviceForceUpgrade = force
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
        val type = intent.getIntExtra(KEY_TYPE, TYPE_MONITOR)
        val force = intent.getBooleanExtra(KEY_FORCE, false)
        val msg = intent.getStringExtra(KEY_MSG)
        showDialog(type, force, msg)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DialogManager.isDeviceUpgradeDialogShowing = true
        EventBusUtil.register(this)
    }

    override fun onResume() {
        super.onResume()
        if (!DeviceManager.isMonitorConnected()) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        DialogManager.isDeviceUpgradeDialogShowing = false
        EventBusUtil.unregister(this)
    }

    override fun onNewIntent(intent: Intent?) {
        DialogManager.isDeviceUpgradeDialogShowing = true
        val type = intent?.getIntExtra(KEY_TYPE, TYPE_MONITOR)
        val force = intent?.getBooleanExtra(KEY_FORCE, false)
        val msg = intent?.getStringExtra(KEY_MSG)
        if (type != null && force != null && msg != null) {
            showDialog(type, force, msg)
        }
        super.onNewIntent(intent)
    }

    /**
     * @param type 0 monitor 1 sleeper
     */
    private fun showDialog(type: Int, force: Boolean, msg: String?) {
        val message = if (TextUtils.isEmpty(msg)) {
            getString(if (type == TYPE_APP) R.string.app_upgrade_dialog_hint else R.string.device_upgrade_dialog_hint)
        } else {
            msg
        }
        SumianDialog(this)
                .setTitleText(if (type == TYPE_APP) R.string.app_upgrade_dialog_title else R.string.device_upgrade_dialog_title)
                .setMessageText(message ?: "")
                .setTopIcon(R.drawable.popups_icon_update)
                .showCloseIcon(!force)
                .setRightBtn(R.string.upgrade_now, View.OnClickListener {
                    if (type == TYPE_APP) {
                        UiUtils.openAppInMarket(App.getAppContext())
                    } else {
                        var intent = Intent(this, DeviceVersionNoticeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        ActivityUtils.startActivity(intent)
                    }
                }, !force)
                .setOnDismissListenerWrap(DialogInterface.OnDismissListener { finish() })
                .setCanceledOnTouchOutsideWrap(false)
                .setOnKeyListenerWrap(object : DialogInterface.OnKeyListener {
                    override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
                        if (force && keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                            AppManager.exitApp()
                            return true
                        }
                        return false
                    }
                })
                .whitenLeft()
                .show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDfuUpgradeSuccess(event: DfuUpgradeSuccessEvent) {
        finish()
    }

    class DfuUpgradeSuccessEvent
}