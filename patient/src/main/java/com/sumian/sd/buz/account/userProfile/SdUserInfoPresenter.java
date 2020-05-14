package com.sumian.sd.buz.account.userProfile;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.sumian.common.base.BaseViewModel;
import com.sumian.common.image.ImagesScopeStorageHelper;
import com.sumian.common.network.response.ErrorResponse;
import com.sumian.sd.app.App;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.buz.account.bean.Ethnicities;
import com.sumian.sd.buz.account.bean.Social;
import com.sumian.sd.buz.account.bean.UserInfo;
import com.sumian.sd.common.network.callback.BaseSdResponseCallback;
import com.sumian.sd.common.oss.OssEngine;
import com.sumian.sd.common.oss.OssResponse;
import com.umeng.socialize.UMAuthListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

import static com.sumian.sd.buz.account.bean.Ethnicities.SP_ETHNICITIES;

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
public class SdUserInfoPresenter extends BaseViewModel {

    private UserInfoActivity mView;

    private SdUserInfoPresenter(UserInfoActivity view) {
        view.setPresenter(this);
        mView = view;
    }

    public static SdUserInfoPresenter init(UserInfoActivity view) {
        return new SdUserInfoPresenter(view);
    }

    public void getUserInfo() {
        //mView.onBegin();
        Call<UserInfo> call = AppManager.getSdHttpService().getUserProfile();
        addCall(call);
        call.enqueue(new BaseSdResponseCallback<UserInfo>() {
            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                mView.onGetUserInfoFailed(errorResponse.getMessage());
            }

            @Override
            protected void onSuccess(@Nullable UserInfo response) {
                AppManager.getAccountViewModel().updateUserInfo(response);
                mView.onGetUserInfoSuccess(response);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                //mView.onFinish();
            }
        });
        addCall(call);
    }

    public void getEthnicities() {
        Call<Ethnicities> call = AppManager.getSdHttpService().getEthnicities();
        addCall(call);
        call.enqueue(new BaseSdResponseCallback<Ethnicities>() {
            @Override
            protected void onSuccess(@Nullable Ethnicities response) {
                mView.onGetEthnicitySuccess(response);
                SharedPreferences sharedPreferences = App.getAppContext().getSharedPreferences(SP_ETHNICITIES, 0);
                String ethnicitiesJson = new Gson().toJson(response);
                sharedPreferences.edit().putString(SP_ETHNICITIES, ethnicitiesJson).commit();
            }

            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                mView.onGetEthnicityFailed(errorResponse.getMessage());
            }
        });
    }

    public void uploadAvatar(String imageUrl) {
        mView.onBegin();
        Call<OssResponse> call = AppManager.getSdHttpService().uploadAvatar();
        addCall(call);
        call.enqueue(new BaseSdResponseCallback<OssResponse>() {
            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                ToastUtils.showShort(errorResponse.getMessage());
                mView.onFinish();
            }

            @Override
            protected void onSuccess(OssResponse ossResponse) {
                OssEngine.UploadCallback callback = new OssEngine.UploadCallback() {
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
                };
                if (imageUrl.startsWith("content://")) {
                    byte[] imageData = ImagesScopeStorageHelper.INSTANCE.contentUriToByte(imageUrl);
                    OssEngine.Companion.uploadFile(ossResponse, imageData, callback);
                } else {
                    OssEngine.Companion.uploadFile(ossResponse, imageUrl, callback);
                }
            }
        });
    }

    public void bindWechat(Activity activity, UMAuthListener umAuthListener) {
        AppManager.getOpenLogin().weChatLogin(activity, umAuthListener);
    }

    public void bindSocial(int socialType, String socialInfo) {
        mView.onBegin();
        Call<Social> call = AppManager.getSdHttpService().bindSocialites(Social.SOCIAL_TYPE_WECHAT, socialInfo);
        addCall(call);
        call.enqueue(new BaseSdResponseCallback<Social>() {
            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                mView.onBindSocialFailed(errorResponse.getMessage());
            }

            @Override
            protected void onSuccess(Social response) {
                mView.onBindSocialSuccess(response);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                mView.onFinish();
            }
        });
    }

    public void unBindWechat(int socialId) {
        mView.onBegin();

        Call<String> call = AppManager.getSdHttpService().unbindSocialites(socialId, true);
        addCall(call);
        call.enqueue(new BaseSdResponseCallback<String>() {
            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                mView.onUnBindWechatFailed(errorResponse.getMessage());
            }

            @Override
            protected void onSuccess(String response) {
                mView.onUnBindWechatSuccess();
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                mView.onFinish();
            }
        });
    }
}
