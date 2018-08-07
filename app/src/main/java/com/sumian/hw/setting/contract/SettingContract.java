package com.sumian.hw.setting.contract;

import android.app.Activity;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;
import com.sumian.hw.network.response.UserSetting;
import com.sumian.sleepdoctor.account.bean.Social;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 * Created by jzz
 * on 2017/12/18.
 * desc:
 */

public interface SettingContract {

    interface View extends HwBaseNetView<Presenter> {

        void syncSleepDiaryCallback(UserSetting userSetting);

        void onBindOpenSuccess(Social social);

        void onBindOpenFailed(String error);

    }


    interface Presenter extends HwBasePresenter {


        void syncSleepDiary();

        void updateSleepDiary(int sleepDiaryEnable);

        void doLoginOpen(SHARE_MEDIA shareMedia, Activity activity, UMAuthListener authListener);

        void bindOpen(SHARE_MEDIA shareMedia, Map<String, String> openMap);
    }
}
