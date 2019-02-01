package com.sumian.sddoctor.service.scale.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter
import com.sumian.sddoctor.service.scale.bean.Scale

/**
 * Created by dq
 *
 * on 2018/8/30
 *
 * desc: 量表模块
 */
interface ScaleContract {


    interface View : BaseShowLoadingView {

        fun onSendScaleSuccess(success: String)

        fun onSendScaleFailed(error: String)

        fun onGetScalesSuccess(plans: List<Scale>)

        fun onGetScalesFailed(error: String)

    }

    interface Presenter : IPresenter {

        fun getScales()

        fun sendScale(patientId: Int, scaleIds: List<Int>)
    }
}