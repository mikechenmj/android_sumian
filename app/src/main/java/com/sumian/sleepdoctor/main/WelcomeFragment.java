package com.sumian.sleepdoctor.main;

import android.app.Fragment;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.Token;
import com.sumian.sleepdoctor.account.fragment.ImproveUserProfileOneFragment;
import com.sumian.sleepdoctor.account.fragment.LoginFragment;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

public class WelcomeFragment extends BaseFragment implements Observer<Token> {

    private static final String TAG = WelcomeFragment.class.getSimpleName();

    @BindView(R.id.lay_container)
    LinearLayout mLayContainer;

    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_welcome;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        //setStatusBar(mLayContainer);
    }

    @Override
    protected void initData() {
        super.initData();
        AppManager.getAccountViewModel().getLiveDataToken().observe(this, this);
    }

    @Override
    public void onChanged(@Nullable Token token) {
        mRootView.postDelayed(() -> {
            if (token == null) {
                commitReplacePager(LoginFragment.newInstance());
            } else if (token.is_new) {
                commitReplacePager(ImproveUserProfileOneFragment.newInstance());
            } else {
                goHome();
            }
        }, 1000);

        Log.e(TAG, "onChanged: ---------->" + token);
    }
}
