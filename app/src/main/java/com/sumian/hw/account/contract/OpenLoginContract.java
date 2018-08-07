package com.sumian.hw.account.contract;

import android.app.Activity;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;
import com.sumian.sleepdoctor.account.bean.Token;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 * Created by jzz
 * on 2017/12/16.
 * desc:
 */

public interface OpenLoginContract {


    interface View extends HwBaseNetView<Presenter> {

        void onBindOpenSuccess(Token token);

        void onNotBindCallback(String error, String openUserInfo);

    }

    interface Presenter extends HwBasePresenter {

        void doLoginOpen(SHARE_MEDIA shareMedia, Activity activity, UMAuthListener authListener);

        void bindOpen(SHARE_MEDIA shareMedia, Map<String, String> OpenMap);

    }
}
