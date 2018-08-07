package com.sumian.hw.account.contract;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;
import com.sumian.hw.network.request.ResetPwdBody;

/**
 * Created by jzz
 * on 2017/10/18.
 * desc:
 */

public interface ResetPwdContract {


    interface View extends HwBaseNetView<Presenter> {

        void onResetPwdSuccess();

        void onResetPwdFailed(String error);
    }


    interface Presenter extends HwBasePresenter {

        void doResetPwd(ResetPwdBody resetPwdBody);
    }
}
