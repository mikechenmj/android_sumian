package com.sumian.hw.network.api;

import com.google.gson.JsonObject;
import com.sumian.hw.improve.report.base.BaseResultResponse;
import com.sumian.hw.improve.report.dailyreport.DailyMeta;
import com.sumian.hw.improve.report.dailyreport.DailyReport;
import com.sumian.hw.improve.report.weeklyreport.WeekMeta;
import com.sumian.hw.log.LogOssResponse;
import com.sumian.hw.network.response.SleepDurationReport;
import com.sumian.hw.oss.bean.OssResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by jzz
 * on 2018/3/5.
 * desc:新版 v1.1.0  api
 */

public interface SleepyV1Api {

    //在日历中获取日报告信息
    @GET("sleeps/days/month-unread")
    Call<JsonObject> getCalendarSleepReport(@QueryMap Map<String, Object> map);

    //填写睡眠日记
    @FormUrlEncoded
    @POST("sleeps/days/{id}/diary")
    Call<DailyReport> writeDiary(@Path("id") int sleepDailyId, @FieldMap Map<String, Object> map);

    //已读周报告医生评价
    @FormUrlEncoded
    @PATCH("sleeps/week-read-evaluation")
    Call<String> readWeekDoctorValuation(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    //已读日报告医生评价
    @PATCH("sleeps/days/read-evaluation")
    Call<Boolean> readDayDoctorValuation(@FieldMap Map<String, Object> map);

    //获取多天睡眠日报告 (左右滑动)
    @GET("sleeps/days/flip-show")
    Call<BaseResultResponse<DailyReport, DailyMeta>> getTodaySleepReport(@QueryMap Map<String, Object> map);

    //获取多条睡眠周报告
    @GET("sleeps/week-flip-show")
    Call<BaseResultResponse<SleepDurationReport, WeekMeta>> getWeeksSleepReport(@QueryMap Map<String, Object> map);

    @Headers({"Accept: application/vnd.sumianapi.v3+json"})
    @FormUrlEncoded
    @POST("feedback")
    Call<OssResponse> feedback(@Field("content") String content, @Field("suffix") String suffix);

    @FormUrlEncoded
    @POST("raw-data/pass-through-file")
    Call<OssResponse> uploadTransData(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("feedback-auto")
    Call<LogOssResponse> autoUploadLog(@FieldMap Map<String, Object> map);

}
