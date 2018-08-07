package com.sumian.hw.setting.contract;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;

/**
 * Created by jzz
 * on 2017/12/29.
 * desc:
 */

public interface SocialContract {


    interface View extends HwBaseNetView<Presenter> {

        void onUnbindSocialSuccess();

    }


    interface Presenter extends HwBasePresenter {

        void unbindSocial(int socialType);

    }
}
