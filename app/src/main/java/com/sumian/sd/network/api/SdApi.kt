package com.sumian.sd.network.api

import com.sumian.hw.device.pattern.PatternData
import com.sumian.sd.account.bean.Social
import com.sumian.sd.account.bean.Token
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.diary.sleeprecord.bean.DoctorServiceList
import com.sumian.sd.diary.sleeprecord.bean.SleepRecord
import com.sumian.sd.diary.sleeprecord.bean.SleepRecordSummary
import com.sumian.sd.doctor.bean.Doctor
import com.sumian.sd.doctor.bean.DoctorService
import com.sumian.sd.doctor.bean.PayOrder
import com.sumian.sd.homepage.bean.*
import com.sumian.sd.network.response.PaginationResponse
import com.sumian.sd.notification.bean.QueryNotificationResponse
import com.sumian.sd.onlinereport.OnlineReport
import com.sumian.sd.order.OrderDetail
import com.sumian.sd.oss.OssResponse
import com.sumian.sd.scale.bean.NotFilledScale
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.sd.scale.bean.FilledScale
import com.sumian.sd.scale.bean.Scale
import com.sumian.sd.service.advisory.bean.Advisory
import com.sumian.sd.service.advisory.bean.PictureOssSts
import com.sumian.sd.service.advisory.body.AdvisoryRecordBody
import com.sumian.sd.service.cbti.bean.*
import com.sumian.sd.service.coupon.bean.Coupon
import com.sumian.sd.service.diary.bean.DiaryEvaluationData
import com.sumian.sd.service.diary.bean.DiaryEvaluationsResponse
import com.sumian.sd.service.tel.bean.TelBooking
import com.sumian.sd.setting.remind.bean.Reminder
import com.sumian.sd.setting.remind.bean.ReminderListResponse
import com.sumian.sd.setting.version.bean.Version
import retrofit2.Call
import retrofit2.http.*

@Suppress("unused")
/**
 * Created by jzz
 * on 2018/1/15.
 * desc:
 */

interface SdApi {

    @PATCH("authorizations/current")
    fun refreshToken(@Header("Authorization") token: String): Call<Any>

    @FormUrlEncoded
    @POST("authorizations")
    fun loginByCaptcha(@Field("mobile") mobile: String, @Field("captcha") captcha: String): Call<Token>

    @FormUrlEncoded
    @POST("authorizations/registration-validation")
    fun validateCaptchaForRegister(@Field("mobile") mobile: String, @Field("captcha") captcha: String): Call<Token>

    @FormUrlEncoded
    @POST("authorizations/retrieve-validation")
    fun validateCaptchaForResetPassword(@Field("mobile") mobile: String, @Field("captcha") captcha: String): Call<Token>

    @FormUrlEncoded
    @POST("authorizations/password")
    fun loginByPassword(@Field("mobile") mobile: String, @Field("password") password: String): Call<Token>

    @DELETE("authorizations/current")
    fun logout(@Query("device_token") deviceToken: String): Call<Unit>

    @FormUrlEncoded
    @POST("captcha")
    fun getCaptcha(@Field("mobile") mobile: String): Call<Unit>

    @GET("user/profile")
    fun getUserProfile(): Call<UserInfo>

    @FormUrlEncoded
    @POST("authorizations/socialite-bound")
    fun loginOpenPlatform(@FieldMap map: MutableMap<String, Any?>): Call<Token>

    @FormUrlEncoded
    @POST("authorizations/socialite-bind")
    fun bindSocial(@FieldMap map: MutableMap<String, Any>): Call<Token>

    @FormUrlEncoded
    @PATCH("user/profile")
    fun modifyUserProfile(@FieldMap map: MutableMap<String, String>): Call<UserInfo>

    /**
     * oldPassword 如果没设置过密码可传入null
     */
    @FormUrlEncoded
    @PATCH("user/password")
    fun modifyPassword(@Field("old_password") oldPassword: String?, @Field("password") password: String, @Field("password_confirmation") passwordConfirm: String): Call<UserInfo>

    @FormUrlEncoded
    @PATCH("user/password")
    fun modifyPasswordWithoutOldPassword(@Field("password") password: String, @Field("password_confirmation") passwordConfirm: String): Call<UserInfo>

