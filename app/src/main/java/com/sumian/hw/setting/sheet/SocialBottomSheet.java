package com.sumian.hw.setting.sheet;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sumian.common.helper.ToastHelper;
import com.sumian.hw.setting.contract.SocialContract;
import com.sumian.hw.setting.presenter.SocialPresenter;
import com.sumian.hw.widget.BottomSheetView;
import com.sumian.hw.widget.refresh.ActionLoadingDialog;
import com.sumian.sd.R;

import java.util.Locale;

/**
 * Created by jzz
 * on 2017/11/15.
 * <p>
 * desc:解绑第三方账号
 */

public class SocialBottomSheet extends BottomSheetView implements View.OnClickListener, SocialContract.View {

    private static final String ARGS_TYPE = "social_type";

    TextView mTvTitle;

    private ActionLoadingDialog mActionLoadingDialog;

    private int mSocialType;

    private SocialContract.Presenter mPresenter;

    private UnbindSocialCallback mUnbindSocialCallback;

    public SocialBottomSheet setUnbindSocialCallback(UnbindSocialCallback unbindSocialCallback) {
        mUnbindSocialCallback = unbindSocialCallback;
        return this;
    }

    public static SocialBottomSheet newInstance(int socialType) {
        SocialBottomSheet socialBottomSheet = new SocialBottomSheet();
        Bundle args = new Bundle();
        args.putInt(ARGS_TYPE, socialType);
        socialBottomSheet.setArguments(args);
        return socialBottomSheet;
    }

    @Override
    protected void initBundle(Bundle arguments) {
        super.initBundle(arguments);
        this.mSocialType = arguments.getInt(ARGS_TYPE);
    }

    @Override
    protected int getLayout() {
        return R.layout.hw_lay_bottom_sheet_social;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mTvTitle = rootView.findViewById(R.id.tv_title);
        rootView.findViewById(R.id.tv_action).setOnClickListener(this);
        rootView.findViewById(R.id.tv_cancel).setOnClickListener(this);

        String title;
        String socialPlatform = getString(R.string.wechat);
        switch (mSocialType) {
            case SocialPresenter.SOCIAL_QQ:
                socialPlatform = getString(R.string.qq);
                break;
            case SocialPresenter.SOCIAL_WECHAT:
                socialPlatform = getString(R.string.wechat);
                break;
            case SocialPresenter.SOCIAL_SINA:
                socialPlatform = getString(R.string.sina);
                break;
            default:
                break;
        }
        title = String.format(Locale.getDefault(), getString(R.string.unbind_social_title), socialPlatform);
        mTvTitle.setText(title);
    }

    @Override
    protected void initData() {
        super.initData();
        SocialPresenter.init(this);
    }

    @Override
    protected void release() {
        mPresenter.release();
        super.release();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_action) {
            mPresenter.unbindSocial(mSocialType);
        } else if (i == R.id.tv_cancel) {
            dismiss();
        }
    }

    @Override
    public void setPresenter(SocialContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onFailure(String error) {
        if (mUnbindSocialCallback != null) {
            mUnbindSocialCallback.unbindSocial(mSocialType, false);
        }
        runUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onBegin() {
        mActionLoadingDialog = new ActionLoadingDialog().show(getFragmentManager());
    }

    @Override
    public void onFinish() {
        if (mActionLoadingDialog != null) {
            mActionLoadingDialog.dismiss();
        }
        dismiss();
    }

    @Override
    public void onUnbindSocialSuccess() {
        if (mUnbindSocialCallback != null) {
            mUnbindSocialCallback.unbindSocial(mSocialType, true);
        }
        runUiThread(() -> ToastHelper.show(getString(R.string.unbind_open_platform_success)));
        dismiss();
    }

    public interface UnbindSocialCallback {

        void unbindSocial(int socialType, boolean isUnBind);
    }
}
