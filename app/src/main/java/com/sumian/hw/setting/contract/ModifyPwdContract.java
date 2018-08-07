package com.sumian.hw.setting.contract;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;
import com.sumian.hw.network.request.ModifyPwdBody;

/**
 * Created by jzz
 * on 2017/10/19.
 * desc:
 */

public interface ModifyPwdContract {


    interface View extends HwBaseNetView<Presenter> {

        void onModifyPwdSuccess();

        void onModifyPwdFailed(String error);

    }


    interface Presenter extends HwBasePresenter {

        void doResetPwd(ModifyPwdBody modifyPwdBody);
    }
}
