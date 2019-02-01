package com.sumian.sddoctor.account.contract;


import com.sumian.sddoctor.base.BasePresenter;
import com.sumian.sddoctor.base.BaseView;

/**
 * Created by jzz
 * on 2017/10/19.
 * desc:
 */

public interface ModifyPwdContract {


    interface View extends BaseView {

        void onModifyPwdSuccess();

        void onModifyPwdFailed(String error);

    }


    interface Presenter extends BasePresenter {

        void doResetPwd(String oldPwd, String newPwd, String newPwdConfirmation);
    }
}
