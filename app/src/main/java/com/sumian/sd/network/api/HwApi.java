package com.sumian.sd.network.api;

import com.google.gson.JsonObject;
import com.sumian.hw.log.LogOssResponse;
import com.sumian.hw.oss.bean.OssResponse;
import com.sumian.hw.report.base.BaseResultResponse;
import com.sumian.hw.report.bean.DailyMeta;
import com.sumian.hw.report.bean.DailyReport;
import com.sumian.hw.report.bean.ReadSleepRecordEvaluationResponse;
import com.sumian.hw.report.weeklyreport.WeekMeta;
import com.sumian.sd.account.bean.UserInfo;
import com.sumian.sd.network.response.AppUpgradeInfo;
import com.sumian.sd.network.response.FirmwareInfo;
import com.sumian.sd.network.response.SleepDurationReport;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

public interface HwApi {

    @FormUrlEncoded
    @PATCH("user/profile")
    Call<UserInfo> doModifyUserInfo(@FieldMap Map<String, Object> map);

    @POST("portables")
    @FormUrlEncoded
    Call<Object> uploadDeviceInfo(@FieldMap Map<String, Object> map);

    @Headers({"Accept: application/vnd.sumianapi.v2+json"})
    @GET("firmware/latest")
    Call<FirmwareInfo> syncFirmwareInfo();

    @GET("app-version/latest")
    Call<AppUpgradeInfo> syncUpgradeAppInfo(@QueryMap Map<String, String> map);

    @SuppressWarnings("SameParameterValue")
    @POST("heartbeats")
    @FormUrlEncoded
    Call<Object> sendHeartbeats(@Field("type") String type);

    @PATCH("user/avatar")
    Call<OssResponse> uploadAvatar();

    /**
     * Created by jzz
     * on 2018/3/5.
     * desc:新版 v1.1.0  api
     */

    //在日历中获取日报告信息
    @GET("sleeps/days/month-unread")
    Call<JsonObject> getCalendarSleepReport(@QueryMap Map<String, Object> map);

    @FormUrlEncoded
    //已读日报告医生评价
    @PATCH("sleeps/days/read-evaluation")
    Call<ReadSleepRecordEvaluationResponse> readDayDoctorValuation(@FieldMap Map<String, Object> map);

    @GET("sleeps/days/flip-show")
    Call<BaseResultResponse<DailyReport, DailyMeta>> getSleepReport(@Query("date") int unixTime, @Query("page_size") int pageSize, @Query("is_include") int isInclude);

    //获取多条睡眠周报告
    @GET("sleeps/week-flip-show")
    Call<BaseResultResponse<SleepDurationReport, WeekMeta>> getWeeksSleepReport(@QueryMap Map<String, Object> map);

    /**
     * @param map 数据类型，1：睡眠特征值，2：事件日志
     */
    @FormUrlEncoded
    @POST("raw-data/pass-through-file")
    Call<OssResponse> uploadTransData(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("feedback-auto")
    Call<LogOssResponse> autoUploadLog(@FieldMap Map<String, Object> map);

}
