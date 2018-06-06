package com.sumian.sleepdoctor.network.api

import com.sumian.sleepdoctor.account.bean.Social
import com.sumian.sleepdoctor.account.bean.Token
import com.sumian.sleepdoctor.account.bean.UserProfile
import com.sumian.sleepdoctor.improve.advisory.bean.Advisory
import com.sumian.sleepdoctor.improve.doctor.bean.Doctor
import com.sumian.sleepdoctor.improve.doctor.bean.PayOrder
import com.sumian.sleepdoctor.network.response.PaginationResponse
import com.sumian.sleepdoctor.notification.bean.QueryNotificationResponse
import com.sumian.sleepdoctor.onlineReport.OnlineReport
import com.sumian.sleepdoctor.oss.bean.OssResponse
import com.sumian.sleepdoctor.pager.bean.OrderDetail
import com.sumian.sleepdoctor.sleepRecord.bean.DoctorServiceList
import com.sumian.sleepdoctor.sleepRecord.bean.SleepRecord
import com.sumian.sleepdoctor.sleepRecord.bean.SleepRecordSummary
import com.sumian.sleepdoctor.tab.bean.GroupDetail
import retrofit2.Call
import retrofit2.http.*

@Suppress("unused")
/**
 * Created by jzz
 * on 2018/1/15.
 * desc:
 */

interface DoctorApi {

    @PATCH("authorizations/current")
    fun refreshToken(): Call<Token>

    @FormUrlEncoded
    @POST("authorizations")
    fun login(@Field("mobile") mobile: String, @Field("captcha") captcha: String): Call<Token>

    @DELETE("authorizations/current")
    fun logout(): Call<Unit>

    @FormUrlEncoded
    @POST("captcha")
    fun getCaptcha(@Field("mobile") mobile: String): Call<Unit>

    @GET("user/profile")
    fun getUserProfile(): Call<UserProfile>

    @FormUrlEncoded
    @POST("authorizations/socialite-bound")
    fun loginOpenPlatform(@FieldMap map: MutableMap<String, Any>): Call<Token>

    @FormUrlEncoded
    @POST("authorizations/socialite-bind")
    fun bindSocial(@FieldMap map: MutableMap<String, Any>): Call<Token>

    @FormUrlEncoded
    @PATCH("user/profile")
    fun modifyUserProfile(@FieldMap map: MutableMap<String, String>): Call<UserProfile>

    @PATCH("user/avatar")
    fun uploadAvatar(): Call<OssResponse>

    @GET("user/groups")
    fun getGroups(@QueryMap map: MutableMap<String, Int>): Call<PaginationResponse<List<GroupDetail<UserProfile, UserProfile>>>>

    @GET("groups/{id}")
    fun getGroupsDetail(@Path("id") groupId: Int, @Query("include") include: String): Call<GroupDetail<UserProfile, UserProfile>>

    @FormUrlEncoded
    @POST("orders")
    fun createOrder(@FieldMap map: MutableMap<String, Any>): Call<String>

    @GET("orders/{order_no}")
    fun getOrderDetail(@Path("order_no") orderNumber: String): Call<OrderDetail>

    @FormUrlEncoded
    @POST("user/leancloud")
    fun getLeancloudGroupUsers(@Field("leancloud_ids") leancloudIds: String, @Field("group_id") groupId: Int): Call<String>

    // socialites
    @FormUrlEncoded
    @POST("socialites")
    fun bindSocialites(@Field("type") type: Int, @Field("info") info: String): Call<Social>

    @DELETE("socialites/{id}")
    fun unbindSocialites(@Path("id") userId: Int): Call<String>

    // ---------- sleep record ----------

    /**
     * date unix时间戳：1519833600，可传当月任何一天，默认这个月
     * is_include 是否包括此时间戳，1：是，0：否。默认0
     * page_size 请求数量不传默认为1
     * direction 请求方向 0：小于查询时间戳，1：大于查询时间戳 不传默认0
     */
    @GET("diary-month")
    fun getSleepDiarySummaryList(@Query("date") unixTime: Int,
                                 @Query("is_include") isInclude: Int,
                                 @Query("page_size") pageSize: Int,
                                 @Query("direction") direction: Int): Call<Map<String, List<SleepRecordSummary>>>

    @GET("diaries")
    fun getSleepDiaryDetail(@Query("date") unixTime: Int): Call<SleepRecord>

    // ---------- online report ----------

    /**
     * page 第几页 Default value: 1
     * per_page 每页数量 Default value: 15
     * user_id 用户id
     */
    @GET("/online-reports")
    fun getReports(@Query("page") page: Int,
                     @Query("per_page") perPage: Int): Call<PaginationResponse<List<OnlineReport>>>

    @GET("services")
    fun getServiceList(): Call<DoctorServiceList>

    // ---------- notification ----------
    @GET("notifications")
    fun getNotificationList(@Query("page") page: Int,
                            @Query("per_page") perPage: Int): Call<QueryNotificationResponse>

    @PATCH("notifications/{id}")
    fun readNotification(@Path("id") notificationId: String): Call<Any>

    // ---------- doctor ----------

    @PATCH("doctor-bind")
    fun bindDoctor(@FieldMap map: MutableMap<String, Any>): Call<Doctor>

    @GET("doctors/{id}")
    fun getDoctorInfo(@Path("id") id: Int, @Query("include") include: String): Call<Doctor>

    @GET("user/doctor")
    fun getBindDoctorInfo(): Call<Doctor>

    @DELETE("doctor/binding")
    fun unbindDoctor(): Call<Unit>

    //order

    @POST("orders")
    fun createOrder(@Body payOrder: PayOrder): Call<Any>

    //user advisory

    @GET("advisories")
    fun getDoctorAdvisories(@QueryMap map: MutableMap<String, Any>): Call<PaginationResponse<ArrayList<Advisory>>>

    @GET("advisories/{id}")
    fun getDoctorAdvisoryDetails(@Path("id") advisoryId: Int, @QueryMap map: MutableMap<String, Any>): Call<Advisory>

    @GET("advisory/latest")
    fun getLastAdvisoryDetails(@QueryMap map: MutableMap<String, Any>): Call<Advisory>

}
