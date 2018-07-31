package com.sumian.app.setting.presenter;

import com.sumian.app.constant.AppUrl;
import com.sumian.app.app.HwAppManager;
import com.sumian.app.network.callback.BaseResponseCallback;
import com.sumian.app.network.response.ConfigInfo;
import com.sumian.app.setting.activity.ConfigActivity;
import com.sumian.app.setting.contract.ConfigContract;

import java.lang.ref.WeakReference;
import java.util.List;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/11/15.
 * <p>
 * desc:
 */

public class ConfigPresenter implements ConfigContract.Presenter {

    private Call mCall;

    private WeakReference<ConfigContract.View> mViewWeakReference;

    private int mConfigType;

    private ConfigPresenter(ConfigContract.View view) {
        if (view != null) {
            view.setPresenter(this);
            this.mViewWeakReference = new WeakReference<>(view);
        }
    }

    public static ConfigPresenter init(ConfigContract.View view) {
        return new ConfigPresenter(view);
    }


    @Override
    public void release() {
        Call call = this.mCall;
        if (call == null || call.isCanceled()) return;
        call.cancel();
        this.mCall = null;
    }

    @Override
    public void doSyncConfigInfo(int configType) {

        this.mConfigType = configType;

        WeakReference<ConfigContract.View> viewWeakReference = this.mViewWeakReference;
        ConfigContract.View view = null;
        if (viewWeakReference != null) {
            view = viewWeakReference.get();
        }

        if (view != null)
            view.onBegin();

        if (configType == ConfigActivity.ABOUT_TYPE) {
            Call<List<ConfigInfo>> call = HwAppManager.getNetEngine().getHttpService().syncConfigInfo();
            this.mCall = call;
            ConfigContract.View finalView = view;
            call.enqueue(new BaseResponseCallback<List<ConfigInfo>>() {

                @Override
                protected void onSuccess(List<ConfigInfo> response) {
                    if (finalView != null) {
                        finalView.onSyncConfigInfoSuccess(response);
                    }
                }

                @Override
                protected void onFailure(String error) {
                    if (finalView == null) return;
                    finalView.onSyncConfigInfoFailed(error);
                }

                @Override
                protected void onFinish() {
                    if (finalView == null) return;
                    finalView.onFinish();
                }
            });
        } else {
            if (view == null) return;
            view.onFinish();
            view.onSyncUrl(configType == ConfigActivity.REGISTER_USER_AGREEMENT_TYPE ? AppUrl.USER_AGREEMENT_URL : AppUrl.PRIVACY_POLICY_URL);
        }

    }
}
