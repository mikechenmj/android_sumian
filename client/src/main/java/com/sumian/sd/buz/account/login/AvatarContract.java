package com.sumian.sd.buz.account.login;

import android.app.Activity;
import android.content.Intent;

import com.sumian.sd.base.HwBaseNetView;
import com.sumian.sd.base.HwBasePresenter;

/**
 * Created by jzz
 * on 2018/1/3.
 * desc:
 */

public interface AvatarContract {

    interface View extends HwBaseNetView<Presenter> {

        void imageIsExit();

        void uploadSuccess(String url);

        void loadLocalImageSuccess(String url);
    }

    interface Presenter extends HwBasePresenter {

        void uploadOss();

        void sendPic(Activity activity, int type);

        void resultCodeDelegate(int requestCode, int resultCode, Intent data);

    }
}
