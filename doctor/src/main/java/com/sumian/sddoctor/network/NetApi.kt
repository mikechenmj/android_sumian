package com.sumian.sddoctor.network

import cn.leancloud.chatkit.bean.ImIds
import cn.leancloud.chatkit.bean.ImUser
import com.google.gson.JsonObject
import com.sumian.module_core.chat.bean.CreateConversationResponse
import com.sumian.module_core.notification.NotificationCategory
import com.sumian.module_core.notification.NotificationListResponse
import com.sumian.sddoctor.account.bean.Feedback
import com.sumian.sddoctor.account.bean.Version
import com.sumian.sddoctor.account.kefu.KeFuMessage
import com.sumian.sddoctor.booking.bean.*
import com.sumian.sddoctor.homepage.FreeCallResponse
import com.sumian.sddoctor.homepage.bean.PatientDashboardData
import com.sumian.sddoctor.login.login.bean.DoctorInfo
import com.sumian.sddoctor.login.login.bean.LoginResponse
import com.sumian.sddoctor.login.login.bean.SocialiteInfo
import com.sumian.sddoctor.login.register.bean.ValidateRegisterCaptchaResponse
import com.sumian.sddoctor.me.myservice.bean.DoctorService
import com.sumian.sddoctor.me.myservice.bean.Packages
import com.sumian.sddoctor.me.mywallet.bean.*
import com.sumian.sddoctor.network.bean.PaginationResponse
import com.sumian.sddoctor.network.bean.PaginationResponseV2
import com.sumian.sddoctor.network.response.PatientsResponse
import com.sumian.sddoctor.notification.bean.SystemNotificationData
import com.sumian.sddoctor.oss.OssResponse
import com.sumian.sddoctor.patient.bean.GroupPatientResponse
import com.sumian.sddoctor.patient.bean.MedicalRecord
import com.sumian.sddoctor.patient.bean.Patient
import com.sumian.sddoctor.patient.bean.PatientRecommendation
import com.sumian.sddoctor.patient.sleepdiary.bean.SleepRecord
import com.sumian.sddoctor.patient.sleepdiary.bean.SleepRecordSummary
import com.sumian.sddoctor.service.advisory.bean.Advisory
import com.sumian.sddoctor.service.advisory.bean.AdvisoryResponse
import com.sumian.sddoctor.service.advisory.onlinereport.OnlineReport
import com.sumian.sddoctor.service.cbti.bean.*
import com.sumian.sddoctor.service.evaluation.bean.EvaluationResponse
import com.sumian.sddoctor.service.plan.bean.Plan
import com.sumian.sddoctor.service.report.bean.DailyReport
import com.sumian.sddoctor.service.scale.bean.Scale
import retrofit2.Call
import retrofit2.http.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/14 10:23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
interface NetApi {

    // ---------- 登录 ----------
    @FormUrlEncoded
    @POST("doctor/authorizations")
    fun loginByPassword(@Field("mobile") mobile: String, @Field("password") password: String): Call<LoginResponse>

    @FormUrlEncoded
    @POST("doctor/authorizations/captcha/validation")
    fun loginByCaptcha(@Field("mobile") mobile: String,
                       @Field("captcha") captcha: String): Call<LoginResponse>

    /**
     * type 0：微信,1: 微信公众号
     * info {"openid": "xxx", "unionid": "zwz", "nickname": "qqq"}
     */
    @FormUrlEncoded
    @POST("doctor/authorizations/socialite-bind")
    fun bindSocialiteAndLogin(@Field("mobile") mobile: String,
                              @Field("captcha") captcha: String,
                              @Field("type") type: Int,
                              @Field("info") info: String): Call<LoginResponse>

    /**
     * type 0：微信,1: 微信公众号
     */
    @FormUrlEncoded
    @POST("doctor/authorizations/socialite-bound")
    fun loginBySocialite(@Field("type") type: Int,
                         @Field("union_id") union_id: String,
                         @Field("openid") openid: String): Call<LoginResponse>

    @FormUrlEncoded
    @POST("doctor/authorizations/captcha")
    fun requestLoginCaptcha(@Field("mobile") mobile: String): Call<Any>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("doctor/registration/validations")
    fun validateRegisterCaptcha(@FieldMap map: Map<String, Any>): Call<ValidateRegisterCaptchaResponse>

