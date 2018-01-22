package com.sumian.sleepdoctor.main.setting;

import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseFragment;
import com.sumian.sleepdoctor.widget.TitleBar;

import butterknife.BindView;

/**
 * Created by jzz
 * on 2018/1/21.
 * desc:
 */

public class SettingFragment extends BaseFragment implements TitleBar.OnBackListener {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_setting;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.addOnBackListener(this);
    }

    @Override
    public void onBack(View v) {
        popBackPressed();
    }
}
