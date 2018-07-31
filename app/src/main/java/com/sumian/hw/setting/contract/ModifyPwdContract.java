package com.sumian.hw.setting.contract;

import com.sumian.hw.base.BaseNetView;
import com.sumian.hw.base.BasePresenter;
import com.sumian.hw.network.request.ModifyPwdBody;

/**
 * Created by jzz
 * on 2017/10/19.
 * desc:
 */

public interface ModifyPwdContract {


    interface View extends BaseNetView<Presenter> {

        void onModifyPwdSuccess();

        void onModifyPwdFailed(String error);

    }


    interface Presenter extends BasePresenter {

        void doResetPwd(ModifyPwdBody modifyPwdBody);
    }
}
