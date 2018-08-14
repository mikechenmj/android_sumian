package com.sumian.sleepdoctor.main

import android.support.v4.app.Fragment
import com.hyphenate.helpdesk.easeui.UIProvider
import com.sumian.hw.improve.device.fragment.DeviceFragment
import com.sumian.hw.improve.report.ReportFragment
import com.sumian.hw.improve.tab.HwMeFragment
import com.sumian.hw.leancloud.HwLeanCloudHelper
import com.sumian.hw.network.callback.BaseResponseCallback
import com.sumian.hw.upgrade.model.VersionModel
import com.sumian.hw.utils.AppUtil
import com.sumian.hw.utils.FragmentUtil
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.account.bean.UserInfo
import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.BaseEventFragment
import com.sumian.sleepdoctor.event.EventBusUtil
import com.sumian.sleepdoctor.event.SwitchMainActivityEvent
import com.sumian.sleepdoctor.utils.SumianExecutor
import com.sumian.sleepdoctor.widget.nav.BottomNavigationBar
import com.sumian.sleepdoctor.widget.nav.NavigationItem
import kotlinx.android.synthetic.main.hw_fragment_main.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/10 16:39
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class HwMainFragment : BaseEventFragment(), HwLeanCloudHelper.OnShowMsgDotCallback, VersionModel.ShowDotCallback, BottomNavigationBar.OnSelectedTabChangeListener, OnEnterListener {

    override fun getLayoutId(): Int {
        return R.layout.hw_fragment_main
    }

    private val mFragmentTags = arrayOf(
            DeviceFragment::class.java.simpleName,
            ReportFragment::class.java.simpleName,
            HwMeFragment::class.java.simpleName)

    override fun initWidget() {
        super.initWidget()
        //注册站内信消息接收容器
        HwLeanCloudHelper.addOnAdminMsgCallback(this)
        AppManager.getVersionModel().registerShowDotCallback(this)
        nav_tab.setOnSelectedTabChangeListener(this)
        iv_switch.setOnClickListener {
            nav_tab.selectItem(0, true)
            launchAnotherMainActivity()
        }
    }

    override fun initData() {
        super.initData()
        if (AppManager.getAccountViewModel().isLogin) {
            HwLeanCloudHelper.loginLeanCloud()
            HwLeanCloudHelper.registerPushService()
        }

        AppManager.getJobScheduler().checkJobScheduler()

        UIProvider.getInstance().showDotCallback { msgLength ->
            HwLeanCloudHelper.haveCustomerMsg(msgLength)
            onShowMsgDotCallback(0, 0, msgLength)
        }

        SumianExecutor.runOnUiThread(({
            syncUserInfo()
            sendHeartBeats()
        }), 200)

        SumianExecutor.runOnUiThread(({ HwLeanCloudHelper.haveCustomerMsg(UIProvider.getInstance().isHaveMsgSize) }), 500)
    }

    private fun sendHeartBeats() {
        val call = AppManager.getHwNetEngine().httpService.sendHeartbeats("open_app")
        call.enqueue(object : BaseResponseCallback<Any?>() {
            override fun onSuccess(response: Any?) {

            }

            override fun onFailure(code: Int, message: String) {

            }
        })
    }

    private fun syncUserInfo() {
        val call = AppManager.getHwNetEngine().httpService.getUserInfo("doctor")
        call.enqueue(object : BaseResponseCallback<UserInfo>() {
            override fun onSuccess(response: UserInfo) {
                AppManager.getAccountViewModel().updateUserInfo(response)
            }

            override fun onFailure(code: Int, message: String) {

            }
        })
    }

    private fun launchAnotherMainActivity() {
        EventBusUtil.postEvent(SwitchMainActivityEvent(SwitchMainActivityEvent.TYPE_SD_ACTIVITY))
    }

    fun onBackPressed() {
        AppUtil.exitApp()
    }

    override fun onDestroy() {
        super.onDestroy()
        val bluePeripheral = AppManager.getBlueManager().bluePeripheral
        bluePeripheral?.close()
        HwLeanCloudHelper.removeOnAdminMsgCallback(this)
        AppManager.getVersionModel().unRegisterShowDotCallback(this)
        AppManager.getBlueManager().release()
    }

    override fun onShowMsgDotCallback(adminMsgLen: Int, doctorMsgLen: Int, customerMsgLen: Int) {
        onHideMsgCallback(adminMsgLen, doctorMsgLen, customerMsgLen)
    }

    override fun onHideMsgCallback(adminMsgLen: Int, doctorMsgLen: Int, customerMsgLen: Int) {
        SumianExecutor.runOnUiThread({
            this.tb_me.showDot(if (adminMsgLen > 0) android.view.View.VISIBLE else android.view.View.GONE)
        })
    }

    override fun showDot(isShowAppDot: Boolean, isShowMonitorDot: Boolean, isShowSleepyDot: Boolean) {
        SumianExecutor.runOnUiThread({ this.tb_me.showDot(if (isShowAppDot || isShowMonitorDot || isShowSleepyDot) android.view.View.VISIBLE else android.view.View.GONE) })
    }

    override fun onSelectedTabChange(navigationItem: NavigationItem?, position: Int) {
        showFragmentByPosition(position)
    }

    private fun showFragmentByPosition(position: Int) {
        FragmentUtil.switchFragment(R.id.hw_main_fragment_container, fragmentManager!!, mFragmentTags, position,
                object : FragmentUtil.FragmentCreator {
                    override fun createFragmentByPosition(position: Int): Fragment {
                        return when (position) {
                            0 -> DeviceFragment.newInstance()
                            1 -> ReportFragment.newInstance()
                            2 -> HwMeFragment.newInstance()
                            else -> throw RuntimeException("Illegal tab position")
                        }
                    }
                })
    }

    private fun showTabAccordingToData() {
        val mPendingTabName = MainTabHelper.mPendingTabName
        if (mPendingTabName == null) {
            if (fragmentManager?.findFragmentByTag(mFragmentTags[0]) == null) {
                nav_tab.selectItem(0, true)
            }
        } else {
            when (mPendingTabName) {
                MainActivity.TAB_HW_0 -> nav_tab.selectItem(0, true)
                MainActivity.TAB_HW_1 -> nav_tab.selectItem(1, true)
                MainActivity.TAB_HW_2 -> nav_tab.selectItem(2, true)
            }
        }
        MainTabHelper.mPendingTabName = null
    }

    override fun onEnter() {
        showTabAccordingToData()
    }
}