package com.sumian.sleepdoctor.network.api

import com.sumian.sleepdoctor.account.bean.Social
import com.sumian.sleepdoctor.account.bean.Token
import com.sumian.sleepdoctor.account.bean.UserProfile
import com.sumian.sleepdoctor.improve.doctor.bean.Doctor
import com.sumian.sleepdoctor.improve.doctor.bean.PayOrder
import com.sumian.sleepdoctor.network.response.BaseResponse
import com.sumian.sleepdoctor.oss.bean.OssResponse
import com.sumian.sleepdoctor.pager.bean.OrderDetail
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
    fun getGroups(@QueryMap map: MutableMap<String, Int>): Call<BaseResponse<List<GroupDetail<UserProfile, UserProfile>>>>

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
     *
     * response:
     * {"1525104000":[{"id":258,"date":1525363200,"is_today":false,"has_doctors_evaluation":false},{"id":307,"date":1525622400,"is_today":false,"has_doctors_evaluation":false},{"id":447,"date":1526313600,"is_today":false,"has_doctors_evaluation":false},{"id":536,"date":1526832000,"is_today":false,"has_doctors_evaluation":false}],"1522512000":[{"id":1,"date":1523721600,"is_today":false,"has_doctors_evaluation":false},{"id":18,"date":1524672000,"is_today":false,"has_doctors_evaluation":false},{"id":118,"date":1524758400,"is_today":false,"has_doctors_evaluation":false},{"id":149,"date":1524844800,"is_today":false,"has_doctors_evaluation":false}],"1519833600":[],"1517414400":[],"1514736000":[],"1512057600":[],"1509465600":[],"1506787200":[],"1504195200":[],"1501516800":[]}
     */
    @GET("diary-month")
    fun querySleepDiarySummaryList(@Query("date") unixTime: Int,
                                   @Query("is_include") isInclude: Int,
                                   @Query("page_size") pageSize: Int,
                                   @Query("direction") direction: Int): Call<Map<String, List<SleepRecordSummary>>>

    @GET("diaries")
    fun querySleepDiaryDetail(@Query("date") unixTime: Int): Call<SleepRecord>
    //doctor

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
}
