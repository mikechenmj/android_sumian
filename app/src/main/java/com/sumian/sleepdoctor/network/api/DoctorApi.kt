package com.sumian.sleepdoctor.network.api

import com.sumian.sleepdoctor.account.bean.Social
import com.sumian.sleepdoctor.account.bean.Token
import com.sumian.sleepdoctor.account.bean.UserProfile
import com.sumian.sleepdoctor.improve.doctor.bean.Doctor
import com.sumian.sleepdoctor.network.response.BaseResponse
import com.sumian.sleepdoctor.oss.bean.OssResponse
import com.sumian.sleepdoctor.pager.bean.OrderDetail
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

    @FormUrlEncoded
    @POST("socialites")
    fun bindSocialites(@Field("type") type: Int, @Field("info") info: String): Call<Social>

    @DELETE("socialites/{id}")
    fun unbindSocialites(@Path("id") userId: Int): Call<String>

    //doctor

    @PATCH("doctor-bind")
    fun bindDoctor(@FieldMap map: MutableMap<String, Any>): Call<Doctor>

    @GET("doctors/{id}")
    fun getDoctorInfo(@Path("id") id: Int, @Query("include") include: String): Call<Doctor>

    @GET
    fun getBindDoctorInfo(): Call<Doctor>

    @DELETE("doctor/binding")
    fun unbindDoctor(): Call<Unit>
}
