package com.sumian.sd.buz.device.scan

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.sumian.common.base.BaseActivity
import com.sumian.common.utils.JsonUtil
import com.sumian.device.data.DeviceType
import com.sumian.sd.R
import com.sumian.sd.buz.devicemanager.BlueDevice
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.buz.upgrade.fragment.ScanUpgradeFragment

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/13 22:23
 * desc   :
 * version: 1.0
 */
class ScanDeviceActivity : BaseActivity() {
    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_scan_device
    }

    companion object {
        const val DATA = "data"
        const val EXTRA_SCAN_FOR_UPGRADE = "extra_scan_for_upgrade"
        const val EXTRA_SCAN_TYPE = "extra_scan_type"

        fun startForResult(fragment: Fragment, requestCode: Int) {
            fragment.startActivityForResult(Intent(fragment.context, ScanDeviceActivity::class.java), requestCode)
        }

        fun startForUpgrade(context: Context, type: Int) {
            var intent = Intent(context, ScanDeviceActivity::class.java)
            intent.putExtra(EXTRA_SCAN_FOR_UPGRADE, true)
            intent.putExtra(EXTRA_SCAN_TYPE, type)
            context.startActivity(intent)
        }
    }

    override fun getPageName(): String {
        return StatConstants.page_add_device
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.add_device)
        var fragment: Fragment? = null
        if (intent.getBooleanExtra(EXTRA_SCAN_FOR_UPGRADE, false)) {
            var type = intent.getIntExtra(EXTRA_SCAN_TYPE, -1)
            fragment = ScanUpgradeFragment(type)
        } else {
            fragment = ScanDeviceFragment()
            fragment.mOnDeviceSelectedListener = object : ScanDeviceFragment.OnDeviceSelectedListener {
                override fun onDeviceSelected(device: BlueDevice) {
                    val intent = Intent()
                    intent.putExtra(DATA, JsonUtil.toJson(device))
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.scan_device_fragment, fragment)
        transaction.commit()
    }
}