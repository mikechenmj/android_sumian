package com.sumian.sleepdoctor.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.sumian.hw.improve.main.HwMainActivity;
import com.sumian.common.utils.SettingsUtil;
import com.sumian.hw.utils.AppUtil;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.constants.SpKeys;
import com.sumian.sleepdoctor.setting.version.delegate.VersionDelegate;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.base.BaseFragment;
import com.sumian.sleepdoctor.doctor.base.BasePagerFragment;
import com.sumian.sleepdoctor.event.EventBusUtil;
import com.sumian.sleepdoctor.event.NotificationReadEvent;
import com.sumian.sleepdoctor.homepage.HomepageFragment;
import com.sumian.sleepdoctor.notification.NotificationViewModel;
import com.sumian.sleepdoctor.tab.DoctorFragment;
import com.sumian.sleepdoctor.tab.MeFragment;
import com.sumian.sleepdoctor.utils.NotificationUtil;
import com.sumian.sleepdoctor.utils.StatusBarUtil;
import com.sumian.sleepdoctor.widget.dialog.SumianAlertDialog;
import com.sumian.sleepdoctor.widget.nav.BottomNavigationBar;
import com.sumian.sleepdoctor.widget.nav.NavigationItem;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

/**
 * Created by jzz
 * on 2018/1/15.
 * desc:
 */

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnSelectedTabChangeListener {

    public static final String KEY_TAB_INDEX = "key_tab_index";
    public static final String KEY_SLEEP_RECORD_TIME = "key_sleep_record_time";
    public static final String KEY_SCROLL_TO_BOTTOM = "key_scroll_to_bottom";
    public static final int REQUEST_CODE_OPEN_NOTIFICATION = 1;

    @SuppressWarnings("unused")
    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.nav_tab)
    BottomNavigationBar mBottomNavigationBar;

    @BindView(R.id.lay_tab_container)
    FrameLayout mFragmentContainer;

    private int mCurrentPosition = -1;

    private FragmentManager mFragmentManager;

    private String[] mFTags = new String[]{HomepageFragment.class.getSimpleName(), DoctorFragment.class.getSimpleName(), "DeviceFragment", MeFragment.class.getSimpleName()};
    private LaunchData<LaunchSleepTabBean> mLaunchData;

    private VersionDelegate mVersionDelegate;

    public static void launch(Context context, int tabIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TAB_INDEX, tabIndex);
        showClearTop(context, MainActivity.class, bundle);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        if (bundle != null) {
            int launchTabIndex = bundle.getInt(KEY_TAB_INDEX);
            mLaunchData = new LaunchData<>(launchTabIndex);
            if (launchTabIndex == 0) {
                long launchSleepRecordTime = bundle.getLong(KEY_SLEEP_RECORD_TIME, 0);
                boolean scrollToBottom = bundle.getBoolean(KEY_SCROLL_TO_BOTTOM, false);
                mLaunchData.data = new LaunchSleepTabBean(launchSleepRecordTime, scrollToBottom);
            }
        }
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mBottomNavigationBar.setOnSelectedTabChangeListener(this);
        mFragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void initData() {
        super.initData();
        this.mVersionDelegate = VersionDelegate.Companion.init();
        //commitReplace(HwWelcomeActivity.class);
        int position = mLaunchData == null ? 0 : mLaunchData.tabIndex;
        selectTab(position);
        mBottomNavigationBar.selectItem(position, false);
        mLaunchData = null;
        showOpenNotificationDialogIfNeeded();
    }

    @Override
    protected void onRelease() {
        super.onRelease();
    }

    private void selectTab(int position) {
        if (mCurrentPosition == position) {
            return;
        }
        if (position == 2) {
            mBottomNavigationBar.selectItem(0, true);
            launchAnotherMainActivity();
            return;
        }
        for (int i = 0, len = mFTags.length; i < len; i++) {
            Fragment fragmentByTag;
            String fTag = mFTags[i];
            fragmentByTag = getFragmentByTag(fTag);
            if (position == i) {
                if (fragmentByTag != null && fragmentByTag.isAdded()) {
                    showFragment(fragmentByTag);
                } else {
                    switch (position) {
                        case 0:
                            fragmentByTag = BaseFragment.newInstance(HomepageFragment.class);
                            break;
                        case 1:
                            fragmentByTag = BaseFragment.newInstance(DoctorFragment.class);
                            break;
                        case 3:
                            fragmentByTag = BaseFragment.newInstance(MeFragment.class);
                            break;
                        default:
                            fragmentByTag = BaseFragment.newInstance(HomepageFragment.class);
                            break;
                    }
                    addFragment(fragmentByTag, fTag);
                }
            } else {
                if (fragmentByTag == null) {
                    continue;
                }
                hideFragment(fragmentByTag);
            }
        }
        mCurrentPosition = position;
        changeStatusBarColorByPosition(position);
    }

    private void launchAnotherMainActivity() {
        ActivityUtils.startActivity(HwMainActivity.class);
    }

    private void changeStatusBarColorByPosition(int position) {
        if (position == 0) {
            StatusBarUtil.Companion.setStatusBarColor(this, Color.WHITE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarUtil.Companion.setStatusBarTextColor(this, false);
            }
        } else {
            StatusBarUtil.Companion.setStatusBarColor(this, Color.TRANSPARENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarUtil.Companion.setStatusBarTextColor(this, true);
            }
        }
    }

    private Fragment getFragmentByTag(String fTag) {
        return mFragmentManager.findFragmentByTag(fTag);
    }

    private void hideFragment(Fragment f) {
        mFragmentManager.beginTransaction().hide(f).commit();
    }

    private void showFragment(Fragment f) {
        mFragmentManager.beginTransaction().show(f).runOnCommit(() -> autoSelectDoctorTab(f)).commit();
    }

    private void addFragment(Fragment f, String fTag) {
        mFragmentManager.beginTransaction().add(R.id.lay_tab_container, f, fTag).runOnCommit(() -> autoSelectDoctorTab(f)).commit();
    }

    private void autoSelectDoctorTab(Fragment f) {
        if (f instanceof BasePagerFragment) {
            ((BasePagerFragment) f).selectTab(1);
        }
    }

    @Override
    public void onSelectedTabChange(NavigationItem navigationItem, int position) {
        if (mCurrentPosition == position) {
            return;
        }
        selectTab(position);
    }

    @Override
    protected boolean openEventBus() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVersionDelegate.checkVersion(this);
    }

    @Subscribe(sticky = true)
    public void onNotificationReadEvent(NotificationReadEvent event) {
        EventBusUtil.removeStickyEvent(event);
        ViewModelProviders.of(this)
                .get(NotificationViewModel.class)
                .updateUnreadCount();
    }

    public static class LaunchSleepTabBean {
        long sleepRecordTime;
        boolean needScrollToBottom;

        LaunchSleepTabBean(long sleepRecordTime, boolean needScrollToBottom) {
            this.sleepRecordTime = sleepRecordTime;
            this.needScrollToBottom = needScrollToBottom;
        }
    }

    public static class LaunchData<T> {
        public T data;
        int tabIndex;

        LaunchData(int launchTabIndex) {
            this.tabIndex = launchTabIndex;
        }
    }

    private void showOpenNotificationDialogIfNeeded() {
        long previousShowTime = SPUtils.getInstance().getLong(SpKeys.SLEEP_RECORD_PREVIOUS_SHOW_NOTIFICATION_TIME, 0);
        boolean alreadyShowed = previousShowTime > 0;
        if (NotificationUtil.Companion.areNotificationsEnabled(getActivity()) || alreadyShowed) {
            return;
        }
        new SumianAlertDialog(getActivity())
                .setCloseIconVisible(true)
                .setTopIconResource(R.mipmap.ic_notification_alert)
                .setTitle(R.string.open_notification)
                .setMessage(R.string.open_notification_and_receive_doctor_response)
                .setRightBtn(R.string.open_notification, v -> SettingsUtil.launchSettingActivityForResult(this, REQUEST_CODE_OPEN_NOTIFICATION))
                .show();
        SPUtils.getInstance().put(SpKeys.SLEEP_RECORD_PREVIOUS_SHOW_NOTIFICATION_TIME, System.currentTimeMillis());
    }

    @Override
    public void onBackPressed() {
        AppUtil.exitApp(this);
    }
}
