package com.sumian.sleepdoctor.network.api

import com.sumian.sleepdoctor.account.bean.Token
import com.sumian.sleepdoctor.account.bean.UserProfile
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by jzz
 * on 2018/1/15.
 * desc:
 */

interface DoctorApi {

    @PATCH("authorizations/current")
    fun refreshTOken(): Call<Token>

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

    @PATCH("user/profile")
    fun modifyUserProfile(@FieldMap map: MutableMap<String, String>): Call<UserProfile>


}
