package com.sumian.sd.device.scan

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment
import com.sumian.common.base.BaseBackActivity
import com.sumian.hw.device.bean.BlueDevice
import com.sumian.hw.utils.JsonUtil
import com.sumian.sd.R

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/13 22:23
 * desc   :
 * version: 1.0
 */
class ScanDeviceActivity : BaseBackActivity() {
    override fun getChildContentId(): Int {
        return R.layout.activity_scan_device
    }

    companion object {
        const val DATA = "data"

        fun startForResult(fragment: Fragment, requestCode: Int) {
            fragment.startActivityForResult(Intent(fragment.context, ScanDeviceActivity::class.java), requestCode)
        }
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.add_device)
        val fragment = supportFragmentManager.findFragmentById(R.id.scan_device_fragment) as ScanDeviceFragment
        fragment.mOnDeviceSelectedListener = object : ScanDeviceFragment.OnDeviceSelectedListener {
            override fun onDeviceSelected(device: BlueDevice) {
                val intent = Intent()
                intent.putExtra(DATA, JsonUtil.toJson(device))
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}