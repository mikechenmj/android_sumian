package com.sumian.sleepdoctor.main

import com.hyphenate.helpdesk.easeui.UIProvider
import com.sumian.hw.base.HwBasePagerFragment
import com.sumian.hw.improve.device.fragment.DeviceFragment
import com.sumian.hw.improve.report.ReportFragment
import com.sumian.hw.improve.tab.HwMeFragment
import com.sumian.hw.leancloud.HwLeanCloudHelper
import com.sumian.hw.network.callback.BaseResponseCallback
import com.sumian.hw.push.ReportPushManager
import com.sumian.hw.upgrade.model.VersionModel
import com.sumian.hw.utils.AppUtil
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
class HwMainFragment : BaseEventFragment(), HwLeanCloudHelper.OnShowMsgDotCallback, VersionModel.ShowDotCallback, BottomNavigationBar.OnSelectedTabChangeListener {

    override fun getLayoutId(): Int {
        return R.layout.hw_fragment_main
    }

    private val mFragmentTags = arrayOf("tab_0", "tab_1", "tab_2")

    override fun initWidget() {
        super.initWidget()
        //注册站内信消息接收容器
        HwLeanCloudHelper.addOnAdminMsgCallback(this)
        AppManager.getVersionModel().registerShowDotCallback(this)
        nav_tab.setOnSelectedTabChangeListener(this)
        showFragmentAccordingToData(true)
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

    private fun createFragmentByPosition(position: Int): HwBasePagerFragment<*> {
        return when (position) {
            0 -> DeviceFragment.newInstance()
            1 -> ReportFragment.newInstance()
            2 -> HwMeFragment.newInstance()
            else -> throw RuntimeException("Illegal tab position")
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            showFragmentAccordingToData(false)
        }
    }

    private fun showFragmentAccordingToData(isInit: Boolean) {
        val pushReport = ReportPushManager.getInstance().pushReport
        if (pushReport != null) {
            nav_tab.selectItem(1, true)
        } else if (isInit) {
            nav_tab.selectItem(0, true)
        }
    }

    override fun onSelectedTabChange(navigationItem: NavigationItem?, position: Int) {
        selectTab(position)
    }

    fun selectTab(position: Int) {
        var fragmentByTag: HwBasePagerFragment<*>?
        var tag: String
        val len = mFragmentTags.size
        for (i in 0 until len) {
            tag = mFragmentTags[i]
            fragmentByTag = fragmentManager!!.findFragmentByTag(tag) as HwBasePagerFragment<*>?
            if (fragmentByTag == null) {
                fragmentByTag = createFragmentByPosition(i)
            }
            if (position == i) {
                if (fragmentByTag.isAdded) {
                    fragmentManager!!.beginTransaction().show(fragmentByTag).runOnCommit { fragmentByTag.onEnterTab() }.commit()
                } else {
                    fragmentManager!!.beginTransaction().add(R.id.main_container, fragmentByTag, tag).runOnCommit { fragmentByTag.onEnterTab() }.commit()
                }
            } else {
                fragmentManager!!.beginTransaction().hide(fragmentByTag).commit()
            }
        }
    }
}