package com.sumian.sd.network.api;

import com.google.gson.JsonObject;
import com.sumian.hw.log.LogOssResponse;
import com.sumian.hw.oss.bean.OssResponse;
import com.sumian.hw.report.base.BaseResultResponse;
import com.sumian.hw.report.bean.DailyMeta;
import com.sumian.hw.report.bean.DailyReport;
import com.sumian.hw.report.bean.ReadSleepRecordEvaluationResponse;
import com.sumian.hw.report.bean.WeekMeta;
import com.sumian.sd.account.bean.UserInfo;
import com.sumian.sd.network.request.RawDataBody;
import com.sumian.sd.network.request.UploadFileBody;
import com.sumian.sd.network.response.AppUpgradeInfo;
import com.sumian.sd.network.response.ConfigInfo;
import com.sumian.sd.network.response.FileLength;
import com.sumian.sd.network.response.FirmwareInfo;
import com.sumian.sd.network.response.RawData;
import com.sumian.sd.network.response.SleepDurationReport;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

public interface HwApi {

    @DELETE("authorizations/current")
    Call<Object> doLogout(@Query("device_token") String deviceToken);

    @FormUrlEncoded
    @PATCH("user/profile")
    Call<UserInfo> doModifyUserInfo(@FieldMap Map<String, Object> map);

    @POST("raw-data")
    Call<RawData> uploadRawData(@Body RawDataBody rawDataBody);

    @POST("raw-data/batch-file-length")
    Call<List<FileLength>> getRawFileLength(@Body List<UploadFileBody> uploadFileBodies);

    @POST("portables")
    @FormUrlEncoded
    Call<Object> uploadDeviceInfo(@FieldMap Map<String, Object> map);

    @Headers({"Accept: application/vnd.sumianapi.v2+json"})
    @GET("firmware/latest")
    Call<FirmwareInfo> syncFirmwareInfo();

    @GET("configs")
    Call<List<ConfigInfo>> syncConfigInfo();

    @GET("sleeps/options")
    Call<JsonObject> syncSleepNoteOptions();

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
    Call<ReadSleepRecordEvaluationResponse> readDayDoctorValuation(@FieldMap Map<String, Object> map);

    //获取多天睡眠日报告 (左右滑动)
    @GET("sleeps/days/flip-show")
    Call<BaseResultResponse<DailyReport, DailyMeta>> getSleepReport(@QueryMap Map<String, Object> map);

    @GET("sleeps/days/flip-show")
    Call<BaseResultResponse<DailyReport, DailyMeta>> getSleepReport(@Query("date") int unixTime, @Query("page_size") int pageSize, @Query("is_include") int isInclude);

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
