package com.sumian.sleepdoctor.pager.fragment;

import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.fragment.LoginFragment;
import com.sumian.sleepdoctor.base.BaseFragment;
import com.sumian.sleepdoctor.widget.TitleBar;
import com.sumian.sleepdoctor.widget.divider.SettingDividerView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2018/1/21.
 * desc:
 */

public class SettingFragment extends BaseFragment implements TitleBar.OnBackListener, View.OnClickListener {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.version)
    SettingDividerView mVersion;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pager_setting;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        setStatusBarColor();
        mTitleBar.addOnBackListener(this);
    }

    @Override
    public void onBack(View v) {
        popBack();
    }

    @OnClick({R.id.version, R.id.about_me, R.id.bt_logout})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.version:

                break;
            case R.id.about_me:
                commitReplace(AboutMefragment.class);
                break;
            case R.id.bt_logout:
                commitReplace(LoginFragment.class);
                break;
            default:
                break;
        }
    }
}
