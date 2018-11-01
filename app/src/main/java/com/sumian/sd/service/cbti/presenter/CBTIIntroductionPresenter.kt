package com.sumian.sd.service.cbti.presenter

import android.text.TextUtils
import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.doctor.bean.DoctorService
import com.sumian.sd.homepage.bean.GetCbtiChaptersResponse
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.service.cbti.contract.CBTIIntroductionContract
import com.sumian.sd.utils.TimeUtil

/**
 * Created by jzz
 *
 * on 2018-10-26.
 *
 * desc:
 *
 */
class CBTIIntroductionPresenter private constructor(var view: CBTIIntroductionContract.View? = null) : CBTIIntroductionContract.Presenter {

    companion object {

        @JvmStatic
        fun init(view: CBTIIntroductionContract.View): CBTIIntroductionContract.Presenter {
            return CBTIIntroductionPresenter(view)
        }
    }

    override fun getCBTIServiceDetail() {
        view?.showLoading()
        val call = AppManager.getSdHttpService().getServiceByType(DoctorService.SERVICE_TYPE_CBTI)
        mCalls.add(call)
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

    override fun getCBTIIntroductionList() {
        view?.showLoading()
        val call = AppManager.getSdHttpService().getCbtiChapters("courses")
        mCalls.add(call)
        call.enqueue(object : BaseSdResponseCallback<GetCbtiChaptersResponse>() {
            override fun onSuccess(response: GetCbtiChaptersResponse?) {
                response?.let {
                    val data = it.data
                    view?.getCBTIIntroductionListSuccess(data)
                    val formatExpiredDate = TimeUtil.formatDate("yyyy.MM.dd 到期", it.meta.expiredAt * 1000L)
                    val isLock = it.meta.isLock
                    view?.onCBTIServiceIsExpired(isLock)
                    view?.getBannerInfo(formatExpiredDate, if (isLock) {
                        "已过期"
                    } else {
                        if (TextUtils.isEmpty(it.meta.totalProgressText)) {
                            "已过期"
                        } else {
                            it.meta.totalProgressText
                        }
                    })
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