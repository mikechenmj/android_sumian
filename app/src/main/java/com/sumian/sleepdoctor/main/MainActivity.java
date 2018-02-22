package com.sumian.sleepdoctor.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.tab.fragment.GroupFragment;
import com.sumian.sleepdoctor.tab.fragment.MeFragment;
import com.sumian.sleepdoctor.widget.nav.ItemTab;
import com.sumian.sleepdoctor.widget.nav.NavTab;

import butterknife.BindView;

/**
 * Created by jzz
 * on 2018/1/15.
 * desc:
 */

public class MainActivity extends BaseActivity implements NavTab.OnTabChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.nav_Tab)
    NavTab mNavTab;

    @BindView(R.id.lay_tab_container)
    FrameLayout mFragmentContainer;

    // private FragmentManagerDelegate mFragmentManagerDelegate;

    private GroupFragment mGroupFragment;

    private MeFragment mMeFragment;

    private int mCurrentPosition = -1;

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

        mNavTab.setOnTabChangeListener(this);
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

    @Override
    public void tab(ItemTab itemTab, int position) {
        if (mCurrentPosition == position) {
            return;
        }
        initTab(position);
    }

    private void initTab(int position) {
        Fragment fragment;
        switch (position) {
            case 1:
                if (mMeFragment == null) {
                    mMeFragment = (MeFragment) MeFragment.newInstance(MeFragment.class);
                }
                fragment = mMeFragment;
                break;
            case 0:
            default:
                if (mGroupFragment == null) {
                    mGroupFragment = (GroupFragment) GroupFragment.newInstance(GroupFragment.class);
                }
                fragment = mGroupFragment;
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.lay_tab_container, fragment, fragment.getClass().getSimpleName()).commitNowAllowingStateLoss();

        mCurrentPosition = position;
    }
}
