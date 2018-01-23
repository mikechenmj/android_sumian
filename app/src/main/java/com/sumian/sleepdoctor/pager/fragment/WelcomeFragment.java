package com.sumian.sleepdoctor.pager.fragment;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.Token;
import com.sumian.sleepdoctor.account.fragment.ImproveUserProfileOneFragment;
import com.sumian.sleepdoctor.account.fragment.LoginFragment;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseFragment;
import com.sumian.sleepdoctor.tab.fragment.TabGroupFragment;

import butterknife.BindView;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

public class WelcomeFragment extends BaseFragment implements Observer<Token> {

    private static final String TAG = WelcomeFragment.class.getSimpleName();

    @BindView(R.id.lay_container)
    LinearLayout mLayContainer;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pager_welcome;
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
                commitReplace(LoginFragment.class);
            } else if (token.is_new) {
                commitReplace(ImproveUserProfileOneFragment.class);
            } else {
                commitReplace(TabGroupFragment.class);
            }
        }, 1000);

        Log.e(TAG, "onChanged: ---------->" + token);
    }
}