    @POST("doctor/registrations")
    @Headers("Accept: application/vnd.sd.v2+json")
    fun register(@Body registerInfo: Map<String, @JvmSuppressWildcards Any>): Call<OssResponse>

    @POST("doctor/registrations")
    fun registerV2(): Call<OssResponse>


    // ---------- 预约 ----------
    /**
     * date seconds
     * is_include 是否包括此周，1：是，0：否 不传默认包含
     * direction 请求方向 0：小于查询周，1：大于查询周 不传默认0
     */
    @GET("doctor/bookings")
    fun getBookings(
            @Query("date") date: Int,
            @Query("is_include") is_include: Int,
            @Query("page_size") page_size: Int,
            @Query("direction") direction: Int
    ): Call<GetBookingsResponse>

    /**
     * date seconds
     * is_include 是否包括此周，1：是，0：否 不传默认包含
     * direction 请求方向 0：小于查询周，1：大于查询周 不传默认0
     */
    @GET("doctor/weekly-bookings")
    fun getWeeklyBookings(
            @Query("date") date: Int,
            @Query("is_include") is_include: Int,
            @Query("page_size") page_size: Int,
            @Query("direction") direction: Int
    ): Call<WeeklyBookingResponse>

    @GET("doctor/bookings/{date}")
    fun getBookingByDate(@Path("date") date: Int): Call<List<Booking>>

    @GET("doctor/booking/{id}")
    fun getBookingDetail(@Path("id") bookingId: Int): Call<BookingDetail>

    @GET("doctor/hanging-bookings")
    fun getIsHanging(@Query("user_id") userId: Int): Call<GetIsHangingResponse>

    @JvmSuppressWildcards
    @GET("doctor/users")
    fun getPatients(@QueryMap map: Map<String, Any>): Call<PatientsResponse>

    @GET("doctor/users/{id}")
    fun getPatientDetail(@Path("id") id: Int): Call<Patient>

    @GET("doctor/users/{user_id}/medical-record")
    fun getUserMedicalRecord(@Path("user_id") userId: Int): Call<MedicalRecord>

    @POST("doctor/calls")
    @FormUrlEncoded
    fun recallPatient(@Field("user_id") userId: Int): Call<FreeCallResponse>

    /**
     * status   1: 已确认 5:取消预约（已关闭）
     */
    @FormUrlEncoded
    @PATCH("doctor/booking-status/{id}")
    fun modifyBookingStatus(@Path("id") bookingId: Int, @Field("status") status: Int): Call<Any>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("doctor/profile")
    fun updateDoctorInfo(@FieldMap map: Map<String, Any>): Call<DoctorInfo>

    @POST("doctor/password")
    @FormUrlEncoded
    fun updatePassword(@Field("old_password") oldPassword: String?,
                       @Field("password") password: String,
                       @Field("password_confirmation") passwordConfirmation: String
    ): Call<DoctorInfo>

    @GET("doctor/profile")
    fun getDoctorInfo(): Call<DoctorInfo>

    @PATCH("doctor/avatar")
    fun uploadAvatar(): Call<OssResponse>

    @DELETE("doctor/authorizations/current")
    fun logout(): Call<Any>

    @FormUrlEncoded
    @POST("doctor/socialites")
    fun bindSocialite(@Field("info") info: String): Call<SocialiteInfo>

    @DELETE("doctor/socialites/{id}")
    fun unbindSocialite(@Path("id") id: Int): Call<Any>

    @GET("doctor/app-version/latest")
    fun getAppVersion(@Query("type") type: Int = 1, @Query("current_version") currentVersion: String): Call<Version>

    // ---------- 推送 ----------
    /**
     * type: all:所有 unread：未读
     */
    @GET("doctor/notifications")
    fun getNotificationList(@Query("page") page: Int,
                            @Query("per_page") perPage: Int,
                            @Query("type") type: String): Call<NotificationListResponse>

    /**
     * type: all:所有 unread：未读
     * category: 类别 0:普通消息 2:cbti周报消息 不传取全部消息
     */
    @GET("doctor/notifications")
    fun getNotificationListByCategory(@Query("page") page: Int,
                                      @Query("per_page") perPage: Int,
                                      @Query("type") type: String,
                                      @Query("category") category: Int): Call<NotificationListResponse>

