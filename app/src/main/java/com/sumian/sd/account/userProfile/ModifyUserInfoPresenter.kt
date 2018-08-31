package com.sumian.sd.account.userProfile

import android.text.TextUtils
import android.util.Log
import android.view.View
import cn.carbswang.android.numberpickerview.library.NumberPickerView
import com.sumian.sd.R
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseResponseCallback
import java.util.*

/**
 * Created by sm
 *
 *
 * on 2018/7/5
 *
 *
 * desc:
 */
class ModifyUserInfoPresenter private constructor(private val mView: ModifyUserInfoContract.View) : ModifyUserInfoContract.Presenter {


    private val TAG: String = ModifyUserInfoPresenter::class.java.simpleName

    init {
        mView.setPresenter(this)
    }

    companion object {

        private const val MIN_YEAR = 1920
        private const val MIN_HEIGHT = 80
        private const val MAX_HEIGHT = 250
        private const val MIN_WEIGHT = 20
        private const val MAX_WEIGHT = 200

        private const val DEFAULT_YEAR = 1980
        private const val DEFAULT_WEIGHT = 50
        private const val DEFAULT_HEIGHT = 170

        fun init(view: ModifyUserInfoContract.View) {
            ModifyUserInfoPresenter(view)
        }
    }

    override fun improveUserProfile(improveKey: String, newUserProfile: String) {

        mView.onBegin()

        val map = HashMap<String, String>(1)

        map[improveKey] = newUserProfile

        val call = AppManager.getHttpService().modifyUserProfile(map)

        call.enqueue(object : BaseResponseCallback<UserInfo>() {
            override fun onSuccess(response: UserInfo?) {
                AppManager.getAccountViewModel().updateUserInfo(response)
                mView.onImproveUserProfileSuccess()
            }

            override fun onFailure(code: Int, message: String) {
                mView.onFailure(message)
            }

            override fun onFinish() {
                super.onFinish()
                mView.onFinish()
            }
        })

        addCall(call)
    }

    override fun transformTitle(modifyKey: String) {
        val userInfo = AppManager.getAccountViewModel().userInfo

        mView.transformTitle(when (modifyKey) {
            ImproveUserProfileContract.IMPROVE_WEIGHT_KEY -> {
                transformWeight(MIN_WEIGHT, MAX_WEIGHT, userInfo)

                mView.showOnePicker(View.VISIBLE)
                mView.showTwoPicker(View.VISIBLE)

                App.getAppContext().getString(R.string.weight)
            }
            ImproveUserProfileContract.IMPROVE_HEIGHT_KEY
            -> {
                transformHeight(MIN_HEIGHT, MAX_HEIGHT, userInfo)

                mView.showOnePicker(View.VISIBLE)
                mView.showTwoPicker(View.VISIBLE)

                App.getAppContext().getString(R.string.height)
            }
            ImproveUserProfileContract.IMPROVE_BIRTHDAY_KEY
            -> {
                transformBirthday(MIN_YEAR, userInfo)

                mView.showOnePicker(View.VISIBLE)
                mView.showTwoPicker(View.VISIBLE)
                App.getAppContext().getString(R.string.birthday)
            }
            ImproveUserProfileContract.IMPROVE_GENDER_KEY
            -> {
                var position = 0

                val genders: Array<out String> = App.getAppContext().resources.getStringArray(R.array.gender)
                genders.forEachIndexed { index, gender ->
                    if (gender == userInfo.formatGander()) {
                        position = index
                    }
                }

                mView.transformOneDisplayedValues(position, null, genders)
                mView.showOnePicker(View.VISIBLE)
                mView.showTwoPicker(View.GONE)

                App.getAppContext().getString(R.string.gender)
            }
            ImproveUserProfileContract.IMPROVE_EDUCATION_KEY
            -> {

                var position = 1

                val eduLevels: Array<out String> = App.getAppContext().resources.getStringArray(R.array.edu_level)
                eduLevels.forEachIndexed { index, eduLevel ->
                    if (eduLevel == userInfo.education) {
                        position = index
                    }
                }

                mView.transformOneDisplayedValues(position, null, eduLevels)

                mView.showOnePicker(View.VISIBLE)
                mView.showTwoPicker(View.GONE)
                App.getAppContext().getString(R.string.edu_level)
            }
            else -> {
                ""
            }
        })
    }

