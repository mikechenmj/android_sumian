package com.sumian.hw.network.api;

import com.google.gson.JsonObject;
import com.sumian.hw.network.request.CaptchaBody;
import com.sumian.hw.network.request.LoginBody;
import com.sumian.hw.network.request.ModifyPwdBody;
import com.sumian.hw.network.request.RawDataBody;
import com.sumian.hw.network.request.RegisterBody;
import com.sumian.hw.network.request.ResetPwdBody;
import com.sumian.hw.network.request.UploadFileBody;
import com.sumian.hw.network.request.ValidationCaptchaBody;
import com.sumian.hw.network.response.AppUpgradeInfo;
import com.sumian.hw.network.response.ConfigInfo;
import com.sumian.hw.network.response.DaySleepReport;
import com.sumian.hw.network.response.FileLength;
import com.sumian.hw.network.response.FirmwareInfo;
import com.sumian.hw.network.response.RawData;
import com.sumian.hw.network.response.Reminder;
import com.sumian.hw.network.response.ResultResponse;
import com.sumian.hw.network.response.SleepDetailReport;
import com.sumian.hw.network.response.SleepDurationReport;
import com.sumian.hw.network.response.Ticket;
import com.sumian.hw.network.response.UserSetting;
import com.sumian.hw.oss.bean.OssResponse;
import com.sumian.sd.account.bean.Social;
import com.sumian.sd.account.bean.Token;
import com.sumian.sd.account.bean.UserInfo;

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
    Call<Token> doRefreshToken();

    @POST("authorizations")
    Call<Token> doLogin(@Body LoginBody loginBody);

    @DELETE("authorizations/current")
    Call<Object> doLogout(@Query("device_token") String deviceToken);

    //account module

    @POST("users")
    Call<Token> doRegister(@Body RegisterBody registerBody);

    /**
     * @param include "doctor"
     * @return
     */
    @GET("user/profile")
    Call<UserInfo> getUserInfo(@Query("include") String include);

    @FormUrlEncoded
    @PATCH("user/profile")
    Call<UserInfo> doModifyUserInfo(@FieldMap Map<String, Object> map);

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
    Call<ResultResponse<Reminder>> getReminder(@QueryMap Map<String, Object> map);

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
    Call<JsonObject> syncSleepNoteOptions();

    @GET("app-version/latest")
    Call<AppUpgradeInfo> syncUpgradeAppInfo(@QueryMap Map<String, String> map);

    @POST("socialite/authorizations")
    @FormUrlEncoded
    Call<Token> loginOpenPlatform(@FieldMap Map<String, Object> map);

    @POST("socialite/users")
    @FormUrlEncoded
    Call<Token> bindOpenPlatform(@FieldMap Map<String, Object> map);

    @POST("socialites")
    @FormUrlEncoded
    Call<Social> bindOpenPlatform(@Field("type") int platformType, @Field("info") String openInfo);

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
