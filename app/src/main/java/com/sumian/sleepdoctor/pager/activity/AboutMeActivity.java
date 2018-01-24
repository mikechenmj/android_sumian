package com.sumian.sleepdoctor.pager.activity;

import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.widget.TitleBar;

import butterknife.BindView;

/**
 * Created by jzz
 * on 2018/1/24.
 * desc:
 */

public class AboutMeActivity extends BaseActivity implements TitleBar.OnBackListener {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pager_about_me;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.addOnBackListener(this);
    }

    @Override
    public void onBack(View v) {
        finish();
    }
}
