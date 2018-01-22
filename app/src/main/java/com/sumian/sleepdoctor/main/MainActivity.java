package com.sumian.sleepdoctor.main;

import android.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.main.tab.group.fragment.GroupFragment;
import com.sumian.sleepdoctor.main.tab.me.MeFragment;
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
    public void commitReplacePagerFragment(Fragment fragment) {
        super.commitReplacePagerFragment(fragment);
        mLayPageContainer.setVisibility(View.VISIBLE);
        mLayTabParentContainer.setVisibility(View.GONE);
    }

    @Override
    public void commitReplaceTabFragment(Fragment fragment) {
        super.commitReplaceTabFragment(fragment);
        mLayTabParentContainer.setVisibility(View.VISIBLE);
        mLayPageContainer.setVisibility(View.GONE);
    }
}
