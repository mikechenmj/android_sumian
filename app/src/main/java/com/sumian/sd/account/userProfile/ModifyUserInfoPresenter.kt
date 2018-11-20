package com.sumian.sd.account.userProfile

import android.text.TextUtils
import android.view.View
import com.alibaba.fastjson.JSON
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.SumianExecutor
import com.sumian.common.widget.picker.NumberPickerView
import com.sumian.hw.utils.StreamUtil
import com.sumian.sd.R
import com.sumian.sd.account.bean.City
import com.sumian.sd.account.bean.Province
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback
import java.util.*
import kotlin.collections.ArrayList

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

    init {
        mView.setPresenter(this)
    }

    companion object {

        private const val MIN_YEAR = 1920
        private const val MIN_HEIGHT = 30
        private const val MAX_HEIGHT = 241
        private const val MIN_WEIGHT = 20
        private const val MAX_WEIGHT = 201

        private const val DEFAULT_YEAR = 1980
        private const val DEFAULT_WEIGHT = 50
        private const val DEFAULT_HEIGHT = 165

        @JvmStatic
        fun init(view: ModifyUserInfoContract.View) {
            ModifyUserInfoPresenter(view)
        }
    }

    private var mProvinces: List<Province>? = null
    //private Map<Province, List<City>> mMapCities;
    private var mMapArea: MutableMap<City, MutableList<String>>? = null

    override fun improveUserProfile(improveKey: String, newUserProfile: String) {

        mView.onBegin()

        val map = HashMap<String, String>(1)

        map[improveKey] = newUserProfile

        val call = AppManager.getSdHttpService().modifyUserProfile(map)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<UserInfo>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView.onFailure(errorResponse.message)
            }

            override fun onSuccess(response: UserInfo?) {
                AppManager.getAccountViewModel().updateUserInfo(response)
                mView.onImproveUserProfileSuccess()
            }

            override fun onFinish() {
                super.onFinish()
                mView.onFinish()
            }
        })

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
                mView.showTwoPicker(View.GONE)

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
            ImproveUserProfileContract.IMPROVE_MEDICINE_HISTORY
            -> {
                val position = if (userInfo.sleep_pills == 1) 1 else 0
                val displayedValues: Array<out String> = App.getAppContext().resources.getStringArray(R.array.medicine_history)
                mView.transformOneDisplayedValues(position, null, displayedValues)
                mView.showOnePicker(View.VISIBLE)
                mView.showTwoPicker(View.GONE)
                App.getAppContext().getString(R.string.is_using_sleep_pills)
            }
            ImproveUserProfileContract.IMPROVE_AREA_KEY -> {

                transformProvince()

                mView.showOnePicker(View.VISIBLE)
                mView.showTwoPicker(View.VISIBLE)
                mView.showThreePicker(View.VISIBLE)
                App.getAppContext().getString(R.string.area)
            }
            else -> {
                ""
            }
        })
    }

    override fun transformModify(modifyKey: String, pickerOne: NumberPickerView, pickerTwo: NumberPickerView, pickerThree: NumberPickerView): String {
        return when (modifyKey) {
            ImproveUserProfileContract.IMPROVE_WEIGHT_KEY -> {
                pickerOne.contentByCurrValue + "." + pickerTwo.contentByCurrValue
            }
            ImproveUserProfileContract.IMPROVE_HEIGHT_KEY -> {
                pickerOne.contentByCurrValue
            }
            ImproveUserProfileContract.IMPROVE_BIRTHDAY_KEY -> {
                "${pickerOne.contentByCurrValue}-${pickerTwo.contentByCurrValue}"
            }
            ImproveUserProfileContract.IMPROVE_GENDER_KEY -> {
                when (pickerOne.contentByCurrValue) {
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
                pickerOne.contentByCurrValue
            }
            ImproveUserProfileContract.IMPROVE_MEDICINE_HISTORY -> {
                if (pickerOne.contentByCurrValue == "有服用") {
                    "2"
                } else {
                    "1"
                }
            }
            ImproveUserProfileContract.IMPROVE_AREA_KEY -> {
                "${pickerOne.contentByCurrValue}\\${pickerTwo.contentByCurrValue}\\${pickerThree.contentByCurrValue}"
            }
            else -> {
                ""
            }
        }
    }

    override fun transformCityForProvince(province: String) {
        SumianExecutor.runOnBackgroundThread {
            val provinces = this.mProvinces
            if (provinces == null || provinces.isEmpty()) return@runOnBackgroundThread

            var p: Province? = null
            var i = 0
            val len = provinces.size
            while (i < len) {
                p = provinces[i]
                if (province == p.name) {
                    break
                }
                i++
            }
            transformCity(p!!)
        }
    }

    override fun transformAreaForCity(city: String) {
        SumianExecutor.runOnBackgroundThread {
            var areas: MutableList<String>? = null
            for ((key) in mMapArea!!) {
                if (key.name == city) {
                    areas = key.area
                    break
                }
            }
            transformArea(areas)
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
                // Log.e(TAG, "position=" + i + " weight=" + weights[i])
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
        // val numberTwoPosition = valueX10 % 10
        for (i in 0 until count) {
            heights[i] = (minHeight + i).toString()
            if (i < 10) {
                decimalHeights[i] = i.toString()
            }
        }

        mView.transformOneDisplayedValues(numberOnePosition, "cm", heights)
//        mView.transformTwoDisplayedValues(numberTwoPosition, "cm", decimalHeights)
    }

    private fun transformProvince() {
        SumianExecutor.runOnBackgroundThread {
            val provinceJson = StreamUtil.getJson("province.json")
            if (TextUtils.isEmpty(provinceJson)) return@runOnBackgroundThread
            val provinces = JSON.parseArray(provinceJson, Province::class.java)
            var mapArea: MutableMap<City, MutableList<String>>? = mMapArea
            if (mapArea == null) {
                mapArea = HashMap()
            }
            val provinceNames = arrayOfNulls<String>(provinces.size)
            var position = 0
            var i = 0
            val len = provinces.size
            while (i < len) {
                val province = provinces[i]
                val name = province.name
                if (name != null && name == AppManager.getAccountViewModel().userInfo.addressArray[0]) {
                    position = i
                }
                provinceNames[i] = name
                val cities = province.city
                for (city in cities) {
                    val areas = city.area
                    if (mapArea.containsKey(city)) {
                        mapArea[city]?.addAll(areas)
                    } else {
                        mapArea[city] = areas
                    }
                }
                i++
            }
            mView.transformOneDisplayedValues(position, null, provinceNames)
            transformCity(provinces[position])
            this.mProvinces = provinces
            this.mMapArea = mapArea
        }
    }

    private fun transformCity(province: Province) {
        val cities = province.city
        val cityNames = ArrayList<String>()
        var cityName: String?
        var position = 0
        for (i in cities.indices) {
            val city = cities[i]
            cityName = city.name
            if (cityName != null && cityName == AppManager.getAccountViewModel().userInfo.addressArray[1]) {
                position = i
            }
            if ("其他" == cityName || "其他市" == cityName) continue
            cityNames.add(cityName)
        }
        mView.transformTwoDisplayedValues(position, null, cityNames.toTypedArray())
        transformArea(cities[position].area)

    }

    private fun transformArea(areas: MutableList<String>?) {
        if (areas == null) {
            return
        }
        var position = 0
        for (i in areas.indices) {
            val areaName = areas[i]
            if (areaName == AppManager.getAccountViewModel().userInfo.addressArray[2]) {
                position = i
            }
            if ("其他" == areaName) areas.remove(areaName)
        }
        mView.transformThreeDisplayedValues(position, null, areas.toTypedArray())
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
