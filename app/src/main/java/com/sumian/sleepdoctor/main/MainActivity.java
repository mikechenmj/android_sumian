package com.sumian.sleepdoctor.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.base.BaseFragment;
import com.sumian.sleepdoctor.improve.doctor.fragment.DoctorFragment;
import com.sumian.sleepdoctor.sleepRecord.RecordFragment;
import com.sumian.sleepdoctor.tab.fragment.MeFragment;
import com.sumian.sleepdoctor.widget.nav.BottomNavigationBar;
import com.sumian.sleepdoctor.widget.nav.NavigationItem;

import butterknife.BindView;

/**
 * Created by jzz
 * on 2018/1/15.
 * desc:
 */

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnSelectedTabChangeListener {

    @SuppressWarnings("unused")
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.nav_tab)
    BottomNavigationBar mNavTab;

    @BindView(R.id.lay_tab_container)
    FrameLayout mFragmentContainer;

    private int mCurrentPosition = -1;

    private FragmentManager mFragmentManager;

    private String[] mFTags = new String[]{RecordFragment.class.getSimpleName(), DoctorFragment.class.getSimpleName(), MeFragment.class.getSimpleName()};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        // StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.colorPrimary));

        // mFragmentManagerDelegate = new FragmentManagerDelegate(this)
        //        .bindNavTab(mNavTab);

        mNavTab.setOnSelectedTabChangeListener(this);
        this.mFragmentManager = getSupportFragmentManager();

    }

    @Override
    protected void initData() {
        super.initData();
        //commitReplace(WelcomeActivity.class);
        initTab(0);
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        //mFragmentManagerDelegate.onRelease();
    }

    private void initTab(int position) {
        if (mCurrentPosition == position) return;
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
                            fragmentByTag = BaseFragment.newInstance(RecordFragment.class);
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
        mFragmentManager.beginTransaction().show(f).commit();
    }

    private void addFragment(Fragment f, String fTag) {
        mFragmentManager.beginTransaction().add(R.id.lay_tab_container, f, fTag).commit();
    }

    @Override
    public void onSelectedTabChange(NavigationItem navigationItem, int position) {
        if (mCurrentPosition == position) {
            return;
        }
        initTab(position);
    }
}