    @GET("doctor/notification-profile")
    fun getNotificationCategoryList(): Call<List<NotificationCategory>>

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
    @PATCH("doctor/notifications/{id}")
    fun readNotification(@Path("id") notificationId: String,
                         @Query("data_id") dataId: Int? = null): Call<Any>

    @GET("doctor/users/{id}/recommendation")
    fun getPatientRecommendation(@Path("id") patientId: Int): Call<PatientRecommendation>

    @PATCH("doctor/users/{id}/recommendation")
    fun recommendPatient(@Path("id") patientId: Int): Call<PatientRecommendation>

    //user advisory

    /**
     * 获取图文咨询列表
     */
    @GET("doctor/advisory-missions")
    fun getDoctorAdvisories(@QueryMap map: MutableMap<String, Any>): Call<AdvisoryResponse>

    /**
     * 获取图文咨询详情
     */
    @GET("doctor/advisory-missions/{id}")
    fun getDoctorAdvisoryDetails(@Path("id") advisoryId: Int, @Query("include") include: String = "traceable.records,traceable.user,traceable.doctor"): Call<Advisory>

    /**
     * 以文本形式回复图文咨询
     */
    @FormUrlEncoded
    @PATCH("doctor/advisory-missions/{id}")
    fun replayDocAdvisory(@Path("id") advisoryId: Int,
                          @FieldMap map: MutableMap<String, Any>): Call<Any>

    /**
     * 以语音形式回复图文咨询
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("doctor/advisory-missions/sts")
    fun getAdvisoryVoiceOssSts(@Field("advisory_mission_id")
                               advisoryId: Int,
                               @Field("sound_duration") soundDuration: Int,
                               @Field("extension") extension: String = "mp3"): Call<OssResponse>

    /**
     * 获取分组患者列表  p=tag 患者分级:  0:普通用户  1:VIP  2:S_VIP
     *                 p=consulted 面诊情况: 1:已面诊 2:未面诊
     */
    @GET()
    fun getGroupPatients(@Url url: String): Call<GroupPatientResponse>

    /**
     * 获取绑定的患者列表  可分页
     */
    @GET("doctor/binding-users")
    fun getPatientList(@QueryMap map: MutableMap<String, Any>): Call<PatientsResponse>

    /**
     * 一次性获取需要发送给患者的量表
     */
    @GET("doctor/scale-lists")
    fun getScale(): Call<List<Scale>>

    /**
     * 发送对应的量表给对应的患者
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("doctor/scale-distributions")
    fun sendScale(@FieldMap map: MutableMap<String, Any>): Call<Void>

    /**
     * 一次性获取需要发送给患者的随访计划
     */
    @GET("doctor/followup-plan/lists")
    fun getFollowupPlan(): Call<List<Plan>>

    /**
     * 发送对应的随访计划给对应的患者
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("doctor/followup-plan/distributions")
    fun sendFollowupPlan(@FieldMap map: MutableMap<String, Any>): Call<Void>

    /**
     * 把患者标为  1已面诊  0未面诊
     */
    @FormUrlEncoded
    @PATCH("doctor/consulted-user/{id}")
    fun consulted(@Path("id") patientId: Int, @Field("consulted") consulted: Int): Call<Patient>

    @FormUrlEncoded
    @POST("doctor/portables")
    fun portables(@Field("device_type") deviceType: String,
                  @Field("device_token") deviceToken: String,
                  @Field("system_version") systemVersion: String): Call<Any?>

    /**
     * page 第几页 Default value: 1
     * per_page 每页数量 Default value: 15
     * user_id 用户id
     */
    @GET("/online-reports")
    fun getReports(@Query("page") page: Int,
                   @Query("per_page") perPage: Int): Call<PaginationResponse<OnlineReport>>

    /**
     *
     * 获取睡眠日记周评估列表
     *
     * page 第几页 Default value: 1
     *per_page 每页数量 Default value: 15
     * status_type 状态 0:待回复 1:已完成 2:已关闭 3:已取消 不填：全部
     * user_id 用户id
     * include  user 用户 doctor 医生 package.servicePackage.service 服务
     */
    @GET("doctor/diary-evaluations")
    fun getDoctorDiaryEvaluations(@QueryMap map: MutableMap<String, Any>): Call<EvaluationResponse>

