package com.sumian.sd.account.userProfile;


import androidx.annotation.NonNull;

import com.sumian.common.widget.picker.NumberPickerView;


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

        void transformThreeDisplayedValues(int currentPosition, String hintText, String[] displayedValues);

        void showOnePicker(int visible);

        void showTwoPicker(int visible);

        void showThreePicker(int visible);

        void transformTitle(@NonNull String title);

    }

    interface Presenter extends ImproveUserProfileContract.Presenter {

        void transformTitle(String modifyKey);

        String transformModify(@NonNull String modifyKey, @NonNull NumberPickerView pickerView, @NonNull NumberPickerView pickerTwo, @NonNull NumberPickerView pickerThree);

        void transformCityForProvince(@NonNull String province);

        void transformAreaForCity(@NonNull String city);
    }
}
