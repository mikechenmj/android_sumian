package com.sumian.sleepdoctor.account.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.sumian.common.operator.AppOperator;
import com.sumian.sleepdoctor.account.bean.Token;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.account.cache.AccountCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

public class AccountViewModel extends AndroidViewModel {

    private static final String TAG = AccountViewModel.class.getSimpleName();

    private MutableLiveData<Token> mTokenLiveData;

    private MutableLiveData<Boolean> mTokenIsInvalid;

    public AccountViewModel(@NonNull Application application) {
        super(application);
    }

    public void LoadToken() {
        if (mTokenLiveData == null) {
            mTokenLiveData = new MutableLiveData<>();
        }

        if (mTokenLiveData.getValue() == null) {
            Future<Token> future = Executors.newSingleThreadExecutor().submit(() -> AccountCache.getTokenCache(Token.class));
            try {
                Token t = future.get();
                //updateTokenInvalidState(t == null);

                mTokenLiveData.postValue(t);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public LiveData<Token> getLiveDataToken() {
        return mTokenLiveData;
    }

    public Token getToken() {
        return mTokenLiveData.getValue();
    }

    public UserProfile getUserProfile() {
        return getToken().user;
    }

    public void updateToken(Token token) {
        mTokenLiveData.postValue(token);
        updateTokenInvalidState(token == null);
        AppOperator.runOnThread(() -> AccountCache.updateTokenCache(token));
    }

    public void updateUserProfile(UserProfile userProfile) {
        Token token = getToken();
        token.is_new = false;
        token.user = userProfile;
        updateToken(token);
    }

    public String accessToken() {
        return getToken() == null ? null : getToken().token;
    }

    public void updateTokenInvalidState(boolean tokenIsInvalid) {
        if (mTokenIsInvalid == null) {
            mTokenIsInvalid = new MutableLiveData<>();
        }
        mTokenIsInvalid.postValue(tokenIsInvalid);
    }

    public LiveData<Boolean> getLiveDataTokenInvalidState() {
        if (mTokenIsInvalid == null) {
            mTokenIsInvalid = new MutableLiveData<>();
        }
        return mTokenIsInvalid;
    }
}
