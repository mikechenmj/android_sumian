package com.sumian.app.network.api;

import com.sumian.app.network.request.CaptchaBody;
import com.sumian.app.network.request.LoginBody;
import com.sumian.app.network.request.ModifyPwdBody;
import com.sumian.app.network.request.RawDataBody;
import com.sumian.app.network.request.RegisterBody;
import com.sumian.app.network.request.ResetPwdBody;
import com.sumian.app.network.request.UploadFileBody;
import com.sumian.app.network.request.ValidationCaptchaBody;
import com.sumian.app.network.response.AppUpgradeInfo;
import com.sumian.app.network.response.ConfigInfo;
import com.sumian.app.network.response.DaySleepReport;
import com.sumian.app.network.response.FileLength;
import com.sumian.app.network.response.FirmwareInfo;
import com.sumian.app.network.response.HwToken;
import com.sumian.app.network.response.HwUserInfo;
import com.sumian.app.network.response.RawData;
import com.sumian.app.network.response.Reminder;
import com.sumian.app.network.response.ResultResponse;
import com.sumian.app.network.response.SleepDetailReport;
import com.sumian.app.network.response.SleepDurationReport;
import com.sumian.app.network.response.Ticket;
import com.sumian.app.network.response.UserSetting;
import com.sumian.app.oss.bean.OssResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

public interface SleepyApi {

    //user authorization module

    @PUT("authorizations/current")
    Call<HwToken> doRefreshToken();

    @POST("authorizations")
    Call<HwToken> doLogin(@Body LoginBody loginBody);

    @DELETE("authorizations/current")
    Call<Object> doLogout(@Query("device_token") String deviceToken);

    //account module

    @POST("users")
    Call<HwToken> doRegister(@Body RegisterBody registerBody);

    @GET("user/profile")
    Call<HwUserInfo> syncUserInfo();

    @FormUrlEncoded
    @PATCH("user/profile")
    Call<HwUserInfo> doModifyUserInfo(@FieldMap Map<String, Object> map);

    @PATCH("user/reset-password")
    Call<Object> doResetPwd(@Body ResetPwdBody resetPwdBody);

    @PATCH("user/change-password")
    Call<Object> doModifyPwd(@Body ModifyPwdBody modifyPwdBody);


    //common  module

    @POST("captchas")
    Call<Object> doCaptcha(@Body CaptchaBody captchaBody);

    @POST("captchas/validation")
    Call<Ticket> doValidationCaptcha(@Body ValidationCaptchaBody validationCaptchaBody);

    //sleep  data  module

    @GET("sleeps/{id}")
    Call<SleepDetailReport> syncSleepDetail(@Path("id") long id);

    @GET("user/sleeps")
    Call<ResultResponse<DaySleepReport>> syncDaySleepReport(@Query("page") int page, @Query("per_page") int
        pageCount);

    @GET("sleeps/weeks/{date}")
    Call<SleepDurationReport> syncWeekSleepReport(@Path("date") String date);

    @GET("sleeps/months/{date}")
    Call<SleepDurationReport> syncMonthSleepReport(@Path("date") String date);

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

    @GET("reminders/subscriptions")
    Call<ResultResponse<Reminder>> syncReminder(@QueryMap Map<String, Object> map);

    @FormUrlEncoded
    @PATCH("reminders/subscriptions/{id}")
    Call<Reminder> modifyReminder(@Path("id") int id, @FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("reminders/subscriptions")
    Call<Reminder> addReminder(@FieldMap Map<String, Object> map);

    @GET("configs")
    Call<List<ConfigInfo>> syncConfigInfo();

    @Headers({"Accept: application/vnd.sumianapi.v2+json"})
    @Multipart
    @POST("raw-data/pass-through")
    Call<String> uploadRawData(@Part MultipartBody.Part typePart, @Part MultipartBody.Part
        partAppReceiveStartedTimePart, @Part MultipartBody.Part partAppReceiveEndedTimePart, @Part
                                   MultipartBody.Part part);

    @FormUrlEncoded
    @POST("sleeps/{id}/diary")
    Call<SleepDetailReport> uploadDiary(@Path("id") long id, @FieldMap Map<String, Object> map);

    @GET("sleeps/options")
    Call<String> syncSleepNoteOptions();

    @GET("app-version/latest")
    Call<AppUpgradeInfo> syncUpgradeAppInfo(@QueryMap Map<String, String> map);

    @POST("socialite/authorizations")
    @FormUrlEncoded
    Call<HwToken> loginOpenPlatform(@FieldMap Map<String, Object> map);

    @POST("socialite/users")
    @FormUrlEncoded
    Call<HwToken> bindOpenPlatform(@FieldMap Map<String, Object> map);

    @POST("socialites")
    @FormUrlEncoded
    Call<HwUserInfo.Social> bindOpenPlatform(@Field("type") int platformType, @Field("info") String openInfo);

    @DELETE("socialites/{id}")
    Call<Object> unBindOpenPlatform(@Path("id") int id);

    @SuppressWarnings("SameParameterValue")
    @POST("heartbeats")
    @FormUrlEncoded
    Call<Object> sendHeartbeats(@Field("type") String type);

    @PATCH("user/settings")
    @FormUrlEncoded
    Call<UserSetting> updateUserSetting(@Field("sleep_diary_enable") int sleepDiaryEnable);

    @GET("user/settings")
    Call<UserSetting> syncUserSetting();

    @PATCH("user/avatar")
    Call<OssResponse> uploadAvatar();

}
