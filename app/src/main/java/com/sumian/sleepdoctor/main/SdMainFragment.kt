package com.sumian.sleepdoctor.main

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.support.v4.app.Fragment
import com.blankj.utilcode.util.SPUtils
import com.sumian.common.utils.SettingsUtil
import com.sumian.hw.utils.AppUtil
import com.sumian.hw.utils.FragmentUtil
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseEventFragment
import com.sumian.sleepdoctor.base.SdBaseFragment
import com.sumian.sleepdoctor.constants.SpKeys
import com.sumian.sleepdoctor.event.EventBusUtil
import com.sumian.sleepdoctor.event.NotificationReadEvent
import com.sumian.sleepdoctor.event.SwitchMainActivityEvent
import com.sumian.sleepdoctor.homepage.HomepageFragment
import com.sumian.sleepdoctor.notification.NotificationViewModel
import com.sumian.sleepdoctor.setting.version.delegate.VersionDelegate
import com.sumian.sleepdoctor.tab.DoctorFragment
import com.sumian.sleepdoctor.tab.MeFragment
import com.sumian.sleepdoctor.utils.NotificationUtil
import com.sumian.sleepdoctor.utils.StatusBarUtil
import com.sumian.sleepdoctor.widget.dialog.SumianAlertDialog
import com.sumian.sleepdoctor.widget.nav.BottomNavigationBar
import com.sumian.sleepdoctor.widget.nav.NavigationItem
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
class SdMainFragment : BaseEventFragment(), BottomNavigationBar.OnSelectedTabChangeListener, OnEnterListener {
    override fun getLayoutId(): Int {
        return R.layout.sd_fragment_main
    }

    private val REQUEST_CODE_OPEN_NOTIFICATION = 1

    private var mCurrentPosition = -1
    private val mFragmentTags = arrayOf(
            HomepageFragment::class.java.simpleName,
            DoctorFragment::class.java.simpleName,
            MeFragment::class.java.simpleName)
    private var mVersionDelegate: VersionDelegate? = null

    override fun initWidget() {
        nav_tab.setOnSelectedTabChangeListener(this)
        iv_switch.setOnClickListener {
            nav_tab.selectItem(0, true)
            launchAnotherMainActivity()
        }
        showOpenNotificationDialogIfNeeded()
    }

    override fun initData() {
        super.initData()
        this.mVersionDelegate = VersionDelegate.init()
    }

    private fun launchAnotherMainActivity() {
        EventBusUtil.postEvent(SwitchMainActivityEvent(SwitchMainActivityEvent.TYPE_HW_ACTIVITY))
    }

    private fun changeStatusBarColorByPosition(position: Int) {
        val activity = activity ?: return
        if (position == 0) {
            StatusBarUtil.setStatusBarColor(activity, Color.WHITE)
            StatusBarUtil.setStatusBarTextColor(activity, false)
        } else {
            StatusBarUtil.setStatusBarColor(activity, Color.TRANSPARENT)
            StatusBarUtil.setStatusBarTextColor(activity, true)
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
                            1 -> SdBaseFragment.newInstance(DoctorFragment::class.java)
                            2 -> SdBaseFragment.newInstance(MeFragment::class.java)
                            else -> SdBaseFragment.newInstance(HomepageFragment::class.java)
                        }
                    }
                })
    }

    override fun openEventBus(): Boolean {
        return true
    }

    override fun onResume() {
        super.onResume()
        mVersionDelegate!!.checkVersion(activity!!)
    }

    @Subscribe(sticky = true)
    fun onNotificationReadEvent(event: NotificationReadEvent) {
        EventBusUtil.removeStickyEvent(event)
        ViewModelProviders.of(this)
                .get(NotificationViewModel::class.java)
                .updateUnreadCount()
    }

    private fun showOpenNotificationDialogIfNeeded() {
        val previousShowTime = SPUtils.getInstance().getLong(SpKeys.SLEEP_RECORD_PREVIOUS_SHOW_NOTIFICATION_TIME, 0)
        val alreadyShowed = previousShowTime > 0
        if (NotificationUtil.areNotificationsEnabled(activity) || alreadyShowed) {
            return
        }
        SumianAlertDialog(activity)
                .setCloseIconVisible(true)
                .setTopIconResource(R.mipmap.ic_notification_alert)
                .setTitle(R.string.open_notification)
                .setMessage(R.string.open_notification_and_receive_doctor_response)
                .setRightBtn(R.string.open_notification) { SettingsUtil.launchSettingActivityForResult(this, REQUEST_CODE_OPEN_NOTIFICATION) }
                .show()
        SPUtils.getInstance().put(SpKeys.SLEEP_RECORD_PREVIOUS_SHOW_NOTIFICATION_TIME, System.currentTimeMillis())
    }

    fun onBackPressed() {
        AppUtil.exitApp()
    }

    private fun showTabAccordingToData() {
        val mPendingTabName = MainTabHelper.mPendingTabName
        if (mPendingTabName == null) {
            if (fragmentManager?.findFragmentByTag(mFragmentTags[0]) == null) {
                nav_tab.selectItem(0, true)
            }
        } else {
            when (mPendingTabName) {
                MainActivity.TAB_SD_0 -> nav_tab.selectItem(0, true)
                MainActivity.TAB_SD_1 -> nav_tab.selectItem(1, true)
                MainActivity.TAB_SD_2 -> nav_tab.selectItem(2, true)
            }
        }
        MainTabHelper.mPendingTabName = null
    }

    override fun onEnter() {
        showTabAccordingToData()
    }
}