package com.sumian.sd.buz.device.widget

import android.content.Intent
import android.text.format.DateUtils
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.R
import com.sumian.sd.app.App
import com.sumian.sd.buz.upgrade.activity.DeviceVersionNoticeActivity
import com.sumian.sd.common.utils.UiUtils

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/27 16:51
 * desc   :
 * version: 1.0
 */
class UpgradeFirmwareDialogActivity : BaseActivity() {

    companion object {
        private const val KEY_TYPE = "KEY_TYPE"
        const val TYPE_MONITOR = 0
        const val TYPE_SLEEP_MASTER = 1
        const val TYPE_APP = 2
        private const val SHOW_UPGRADE_MONITOR_DIALOG_TIME = "SHOW_UPGRADE_MONITOR_DIALOG_TIME"

        /**
         * @param type
         * @see TYPE_MONITOR,
         * @see TYPE_SLEEP_MASTER
         */
        fun start(type: Int) {
            val spKey = SHOW_UPGRADE_MONITOR_DIALOG_TIME
            if (System.currentTimeMillis() - SPUtils.getInstance().getLong(spKey) < DateUtils.DAY_IN_MILLIS) {
                return
            }
            SPUtils.getInstance().put(spKey, System.currentTimeMillis())
            val intent = Intent(ActivityUtils.getTopActivity(), UpgradeFirmwareDialogActivity::class.java)
            intent.putExtra(KEY_TYPE, type)
            ActivityUtils.startActivity(intent)
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
        showUpgradeFirmwareDialog(intent.getIntExtra(KEY_TYPE, 0))
    }

    /**
     * @param type 0 monitor 1 sleeper
     */
    private fun showUpgradeFirmwareDialog(type: Int) {
        val dialog = SumianDialog(this)
                .setTitleText(if (type == TYPE_APP) R.string.app_upgrade_dialog_title else R.string.device_upgrade_dialog_title)
                .setMessageText(if (type == TYPE_APP) R.string.app_upgrade_dialog_hint else R.string.device_upgrade_dialog_hint)
                .setTopIcon(R.drawable.popups_icon_update)
                .showCloseIcon(true)
                .setRightBtn(R.string.upgrade_now, View.OnClickListener {
                    if (type == TYPE_APP) {
                        UiUtils.openAppInMarket(App.getAppContext())
                    } else {
                        ActivityUtils.startActivity(DeviceVersionNoticeActivity::class.java)
                    }
                })
                .whitenLeft()
        dialog.setOnDismissListener { finish() }
        dialog.show()
    }
}