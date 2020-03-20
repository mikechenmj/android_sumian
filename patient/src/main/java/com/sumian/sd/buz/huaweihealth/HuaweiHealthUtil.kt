package com.sumian.sd.buz.huaweihealth

import android.content.Context
import android.os.Looper
import android.util.Log

import com.huawei.hihealth.error.HiHealthError
import com.huawei.hihealthkit.HiHealthDataQuery
import com.huawei.hihealthkit.HiHealthDataQueryOption
import com.huawei.hihealthkit.auth.HiHealthAuth
import com.huawei.hihealthkit.auth.IAuthorizationListener
import com.huawei.hihealthkit.data.HiHealthData
import com.huawei.hihealthkit.data.HiHealthPointData
import com.huawei.hihealthkit.data.HiHealthSetData
import com.huawei.hihealthkit.data.store.HiRealTimeListener
import com.huawei.hihealthkit.data.type.HiHealthPointType
import com.huawei.hihealthkit.data.type.HiHealthSetType
import com.sumian.sd.R
import com.sumian.sd.common.log.SdLogManager
import com.sumian.sd.common.utils.TimeUtil

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import kotlin.collections.ArrayList

object HuaweiHealthUtil {

    fun queryHuaweiHealthData(context: Context, start: String, end: String, onResult: (Int, HuaweiHealthData?) -> Unit) {
        Thread {
            var huaweiHealthData = HuaweiHealthData()
            queryGender(context) { code, gender ->
                if (code != HiHealthError.SUCCESS) {
                    onResult(-1, null)
                    return@queryGender
                }
                huaweiHealthData.gender = gender
                queryBirthday(context) { code, birthday ->
                    if (code != HiHealthError.SUCCESS) {
                        onResult(-1, null)
                        return@queryBirthday
                    }
                    huaweiHealthData.birthday = birthday
                    queryHeight(context) { code, height ->
                        if (code != HiHealthError.SUCCESS) {
                            onResult(-1, null)
                            return@queryHeight
                        }
                        huaweiHealthData.height = height
                        queryWeight(context) { code, weight ->
                            if (code != HiHealthError.SUCCESS) {
                                onResult(-1, null)
                                return@queryWeight
                            }
                            huaweiHealthData.weight = weight
                            queryStepSum(context, start, end) { code, stepSum ->
                                if (code != HiHealthPointType.DATA_POINT_STEP_SUM) {
                                    onResult(-1, null)
                                    return@queryStepSum
                                }
                                huaweiHealthData.stepSum = stepSum
                                queryDistanceSum(context, start, end) { code, distanceSum ->
                                    if (code != HiHealthPointType.DATA_POINT_DISTANCE_SUM) {
                                        onResult(-1, null)
                                        return@queryDistanceSum
                                    }
                                    huaweiHealthData.distanceSum = distanceSum
                                    queryCaloriesSum(context, start, end) { code, caloriesSum ->
                                        if (code != HiHealthPointType.DATA_POINT_CALORIES_SUM) {
                                            onResult(-1, null)
                                            return@queryCaloriesSum
                                        }
                                        huaweiHealthData.caloriesSum = caloriesSum
                                        queryCoreSleep(context, start, end) { code, coreSleeps ->
                                            huaweiHealthData.coreSleeps = coreSleeps
                                            onResult(code, huaweiHealthData)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.start()
    }

    fun requestAuthorization(context: Context, onResult: (Int, String) -> Unit) {
        val read = intArrayOf(
                HiHealthPointType.DATA_POINT_STEP_SUM,
                HiHealthPointType.DATA_POINT_DISTANCE_SUM,
                HiHealthPointType.DATA_POINT_CALORIES_SUM,
                HiHealthSetType.DATA_SET_CORE_SLEEP,
                101001, 101002)
        val write = intArrayOf()
        SumianHiHealthAuth.requestAuthorization(context, write, read) { code, message ->
            onResult(code, message.toString())
        }
    }

    private fun parseCoreSleepData(data: ArrayList<HiHealthData>): ArrayList<HuaweiHealthData.CoreSleep> {
        var coreSleepData = ArrayList<HuaweiHealthData.CoreSleep>(data.size)
        var totalSleepDuration: Float? = null
        var nightSleepDuration: Float? = null
        for (hiHealthData in data) {
            if (hiHealthData is HiHealthSetData) {
                var coreSleep = HuaweiHealthData.CoreSleep(startTime = hiHealthData.startTime, endTime = hiHealthData.endTime)
                for (entry in hiHealthData.map.entries) {
                    entry.toString().split("=").let {
                        val key = it[0]
                        val value = it[it.size - 1]
                        when (key) {
                            "44209" -> {
                                coreSleep.nightSleepDuration = value
                                nightSleepDuration = value.toFloatOrNull()
                            }
                            "44101" -> {
                                coreSleep.remSleepDuration = value
                            }
                            "44102" -> {
                                coreSleep.deepSleepDuration = value
                            }
                            "44103" -> {
                                coreSleep.lightSleepDuration = value
                            }
                            "44105" -> {
                                coreSleep.totalSleepDuration = value
                                totalSleepDuration = value.toFloatOrNull()
                            }
                            "44201" -> {
                                coreSleep.fallAsleepTime = value
                            }
                            "44202" -> {
                                coreSleep.wakeUpTime = value
                            }
                            "44106" -> {
                                coreSleep.deepSleepContinuityScore = value
                            }
                            "44107" -> {
                                coreSleep.numberOfSleepWakefulness = value
                            }
                            "44203" -> {
                                coreSleep.SleepScore = value
                            }
                        }
                        if (totalSleepDuration != null && nightSleepDuration != null) {
                            coreSleep.dozeDuration = (totalSleepDuration!! - nightSleepDuration!!).toString()
                        }
                    }
                }
                coreSleepData.add(coreSleep)
            }
        }
        return coreSleepData
    }

    fun queryCoreSleep(context: Context, start: String, end: String, onResult: (Int, ArrayList<HuaweiHealthData.CoreSleep>) -> Unit) {
        execQuery(context, HiHealthSetType.DATA_SET_CORE_SLEEP, TimeUtil.dateStringToTime(start), TimeUtil.dateStringToTime(end))
        { i, arrayList ->
            SdLogManager.logHuaweiHealth("查询核心睡眠数据，结果码为：$i 数据量为: ${arrayList.size}")
            onResult(i, parseCoreSleepData(arrayList))
        }
    }

    private fun parseStepSum(data: ArrayList<HiHealthData>): ArrayList<HuaweiHealthData.StepSum> {
        var stepSum = ArrayList<HuaweiHealthData.StepSum>(data.size)
        for (hiHealthData in data) {
            if (hiHealthData is HiHealthPointData) {
                stepSum.add(HuaweiHealthData.StepSum(hiHealthData.value, hiHealthData.startTime, hiHealthData.endTime))
            }
        }
        return stepSum
    }

    private fun queryStepSum(context: Context, start: String, end: String, onResult: (Int, ArrayList<HuaweiHealthData.StepSum>) -> Unit) {
        execQuery(context, HiHealthPointType.DATA_POINT_STEP_SUM, TimeUtil.dateStringToTime(start), TimeUtil.dateStringToTime(end))
        { i, arrayList ->
            onResult(i, parseStepSum(arrayList))
        }
    }

    private fun parseDistanceSum(data: ArrayList<HiHealthData>): ArrayList<HuaweiHealthData.DistanceSum> {
        var distanceSum = ArrayList<HuaweiHealthData.DistanceSum>(data.size)
        for (hiHealthData in data) {
            if (hiHealthData is HiHealthPointData) {
                distanceSum.add(HuaweiHealthData.DistanceSum(hiHealthData.value, hiHealthData.startTime, hiHealthData.endTime))
            }
        }
        return distanceSum
    }

    fun queryDistanceSum(context: Context, start: String, end: String, onResult: (Int, ArrayList<HuaweiHealthData.DistanceSum>) -> Unit) {
        execQuery(context, HiHealthPointType.DATA_POINT_DISTANCE_SUM, TimeUtil.dateStringToTime(start), TimeUtil.dateStringToTime(end))
        { i, arrayList ->
            onResult(i, parseDistanceSum(arrayList))
        }
    }

    private fun parseCaloriesSum(data: ArrayList<HiHealthData>): ArrayList<HuaweiHealthData.CaloriesSum> {
        var calories = ArrayList<HuaweiHealthData.CaloriesSum>(data.size)
        for (hiHealthData in data) {
            if (hiHealthData is HiHealthPointData) {
                calories.add(HuaweiHealthData.CaloriesSum(hiHealthData.value, hiHealthData.startTime, hiHealthData.endTime))
            }
        }
        return calories
    }

    private fun queryCaloriesSum(context: Context, start: String, end: String, onResult: (Int, ArrayList<HuaweiHealthData.CaloriesSum>) -> Unit) {
        execQuery(context, HiHealthPointType.DATA_POINT_CALORIES_SUM, TimeUtil.dateStringToTime(start), TimeUtil.dateStringToTime(end))
        { i, arrayList ->
            onResult(i, parseCaloriesSum(arrayList))
        }
    }

    fun getDataAuthStatus(context: Context) {
        val write = HiHealthSetType.DATA_SET_BLOOD_SUGAR
        SumianHiHealthAuth.getDataAuthStatus(context, write,
                IAuthorizationListener { i, o ->
                    if (i != 0) {
                        return@IAuthorizationListener
                    }
                    val list = o as List<*>
                    val permission = list[0]
                    var perStr = ""
                    when (permission) {
                        0 -> perStr = "未申请"
                        1 -> perStr = "已允许"
                        2 -> perStr = "已拒绝"
                    }
                })
    }

    fun queryGender(context: Context, onResult: (Int, String) -> Unit) {
        SumianHiHealthDataStore.getGender(context) { code, gender ->
            if (code == HiHealthError.SUCCESS) {
                var genderStr = "未知"
                when (gender) {
                    0 -> genderStr = "女"
                    1 -> genderStr = "男"
                    2 -> genderStr = "未知"
                }
                onResult(code, genderStr)
            } else {
                onResult(code, "获取错误")
            }
        }
    }

    fun queryBirthday(context: Context, onResult: (Int, String) -> Unit) {
        SumianHiHealthDataStore.getBirthday(context) { code, birthday ->
            if (code == HiHealthError.SUCCESS) {
                onResult(code, birthday.toString())
            } else {
                onResult(code, "获取错误")
            }
        }
    }

    fun queryHeight(context: Context, onResult: (Int, String) -> Unit) {
        SumianHiHealthDataStore.getHeight(context) { code, height ->
            if (code == HiHealthError.SUCCESS) {
                onResult(code, height.toString())
            } else {
                onResult(code, "获取错误")
            }
        }
    }

    fun queryWeight(context: Context, onResult: (Int, String) -> Unit) {
        SumianHiHealthDataStore.getWeight(context) { code, weight ->
            if (code == HiHealthError.SUCCESS) {
                onResult(code, weight.toString())
            } else {
                onResult(code, "获取错误")
            }
        }
    }

    fun execQuery(context: Context, type: Int, start: Long, end: Long, onResult: (Int, ArrayList<HiHealthData>) -> Unit) {
        val timeout = 0
        val hiHealthDataQuery = HiHealthDataQuery(type,
                start, end, HiHealthDataQueryOption())
        SumianHiHealthDataStore.execQuery(context, hiHealthDataQuery, timeout) { i, data ->
            if (data != null) {
                onResult(i, data as ArrayList<HiHealthData>)
            } else {
                onResult(i, ArrayList())
            }
        }

    }

    fun getCount(context: Context) {
        val endTime = System.currentTimeMillis()
        var startTime = endTime - 86400000L * 30
        startTime = endTime - 63072000 * 1000L
        val type = HiHealthSetType.DATA_SET_CORE_SLEEP
        val hiHealthDataQuery = HiHealthDataQuery(type,
                startTime, endTime, HiHealthDataQueryOption())
        SumianHiHealthDataStore.getCount(context, hiHealthDataQuery) { i, data ->
            if (data != null) {
            }
        }
    }

    fun startReadingHeartRate(context: Context) {
        SumianHiHealthDataStore.startReadingHeartRate(context, object : HiRealTimeListener {
            override fun onResult(state: Int) {
                if (state == HiHealthError.SUCCESS) {
                } else {
                }
            }

            override fun onChange(errCode: Int, value: String) {
                if (errCode == HiHealthError.SUCCESS) {
                    try {
                        val jsonArray = JSONArray(value)
                        val heartRateStr = jsonArray.getString(0)
                        val jsonObject = JSONObject(heartRateStr)
                        val timeStamp = jsonObject.getLong("time_info")
                        val rate = jsonObject.getInt("hr_info")
                    } catch (e: JSONException) {
                    }

                } else {
                }
            }
        })
    }

    fun startReadingRri(context: Context) {
        SumianHiHealthDataStore.startReadingRri(context, object : HiRealTimeListener {
            override fun onResult(state: Int) {
                if (state == HiHealthError.SUCCESS) {
                } else {
                }
            }

            override fun onChange(errCode: Int, value: String) {
                try {
                    val jsonObject = JSONObject(value)
                    val str = jsonObject.getString("value")
                } catch (e: JSONException) {
                }
            }
        })
    }

    fun isHuaweiHealthInstalled(context: Context): Boolean {
        try {
            var info = context.packageManager.getApplicationInfo("com.huawei.health", 0)
            return info.enabled
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SdLogManager.logHuaweiHealth(context.getString(R.string.bind_huawei_health_dialog_content))
        return false
    }
}
