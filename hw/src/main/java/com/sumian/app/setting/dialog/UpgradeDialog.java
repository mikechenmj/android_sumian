package com.sumian.app.setting.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sumian.app.R;
import com.sumian.app.network.response.AppUpgradeInfo;
import com.sumian.app.upgrade.activity.VersionNoticeActivity;
import com.sumian.app.widget.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2017/11/15.
 * <p>
 * desc:
 */

public class UpgradeDialog extends BaseDialogFragment implements View.OnClickListener {

    @BindView(R.id.tv_desc)
    TextView mTvDesc;
    @BindView(R.id.tv_cancel)
    TextView mTvCancel;
    @BindView(R.id.tv_upgrade)
    TextView mTvUpgrade;
    @BindView(R.id.divider)
    View mDivider;

    private AppUpgradeInfo mAppUpgradeInfo;

    @Override
    protected void initBundle(Bundle arguments) {
        super.initBundle(arguments);
        if (arguments != null) {
            this.mAppUpgradeInfo = (AppUpgradeInfo) arguments.getSerializable("app_info");
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.hw_lay_dialog_upgrade;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);

    }

    @Override
    protected void initData() {
        super.initData();
        if (mAppUpgradeInfo != null) {
            mTvDesc.setText(mAppUpgradeInfo.description);
            if (mAppUpgradeInfo.need_force_update) {
                mTvCancel.setVisibility(View.GONE);
                mDivider.setVisibility(View.GONE);
                setCancelable(false);
            }
        }
    }

    @OnClick({R.id.tv_cancel, R.id.tv_upgrade})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_upgrade:
                VersionNoticeActivity.show(v.getContext());
                dismiss();
                break;
        }

    }
}
