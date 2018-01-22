package com.sumian.sleepdoctor.main.tab.me;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.base.BaseFragment;
import com.sumian.sleepdoctor.main.setting.SettingFragment;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

public class MeFragment extends BaseFragment<UserProfile> implements View.OnClickListener {

    @BindView(R.id.iv_avatar)
    CircleImageView mIvAvatar;
    @BindView(R.id.tv_nickname)
    TextView mTvNickname;

    public static MeFragment newInstance() {
        return new MeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_me;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        ViewGroup decorView = (ViewGroup) mActivity.getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) ((ViewGroup) mActivity.findViewById(android.R.id.content)).getChildAt(0);
        decorView.removeView(rootView);
        rootView.setFitsSystemWindows(false);
        rootView.setClipToPadding(false);
        StatusBarUtil.setTransparent(mActivity);
        StatusBarUtil.setTranslucent(mActivity, 0);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @OnClick({R.id.dv_user_info_center, R.id.dv_setting})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dv_user_info_center:
                break;
            case R.id.dv_setting:
                commitReplacePager(SettingFragment.newInstance());
                break;
        }
    }
}