    @FormUrlEncoded
    @PATCH("user/password")
    fun modifyPasswordWithToken(@Header("Authorization") authorization: String, @Field("password") password: String, @Field("password_confirmation") passwordConfirm: String): Call<UserInfo>

    @PATCH("user/avatar")
    fun uploadAvatar(): Call<OssResponse>

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
                   @Query("per_page") perPage: Int): Call<PaginationResponse<OnlineReport>>

    @GET("services")
    fun getServiceList(): Call<DoctorServiceList>

    /**
     * 服务类型 0：睡眠日记 1：图文咨询 2：电话咨询 3：CBTI
     */
    @GET("service")
    fun getServiceByType(@Query("type") type: Int): Call<DoctorService>

    @GET("services/{id}")
    fun getServiceDetailById(@Path("id") id: Int): Call<DoctorService>

    /**
     * type 0：睡眠日记 1：图文咨询 2：电话咨询 3：CBTI
     */
    @GET("services")
    fun getServiceDetailByType(@Query("type") type: Int): Call<Any>

    // ---------- notification ----------
    @GET("notifications")
    fun getNotificationList(@Query("page") page: Int,
                            @Query("per_page") perPage: Int): Call<QueryNotificationResponse>

    @PATCH("notifications/{id}")
    fun readNotification(@Path("id") notificationId: String): Call<Any>

    // ---------- scale ----------
    @GET("scale-distributions")
    fun getScaleList(@Query("page") page: Int,
                     @Query("per_page") perPage: Int = 15,
                     @Query("type") type: String): Call<PaginationResponse<Scale>>

    @GET("scale-distributions")
    fun getNotFilledScaleList(@Query("page") page: Int,
                              @Query("per_page") perPage: Int = 15,
                              @Query("type") type: String = "not_filled"): Call<PaginationResponseV2<NotFilledScale>>

    @GET("filled-scales")
    fun getFilledScaleList(@Query("page") page: Int,
                           @Query("per_page") perPage: Int = 15): Call<PaginationResponseV2<FilledScale>>

    // ---------- device info ----------
    /**
     *
     * device_type 设备类型，0：Android，1：iOS
     */
    @POST("portables")
    @FormUrlEncoded
    fun uploadDeviceInfo(@Field("device_type") deviceType: String,
                         @Field("device_token") deviceToken: String,
                         @Field("system_version") systemVersion: String): Call<Any>

    // ---------- doctor ----------

    @PATCH("doctor-bind")
    fun bindDoctor(@FieldMap map: MutableMap<String, Any>): Call<Doctor>

    @GET("doctors/{id}")
    fun getDoctorInfo(@Path("id") id: Int): Call<Doctor>

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

    // ---------- homepage ----------

    @Headers("Accept: application/vnd.sd.v2+json")
    @GET("sleep-prescriptions")
    fun getSleepPrescriptions(): Call<SleepPrescription?>

    @GET("sleep-prescriptions/status")
    fun getSleepPrescriptionStatus(): Call<SleepPrescriptionStatus?>

    @POST("sleep-prescriptions")
    fun updateSleepPrescriptions(@Body sleepPrescription: SleepPrescription): Call<SleepPrescriptionWrapper>

    @POST("sleep-prescriptions/fatigue")
    fun updateSleepPrescriptionsWhenFatigue(@Body data: UpdateSleepPrescriptionWhenFatiguedData): Call<SleepPrescriptionWrapper>

    /**
     * include
     * courses 课程
     * courses.exercise 课后练习
     */
    @GET("cbti-chapters")
    fun getCbtiChapters(@Query("include") include: String?): Call<GetCbtiChaptersResponse>

    //cbti

    /**
     * 获取该章节下的所有课程
     */
    @GET("cbti-chapter/{chapter-id}/courses")
    fun getCBTICourseWeekPart(@Path("chapter-id") id: Int): Call<CBTIDataResponse<Course>>

    /**
     * 获取该章节下的所有练习
     */
    @GET("cbti-chapter/{chapter-id}/exercises")
    fun getCBTIExerciseWeekPart(@Path("chapter-id") id: Int): Call<CBTIDataResponse<Exercise>>

    /**
     * 获取该章节下的单独课时详情信息,包括视频播放权限,及相关信息
     */
    @GET("cbti-courses/{id}")
    fun getCBTIPLayAuth(@Path("id") id: Int): Call<CoursePlayAuth>

    /**
     * 上传当前课时的视频播放 log 日志
     */
    @FormUrlEncoded
    @POST("cbti-course/{id}/logs")
    fun uploadCBTICourseLogs(@Path("id") id: Int, @Field("video_id") videoId: String, @Field("video_progress") video_progress: String, @Field("end_point") end_point: Int): Call<CoursePlayLog>

    /**
     * 上传视频问卷
     */
    @FormUrlEncoded
    @POST("cbti-course/{id}/questionnaires")
    fun uploadCBTIVideoQuestionnaires(@Path("id") id: Int, @Field("data") json: String): Call<CoursePlayAuth>

    /**
     * 获取 app 的版本信息
     */
    @GET("app-version/latest")
    fun getAppVersion(@Query("type") type: Int = 1, @Query("current_version") currentVersion: String): Call<Version>

    /**
     * 使用该电话预约清单
     */
    @FormUrlEncoded
    @PATCH("bookings/{id}")
    fun publishTelBooking(@Path("id") telBookingId: Int, @FieldMap map: MutableMap<String, Any>): Call<TelBooking>

    /**
     * 获取最新未使用的电话预约订单,可用于提交新的电话预约
     */
    @GET("booking/latest")
    fun getLatestTelBookingDetail(@QueryMap map: MutableMap<String, Any>): Call<TelBooking>

    /**
     * 获取电话预约列表  包括已使用/未使用列表清单
     */
    @GET("bookings")
    fun getTelBookingList(@QueryMap map: MutableMap<String, Any>): Call<PaginationResponse<TelBooking>>

    /**
     * 通过 id 获取该电话预约清单的详情
     */
    @GET("bookings/{id}")
    fun getTelBookingDetail(@Path("id") telBookingId: Int, @QueryMap() map: MutableMap<String, Any>): Call<TelBooking>


    /**
     * type 提醒类型，1：睡眠提醒 2：睡眠日记提醒 3:放松训练提醒
     */
    @GET("reminders/subscriptions")
    fun getReminderList(@Query("type") reminderType: Int = 2): Call<ReminderListResponse>

    /**
     * type 提醒类型，1：睡眠提醒 2：睡眠日记提醒 3:放松训练提醒
     * enable 0 false ,1 true
     */
    @FormUrlEncoded
    @POST("reminders/subscriptions")
    fun addReminder(@Field("type") type: Int = 2, @Field("remind_at") remindAtInSecond: Int, @Field("enable") enable: Int = 1): Call<Reminder>

    @FormUrlEncoded
    @PATCH("reminders/subscriptions/{id}")
    fun modifyReminder(@Path("id") id: Int, @Field("remind_at") remindAtInSecond: Int, @Field("enable") enable: Int): Call<Reminder>

    @POST("customer-service/message-event")
    fun newCustomerMessage(): Call<Any>

    @GET("user/patterns")
    fun getUserPattern(): Call<List<PatternData>>

    /**
     * type optiona 0:未完成 1:已完成
     * include optional	user,doctor,package.servicePackage.service
     */
    @GET("diary-evaluations")
    fun getDiaryEvaluations(@Query("type") type: Int,
                            @Query("include") include: String?,
                            @Query("page") page: Int = 1,
                            @Query("per_page") perPage: Int = 10): Call<DiaryEvaluationsResponse>

    @GET("diary-evaluation/latest")
    fun getLatestDiaryEvaluation(@Query("include") include: String? = null): Call<DiaryEvaluationData>

    @FormUrlEncoded
    @POST("feedback")
    fun feedback(@Field("content") content: String, @Field("suffix") suffix: String): Call<com.sumian.hw.oss.bean.OssResponse>

    /**
     * 兑换码  兑换
     */
    @FormUrlEncoded
    @POST("coupon")
    fun couponAction(@Field("coupon") coupon: String): Call<Any>

    /**
     * 获取兑换码  兑换列表
     */
    @GET("coupon")
    fun getCouponList(@QueryMap map: MutableMap<String, Any>): Call<PaginationResponse<Coupon>>

}
