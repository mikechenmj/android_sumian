package com.sumian.sleepdoctor.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FrameMetricsAggregator
import com.alipay.a.a.i
import com.blankj.utilcode.util.SPUtils
import com.sumian.common.utils.SettingsUtil
import com.sumian.hw.utils.AppUtil
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseEventFragment
import com.sumian.sleepdoctor.base.SdBaseActivity
import com.sumian.sleepdoctor.base.SdBaseFragment
import com.sumian.sleepdoctor.constants.SpKeys
import com.sumian.sleepdoctor.doctor.base.BasePagerFragment
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
class SdMainFragment : BaseEventFragment(), BottomNavigationBar.OnSelectedTabChangeListener {
    override fun getLayoutId(): Int {
        return R.layout.sd_fragment_main
    }

    val KEY_TAB_INDEX = "key_tab_index"
    val KEY_SLEEP_RECORD_TIME = "key_sleep_record_time"
    val KEY_SCROLL_TO_BOTTOM = "key_scroll_to_bottom"
    val REQUEST_CODE_OPEN_NOTIFICATION = 1

    private var mCurrentPosition = -1
    private val mFTags = arrayOf(HomepageFragment::class.java.simpleName, DoctorFragment::class.java.simpleName, "DeviceFragment", MeFragment::class.java.simpleName)
    private var mLaunchData: LaunchData<LaunchSleepTabBean>? = null
    private var mVersionDelegate: VersionDelegate? = null

    override fun initBundle(bundle: Bundle) {
        val launchTabIndex = bundle.getInt(KEY_TAB_INDEX)
        mLaunchData = LaunchData(launchTabIndex)
        if (launchTabIndex == 0) {
            val launchSleepRecordTime = bundle.getLong(KEY_SLEEP_RECORD_TIME, 0)
            val scrollToBottom = bundle.getBoolean(KEY_SCROLL_TO_BOTTOM, false)
            mLaunchData!!.data = LaunchSleepTabBean(launchSleepRecordTime, scrollToBottom)
        }
    }

    override fun initWidget() {
        nav_tab!!.setOnSelectedTabChangeListener(this)
    }

    override fun initData() {
        super.initData()
        this.mVersionDelegate = VersionDelegate.init()
        //commitReplace(HwWelcomeActivity.class);
        val position = if (mLaunchData == null) 0 else mLaunchData!!.tabIndex
        selectTab(position)
        nav_tab!!.selectItem(position, false)
        mLaunchData = null
        showOpenNotificationDialogIfNeeded()
    }

    private fun selectTab(position: Int) {
        if (mCurrentPosition == position) {
            return
        }
        if (position == 2) {
            nav_tab!!.selectItem(0, true)
            launchAnotherMainActivity()
            return
        }

        for (i in 0 until mFTags.size) {
            val tag = mFTags[i]
            var fragmentByTag = fragmentManager!!.findFragmentByTag(tag)
            if (fragmentByTag == null) {
                fragmentByTag = createFragmentByPosition(i)
            }
            if (position == i) {
                if (fragmentByTag.isAdded) {
                    fragmentManager!!.beginTransaction().show(fragmentByTag).runOnCommit { autoSelectDoctorTab(fragmentByTag) }.commit()
                } else {
                    fragmentManager!!.beginTransaction().add(R.id.lay_tab_container, fragmentByTag, tag).runOnCommit { autoSelectDoctorTab(fragmentByTag) }.commit()
                }
            } else {
                fragmentManager!!.beginTransaction().hide(fragmentByTag).commit()
            }
        }
        mCurrentPosition = position
        changeStatusBarColorByPosition(position)
    }

    private fun createFragmentByPosition(position: Int): Fragment {
        return when (position) {
            0 -> SdBaseFragment.newInstance(HomepageFragment::class.java)
            1 -> SdBaseFragment.newInstance(DoctorFragment::class.java)
            3 -> SdBaseFragment.newInstance(MeFragment::class.java)
            else -> SdBaseFragment.newInstance(HomepageFragment::class.java)
        }
    }

    private fun launchAnotherMainActivity() {
//        ActivityUtils.startActivity(HwMainActivity::class.java)
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

    private fun autoSelectDoctorTab(f: Fragment?) {
        if (f is BasePagerFragment<*>) {
            f.selectTab(1)
        }
    }

    override fun onSelectedTabChange(navigationItem: NavigationItem, position: Int) {
        if (mCurrentPosition == position) {
            return
        }
        selectTab(position)
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

    class LaunchSleepTabBean internal constructor(internal var sleepRecordTime: Long, internal var needScrollToBottom: Boolean)

    class LaunchData<T> internal constructor(internal var tabIndex: Int) {
        var data: T? = null
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
                .setRightBtn(R.string.open_notification) { v -> SettingsUtil.launchSettingActivityForResult(this, REQUEST_CODE_OPEN_NOTIFICATION) }
                .show()
        SPUtils.getInstance().put(SpKeys.SLEEP_RECORD_PREVIOUS_SHOW_NOTIFICATION_TIME, System.currentTimeMillis())
    }

    fun onBackPressed() {
        AppUtil.exitApp()
    }
}