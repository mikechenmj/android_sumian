package com.sumian.hw.tab;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.helpdesk.easeui.UIProvider;
import com.sumian.common.image.ImageLoader;
import com.sumian.sd.account.userProfile.HwUserInfoActivity;
import com.sumian.sd.account.userProfile.HwUserInfoContract;
import com.sumian.hw.base.HwBaseFragment;
import com.sumian.hw.leancloud.HwLeanCloudHelper;
import com.sumian.hw.log.LogManager;
import com.sumian.hw.setting.activity.HwSettingActivity;
import com.sumian.hw.upgrade.model.VersionModel;
import com.sumian.sd.R;
import com.sumian.sd.account.bean.UserInfo;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.kefu.KefuManager;
import com.sumian.sd.main.OnEnterListener;
import com.sumian.sd.utils.ColorCompatUtil;
import com.sumian.sd.widget.divider.SettingDividerView;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

@SuppressWarnings("ConstantConditions")
public class HwMeFragment extends HwBaseFragment implements View.OnClickListener, HwUserInfoContract.View,
        HwLeanCloudHelper.OnShowMsgDotCallback, VersionModel.ShowDotCallback, OnEnterListener {

    @BindView(R.id.iv_avatar)
    CircleImageView mIvAvatar;

    @BindView(R.id.tv_nickname)
    TextView mTvNickname;

    @BindView(R.id.siv_customer_service)
    ImageView mSivKefu;

    @BindView(R.id.siv_setting)
    SettingDividerView mSdvSetting;

    public static HwMeFragment newInstance() {
        return new HwMeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_fragment_tab_me;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mSdvSetting.setBgColor(ColorCompatUtil.Companion.getColor(getActivity(), R.color.light_content_bg_color));
    }

    @Override
    protected void initData() {
        super.initData();
        HwLeanCloudHelper.addOnAdminMsgCallback(this);
        AppManager.getVersionModel().syncAppVersion();
        AppManager.getVersionModel().registerShowDotCallback(this);
        AppManager.getAccountViewModel().getLiveDataToken().observe(this, token -> {
            if (token != null && token.user != null) {
                updateUserInfoUI(token.user);
            }
        });
    }

    @Override
    public void onEnter(String data) {
        LogManager.appendUserOperationLog("点击进入 '我的'  界面");
    }

    @OnClick({R.id.ll_user_info_container, R.id.siv_customer_service, R.id.siv_setting})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_user_info_container:
                HwUserInfoActivity.show(getContext());
                break;
            case R.id.siv_customer_service:
                UIProvider.getInstance().clearCacheMsg();
                KefuManager.Companion.launchKefuActivity();
                break;
            case R.id.siv_setting:
                HwSettingActivity.show(getContext());
                break;
            default:
                break;
        }
    }

    @Override
    protected void onRelease() {
        HwLeanCloudHelper.removeOnAdminMsgCallback(this);
        AppManager.getVersionModel().unRegisterShowDotCallback(this);
        super.onRelease();
    }

    @Override
    public void setPresenter(HwUserInfoContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onSyncCacheUserInfoSuccess(UserInfo userInfo) {
        updateUserInfoUI(userInfo);
    }

    private void updateUserInfoUI(UserInfo userInfo) {
        runOnUiThread(() -> {
            ImageLoader.loadImage(userInfo.getAvatar(), mIvAvatar, R.mipmap.ic_info_avatar_patient, R.mipmap.ic_info_avatar_patient);
            String nickname = userInfo.nickname;
            mTvNickname.setText(TextUtils.isEmpty(nickname) ? userInfo.mobile : nickname);
        });
    }

    @Override
    public void onSyncCacheUserInfoFailed(String error) {

    }

    @Override
    public void onStartSyncUserInfo() {

    }

    @Override
    public void onCompletedUserInfo() {

    }

    @Override
    public void showDot(boolean isShowAppDot, boolean isShowMonitorDot, boolean isShowSleepyDot) {
        // mSivUpgrade.showDot(isShowAppDot || isShowMonitorDot || isShowSleepyDot);
    }

    @Override
    public void onShowMsgDotCallback(int adminMsgLen, int doctorMsgLen, int customerMsgLen) {
        onHideMsgCallback(adminMsgLen, doctorMsgLen, customerMsgLen);
    }

    @Override
    public void onHideMsgCallback(int adminMsgLen, int doctorMsgLen, int customerMsgLen) {
        runOnUiThread(() -> mSivKefu.setImageResource((adminMsgLen > 0 || doctorMsgLen > 0 || customerMsgLen > 0) ? R.drawable.ic_info_customerservice_reddot : R.drawable.ic_info_customerservice_black));
    }
}
