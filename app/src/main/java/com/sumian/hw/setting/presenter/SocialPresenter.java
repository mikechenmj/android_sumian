package com.sumian.hw.setting.presenter;

import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.setting.contract.SocialContract;
import com.sumian.sd.R;
import com.sumian.sd.account.bean.Social;
import com.sumian.sd.account.bean.UserInfo;
import com.sumian.sd.account.model.AccountViewModel;
import com.sumian.sd.app.App;
import com.sumian.sd.app.AppManager;

import java.lang.ref.WeakReference;
import java.util.List;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/12/29.
 * desc:
 */

public class SocialPresenter implements SocialContract.Presenter {

    public static final int SOCIAL_WECHAT = 0x00;
    public static final int SOCIAL_QQ = 0x01;
    public static final int SOCIAL_SINA = 0x02;

    private WeakReference<SocialContract.View> mViewWeakReference;

    private Call<Object> mCall;

    private SocialPresenter(SocialContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
    }

    public static void init(SocialContract.View view) {
        new SocialPresenter(view);
    }


    @Override
    public void release() {
        if (mCall == null || !mCall.isCanceled()) {
            return;
        }
        mCall.cancel();
        mCall = null;
    }

    @Override
    public void unbindSocial(int socialType) {

        WeakReference<SocialContract.View> viewWeakReference = this.mViewWeakReference;
        SocialContract.View view = viewWeakReference.get();
        if (view == null) {
            return;
        }

        view.onBegin();

        List<Social> socialites = AppManager.getAccountViewModel().getUserInfo().getSocialites();
        if (socialites == null || socialites.isEmpty()) {
            view.onFinish();
            view.onFailure(App.Companion.getAppContext().getString(R.string.unbind_open_platform_failed));
            return;
        }

        int socialId = 0;
        switch (socialType) {
            case SOCIAL_WECHAT:
                socialId = socialites.get(0).getId();
                break;
            case SOCIAL_QQ:
                socialId = socialites.get(1).getId();
                break;
            case SOCIAL_SINA:
                socialId = socialites.get(2).getId();
                break;
            default:
                break;
        }

        Call<Object> call = AppManager.getHwNetEngine().getHttpService().unBindOpenPlatform(socialId);
        call.enqueue(new BaseResponseCallback<Object>() {
            @Override
            protected void onSuccess(Object response) {
                view.onUnbindSocialSuccess();

                syncUserInfo();

                AccountViewModel accountViewModel = AppManager.getAccountViewModel();
                UserInfo user = accountViewModel.getToken().user;
                List<Social> socialites = user.getSocialites();
                if (socialites == null || socialites.isEmpty()) {
                    return;
                }

                for (int i = 0; i < socialites.size(); i++) {
                    int type = socialites.get(i).getType();
                    if (type == socialType) {
                        socialites.remove(i);
                        break;
                    }
                }
                user.setSocialites(socialites);
                accountViewModel.updateUserInfo(user);
            }

            @Override
            protected void onFailure(int code, String error) {
                view.onFailure(error);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                view.onFinish();
            }
        });
        this.mCall = call;
    }


    private void syncUserInfo() {
        Call<UserInfo> call = AppManager.getHwNetEngine().getHttpService().getUserInfo("doctor");
        call.enqueue(new BaseResponseCallback<UserInfo>() {
            @Override
            protected void onSuccess(UserInfo response) {
                AppManager.getAccountViewModel().updateUserInfo(response);
            }

            @Override
            protected void onFailure(int code, String message) {

            }
        });
    }
}
