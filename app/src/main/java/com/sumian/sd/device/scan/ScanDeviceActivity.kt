package com.sumian.sd.device.scan

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.mvp.IPresenter
import com.sumian.hw.utils.JsonUtil
import com.sumian.sd.R
import com.sumian.sd.device.bean.BlueDevice

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/13 22:23
 * desc   :
 * version: 1.0
 */
class ScanDeviceActivity : BasePresenterActivity<IPresenter>() {
    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_scan_device
    }

    companion object {
        const val DATA = "data"

        fun startForResult(fragment: androidx.fragment.app.Fragment, requestCode: Int) {
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