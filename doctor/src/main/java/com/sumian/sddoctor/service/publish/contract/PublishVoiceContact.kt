package com.sumian.sddoctor.service.publish.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter

/**
 *
 *Created by sm
 * on 2018/6/8 10:42
 * desc:
 **/
interface PublishVoiceContact {

    interface View : BaseShowLoadingView {

        fun onPublishVoiceSuccess()

        fun onPublishVoiceFailed(error: String)

        fun onGetPublishVoiceStsSuccess()

        fun onGetPublishVoiceStsFailed(error: String)

    }

    interface Presenter : IPresenter {
        fun getPublishVoiceSts(publishType: Int, publishId: Int, voiceFilePath: String, duration: Int)
    }
}