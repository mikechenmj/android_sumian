package com.sumian.sleepdoctor.main;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.fragment.LoginFragment;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.main.tab.GroupFragment;
import com.sumian.sleepdoctor.main.tab.MeFragment;
import com.sumian.sleepdoctor.widget.nav.ItemTab;
import com.sumian.sleepdoctor.widget.nav.NavTab;

import java.util.List;

import butterknife.BindView;

/**
 * Created by jzz
 * on 2018/1/15.
 * desc:
 */

public class MainActivity extends BaseActivity implements NavTab.OnTabChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.lay_tab_parent_container)
    LinearLayout mLayTabParentContainer;

    @BindView(R.id.nav_Tab)
    NavTab mNavTab;

    @BindView(R.id.lay_page_container)
    FrameLayout mLayPageContainer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mNavTab.setOnTabChangeListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        commitReplacePagerFragment(WelcomeFragment.newInstance());
    }

    @Override
    public void tab(ItemTab itemTab, int position) {
        Fragment tempFragment = null;
        switch (position) {
            case 0:
                tempFragment = GroupFragment.newInstance();
                break;
            case 1:
                tempFragment = MeFragment.newInstance();
                break;
        }
        commitReplaceTabFragment(tempFragment);
    }

    @Override
    public void goHome() {
        super.goHome();
        commitReplaceTabFragment(GroupFragment.newInstance());
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        Log.e(TAG, "onBackPressed: -----1--->" + backStackEntryCount);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        Log.e(TAG, "onBackPressed: -----2----->" + fragments.toString());

        if (fragments.isEmpty() || isGroupFragment(fragments) || isLoginFragment(fragments) || backStackEntryCount <= 1) {
            finishAffinity();
        } else {
            super.onBackPressed();
        }
        Log.e(TAG, "onBackPressed: ------3---->" + fragments.toString());
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private boolean isLoginFragment(List<Fragment> fragments) {
        if (fragments == null || fragments.isEmpty()) return false;
        for (Fragment fragment : fragments) {
            return fragment instanceof LoginFragment;
        }
        return false;
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private boolean isGroupFragment(List<Fragment> fragments) {
        if (fragments == null || fragments.isEmpty()) return false;
        for (Fragment fragment : fragments) {
            return fragment instanceof GroupFragment;
        }
        return false;
    }

    @Override
    public void commitReplacePagerFragment(Fragment fragment) {
        mLayTabParentContainer.setVisibility(View.GONE);
        mLayPageContainer.setVisibility(View.VISIBLE);
        super.commitReplacePagerFragment(fragment);
    }

    @Override
    public void commitReplaceTabFragment(Fragment fragment) {
        mLayTabParentContainer.setVisibility(View.VISIBLE);
        mLayPageContainer.setVisibility(View.GONE);
        super.commitReplaceTabFragment(fragment);
    }

}
