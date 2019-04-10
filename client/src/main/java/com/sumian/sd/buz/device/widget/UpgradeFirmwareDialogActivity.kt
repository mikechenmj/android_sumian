package com.sumian.sd.buz.device.widget

import android.content.Intent
import android.text.format.DateUtils
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.R
import com.sumian.sd.buz.upgrade.activity.DeviceVersionNoticeActivity

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
        const val KEY_TYPE_MONITOR = 0
        const val KEY_TYPE_SLEEPER = 1
        private const val SHOW_UPGRADE_MONITOR_DIALOG_TIME = "SHOW_UPGRADE_MONITOR_DIALOG_TIME"

        /**
         * @param type
         * @see KEY_TYPE_MONITOR,
         * @see KEY_TYPE_SLEEPER
         */
        fun start(monitorHasNewVersion: Boolean) {
            val spKey = SHOW_UPGRADE_MONITOR_DIALOG_TIME
            if (System.currentTimeMillis() - SPUtils.getInstance().getLong(spKey) < DateUtils.DAY_IN_MILLIS) {
                return
            }
            SPUtils.getInstance().put(spKey, System.currentTimeMillis())
            val intent = Intent(ActivityUtils.getTopActivity(), UpgradeFirmwareDialogActivity::class.java)
            intent.putExtra(KEY_TYPE, if (monitorHasNewVersion) KEY_TYPE_MONITOR else KEY_TYPE_MONITOR)
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
                .setTitleText(if (type == KEY_TYPE_MONITOR) R.string.monitor_firmware_upgrade else R.string.sleeper_firmware_upgrade)
                .setMessageText(if (type == KEY_TYPE_MONITOR) R.string.monitor_firmware_upgrade_hint else R.string.sleeper_firmware_upgrade_hint)
                .setLeftBtn(R.string.cancel, null)
                .setRightBtn(R.string.confirm, View.OnClickListener {
                    ActivityUtils.startActivity(DeviceVersionNoticeActivity::class.java)
                })
                .whitenLeft()
        dialog.setOnDismissListener { finish() }
        dialog.show()
    }
}