    override fun transformModify(modifyKey: String, mPickerOne: NumberPickerView, mPickerTwo: NumberPickerView): String {
        return when (modifyKey) {
            ImproveUserProfileContract.IMPROVE_WEIGHT_KEY,
            ImproveUserProfileContract.IMPROVE_HEIGHT_KEY -> {
                "${mPickerOne.contentByCurrValue}.${mPickerTwo.contentByCurrValue}"
            }
            ImproveUserProfileContract.IMPROVE_BIRTHDAY_KEY -> {
                "${mPickerOne.contentByCurrValue}-${mPickerTwo.contentByCurrValue}"
            }
            ImproveUserProfileContract.IMPROVE_GENDER_KEY -> {
                when (mPickerOne.contentByCurrValue) {
                    "女" -> {
                        "female"
                    }
                    "男" -> {
                        "male"
                    }
                    else -> {
                        "secrecy"
                    }
                }
            }
            ImproveUserProfileContract.IMPROVE_EDUCATION_KEY -> {
                mPickerOne.contentByCurrValue
            }
            else -> {
                ""
            }
        }
    }

    private fun transformBirthday(minYear: Int, userInfo: UserInfo) {
        val year = Calendar.getInstance().get(Calendar.YEAR) + 1
        val count = year - minYear
        val years = arrayOfNulls<String>(count)

        var monthCount = 12
        if (userInfo.birthdayYear == Calendar.getInstance().get(Calendar.YEAR)) {
            monthCount = Calendar.getInstance().get(Calendar.MONTH) + 1
        }
        val months = arrayOfNulls<String>(monthCount)

        var numberOnePosition = userInfo.birthdayYear - minYear
        if (numberOnePosition < 0) {
            numberOnePosition = calculateDefaultPosition(DEFAULT_YEAR, minYear)
        }
        var numberTwoPosition = userInfo.birthdayMonth - 1
        if (numberTwoPosition < 0) {
            numberTwoPosition = 0
        }
        for (i in 0 until count) {
            years[i] = (minYear + i).toString()
        }
        for (i in months.indices) {
            months[i] = String.format(Locale.getDefault(), "%02d", i + 1)
        }
        mView.transformOneDisplayedValues(numberOnePosition, "年", years)
        mView.transformTwoDisplayedValues(numberTwoPosition, "月", months)
    }


    private fun transformWeight(minWeight: Int, maxWeight: Int, userInfo: UserInfo) {

        val count = maxWeight - minWeight

        val weights = arrayOfNulls<String>(count)
        val decimalWeights = arrayOfNulls<String>(10)

        var weight = userInfo.weight
        if (!TextUtils.isEmpty(weight)) {
            weight = java.lang.Float.parseFloat(weight).toInt().toString()
        }

        val weightValue = userInfo.weightValue
        val valueX10 = (weightValue * 10).toInt()
        var numberOnePosition = valueX10 / 10 - minWeight
        if (numberOnePosition < 0) {
            numberOnePosition = calculateDefaultPosition(DEFAULT_WEIGHT, minWeight)
        }
        val numberTwoPosition = valueX10 % 10
        for (i in 0 until count) {
            weights[i] = (minWeight + i).toString()
            if (weights[i] == weight) {
                Log.e(TAG, "position=" + i + " weight=" + weights[i])
            }
            if (i < 10) {
                decimalWeights[i] = i.toString()
            }
        }

        mView.transformOneDisplayedValues(numberOnePosition, ".", weights)
        mView.transformTwoDisplayedValues(numberTwoPosition, "kg", decimalWeights)
    }

    private fun transformHeight(minHeight: Int, maxHeight: Int, userInfo: UserInfo) {
        val count = maxHeight - minHeight
        val heights = arrayOfNulls<String>(count)
        val decimalHeights = arrayOfNulls<String>(10)
        val heightValue = userInfo.heightValue
        val valueX10 = (heightValue * 10).toInt()
        var numberOnePosition = valueX10 / 10 - minHeight
        if (numberOnePosition < 0) {
            numberOnePosition = calculateDefaultPosition(DEFAULT_HEIGHT, minHeight)
        }
        val numberTwoPosition = valueX10 % 10
        for (i in 0 until count) {
            heights[i] = (minHeight + i).toString()
            if (i < 10) {
                decimalHeights[i] = i.toString()
            }
        }

        mView.transformOneDisplayedValues(numberOnePosition, ".", heights)
        mView.transformTwoDisplayedValues(numberTwoPosition, "cm", decimalHeights)
    }

    /**
     * 当用户未设置相关用户信息时,计算出默认值 [min ~ default  ~ max]
     *
     * @param defaultValue defaultValue
     * @param minValue     minValue
     * @return DefaultValuePosition
     */
    private fun calculateDefaultPosition(defaultValue: Int, minValue: Int): Int {
        return defaultValue - minValue
    }


}
