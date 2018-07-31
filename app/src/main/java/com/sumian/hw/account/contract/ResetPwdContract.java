package com.sumian.hw.account.contract;

import com.sumian.hw.base.BaseNetView;
import com.sumian.hw.base.BasePresenter;
import com.sumian.hw.network.request.ResetPwdBody;

/**
 * Created by jzz
 * on 2017/10/18.
 * desc:
 */

public interface ResetPwdContract {


    interface View extends BaseNetView<Presenter> {

        void onResetPwdSuccess();

        void onResetPwdFailed(String error);
    }


    interface Presenter extends BasePresenter {

        void doResetPwd(ResetPwdBody resetPwdBody);
    }
}
