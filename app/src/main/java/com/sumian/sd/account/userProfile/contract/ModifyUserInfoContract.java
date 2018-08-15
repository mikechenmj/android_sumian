package com.sumian.sd.account.userProfile.contract;


import android.support.annotation.NonNull;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * Created by sm
 * <p>
 * on 2018/7/5
 * <p>
 * desc:
 */
public interface ModifyUserInfoContract extends ImproveUserProfileContract {

    interface View extends ImproveUserProfileContract.View {

        void transformOneDisplayedValues(int currentPosition, String hintText, String[] displayedValues);

        void transformTwoDisplayedValues(int currentPosition, String hintText, String[] displayedValues);

        void showOnePicker(int visible);

        void showTwoPicker(int visible);

        void transformTitle(@NonNull String title);

    }

    interface Presenter extends ImproveUserProfileContract.Presenter {

        void transformTitle(String modifyKey);

        String transformModify(@NonNull String modifyKey, @NonNull NumberPickerView mPickerOne, @NonNull NumberPickerView mPickerTwo);
    }
}