    /**
     * 或者睡眠日记周评估语音回复凭证
     */
    @FormUrlEncoded
    @POST("doctor/diary-evaluations/sts")
    fun getDiaryEvaluationVoiceSts(@Field("diary_evaluation_id") diaryEvaluationId: Int,
                                   @Field("sound_duration") duration: Int,
                                   @Field("extension") extension: String = "mp3"): Call<OssResponse>

    /**
     * 以文字形式评估睡眠日记
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @PATCH("doctor/diary-evaluations/{id}")
    fun publishDocDiaryEvaluation(@Path("id") evaluationId: Int,
                                  @Field("content") content: String): Call<Any>


    @GET("/doctor/services?include=servicePackages.packages")
    fun getMyServiceList(): Call<PaginationResponse<DoctorService>>

    @GET("/doctor/services/{id}?include=servicePackages.packages")
    fun getServiceDetail(@Path("id") id: Int): Call<DoctorService>

    /**
     * price 服务包价格，单位：分
     * enable 是否开启 1：是 0：否
     */
    @FormUrlEncoded
    @PATCH("doctor/packages/{id}")
    fun updateServicePackage(@Path("id") id: Int, @Field("unit_price") price: Int, @Field("enable") enable: Int): Call<Packages>

    /**
     * 钱包
     */
    @GET("doctor/wallet")
    fun getWalletBalance(): Call<WalletBalance>

    @GET("doctor/wallet-details")
    fun getWalletDetailList(@Query("page") page: Int, @Query("per_page") per_page: Int = 15): Call<WalletDetailResponse>

    @GET("doctor/wallet-details/{id}")
    fun getWalletDetail(@Path("id") id: Int): Call<WalletDetail>

    @GET("doctor/pending-incomes")
    fun getPendingIncomeList(@Query("page") page: Int, @Query("per_page") per_page: Int = 15): Call<PaginationResponseV2<SettlingRecord>>

    @GET("doctor/pending-incomes/{id}")
    fun getPendingIncomeDetail(@Path("id") id: Int): Call<SettlingRecord>

    @GET("doctor/withdrawals/permission")
    fun getWithdrawAbility(): Call<WithdrawAbility>

    /**
     * 提现
     */
    @GET("doctor/withdrawals/tax-rule-code")
    fun getWithdrawRule(): Call<WithdrawRule>

    @FormUrlEncoded
    @POST("doctor/withdrawals")
    fun withdraw(@Field("amount") amount: Long): Call<WithdrawRecord>

    @GET("doctor/withdrawals")
    fun getWithdrawRecords(@Query("page") page: Int,
                           @Query("per_page") perPage: Int = 15): Call<PaginationResponseV2<WithdrawRecord>>

    @GET("doctor/withdrawals/{id}")
    fun getWithdrawDetail(@Path("id") id: Int): Call<WithdrawRecord>

    /**
     * 获取 cbti 进度分组情况
     *
     * include 可选 {users}
     *
     * groups id
     */
    @GET("doctor/cbti-users")
    fun getCBTIProgressGroups(@QueryMap map: MutableMap<String, Any>): Call<CBTIProgressGroupResponse>

    @GET("doctor/simple-dashboard")
    fun getPatientDashboard(): Call<PatientDashboardData>

    //在日历中获取日报告信息
    @GET("doctor/sleeps/{userId}/calendar")
    fun getCalendarSleepReport(@Path("userId") patientId: Int, @QueryMap map: MutableMap<String, Any>): Call<JsonObject>

    //获取患者的睡眠数据日报告
    @GET("doctor/sleep-daily/reports")
    fun getSleepReport(@Query("date") unixTime: Int, @Query("user_id") patientId: Int): Call<DailyReport>

    @FormUrlEncoded
    @POST("doctor/feedback")
    fun feedback(@Field("content") content: String): Call<Feedback>

    @GET("doctor/users/{userId}/diary")
    fun getUserDiary(@Path("userId") userId: Int, @Query("date") unixTime: Int): Call<SleepRecord>

    /**
     * date unix时间戳：1519833600，可传当月任何一天，默认这个月
     * is_include 是否包括此时间戳，1：是，0：否。默认0
     * page_size 请求数量不传默认为1
     * direction 请求方向 0：小于查询时间戳，1：大于查询时间戳 不传默认0
     */
    @GET("doctor/users/{userId}/diary-month")
    fun getSleepDiarySummaryList(
            @Path("userId") userId: Int,
            @Query("date") unixTime: Int,
            @Query("is_include") isInclude: Int,
            @Query("page_size") pageSize: Int,
            @Query("direction") direction: Int): Call<Map<String, List<SleepRecordSummary>>>

