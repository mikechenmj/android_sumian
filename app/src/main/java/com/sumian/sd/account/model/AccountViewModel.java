package com.sumian.sd.account.model;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.os.Looper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.sumian.common.utils.SumianExecutor;
import com.sumian.sd.BuildConfig;
import com.sumian.sd.account.bean.Token;
import com.sumian.sd.account.bean.UserInfo;
import com.sumian.sd.doctor.bean.Doctor;
import com.sumian.sd.utils.JsonUtil;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

public class AccountViewModel extends AndroidViewModel {

    private static final String SP_KEY_TOKEN = "token";
    private MutableLiveData<Token> mTokenLiveData = new MutableLiveData<>();

    public AccountViewModel(@NonNull Application application) {
        super(application);
        loadTokenFromSp();
    }

    private void loadTokenFromSp() {
        String tokenJson = SPUtils.getInstance().getString(SP_KEY_TOKEN, null);
        Token token = JsonUtil.fromJson(tokenJson, Token.class);
        if (BuildConfig.DEBUG) {
            if (token != null) {
                LogUtils.d(token.getToken());
            }
        }
        runUiThread(() -> mTokenLiveData.setValue(token));
    }

    private void runUiThread(Runnable run) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            run.run();
        } else {
            SumianExecutor.INSTANCE.runOnUiThread(run);
        }
    }

    public LiveData<Token> getLiveDataToken() {
        return mTokenLiveData;
    }

    public Token getToken() {
        return mTokenLiveData.getValue();
    }

    public UserInfo getUserInfo() {
        Token token = getToken();
        if (token == null) {
            return null;
        }
        return token.user;
    }

    public void updateBoundDoctor(Doctor doctor) {
        Token token = getToken();
        token.is_new = false;
        UserInfo userProfile = getUserInfo();
        if (doctor != null) {
            userProfile.doctor_id = doctor.getId();
        }
        userProfile.doctor = doctor;
        token.user = userProfile;
        updateUserInfo(userProfile);
    }

    public void updateToken(Token token) {
        runUiThread(() -> mTokenLiveData.setValue(token));
        persistentTokenInSp();
    }

    public void asyncUpdateToken(Token token) {
        mTokenLiveData.postValue(token);
        persistentTokenInSp();
    }

    public void asyncUpdateUserInfo(UserInfo userInfo) {
        Token token = getToken();
        token.is_new = false;
        token.user = userInfo;
        asyncUpdateToken(token);
    }

    @MainThread
    public void updateUserInfo(UserInfo userInfo) {
        Token token = getToken();
        token.user = userInfo;
        updateToken(token);
    }

    public String getTokenString() {
        return getToken() == null ? null : getToken().token;
    }

    private void persistentTokenInSp() {
        SPUtils.getInstance().put(SP_KEY_TOKEN, JsonUtil.toJson(mTokenLiveData.getValue()));
    }

    public String getLeanCloudId() {
        Token token = getToken();
        if (token == null) {
            return null;
        }
        UserInfo user = token.user;
        if (user == null) {
            return null;
        }
        return user.leancloud_id;
    }

    public boolean isLogin() {
        Token token = getToken();
        return token != null && !TextUtils.isEmpty(token.getToken());// && token.getExpired_at() * 1000L > System.currentTimeMillis();
    }

    public boolean isHaveUserInfoAndSleepBarrierTest() {
        UserInfo userInfo = getUserInfo();
        return userInfo != null && userInfo.isHaveUserInfoAndSleepBarrierTest();
    }

    public boolean isHaveAnswers() {
        UserInfo userInfo = getUserInfo();
        return userInfo != null && userInfo.isHaveAnswers();
    }

    public boolean isHaveFullUserInfo() {
        UserInfo userInfo = getUserInfo();
        return userInfo != null && userInfo.isHaveFullUserInfo();
    }

    public boolean isBindDoctor() {
        UserInfo userInfo = getUserInfo();
        return userInfo != null && userInfo.isBindDoctor();
    }
}
