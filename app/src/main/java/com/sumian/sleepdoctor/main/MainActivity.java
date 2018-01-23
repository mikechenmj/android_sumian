package com.sumian.sleepdoctor.main;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;

import com.jaeger.library.StatusBarUtil;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.delegate.FragmentManagerDelegate;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.pager.fragment.WelcomeFragment;
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

    @BindView(R.id.lay_fragment_container)
    FrameLayout mFragmentContainer;

    private FragmentManagerDelegate mFragmentManagerDelegate;

    private int mCurrentPosition = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setTranslucent(this, 0);

        mFragmentManagerDelegate = new FragmentManagerDelegate(this)
                .bindNavTab(mNavTab);

        mNavTab.setOnTabChangeListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        commitReplace(WelcomeFragment.class);
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        mFragmentManagerDelegate.onRelease();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        mFragmentManagerDelegate.onBackPressedDelegate();
    }

    @Override
    public void tab(ItemTab itemTab, int position) {
        if (mCurrentPosition == position) {
            return;
        }
        Class<? extends Fragment> clx;
        switch (position) {
            case 0:
                clx = GroupFragment.class;
                break;
            case 1:
                clx = MeFragment.class;
                break;
            default:
                clx = GroupFragment.class;
                break;
        }
        mCurrentPosition = position;
        commitReplace(clx);
    }

    @UiThread
    public void goHome() {
        mFragmentManagerDelegate.goHome();
    }

    @UiThread
    public void commitReplace(@NonNull Class<? extends Fragment> clx) {
        mFragmentManagerDelegate.replaceFragment(clx);
    }
}
