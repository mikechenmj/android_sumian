package com.sumian.hw.setting.presenter;

import com.sumian.hw.app.App;
import com.sumian.hw.app.HwAppManager;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.setting.contract.SocialContract;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.Social;
import com.sumian.sleepdoctor.account.bean.UserInfo;

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

        List<Social> socialites = HwAppManager.getAccountModel().getUserInfo().getSocialites();
        if (socialites == null || socialites.isEmpty()) {
            view.onFinish();
            view.onFailure(App.getAppContext().getString(R.string.unbind_open_platform_failed));
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

        Call<Object> call = HwAppManager.getNetEngine().getHttpService().unBindOpenPlatform(socialId);
        call.enqueue(new BaseResponseCallback<Object>() {
            @Override
            protected void onSuccess(Object response) {
                view.onUnbindSocialSuccess();
                HwAppManager.getAccountModel().unbindOpenPlatform(socialType);
            }

            @Override
            protected void onFailure(String error) {
                view.onFailure(error);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                view.onFinish();
            }

            @Override
            protected void onNotFound(String error) {
                super.onNotFound(error);
                view.onFailure(error);
            }
        });
        this.mCall = call;
    }
}
