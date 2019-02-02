package com.sumian.sddoctor.service.cbti.presenter

import com.google.gson.JsonObject
import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.me.myservice.bean.DoctorService
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.service.cbti.bean.GetCbtiChaptersResponse
import com.sumian.sddoctor.service.cbti.contract.CBTIIntroductionContract
import org.json.JSONObject

/**
 * Created by jzz
 *
 * on 2018-10-26.
 *
 * desc:
 *
 */
class CBTIIntroductionPresenter private constructor(var view: CBTIIntroductionContract.View? = null) : BaseViewModel() {
     fun getConfigs() {
        view?.showLoading()
        val call = AppManager.getHttpService().getConfigs()
         addCall(call)
        call.enqueue(object : BaseSdResponseCallback<JsonObject>() {
            override fun onSuccess(response: JsonObject?) {
                response?.let {
                    var bannerUrl = ""
                    var name = ""
                    var introduction = ""
                    it.entrySet().forEach { itt ->
                        val key = itt.key
                        val value = itt.value
                        when (key) {
                            "cbti_banner_for_doctor" -> {
                                bannerUrl = value.asString
                            }
                            "cbti_short_introduction_for_doctor" -> {
                                introduction = value.asString
                            }
                            "cbti_name_for_doctor" -> {
                                name = value.asString
                            }
                        }
                    }
                    view?.getCBTIServiceDetailSuccess(name, introduction, bannerUrl)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                view?.getConfigsFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                view?.dismissLoading()
            }
        })

    }

    companion object {

        @JvmStatic
        fun init(view: CBTIIntroductionContract.View): CBTIIntroductionPresenter {
            return CBTIIntroductionPresenter(view)
        }
    }

     fun getCBTIServiceDetail() {
        view?.showLoading()
        val call = AppManager.getHttpService().getServiceByType(DoctorService.SERVICE_TYPE_CBTI)
         addCall(call)
        call.enqueue(object : BaseSdResponseCallback<DoctorService>() {
            override fun onSuccess(response: DoctorService?) {
                response?.let {
                    view?.getCBTIServiceDetailSuccess(it.name, it.introduction, it.picture)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                view?.getCBTIServiceDetailFailed(errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                view?.dismissLoading()
            }
        })
    }

     fun getCBTIIntroductionList() {
        view?.showLoading()
        val call = AppManager.getHttpService().getCbtiChapters("courses")
         addCall(call)
        call.enqueue(object : BaseSdResponseCallback<GetCbtiChaptersResponse>() {
            override fun onSuccess(response: GetCbtiChaptersResponse?) {
                response?.let {
                    val data = it.data
                    view?.getCBTIIntroductionListSuccess(data)
                    val meta = response.meta
                    view?.getBannerInfo("共 ${meta.totalVideos} 个视频，已学习 ${meta.finishedPercent}%")
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                view?.getCBTIIntroductionListFailed(errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                view?.dismissLoading()
            }

        })
    }
}