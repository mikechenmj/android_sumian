package com.sumian.sd.tab;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.sumian.common.image.ImageLoader;
import com.sumian.hw.device.bean.BlueDevice;
import com.sumian.hw.leancloud.HwLeanCloudHelper;
import com.sumian.hw.upgrade.model.VersionModel;
import com.sumian.sd.R;
import com.sumian.sd.account.bean.UserInfo;
import com.sumian.sd.account.userProfile.SdUserProfileActivity;
import com.sumian.sd.app.App;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.base.SdBaseFragment;
import com.sumian.sd.device.DeviceManageActivity;
import com.sumian.sd.device.DeviceManager;
import com.sumian.sd.h5.SleepFileWebActivity;
import com.sumian.sd.kefu.KefuManager;
import com.sumian.sd.main.OnEnterListener;
import com.sumian.sd.notification.NotificationListActivity;
import com.sumian.sd.notification.NotificationViewModel;
import com.sumian.sd.onlinereport.OnlineReportListActivity;
import com.sumian.sd.scale.ScaleListActivity;
import com.sumian.sd.service.advisory.activity.AdvisoryListActivity;
import com.sumian.sd.service.diary.DiaryEvaluationListActivity;
import com.sumian.sd.service.tel.activity.TelBookingListActivity;
import com.sumian.sd.setting.SettingActivity;
import com.sumian.sd.theme.three.SkinConfig;
import com.sumian.sd.widget.divider.SettingDividerView;
import com.sumian.sd.widget.tips.PatientRecordTips;
import com.sumian.sd.widget.tips.PatientServiceTips;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

@SuppressWarnings("ALL")
public class MeFragment extends SdBaseFragment implements View.OnClickListener, PatientServiceTips.OnServiceTipsCallback,
        PatientRecordTips.OnRecordTipsCallback, HwLeanCloudHelper.OnShowMsgDotCallback, OnEnterListener, VersionModel.ShowDotCallback {

    @BindView(R.id.iv_avatar)
    CircleImageView mIvAvatar;
    @BindView(R.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R.id.iv_notification)
    ImageView mIvNotification;

    //@BindView(R.id.siv_customer_service)
    //ImageView mSivKefu;

    @BindView(R.id.tips_service)
    PatientServiceTips mTipsService;
    @BindView(R.id.tips_record)
    PatientRecordTips mTipsRecord;
    @BindView(R.id.dv_device_manage)
    SettingDividerView mSdDeviceManage;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_me;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        this.mTipsService.setOnServiceTipsCallback(this);
        this.mTipsRecord.setOnRecordTipsCallback(this);
    }

    @Override
    protected void initData() {
        super.initData();
        UserInfo userProfile = AppManager.getAccountViewModel().getToken().user;
        updateUserProfile(userProfile);
        AppManager.getAccountViewModel().getLiveDataToken().observe(this, token -> {
            if (token != null) {
                updateUserProfile(token.user);
            }
        });
        ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(NotificationViewModel.class)
                .getUnreadCount()
                .observe(this, unreadCount -> mIvNotification.setActivated(unreadCount != null && unreadCount > 0));

        // HwLeanCloudHelper.addOnAdminMsgCallback(this);
        DeviceManager.INSTANCE.getMonitorLiveData().observe(this, new Observer<BlueDevice>() {
            @Override
            public void onChanged(@android.support.annotation.Nullable BlueDevice blueDevice) {
                String monitorSn = blueDevice == null ? null : blueDevice.sn;
                if (TextUtils.isEmpty(monitorSn)) {
                    monitorSn = getString(R.string.add_new_device);
                }
                mSdDeviceManage.setContent(monitorSn);
            }
        });

    }

    @Override
    @OnClick({R.id.iv_avatar, R.id.tv_nickname, R.id.dv_setting, R.id.iv_notification, R.id.siv_customer_service, R.id.dv_device_manage})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_avatar:
            case R.id.tv_nickname:
                SdUserProfileActivity.show(getContext(), SdUserProfileActivity.class);
                break;
            case R.id.dv_setting:
                SettingActivity.show(getContext(), SettingActivity.class);
                break;
            case R.id.iv_notification:
                NotificationListActivity.launch(getActivity());
                break;
            case R.id.siv_customer_service:
                UIProvider.getInstance().clearCacheMsg();
                UIProvider.getInstance().setThemeMode(SkinConfig.isInNightMode(App.getAppContext()) ? 0x02 : 0x01);
                KefuManager.launchKefuActivity();
                break;
            case R.id.dv_device_manage:
                ActivityUtils.startActivity(DeviceManageActivity.class);
                break;
            default:
                break;
        }
    }

    private void updateUserProfile(UserInfo userProfile) {
        ImageLoader.loadImage(userProfile.avatar, mIvAvatar, R.mipmap.ic_info_avatar_patient);
        String nickname = userProfile.nickname;
        mTvNickname.setText(TextUtils.isEmpty(nickname) ? userProfile.mobile : nickname);
    }

    @Override
    public void showGraphicService() {
        AdvisoryListActivity.show(getActivity(), AdvisoryListActivity.class);
    }

    @Override
    public void showTelService() {
        TelBookingListActivity.show();
    }

    @Override
    public void onDiaryEvaluationClick() {
        ActivityUtils.startActivity(DiaryEvaluationListActivity.class);
    }

    @Override
    public void showSleepRecord() {
        SleepFileWebActivity.show(getContext());
    }

    @Override
    public void showEvaluation() {
        ScaleListActivity.launch(getContext(), ScaleListActivity.TYPE_FILLED);
    }

    @Override
    public void showOnlineReport() {
        OnlineReportListActivity.launchForShowAll(this);
    }

    @Override
    public void onShowMsgDotCallback(int adminMsgLen, int doctorMsgLen, int customerMsgLen) {
        // onHideMsgCallback(adminMsgLen, doctorMsgLen, customerMsgLen);
    }

    @Override
    public void onHideMsgCallback(int adminMsgLen, int doctorMsgLen, int customerMsgLen) {
        // runOnUiThread(() -> mSivKefu.setImageResource((customerMsgLen > 0) ? R.drawable.ic_info_customerservice_reply : R.drawable.ic_info_customerservice));
    }

    @Override
    public void onEnter(@Nullable String data) {
        // if (mSivKefu != null) {
        //    runOnUiThread(() -> mSivKefu.setImageResource((HwLeanCloudHelper.isHaveCustomerMsg()) ? R.drawable.ic_info_customerservice_reply : R.drawable.ic_info_customerservice));
        // }
    }

    @Override
    public void showDot(boolean isShowAppDot, boolean isShowMonitorDot, boolean isShowSleepyDot) {

    }
}
