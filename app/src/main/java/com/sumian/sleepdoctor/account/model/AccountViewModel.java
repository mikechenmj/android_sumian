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

    public AccountViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Token> LoadToken() {
        if (mTokenLiveData == null) {
            mTokenLiveData = new MutableLiveData<>();
        }

        if (mTokenLiveData.getValue() == null) {
            Future<Token> future = Executors.newSingleThreadExecutor().submit(() -> AccountCache.getTokenCache(Token.class));
            try {
                Token t = future.get();
                mTokenLiveData.postValue(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return mTokenLiveData;
    }

    public LiveData<Token> getLiveDataToken() {
        if (mTokenLiveData == null) {
            mTokenLiveData = new MutableLiveData<>();
        }
        return mTokenLiveData;
    }

    public Token getToken() {
        return mTokenLiveData.getValue();
    }

    public void updateToken(Token token) {
        mTokenLiveData.postValue(token);
        AppOperator.runOnThread(() -> AccountCache.updateTokenCache(token));
    }

    public void updateUserProfile(UserProfile userProfile) {
        Token token = getToken();
        token.user = userProfile;
        updateToken(token);
    }

    public boolean isLogin() {
        return mTokenLiveData.getValue() != null;
    }

    public String accessToken() {
        return getToken() == null ? null : getToken().token;
    }

}
