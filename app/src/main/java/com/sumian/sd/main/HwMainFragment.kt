package com.sumian.sd.main

import android.support.v4.app.Fragment
import android.view.View
import com.hyphenate.helpdesk.easeui.UIProvider
import com.sumian.common.network.response.ErrorResponse
import com.sumian.hw.base.HwBasePresenter
import com.sumian.hw.device.fragment.DeviceFragment
import com.sumian.hw.leancloud.HwLeanCloudHelper
import com.sumian.hw.report.ReportFragment
import com.sumian.hw.tab.HwMeFragment
import com.sumian.hw.upgrade.model.VersionModel
import com.sumian.hw.utils.FragmentUtil
import com.sumian.sd.R
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.BaseEventFragment
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.event.SwitchMainActivityEvent
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.utils.AppUtil
import com.sumian.sd.utils.SumianExecutor
import com.sumian.sd.widget.nav.BottomNavigationBar
import com.sumian.sd.widget.nav.NavigationItem
import kotlinx.android.synthetic.main.hw_fragment_main.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/10 16:39
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class HwMainFragment : BaseEventFragment<HwBasePresenter>(), HwLeanCloudHelper.OnShowMsgDotCallback, VersionModel.ShowDotCallback, BottomNavigationBar.OnSelectedTabChangeListener, OnEnterListener {

    override fun getLayoutId(): Int {
        return R.layout.hw_fragment_main
    }

    private val mFragmentTags = arrayOf(
            DeviceFragment::class.java.simpleName,
            ReportFragment::class.java.simpleName,
            HwMeFragment::class.java.simpleName)

    override fun initWidget(root: View?) {
        super.initWidget(root)
        //注册站内信消息接收容器
        HwLeanCloudHelper.addOnAdminMsgCallback(this)
        AppManager.getVersionModel().registerShowDotCallback(this)
        nav_tab.setOnSelectedTabChangeListener(this)
        iv_switch.setOnClickListener {
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
        val call = AppManager.getHwHttpService().sendHeartbeats("open_app")
        call.enqueue(object : BaseSdResponseCallback<Any?>() {
            override fun onFailure(errorResponse: ErrorResponse) {

            }

            override fun onSuccess(response: Any?) {

            }

        })
    }

    private fun syncUserInfo() {
        val call = AppManager.getSdHttpService().getUserProfile()
        call.enqueue(object : BaseSdResponseCallback<UserInfo>() {
            override fun onFailure(errorResponse: ErrorResponse) {
            }

            override fun onSuccess(response: UserInfo?) {
                AppManager.getAccountViewModel().updateUserInfo(response)
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
            this.tb_me.showDot(if (adminMsgLen > 0 || doctorMsgLen > 0 || customerMsgLen > 0) View.VISIBLE else View.GONE)
        })
    }

    override fun showDot(isShowAppDot: Boolean, isShowMonitorDot: Boolean, isShowSleepyDot: Boolean) {
        SumianExecutor.runOnUiThread({ this.tb_me.showDot(if (isShowAppDot || isShowMonitorDot || isShowSleepyDot) View.VISIBLE else View.GONE) })
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

    private fun showTabAccordingToData(data: String?) {
        if (data == null) {
            if (fragmentManager?.findFragmentByTag(mFragmentTags[0]) == null) {
                nav_tab.selectItem(0, true)
            }
        } else {
            when (data) {
                MainActivity.TAB_HW_0 -> nav_tab.selectItem(0, true)
                MainActivity.TAB_HW_1 -> nav_tab.selectItem(1, true)
                MainActivity.TAB_HW_2 -> nav_tab.selectItem(2, true)
            }
        }
    }

    override fun onEnter(data: String?) {
        showTabAccordingToData(data)
        SumianExecutor.runOnUiThread({ tb_me?.showDot(if (HwLeanCloudHelper.isHaveCustomerMsg()) View.VISIBLE else View.GONE) })
    }
}