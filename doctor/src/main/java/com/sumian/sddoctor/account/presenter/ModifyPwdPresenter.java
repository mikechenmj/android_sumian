package com.sumian.sddoctor.account.presenter;


import com.sumian.common.network.response.ErrorResponse;
import com.sumian.sddoctor.account.contract.ModifyPwdContract;
import com.sumian.sddoctor.app.AppManager;
import com.sumian.sddoctor.login.login.bean.DoctorInfo;
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/10/17.
 * desc:
 */

public class ModifyPwdPresenter implements ModifyPwdContract.Presenter {

    private ModifyPwdContract.View mView;

    private ModifyPwdPresenter(ModifyPwdContract.View view) {
        this.mView = view;
    }

    public static ModifyPwdContract.Presenter init(ModifyPwdContract.View view) {
        return new ModifyPwdPresenter(view);
    }

    @Override
    public void doResetPwd(String oldPwd, String newPwd, String newPwdConfirmation) {

        mView.showLoading();

        Map<String, Object> map = new HashMap<>(0);
        map.put("old_password", oldPwd);
        map.put("password", newPwd);
        map.put("password_confirmation", newPwdConfirmation);

        Call<DoctorInfo> call = AppManager.getHttpService().updateDoctorInfo(map);

        call.enqueue(new BaseSdResponseCallback<DoctorInfo>() {

            @Override
            protected void onSuccess(@Nullable DoctorInfo response) {
                AppManager.getAccountViewModel().updateDoctorInfo(response, true);
                mView.onModifyPwdSuccess();
            }

            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                mView.onModifyPwdFailed(errorResponse.getMessage());
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                mView.dismissLoading();
            }
        });


    }
}
