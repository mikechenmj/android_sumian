package com.sumian.sddoctor.account.presenter

import android.text.TextUtils
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.account.contract.UserAvatarContract
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.oss.OssEngine
import com.sumian.sddoctor.oss.OssResponse
import org.json.JSONException
import org.json.JSONObject

open class UserAvatarPresenter private constructor(view: UserAvatarContract.View) : BaseViewModel() {

    companion object {
        @JvmStatic
        fun init(view: UserAvatarContract.View): UserAvatarPresenter {
            return UserAvatarPresenter(view)
        }
    }

    private var mView: UserAvatarContract.View? = null

    init {
        this.mView = view
    }

    fun uploadAvatar(avatarPathUrl: String) {
        mView?.showLoading()
        val call = AppManager.getHttpService().uploadAvatar()
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<OssResponse>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onUploadAvatarFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: OssResponse?) {
                response?.let {
                    OssEngine.uploadFile(it, avatarPathUrl, object : OssEngine.UploadCallback {
                        override fun onSuccess(response: String?) {
                            try {
                                if (!TextUtils.isEmpty(response)) {
                                    val jsonObject = JSONObject(response)
                                    val avatarUrl = jsonObject.getString("avatar")
                                    val qrCodeRaw = jsonObject.getString("qr_code_raw")
                                    if (!TextUtils.isEmpty(avatarUrl)) {
                                        val userProfile = AppManager.getAccountViewModel().getDoctorInfo().value
                                        userProfile?.let {
                                            userProfile.avatar = avatarUrl
                                            LogUtils.d("qr_code_raw", userProfile.qr_code_raw)
                                            userProfile.qr_code_raw = qrCodeRaw
                                            LogUtils.d("qr_code_raw", userProfile.qr_code_raw)
                                            AppManager.getAccountViewModel().updateDoctorInfo(userProfile)
                                        }
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            mView?.dismissLoading()
                        }

                        override fun onFailure(errorCode: String?, message: String?) {
                            message?.let {
                                mView?.onUploadAvatarFailed(error = message)
                            }
                        }
                    })
                }
            }

            override fun onFinish() {
                super.onFinish()
                //mView?.dismissLoading()
            }
        })

    }

//    /**
//     * 为了刷新 qr code
//     */
//    private fun getDoctorInfo() {
//        val call = AppManager.getHttpService().queryDoctorInfo(
//                AppManager.getAccountViewModel().getDoctorInfo().value?.id ?: 0)
//        mCalls.add(call)
//        call.enqueue(object : BaseSdResponseCallback<DoctorInfo>() {
//            override fun onSuccess(response: DoctorInfo?) {
//                AppManager.getAccountViewModel().updateDoctorInfo(response)
//            }
//
//            override fun onFailure(errorResponse: ErrorResponse) {
//            }
//
//        })
//    }
}