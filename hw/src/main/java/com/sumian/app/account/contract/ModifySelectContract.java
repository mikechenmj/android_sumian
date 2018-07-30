package com.sumian.app.account.contract;

import com.sumian.app.network.response.HwUserInfo;

/**
 * Created by jzz
 * on 2017/10/26.
 * <p>
 * desc:
 */

public interface ModifySelectContract {

    interface View<T> extends ModifyUserInfoContract.View<T> {

        void transformOneDisplayedValues(int currentPosition, String hintText, String[]
            displayedValues);

        void transformTwoDisplayedValues(int currentPosition, String hintText, String[]
            displayedValues);

        void transformThreeDisplayedValues(int currentPosition, String hintText, String[] displayedValues);

    }


    interface Presenter extends ModifyUserInfoContract.Presenter {

        void transformFormKey(String formKey, HwUserInfo userInfo);

        Object transformFormValue(String formKey, String oneValue, String twoValue, String threeValue);

        void transformCityForProvince(String province);

        void transformAreaForCity(String city);
    }
}
