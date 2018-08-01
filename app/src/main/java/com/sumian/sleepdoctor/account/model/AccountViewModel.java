package com.sumian.sleepdoctor.account.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.SPUtils;
import com.sumian.sleepdoctor.account.bean.Token;
import com.sumian.sleepdoctor.account.bean.UserInfo;
import com.sumian.sleepdoctor.doctor.bean.Doctor;
import com.sumian.sleepdoctor.utils.JsonUtil;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

public class AccountViewModel extends AndroidViewModel {

    private static final String SP_KEY_TOKEN = "token";
    private MutableLiveData<Token> mTokenLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> mTokenIsInvalid = new MutableLiveData<>();

    public AccountViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadTokenFromSp() {
        String tokenJson = SPUtils.getInstance().getString(SP_KEY_TOKEN, null);
        Token token = JsonUtil.fromJson(tokenJson, Token.class);
        mTokenLiveData.setValue(token);
    }

    public LiveData<Token> getLiveDataToken() {
        return mTokenLiveData;
    }

    public Token getToken() {
        return mTokenLiveData.getValue();
    }

    public UserInfo getUserProfile() {
        return getToken().user;
    }

    public void updateBoundDoctor(Doctor doctor) {
        Token token = getToken();
        token.is_new = false;
        UserInfo userProfile = getUserProfile();
        if (doctor != null) {
            userProfile.doctor_id = doctor.getId();
        }
        userProfile.doctor = doctor;
        token.user = userProfile;
        updateUserProfile(userProfile);
    }

    public void updateToken(Token token) {
        mTokenLiveData.setValue(token);
        updateTokenInvalidState(token == null);
        persistentTokenInSp();
    }

    public void updateUserProfile(UserInfo userProfile) {
        Token token = getToken();
        token.is_new = false;
        token.user = userProfile;
        updateToken(token);
    }

    public String getTokenString() {
        return getToken() == null ? null : getToken().token;
    }

    public void updateTokenInvalidState(boolean tokenIsInvalid) {
        mTokenIsInvalid.setValue(tokenIsInvalid);
    }

    public LiveData<Boolean> getLiveDataTokenInvalidState() {
        return mTokenIsInvalid;
    }

    private void persistentTokenInSp() {
        SPUtils.getInstance().put(SP_KEY_TOKEN, JsonUtil.toJson(mTokenLiveData.getValue()));
    }
}
