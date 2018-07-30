package com.sumian.app.setting.contract;

import android.app.Activity;

import com.sumian.app.base.BaseNetView;
import com.sumian.app.base.BasePresenter;
import com.sumian.app.network.response.HwUserInfo;
import com.sumian.app.network.response.UserSetting;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 * Created by jzz
 * on 2017/12/18.
 * desc:
 */

public interface SettingContract {

    interface View extends BaseNetView<Presenter> {

        void syncSleepDiaryCallback(UserSetting userSetting);

        void onBindOpenSuccess(HwUserInfo.Social social);

        void onBindOpenFailed(String error);

    }


    interface Presenter extends BasePresenter {


        void syncSleepDiary();

        void updateSleepDiary(int sleepDiaryEnable);

        void doLoginOpen(SHARE_MEDIA shareMedia, Activity activity, UMAuthListener authListener);

        void bindOpen(SHARE_MEDIA shareMedia, Map<String, String> openMap);
    }
}
