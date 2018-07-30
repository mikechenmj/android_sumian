package com.sumian.app.setting.dialog;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sumian.app.R;
import com.sumian.app.network.response.ConfigInfo;
import com.sumian.app.setting.activity.ConfigActivity;
import com.sumian.app.setting.contract.ConfigContract;
import com.sumian.app.setting.presenter.ConfigPresenter;
import com.sumian.app.widget.BaseDialogFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2017/11/15.
 * <p>
 * desc:
 */

public class ContactDialog extends BaseDialogFragment implements View.OnClickListener, ConfigContract.View {
    TextView mTvTel;
    private ConfigContract.Presenter mPresenter;

    @Override
    protected int getLayout() {
        return R.layout.hw_lay_dialog_contact;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        ConfigPresenter.init(this);
        mTvTel = rootView.findViewById(R.id.tv_message);
        rootView.findViewById(R.id.tv_cancel).setOnClickListener(this);
        rootView.findViewById(R.id.tv_call).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.doSyncConfigInfo(ConfigActivity.ABOUT_TYPE);
    }


    @Override
    public void setPresenter(ConfigContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onFailure(String error) {

    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onSyncConfigInfoSuccess(List<ConfigInfo> configs) {
        if (configs != null) {
            ConfigInfo configInfo = configs.get(0);
            mTvTel.setText(configInfo.getValue());
        }

    }

    @Override
    public void onSyncConfigInfoFailed(String error) {

    }

    @Override
    public void onSyncUrl(String url) {

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_cancel) {
            dismiss();
        } else if (i == R.id.tv_call) {
            call(mTvTel.getText().toString().trim());
            dismiss();
        }
    }

    /**
     * 调用拨号界面
     *
     * @param phone 电话号码
     */
    private void call(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
