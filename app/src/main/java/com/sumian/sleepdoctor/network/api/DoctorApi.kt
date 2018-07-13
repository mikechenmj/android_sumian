package com.sumian.sleepdoctor.network.api

import com.sumian.sleepdoctor.account.bean.Social
import com.sumian.sleepdoctor.account.bean.Token
import com.sumian.sleepdoctor.account.bean.UserProfile
import com.sumian.sleepdoctor.advisory.bean.Advisory
import com.sumian.sleepdoctor.advisory.bean.PictureOssSts
import com.sumian.sleepdoctor.cbti.bean.Courses
import com.sumian.sleepdoctor.doctor.bean.Doctor
import com.sumian.sleepdoctor.doctor.bean.DoctorService
import com.sumian.sleepdoctor.doctor.bean.PayOrder
import com.sumian.sleepdoctor.homepage.bean.GetCbtiChaptersResponse
import com.sumian.sleepdoctor.network.body.AdvisoryRecordBody
import com.sumian.sleepdoctor.network.response.PaginationResponse
import com.sumian.sleepdoctor.notification.bean.QueryNotificationResponse
import com.sumian.sleepdoctor.onlinereport.OnlineReport
import com.sumian.sleepdoctor.order.OrderDetail
import com.sumian.sleepdoctor.order.OrderDetailV2
import com.sumian.sleepdoctor.oss.bean.OssResponse
import com.sumian.sleepdoctor.record.bean.DoctorServiceList
import com.sumian.sleepdoctor.record.bean.SleepRecord
import com.sumian.sleepdoctor.record.bean.SleepRecordSummary
import com.sumian.sleepdoctor.scale.bean.Scale
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
    fun refreshToken(@Header("Authorization") token: String): Call<Any>

    @FormUrlEncoded
    @POST("authorizations")
    fun login(@Field("mobile") mobile: String, @Field("captcha") captcha: String): Call<Token>

    @DELETE("authorizations/current")
    fun logout(@Query("device_token") deviceToken: String): Call<Unit>

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

    @FormUrlEncoded
    @POST("orders")
    fun createOrder(@FieldMap map: MutableMap<String, Any>): Call<String>

    @GET("orders/{order_no}")
    fun getOrderDetail(@Path("order_no") orderNumber: String): Call<OrderDetail>

    @GET("orders/{order_no}")
    fun getOrderDetailV2(@Path("order_no") orderNumber: String): Call<OrderDetailV2>

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
                   @Query("per_page") perPage: Int): Call<PaginationResponse<OnlineReport>>

    @GET("services")
    fun getServiceList(): Call<DoctorServiceList>

    @GET("services/{id}")
    fun getServiceDetailById(@Path("id") id:Int):Call<DoctorService>

    /**
     * type 0：睡眠日记 1：图文咨询 2：电话咨询 3：CBTI
     */
    @GET("services")
    fun getServiceDetailByType(@Query("type") type:Int):Call<Any>

    // ---------- notification ----------
    @GET("notifications")
    fun getNotificationList(@Query("page") page: Int,
                            @Query("per_page") perPage: Int): Call<QueryNotificationResponse>

    @PATCH("notifications/{id}")
    fun readNotification(@Path("id") notificationId: String): Call<Any>

    // ---------- scale ----------
    @GET("scale-distributions")
    fun getScaleList(@Query("page") page: Int,
                     @Query("per_page") perPage: Int,
                     @Query("type") type: String): Call<PaginationResponse<Scale>>

    // ---------- device info ----------
    @POST("portables")
    @FormUrlEncoded
    fun uploadDeviceInfo(@Field("device_type") deviceType: String,
                         @Field("device_token") deviceToken: String,
                         @Field("system_version") systemVersion: String): Call<Any>

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
    fun getDoctorAdvisories(@QueryMap map: MutableMap<String, Any>): Call<PaginationResponse<Advisory>>

    @GET("advisories/{id}")
    fun getDoctorAdvisoryDetails(@Path("id") advisoryId: Int, @QueryMap map: MutableMap<String, Any>): Call<Advisory>

    @GET("advisory/latest")
    fun getLastAdvisoryDetails(@QueryMap map: MutableMap<String, Any>): Call<Advisory>

    @POST("advisory-records")
    fun publishAdvisoryRecord(@Body advisoryRecordBody: AdvisoryRecordBody): Call<Advisory>

    @POST("advisory-records/sts")
    fun publishPicturesAdvisoryRecord(@Body advisoryRecordBody: AdvisoryRecordBody): Call<PictureOssSts>

    /**
     * include
     * courses 课程
     * courses.exercise 课后练习
     */
    @GET("cbti-chapters")
    fun getCbtiChapters(@Query("include") include: String?): Call<GetCbtiChaptersResponse>

    //cbti

    @GET("cbti-chapter/{chapter-id}/courses")
    fun getCBTILessonWeekPart(@Path("chapter-id") id: Int): Call<Courses>

    @GET("cbti-chapter/{chapter-id}/exercises")
    fun getCBTIExerciseWeekPart(@Path("chapter-id") id: Int): Call<Any>
}
