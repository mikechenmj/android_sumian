package com.sumian.app.setting.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
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

    TextView mTvDesc;
    TextView mTvCancel;
    TextView mTvUpgrade;
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
        mTvDesc = rootView.findViewById(R.id.tv_desc);
        mTvCancel = rootView.findViewById(R.id.tv_cancel);
        mTvUpgrade = rootView.findViewById(R.id.tv_upgrade);
        mDivider = rootView.findViewById(R.id.divider);
        rootView.findViewById(R.id.tv_cancel).setOnClickListener(this);
        rootView.findViewById(R.id.tv_upgrade).setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_cancel) {
            dismiss();
        } else if (i == R.id.tv_upgrade) {
            VersionNoticeActivity.show(v.getContext());
            dismiss();
        }
    }
}
