package com.sumian.device.net

import com.google.gson.JsonObject
import com.sumian.device.authentication.Token
import com.sumian.device.data.DeviceVersionInfo
import com.sumian.device.data.PatternData
import com.sumian.device.manager.upload.bean.UploadSleepDataParams
import com.sumian.device.oss.OssResponse
import retrofit2.Call
import retrofit2.http.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/14 14:11
 * desc   :
 * version: 1.0
 */
interface Api {

    @FormUrlEncoded
    @POST("authorizations")
    fun loginByCaptcha(@Field("mobile") mobile: String, @Field("captcha") captcha: String): Call<Token>

    @POST("raw-data/pass-through-file")
    fun uploadTransData(@Body params: UploadSleepDataParams): Call<OssResponse>

    @FormUrlEncoded
    @POST("raw-data/pass-through-file")
    fun uploadTransData(@FieldMap map: MutableMap<String, Any>): Call<OssResponse>

    @GET("firmware/latest")
    fun getDeviceLatestVersionInfo(
            @Query("monitor_hw_version") monitorHardwareVersion: String?,
            @Query("sleeper_hw_version") sleeperHardwareVersion: String?
    ): Call<DeviceVersionInfo>

    //在日历中获取日报告信息
    @GET("sleeps/days/month-unread")
    fun getCalendarSleepReport(@QueryMap map: MutableMap<String, Any>): Call<JsonObject>

    @GET("sleeps/days/flip-show")
    fun getSleepReport(@Query("date") unixTime: Int, @Query("page_size") pageSize: Int, @Query("is_include") isInclude: Int): Call<JsonObject>

    /**
     * date unix time
     * is_include 是否包括此时间戳，1：是，0：否
     * page_size 请求数量不传默认为1
     * direction 请求方向 0：小于查询时间戳，1：大于查询时间戳
     */
    @GET("sleeps/week-flip-show")
    fun getWeeksSleepReportV2(
            @Query("date") date: Int,
            @Query("is_include") is_include: Int = 1,
            @Query("page_size") pageSize: Int = 1,
            @Query("direction") direction: Int = 0
    ): Call<JsonObject>

    @GET("user/patterns")
    fun getUserPattern(): Call<List<PatternData>>
}