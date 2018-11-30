package com.sumian.sd.network.api

import com.google.gson.JsonObject
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.hw.log.LogOssResponse
import com.sumian.hw.report.base.BaseResultResponse
import com.sumian.hw.report.bean.DailyMeta
import com.sumian.hw.report.bean.DailyReport
import com.sumian.hw.report.weeklyreport.WeekMeta
import com.sumian.hw.report.weeklyreport.bean.SleepDurationReport
import com.sumian.hw.report.weeklyreport.bean.WeeklyReportResponse
import com.sumian.sd.account.bean.Social
import com.sumian.sd.account.bean.Token
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.anxiousandfaith.bean.AnxietyData
import com.sumian.sd.anxiousandfaith.bean.FaithData
import com.sumian.sd.device.pattern.PatternData
import com.sumian.sd.diary.sleeprecord.bean.DoctorServiceList
import com.sumian.sd.diary.sleeprecord.bean.SleepRecord
import com.sumian.sd.diary.sleeprecord.bean.SleepRecordSummary
import com.sumian.sd.doctor.bean.Doctor
import com.sumian.sd.doctor.bean.DoctorService
import com.sumian.sd.homepage.bean.*
import com.sumian.sd.network.response.AppUpgradeInfo
import com.sumian.sd.network.response.FirmwareInfo
import com.sumian.sd.notification.bean.NotificationListResponse
import com.sumian.sd.onlinereport.OnlineReport
import com.sumian.sd.oss.OssResponse
import com.sumian.sd.pay.bean.OrderDetail
import com.sumian.sd.pay.bean.PayCouponCode
import com.sumian.sd.pay.bean.PayOrder
import com.sumian.sd.relaxation.bean.RelaxationData
import com.sumian.sd.scale.bean.FilledScale
import com.sumian.sd.scale.bean.NotFilledScale
import com.sumian.sd.scale.bean.Scale
import com.sumian.sd.service.advisory.bean.Advisory
import com.sumian.sd.service.advisory.bean.PictureOssSts
import com.sumian.sd.service.advisory.body.AdvisoryRecordBody
import com.sumian.sd.service.cbti.bean.*
import com.sumian.sd.service.coupon.bean.Coupon
import com.sumian.sd.service.diary.bean.DiaryEvaluationData
import com.sumian.sd.service.tel.bean.TelBooking
import com.sumian.sd.setting.bean.Feedback
import com.sumian.sd.setting.remind.bean.Reminder
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
                   @Query("per_page") perPage: Int): Call<PaginationResponseV2<OnlineReport>>

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
                            @Query("per_page") perPage: Int): Call<NotificationListResponse>

    /**
     * notificationId 单消息id
     * dataId 群消息id，
     *
     * 单消息 notificationId != null，dataId = null，
     * 群消息 notificationId="0", dataId != null
     *
     * 单消息 标为已读 notificationId != 0， dataId = null
     * 群消息 标为已读 notificationId = 0， dataId != null
     * 全部 标为已读 notificationId = 0， dataId = null
     */
    @PATCH("notifications/{id}")
    fun readNotification(@Path("id") notificationId: String,
                         @Query("data_id") dataId: Int?): Call<Any>

    // ---------- scale ----------
    @GET("scale-distributions")
    fun getScaleList(@Query("page") page: Int,
                     @Query("per_page") perPage: Int = 15,
                     @Query("type") type: String): Call<PaginationResponseV2<Scale>>

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

    //校验支付优惠码
    @GET("discount-code/available")
    fun checkCouponCode(@Query("code") code: String, @Query("package_id") packageId: Int): Call<PayCouponCode>

    //user advisory

    @GET("advisories")
    fun getDoctorAdvisories(@QueryMap map: MutableMap<String, Any>): Call<PaginationResponseV2<Advisory>>

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
     * 上传当前课时的视频观看时长 log 日志
     * ps：观看时长，需要更新观看时长的时候才传这个参数，有这个参数的时候，后台会增加一次观看次数，并累加观看时长
     */
    @FormUrlEncoded
    @POST("cbti-course/{id}/logs")
    fun uploadCBTICourseWatchLengthLogs(@Path("id") id: Int, @Field("video_id") videoId: String, @Field("video_progress") video_progress: String, @Field("end_point") end_point: Int, @Field("watch_length") watchLength: Int): Call<CoursePlayLog>

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
    fun getTelBookingList(@QueryMap map: MutableMap<String, Any>): Call<PaginationResponseV2<TelBooking>>

    /**
     * 通过 id 获取该电话预约清单的详情
     */
    @GET("bookings/{id}")
    fun getTelBookingDetail(@Path("id") telBookingId: Int, @QueryMap() map: MutableMap<String, Any>): Call<TelBooking>


    /**
     * type 提醒类型，1：睡眠提醒 2：睡眠日记提醒 3:放松训练提醒
     */
    @GET("reminders/subscriptions")
    fun getReminderList(@Query("type") reminderType: Int = 2): Call<PaginationResponseV2<Reminder>>

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
                            @Query("per_page") perPage: Int = 10): Call<PaginationResponseV2<DiaryEvaluationData>>

    @GET("diary-evaluation/latest")
    fun getLatestDiaryEvaluation(@Query("include") include: String? = null): Call<DiaryEvaluationData>

    @FormUrlEncoded
    @POST("feedback")
    fun feedback(@Field("content") content: String, @Field("suffix") suffix: String = "text"): Call<Feedback>

    /**
     * 兑换码  兑换
     */
    @FormUrlEncoded
    @POST("redeem-codes/exchange")
    fun couponAction(@Field("code") coupon: String): Call<Any>

    /**
     * 获取兑换码  兑换列表
     */
    @GET("redeem-codes")
    fun getCouponList(@QueryMap map: MutableMap<String, Any>): Call<PaginationResponseV2<Coupon>>

    /**
     * anxiety
     */
    @GET("user/anxieties")
    fun getAnxieties(@Query("page") page: Int = 1,
                     @Query("per_page") perPage: Int = 15): Call<PaginationResponseV2<AnxietyData>>

    @FormUrlEncoded
    @POST("user/anxieties")
    fun addAnxiety(@Field("anxiety") anxiety: String, @Field("solution") solution: String): Call<AnxietyData>

    @FormUrlEncoded
    @PATCH("user/anxieties/{id}")
    fun updateAnxiety(@Path("id") id: Int, @Field("anxiety") anxiety: String, @Field("solution") solution: String): Call<AnxietyData>

    @DELETE("user/anxieties/{id}")
    fun deleteAnxiety(@Path("id") id: Int): Call<Any>

    /**
     * faiths
     */
    @FormUrlEncoded
    @PATCH("user/faiths/{id}")
    fun updateFaiths(@Path("id") id: Int, @Field("scene") scene: String, @Field("idea") idea: String, @Field("emotion_type") emotion_type: Int): Call<FaithData>

    @DELETE("user/faiths/{id}")
    fun deleteFaiths(@Path("id") id: Int): Call<Any>

    @FormUrlEncoded
    @POST("user/faiths")
    fun addFaiths(@Field("scene") scene: String, @Field("idea") idea: String, @Field("emotion_type") emotion_type: Int): Call<FaithData>

    @GET("user/faiths")
    fun getFaiths(@Query("page") page: Int = 1,
                  @Query("per_page") perPage: Int = 15): Call<PaginationResponseV2<FaithData>>

    // hw api start ---
    @Headers("Accept: application/vnd.sumianapi.v2+json")
    @GET("firmware/latest")
    fun syncFirmwareInfo(): Call<FirmwareInfo>

    @GET("app-version/latest")
    fun syncUpgradeAppInfo(@QueryMap map: MutableMap<String, String>): Call<AppUpgradeInfo>

    @POST("heartbeats")
    @FormUrlEncoded
    fun sendHeartbeats(@Field("type") type: String): Call<Any>

    //在日历中获取日报告信息
    @GET("sleeps/days/month-unread")
    fun getCalendarSleepReport(@QueryMap map: MutableMap<String, Any>): Call<JsonObject>

    @GET("sleeps/days/flip-show")
    fun getSleepReport(@Query("date") unixTime: Int, @Query("page_size") pageSize: Int, @Query("is_include") isInclude: Int): Call<BaseResultResponse<DailyReport, DailyMeta>>

    //获取多条睡眠周报告
    @GET("sleeps/week-flip-show")
    fun getWeeksSleepReport(@QueryMap map: MutableMap<String, Any>): Call<BaseResultResponse<SleepDurationReport, WeekMeta>>

    /**
     * @param map 数据类型，1：睡眠特征值，2：事件日志
     */
    @FormUrlEncoded
    @POST("raw-data/pass-through-file")
    fun uploadTransData(@FieldMap map: MutableMap<String, Any>): Call<com.sumian.hw.oss.bean.OssResponse>

    /**
     *
     * @param map MutableMap<String, Any>
     * @return Call<LogOssResponse>
     */
    @FormUrlEncoded
    @POST("feedback-auto")
    fun autoUploadLog(@FieldMap map: MutableMap<String, Any>): Call<LogOssResponse>

    // -- hwapi end

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
    ): Call<WeeklyReportResponse>

    /**
     * 获取留言板列表
     */
    @GET("message-boards")
    fun getCBTIMessageBoardList(@QueryMap map: MutableMap<String, Any>): Call<PaginationResponseV2<MessageBoard>>

    /**
     * 发送留言
     */
    @FormUrlEncoded
    @POST("message-boards")
    fun writeCBTIMessageBoard(@FieldMap map: MutableMap<String, Any>): Call<Any>

    /**
     *
     * 获取首页显示一句话
     * @return Call<SentencePoolText>
     */
    @GET("sentence-pools")
    fun getSentencePool(): Call<SentencePoolText>

    @GET("cbti-relaxations")
    fun getRelaxations(): Call<MutableList<RelaxationData>>

    @GET("cbti-relaxations/{id}")
    fun getRelaxationDetail(@Path("id") id: Int): Call<RelaxationData>

}
