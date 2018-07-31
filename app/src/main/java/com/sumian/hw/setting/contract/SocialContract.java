package com.sumian.hw.setting.contract;

import com.sumian.hw.base.BaseNetView;
import com.sumian.hw.base.BasePresenter;

/**
 * Created by jzz
 * on 2017/12/29.
 * desc:
 */

public interface SocialContract {


    interface View extends BaseNetView<Presenter> {

        void onUnbindSocialSuccess();

    }


    interface Presenter extends BasePresenter {

        void unbindSocial(int socialType);

    }
}
