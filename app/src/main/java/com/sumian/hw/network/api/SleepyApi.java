package com.sumian.hw.network.api;

import com.google.gson.JsonObject;
import com.sumian.hw.network.request.RawDataBody;
import com.sumian.hw.network.request.UploadFileBody;
import com.sumian.hw.network.response.AppUpgradeInfo;
import com.sumian.hw.network.response.ConfigInfo;
import com.sumian.hw.network.response.FileLength;
import com.sumian.hw.network.response.FirmwareInfo;
import com.sumian.hw.network.response.RawData;
import com.sumian.hw.oss.bean.OssResponse;
import com.sumian.sd.account.bean.UserInfo;

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
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

public interface SleepyApi {

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

}
