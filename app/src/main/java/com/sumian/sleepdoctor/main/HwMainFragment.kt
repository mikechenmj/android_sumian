package com.sumian.sleepdoctor.main

import android.app.Application
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.hyphenate.helpdesk.easeui.UIProvider
import com.sumian.hw.base.HwBasePagerFragment
import com.sumian.hw.improve.consultant.ConsultantFragment
import com.sumian.hw.improve.device.fragment.DeviceFragment
import com.sumian.hw.improve.fragment.HwMeFragment
import com.sumian.hw.improve.main.HwMainActivity
import com.sumian.hw.improve.report.ReportFragment
import com.sumian.hw.leancloud.HwLeanCloudHelper
import com.sumian.hw.network.callback.BaseResponseCallback
import com.sumian.hw.push.ReportPushManager
import com.sumian.hw.upgrade.model.VersionModel
import com.sumian.hw.utils.AppUtil
import com.sumian.hw.widget.nav.NavTab
import com.sumian.hw.widget.nav.TabButton
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.account.bean.UserInfo
import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.BaseEventFragment
import com.sumian.sleepdoctor.event.EventBusUtil
import com.sumian.sleepdoctor.event.SwitchMainActivityEvent
import com.sumian.sleepdoctor.utils.SumianExecutor
import kotlinx.android.synthetic.main.hw_fragment_main.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/10 16:39
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class HwMainFragment : BaseEventFragment(), NavTab.OnTabChangeListener,
        HwLeanCloudHelper.OnShowMsgDotCallback, VersionModel.ShowDotCallback {

    override fun getLayoutId(): Int {
        return R.layout.hw_fragment_main
    }

    private val mFragmentTags = arrayOf("tab_0", "tab_1", "tab_2", "tab_3")

    private val KEY_PUSH_REPORT_SCHEME = "key_push_report_scheme"

    fun show(context: Context) {
        val intent = Intent(context, HwMainActivity::class.java)
        if (context is Application || context is Service) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(intent)
    }

    fun getLaunchIntentForPushReport(context: Context, scheme: String): Intent {
        val intent = Intent(context, HwMainActivity::class.java)
        intent.putExtra(KEY_PUSH_REPORT_SCHEME, scheme)
        return intent
    }

    override fun initBundle(bundle: Bundle) {
        val scheme = bundle.getString(KEY_PUSH_REPORT_SCHEME) ?: return
        ReportPushManager.getInstance().setPushReportByUriStr(scheme)
    }

//    protected override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        val pushReport = ReportPushManager.getInstance().pushReport
//        if (pushReport != null) {
//            tab_main.onClick(tab_report)
//        }
//    }

    override fun initWidget() {
        super.initWidget()
        tab_main.setOnTabChangeListener(this)
        //注册站内信消息接收容器
        HwLeanCloudHelper.addOnAdminMsgCallback(this)
        AppManager.getVersionModel().registerShowDotCallback(this)
        val pushReport = ReportPushManager.getInstance().pushReport
        if (pushReport != null) {
            tab_main.onClick(tab_report)
        } else {
            tab_main.onClick(tab_device)
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

    override fun tab(tabButton: TabButton, position: Int) {
        if (position == 2) {
            tab_main.onClick(tab_device)
            launchAnotherMainActivity()
            return
        }
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
                    fragmentManager!!.beginTransaction().show(fragmentByTag).runOnCommit({ fragmentByTag.onEnterTab() }).commit()
                } else {
                    fragmentManager!!.beginTransaction().add(R.id.main_container, fragmentByTag, tag).runOnCommit({ fragmentByTag.onEnterTab() }).commit()
                }
            } else {
                fragmentManager!!.beginTransaction().hide(fragmentByTag).commit()
            }
        }
    }

    private fun launchAnotherMainActivity() {
//        ActivityUtils.startActivity(SdMainActivity::class.java)
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
            // TODO 注释掉该功能,目前消息中心小红点变化转需求,移入下一个新迭代的新消息中心
            // this.tab_consultant.showDot(doctorMsgLen + customerMsgLen > 0 ? android.view.View.VISIBLE : android.view.View.GONE);
            this.tab_me.showDot(if (adminMsgLen > 0) android.view.View.VISIBLE else android.view.View.GONE)
        })
    }

    override fun showDot(isShowAppDot: Boolean, isShowMonitorDot: Boolean, isShowSleepyDot: Boolean) {
        // Log.e(TAG, "showDot: ------->" + isShowAppDot);
        SumianExecutor.runOnUiThread({ this.tab_me.showDot(if (isShowAppDot || isShowMonitorDot || isShowSleepyDot) android.view.View.VISIBLE else android.view.View.GONE) })
    }

    private fun createFragmentByPosition(position: Int): HwBasePagerFragment<*> {
        when (position) {
            0 -> return DeviceFragment.newInstance()
            1 -> return ReportFragment.newInstance()
            2 -> return ConsultantFragment.newInstance()
            3 -> return HwMeFragment.newInstance()
            else -> throw RuntimeException("Illegal tab position")
        }
    }
}