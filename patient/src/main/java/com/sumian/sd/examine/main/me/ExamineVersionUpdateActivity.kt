package com.sumian.sd.examine.main.me

import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseActivity
import com.sumian.device.data.SumianDevice
import com.sumian.device.manager.DeviceManager
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.common.utils.UiUtils
import kotlinx.android.synthetic.main.examine_version_update.*
import java.util.*

class ExamineVersionUpdateActivity : BaseActivity() {

    companion object {
        fun show() {
            ActivityUtils.startActivity(ExamineVersionUpdateActivity::class.java)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.examine_version_update
    }

    override fun initWidget() {
        super.initWidget()
        examine_title_bar.setOnBackClickListener { finish() }
        tv_app_version_name.text = formatVersion("APP版本：", UiUtils.getPackageInfo(this).versionName)
        val device: SumianDevice? = DeviceManager.getDevice()
        tv_monitor_version_name.text = "监测仪版本：未连接"
        tv_sleepy_version_name.text = "速眠仪版本：未连接"
        if (device != null) {
            if (device.monitorVersionInfo != null) {
                tv_monitor_version_name.text = formatVersion("监测仪版本：", device.monitorVersionInfo?.softwareVersion
                        ?: "未连接")
            }
            if (device.sleepMasterVersionInfo != null) {
                tv_sleepy_version_name.text = formatVersion("速眠仪版本：", device.sleepMasterVersionInfo?.softwareVersion
                        ?: "未连接")
            }
        }
    }

    private fun formatVersion(versionLabel: String, version: String): String {
        return String.format(Locale.getDefault(), "%s%s%s", versionLabel, " ", version)
    }
}