package com.sumian.sd.device

import android.support.v4.app.Fragment
import com.sumian.common.base.BaseBackActivity
import com.sumian.hw.device.bean.BlueDevice
import com.sumian.hw.utils.FragmentUtil
import com.sumian.hw.utils.FragmentUtil.Companion.switchFragment
import com.sumian.sd.R
import com.sumian.sd.device.scan.ScanDeviceFragment

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/14 5:39
 * desc   :
 * version: 1.0
 */
class DeviceManageActivity : BaseBackActivity() {

    companion object {
        private val FRAGMENT_TAGS = arrayOf(DeviceManageFragment::class.java.simpleName, ScanDeviceFragment::class.java.simpleName)
    }

    private var mCurrentFragmentIndex = 0

    override fun getChildContentId(): Int {
        return R.layout.activity_device_manage
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.device_manage)
        switchFragment(0)
    }

    private fun switchFragment(position: Int) {
        mCurrentFragmentIndex = position
        switchFragment(R.id.vg, supportFragmentManager, FRAGMENT_TAGS, position, object : FragmentUtil.FragmentCreator {
            override fun createFragmentByPosition(position: Int): Fragment {
                return when (position) {
                    0 -> {
                        val deviceManageFragment = DeviceManageFragment()
                        deviceManageFragment.mHost = mHost
                        deviceManageFragment
                    }
                    else -> {
                        val scanDeviceFragment = ScanDeviceFragment()
                        scanDeviceFragment.mOnDeviceSelectedListener = mOnDeviceSelectedListener
                        scanDeviceFragment
                    }
                }
            }
        }, object : FragmentUtil.RunOnCommitCallback {
            override fun runOnCommit(selectFragment: Fragment) {
                if (selectFragment is ScanDeviceFragment) {
                    selectFragment.rollback()
                }
            }
        })
    }

    private val mHost = object : DeviceManageFragment.Host {
        override fun scanForDevice() {
            switchFragment(1)
        }

        override fun showBluetoothNotEnableUI() {
            switchFragment(1)
        }

    }

    private val mOnDeviceSelectedListener = object : ScanDeviceFragment.OnDeviceSelectedListener {
        override fun onDeviceSelected(device: BlueDevice) {
            DeviceManager.scanAndConnect(device)
            switchFragment(0)
        }
    }


    override fun onBackPressed() {
        if (mCurrentFragmentIndex == 1) {
            switchFragment(0)
        } else {
            super.onBackPressed()
        }
    }
}