    @POST("doctor/sign-up")
    @FormUrlEncoded
    fun signUp(@Field("mobile") mobile: String,
               @Field("captcha") captcha: String,
               @Field("retailer_invitation_code") invitationCode: String
    ): Call<LoginResponse>

    @POST("doctor/heartbeats")
    @FormUrlEncoded
    fun sendHeartbeats(@Field("type") type: String): Call<Any>

    @GET("doctor/notice/{id}/content-detail")
    fun getSystemNotificationDetail(@Path("id") id: Int): Call<SystemNotificationData>

    //cbti

    /**
     * 服务类型 0：睡眠日记 1：图文咨询 2：电话咨询 3：CBTI
     */
    @GET("service")
    fun getServiceByType(@Query("type") type: Int): Call<DoctorService>

    /**
     * include
     * courses 课程
     * courses.exercise 课后练习
     */
    @GET("doctor/cbti-chapters")
    fun getCbtiChapters(@Query("include") include: String?): Call<GetCbtiChaptersResponse>

    /**
     * 获取该章节下的所有课程
     */
    @GET("doctor/cbti-chapter/{chapter-id}/courses")
    fun getCBTICourseWeekPart(@Path("chapter-id") id: Int): Call<CBTIDataResponse<Course>>

    /**
     * 获取该章节下的所有练习
     */
    @GET("cbti-chapter/{chapter-id}/exercises")
    fun getCBTIExerciseWeekPart(@Path("chapter-id") id: Int): Call<CBTIDataResponse<Exercise>>

    /**
     * 获取该章节下的单独课时详情信息,包括视频播放权限,及相关信息
     */
    @GET("doctor/cbti-courses/{id}")
    fun getCBTIPlayAuth(@Path("id") id: Int): Call<CoursePlayAuth>

    /**
     * 上传当前课时的视频播放 log 日志
     */
    @FormUrlEncoded
    @POST("doctor/cbti-course/{id}/logs")
    fun uploadCBTICourseLogs(@Path("id") id: Int, @Field("video_id") videoId: String, @Field("video_progress") video_progress: String, @Field("end_point") end_point: Int): Call<CoursePlayLog>

    /**
     * 上传当前课时的视频观看时长 log 日志
     * ps：观看时长，需要更新观看时长的时候才传这个参数，有这个参数的时候，后台会增加一次观看次数，并累加观看时长
     */
    @FormUrlEncoded
    @POST("doctor/cbti-course/{id}/logs")
    fun uploadCBTICourseWatchLengthLogs(@Path("id") id: Int, @Field("video_id") videoId: String, @Field("video_progress") video_progress: String, @Field("end_point") end_point: Int, @Field("watch_length") watchLength: Int): Call<CoursePlayLog>

    /**
     * 上传视频问卷
     */
    @FormUrlEncoded
    @POST("cbti-course/{id}/questionnaires")
    fun uploadCBTIVideoQuestionnaires(@Path("id") id: Int, @Field("data") json: String): Call<CoursePlayAuth>

    //留言板

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

    @DELETE("message-boards/{id}")
    fun delSelfMessageKeyboard(@Path("id") msgId: Int): Call<Any>

    @GET("message-boards/{id}")
    fun getMsgKeyboardDetail(@Path("id") msgId: Int): Call<MessageBoard>

    @GET("doctor/cbti-configs")
    fun getConfigs(): Call<JsonObject>

    //环信客服相关
    /**
     * 新用户登录环信
     * @return Call<Any>
     */
    @POST("doctor/customer-service/message-event")
    fun newCustomerMessage(): Call<Any>

    /**
     *
     * @param userId ") userId: Int
     * @return Call<UserInfo>
     */
    @POST("doctor/authorizations/{doctorId}/easemob")
    fun notifyRegisterImServer(@Path("doctorId") userId: Int): Call<KeFuMessage>

    @POST("doctor/user/{userId}/conversations")
    fun createConversation(@Path("userId") userId: Int): Call<CreateConversationResponse>

    @POST("doctor/user/im-ids")
    fun queryImUserInfo(@Body imIds: ImIds): Call<Map<String, ImUser?>>
}
