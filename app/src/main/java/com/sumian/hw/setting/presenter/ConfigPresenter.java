package com.sumian.hw.setting.presenter;

import com.sumian.common.network.response.ErrorResponse;
import com.sumian.hw.setting.activity.ConfigActivity;
import com.sumian.hw.setting.contract.ConfigContract;
import com.sumian.sd.BuildConfig;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.network.callback.BaseSdResponseCallback;
import com.sumian.sd.network.response.ConfigInfo;

import org.jetbrains.annotations.NotNull;

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
        if (call == null) return;
        if (call.isExecuted()) {
            call.cancel();
        }
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
            Call<List<ConfigInfo>> call = AppManager.getHwHttpService().syncConfigInfo();
            this.mCall = call;
            ConfigContract.View finalView = view;
            call.enqueue(new BaseSdResponseCallback<List<ConfigInfo>>() {

                @Override
                protected void onSuccess(List<ConfigInfo> response) {
                    if (finalView != null) {
                        finalView.onSyncConfigInfoSuccess(response);
                    }
                }

                @Override
                protected void onFailure(@NotNull ErrorResponse errorResponse) {
                    if (finalView == null) return;
                    finalView.onSyncConfigInfoFailed(errorResponse.getMessage());
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
            view.onSyncUrl(configType == ConfigActivity.REGISTER_USER_AGREEMENT_TYPE ? BuildConfig.USER_AGREEMENT_URL : BuildConfig.USER_PRIVACY_POLICY_URL);
        }

    }
}
