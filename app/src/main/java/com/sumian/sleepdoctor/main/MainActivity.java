package com.sumian.sleepdoctor.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.base.BaseFragment;
import com.sumian.sleepdoctor.event.NotificationReadEvent;
import com.sumian.sleepdoctor.event.SwitchMainActivityTabEvent;
import com.sumian.sleepdoctor.homepage.HomepageFragment;
import com.sumian.sleepdoctor.improve.doctor.base.BasePagerFragment;
import com.sumian.sleepdoctor.improve.doctor.fragment.DoctorFragment;
import com.sumian.sleepdoctor.leancloud.LeanCloudManager;
import com.sumian.sleepdoctor.notification.NotificationViewModel;
import com.sumian.sleepdoctor.sleepRecord.RecordFragment;
import com.sumian.sleepdoctor.tab.fragment.MeFragment;
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

    @SuppressWarnings("unused")
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String KEY_TAB_INDEX = "key_tab_index";
    public static final String KEY_SLEEP_RECORD_TIME = "key_sleep_record_time";
    public static final String KEY_SCROLL_TO_BOTTOM = "key_scroll_to_bottom";

    @BindView(R.id.nav_tab)
    BottomNavigationBar mBottomNavigationBar;

    @BindView(R.id.lay_tab_container)
    FrameLayout mFragmentContainer;

    private int mCurrentPosition = -1;

    private FragmentManager mFragmentManager;

    private String[] mFTags = new String[]{RecordFragment.class.getSimpleName(), DoctorFragment.class.getSimpleName(), MeFragment.class.getSimpleName()};
    private LaunchData<LaunchSleepTabBean> mLaunchData;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    public static void launch(Context context, int tabIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TAB_INDEX, tabIndex);
        showClearTop(context, MainActivity.class, bundle);
    }

    public static void launchSleepRecordTab(Context context, long sleepRecordTime) {
        showClearTop(context, getLaunchSleepRecordTabIntent(context, sleepRecordTime));
    }

    public static Intent getLaunchSleepRecordTabIntent(Context context, long sleepRecordTime) {
        Bundle bundle = new Bundle();
        bundle.putInt(MainActivity.KEY_TAB_INDEX, 0);
        bundle.putLong(MainActivity.KEY_SLEEP_RECORD_TIME, sleepRecordTime);
        bundle.putBoolean(MainActivity.KEY_SCROLL_TO_BOTTOM, true);
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mBottomNavigationBar.setOnSelectedTabChangeListener(this);
        this.mFragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void onRelease() {
        super.onRelease();
    }

    private void initTab(int position) {
        if (mCurrentPosition == position) {
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
                            if (mLaunchData != null) {
                                LaunchSleepTabBean data = mLaunchData.data;
                                fragmentByTag = RecordFragment.newInstance(data.sleepRecordTime, data.needScrollToBottom);
                            } else {
                                fragmentByTag = BaseFragment.newInstance(HomepageFragment.class);
                            }
                            break;
                        case 1:
                            fragmentByTag = BaseFragment.newInstance(DoctorFragment.class);
                            break;
                        case 2:
                            fragmentByTag = BaseFragment.newInstance(MeFragment.class);
                            break;
                        default:
                            fragmentByTag = BaseFragment.newInstance(RecordFragment.class);
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
        initTab(position);
    }

    @Override
    protected void initData() {
        super.initData();
        //commitReplace(WelcomeActivity.class);
        int position = mLaunchData == null ? 0 : mLaunchData.tabIndex;
        initTab(position);
        mBottomNavigationBar.selectItem(position, false);
        mLaunchData = null;
        LeanCloudManager.registerPushService(this);
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

    @Override
    protected boolean openEventBus() {
        return true;
    }

    @Subscribe(sticky = true)
    public void onNotificationReadEvent(NotificationReadEvent event) {
        removeStickyEvent(event);
        ViewModelProviders.of(this)
                .get(NotificationViewModel.class)
                .updateUnreadCount();
    }

    @Subscribe
    public void onSwitchTabEvent(SwitchMainActivityTabEvent event) {
        mBottomNavigationBar.selectItem(event.index, true);
    }
}
