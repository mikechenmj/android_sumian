package com.sumian.hw.account.contract;

import android.app.Activity;
import android.content.Intent;

import com.sumian.hw.base.BaseNetView;
import com.sumian.hw.base.BasePresenter;

/**
 * Created by jzz
 * on 2018/1/3.
 * desc:
 */

public interface AvatarContract {

    interface View extends BaseNetView<Presenter> {

        void imageIsExit();

        void uploadSuccess(String url);

        void loadLocalImageSuccess(String url);
    }

    interface Presenter extends BasePresenter {

        void uploadOss();

        void sendPic(Activity activity, int type);

        void resultCodeDelegate(int requestCode, int resultCode, Intent data);

    }
}
