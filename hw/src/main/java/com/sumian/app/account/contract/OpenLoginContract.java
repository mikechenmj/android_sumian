package com.sumian.app.account.contract;

import android.app.Activity;

import com.sumian.app.base.BaseNetView;
import com.sumian.app.base.BasePresenter;
import com.sumian.app.network.response.Token;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 * Created by jzz
 * on 2017/12/16.
 * desc:
 */

public interface OpenLoginContract {


    interface View extends BaseNetView<Presenter> {

        void onBindOpenSuccess(Token token);

        void onNotBindCallback(String error, String openUserInfo);

    }

    interface Presenter extends BasePresenter {

        void doLoginOpen(SHARE_MEDIA shareMedia, Activity activity, UMAuthListener authListener);

        void bindOpen(SHARE_MEDIA shareMedia, Map<String, String> OpenMap);

    }
}
