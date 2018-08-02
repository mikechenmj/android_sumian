package com.sumian.sleepdoctor.account.userProfile.presenter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.sumian.sleepdoctor.account.bean.Social;
import com.sumian.sleepdoctor.account.bean.UserInfo;
import com.sumian.sleepdoctor.account.userProfile.contract.UserInfoContract;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.ErrorResponse;
import com.sumian.sleepdoctor.oss.OssResponse;
import com.sumian.sleepdoctor.oss.OssEngine;
import com.umeng.socialize.UMAuthListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

/**
 * <pre>
 *     @author : sm
 *
 *     e-mail : yaoqi.y@sumian.com
 *     time: 2018/7/3 11:09
 *
 *     version: 1.0
 *
 *     desc:
 *
 * </pre>
 */
public class UserInfoPresenter implements UserInfoContract.Presenter {

    private UserInfoContract.View mView;

    private UserInfoPresenter(UserInfoContract.View view) {
        view.setPresenter(this);
        mView = view;
    }

    public static UserInfoPresenter init(UserInfoContract.View view) {
        return new UserInfoPresenter(view);
    }

    @Override
    public void getUserInfo() {
        mView.onBegin();
        Call<UserInfo> call = AppManager.getHttpService().getUserProfile();
        addCall(call);
        call.enqueue(new BaseResponseCallback<UserInfo>() {
            @Override
            protected void onSuccess(@Nullable UserInfo response) {
                AppManager.getAccountViewModel().updateUserInfo(response);
                mView.onGetUserInfoSuccess(response);
            }

            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                mView.onGetUserInfoFailed(errorResponse.getMessage());
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                mView.onFinish();
            }
        });

        addCall(call);
    }

    @Override
    public void uploadAvatar(String imageUrl) {
        mView.onBegin();
        Call<OssResponse> call = AppManager.getHttpService().uploadAvatar();
        addCall(call);
        call.enqueue(new BaseResponseCallback<OssResponse>() {
            @Override
            protected void onSuccess(OssResponse ossResponse) {
                OssEngine.Companion.uploadFile(ossResponse, imageUrl, new OssEngine.UploadCallback() {
                    @Override
                    public void onSuccess(String response) {
                        mView.onFinish();
                        try {
                            if (!TextUtils.isEmpty(response)) {
                                JSONObject jsonObject = new JSONObject(response);
                                String avatarUrl = jsonObject.getString("avatar");
                                if (!TextUtils.isEmpty(avatarUrl)) {
                                    UserInfo userProfile = AppManager.getAccountViewModel().getUserInfo();
                                    userProfile.avatar = avatarUrl;
                                    AppManager.getAccountViewModel().updateUserInfo(userProfile);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String errorCode, String serviceExceptionMessage) {
                        mView.onFinish();
                    }
                });
            }

            @Override
            protected void onFailure(@NonNull ErrorResponse errorResponse) {
                ToastUtils.showShort(errorResponse.getMessage());
                mView.onFinish();
            }
        });
    }

    @Override
    public void bindWechat(Activity activity, UMAuthListener umAuthListener) {
        AppManager.getOpenLogin().weChatLogin(activity, umAuthListener);
    }

    @Override
    public void bindSocial(int socialType, String socialInfo) {
        mView.onBegin();
        Call<Social> call = AppManager.getHttpService().bindSocialites(Social.SOCIAL_TYPE_WECHAT, socialInfo);
        addCall(call);
        call.enqueue(new BaseResponseCallback<Social>() {
            @Override
            protected void onSuccess(Social response) {
                mView.onBindSocialSuccess(response);
            }

            @Override
            protected void onFailure(@NonNull ErrorResponse errorResponse) {
                mView.onBindSocialFailed(errorResponse.getMessage());
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                mView.onFinish();
            }
        });
    }

    @Override
    public void unBindWechat(int socialId) {
        mView.onBegin();

        Call<String> call = AppManager.getHttpService().unbindSocialites(socialId);
        addCall(call);
        call.enqueue(new BaseResponseCallback<String>() {
            @Override
            protected void onSuccess(String response) {
                mView.onUnBindWechatSuccess();
            }

            @Override
            protected void onFailure(@NonNull ErrorResponse errorResponse) {
                mView.onUnBindWechatFailed(errorResponse.getMessage());
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                mView.onFinish();
            }
        });
    }
}
