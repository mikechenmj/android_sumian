package com.sumian.hw.account.contract;

import android.app.Activity;

import com.sumian.hw.base.BaseNetView;
import com.sumian.hw.base.BasePresenter;
import com.sumian.hw.network.response.HwToken;
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

        void onBindOpenSuccess(HwToken token);

        void onNotBindCallback(String error, String openUserInfo);

    }

    interface Presenter extends BasePresenter {

        void doLoginOpen(SHARE_MEDIA shareMedia, Activity activity, UMAuthListener authListener);

        void bindOpen(SHARE_MEDIA shareMedia, Map<String, String> OpenMap);

    }
}
