package com.sumian.sd.examine.main

import android.Manifest
import androidx.fragment.app.Fragment
import com.sumian.common.base.BaseActivity
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.device.devicemanage.DeviceManageFragment
import com.sumian.sd.buz.diary.monitorrecord.MonitorDataVpFragment
import com.sumian.sd.examine.main.fragment.ExamineConsultantFragment
import com.sumian.sd.examine.main.fragment.ExamineDeviceFragment
import com.sumian.sd.examine.main.me.ExamineMeFragment
import com.sumian.sd.examine.main.fragment.ExamineReportFragment
import com.sumian.sd.examine.widget.NavTab
import com.sumian.sd.examine.widget.TabButton
import com.sumian.sd.main.OnEnterListener
import kotlinx.android.synthetic.main.activity_examine_main.*
import pub.devrel.easypermissions.EasyPermissions

class ExamineMainActivity : BaseActivity(), NavTab.OnTabChangeListener {

    private val mPagerFragments by lazy {
        mutableListOf<Fragment>(DeviceManageFragment(),
                MonitorDataVpFragment(), ExamineConsultantFragment(), ExamineMeFragment())
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_examine_main
    }

    override fun initWidget() {
        super.initWidget()
        tab_main.setOnTabChangeListener(this)
        tab_main.onClick(tab_device)
    }

    override fun initData() {
        super.initData()
        AppManager.onMainActivityCreate()
        requestPermission()
    }

    override fun tab(tabButton: TabButton?, position: Int) {
        for (i in mPagerFragments.indices) {
            var fragment = mPagerFragments[i]
            val tag = fragment::class.java.simpleName
            val fragmentByTag = supportFragmentManager.findFragmentByTag(tag)
            if (fragmentByTag != null) {
                fragment = fragmentByTag
            }
            if (position == i) {
                var transaction = if (fragment.isAdded) {
                    supportFragmentManager.beginTransaction().show(fragment)
                } else {
                    supportFragmentManager.beginTransaction().add(R.id.main_container, fragment, tag)
                }
                if (fragmentByTag is OnEnterListener) {
                    transaction.runOnCommit { fragmentByTag.onEnter(null) }
                }
                transaction.commit()
            } else {
                if (fragment.isAdded) {
                    supportFragmentManager.beginTransaction().hide(fragment).commit()
                }
            }
        }
    }

    private fun requestPermission() {
        val readPhoneStatePermission = Manifest.permission.READ_PHONE_STATE
        val hasPermissions = EasyPermissions.hasPermissions(this, readPhoneStatePermission)
        if (!hasPermissions) {
            EasyPermissions.requestPermissions(this, "获取手机状态信息", 100, readPhoneStatePermission)
        }
    }

}