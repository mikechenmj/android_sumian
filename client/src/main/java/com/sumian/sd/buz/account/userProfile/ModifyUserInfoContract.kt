package com.sumian.sd.buz.account.userProfile


import com.sumian.common.widget.picker.NumberPickerView


/**
 * Created by sm
 *
 *
 * on 2018/7/5
 *
 *
 * desc:
 */
interface ModifyUserInfoContract : ImproveUserProfileContract {

    interface View : ImproveUserProfileContract.View {

        fun transformOneDisplayedValues(currentPosition: Int, hintText: String?, displayedValues: Array<out String?>?)

        fun transformTwoDisplayedValues(currentPosition: Int, hintText: String?, displayedValues: Array<out String?>?)

        fun transformThreeDisplayedValues(currentPosition: Int, hintText: String?, displayedValues: Array<out String?>?)

        fun showOnePicker(visible: Int)

        fun showTwoPicker(visible: Int)

        fun showThreePicker(visible: Int)

        fun transformTitle(title: String)

    }

    interface Presenter : ImproveUserProfileContract.Presenter {

        fun transformTitle(modifyKey: String)

        fun transformModify(modifyKey: String, pickerView: NumberPickerView, pickerTwo: NumberPickerView, pickerThree: NumberPickerView): String

        fun transformCityForProvince(province: String)

        fun transformAreaForCity(city: String)
    }
}
