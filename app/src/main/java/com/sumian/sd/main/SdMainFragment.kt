package com.sumian.sd.main

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.view.View
import com.sumian.hw.base.HwBasePresenter
import com.sumian.hw.leancloud.HwLeanCloudHelper
import com.sumian.hw.utils.FragmentUtil
import com.sumian.sd.R
import com.sumian.sd.base.BaseEventFragment
import com.sumian.sd.base.SdBaseFragment
import com.sumian.sd.diary.DiaryFragment
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.event.NotificationUnreadCountChangeEvent
import com.sumian.sd.event.SwitchMainActivityEvent
import com.sumian.sd.homepage.HomepageFragment
import com.sumian.sd.notification.NotificationViewModel
import com.sumian.sd.tab.DoctorFragment
import com.sumian.sd.tab.MeFragment
import com.sumian.sd.utils.AppUtil
import com.sumian.sd.utils.StatusBarUtil
import com.sumian.sd.utils.SumianExecutor
import com.sumian.sd.widget.nav.BottomNavigationBar
import com.sumian.sd.widget.nav.NavigationItem
import kotlinx.android.synthetic.main.sd_fragment_main.*
import org.greenrobot.eventbus.Subscribe

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/10 16:17
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SdMainFragment : BaseEventFragment<HwBasePresenter>(), BottomNavigationBar.OnSelectedTabChangeListener, OnEnterListener, HwLeanCloudHelper.OnShowMsgDotCallback {

    companion object {

        private const val REQUEST_CODE_OPEN_NOTIFICATION = 1

    }

    private var mCurrentPosition = -1
    private val mFragmentTags = arrayOf(
            HomepageFragment::class.java.simpleName,
            DiaryFragment::class.java.simpleName,
            DoctorFragment::class.java.simpleName,
            MeFragment::class.java.simpleName)

    override fun getLayoutId(): Int {
        return R.layout.sd_fragment_main
    }

    override fun initWidget(root: View?) {
        nav_tab.setOnSelectedTabChangeListener(this)
    }

    override fun initData() {
        super.initData()
        //注册站内信消息接收容器
        HwLeanCloudHelper.addOnAdminMsgCallback(this)
    }

    override fun onStart() {
        super.onStart()
        updateNotificationUnreadCount()
    }

    private fun launchAnotherMainActivity() {
        EventBusUtil.postEvent(SwitchMainActivityEvent(SwitchMainActivityEvent.TYPE_HW_ACTIVITY))
    }

    private fun changeStatusBarColorByPosition(position: Int) {
        val activity = activity ?: return
        if (position == 0) {
            StatusBarUtil.setStatusBarTextColorDark(activity, false)
        } else {
            StatusBarUtil.setStatusBarTextColorDark(activity, true)
        }
    }

    override fun onSelectedTabChange(navigationItem: NavigationItem, position: Int) {
        if (mCurrentPosition == position) {
            return
        }
        showFragmentByPosition(position)
        mCurrentPosition = position
        changeStatusBarColorByPosition(position)
    }

    private fun showFragmentByPosition(position: Int) {
        FragmentUtil.switchFragment(R.id.sd_main_fragment_container, fragmentManager!!, mFragmentTags, position,
                object : FragmentUtil.FragmentCreator {
                    override fun createFragmentByPosition(position: Int): Fragment {
                        return when (position) {
                            0 -> SdBaseFragment.newInstance(HomepageFragment::class.java)
                            1 -> DiaryFragment()
                            2 -> SdBaseFragment.newInstance(DoctorFragment::class.java)
                            3 -> SdBaseFragment.newInstance(MeFragment::class.java)
                            else -> SdBaseFragment.newInstance(HomepageFragment::class.java)
                        }
                    }
                })
    }

    override fun openEventBus(): Boolean {
        return true
    }

    @Subscribe(sticky = true)
    public fun onNotificationUnreadCountChangeEvent(event: NotificationUnreadCountChangeEvent) {
        updateNotificationUnreadCount()
        EventBusUtil.removeStickyEvent(event)
    }

    private fun updateNotificationUnreadCount() {
        ViewModelProviders.of(activity!!)
                .get(NotificationViewModel::class.java)
                .updateUnreadCount()
    }

    fun onBackPressed() {
        AppUtil.exitApp()
    }

    private fun showTabAccordingToData(data: String?) {
        if (data == null) {
            if (fragmentManager?.findFragmentByTag(mFragmentTags[0]) == null) {
                nav_tab.selectItem(0, true)
            }
        } else {
            when (data) {
//                MainActivity.TAB_0 -> nav_tab.selectItem(0, true)
//                MainActivity.TAB_1 -> nav_tab.selectItem(1, true)
//                MainActivity.TAB_2 -> nav_tab.selectItem(2, true)
//                MainActivity.TAB_3 -> nav_tab.selectItem(3, true)
            }
        }
    }

    override fun onEnter(data: String?) {
        showTabAccordingToData(data)
        updateNotificationUnreadCount()
    }

    override fun onShowMsgDotCallback(adminMsgLen: Int, doctorMsgLen: Int, customerMsgLen: Int) {
        onHideMsgCallback(adminMsgLen, doctorMsgLen, customerMsgLen)
    }

    override fun onHideMsgCallback(adminMsgLen: Int, doctorMsgLen: Int, customerMsgLen: Int) {
        SumianExecutor.runOnUiThread({
            this.tb_doctor?.showDot(if (adminMsgLen > 0 || doctorMsgLen > 0 || customerMsgLen > 0) android.view.View.VISIBLE else android.view.View.GONE)
            this.tb_me?.showDot(if (adminMsgLen > 0 || doctorMsgLen > 0 || customerMsgLen > 0) android.view.View.VISIBLE else android.view.View.GONE)
        })
    }
}