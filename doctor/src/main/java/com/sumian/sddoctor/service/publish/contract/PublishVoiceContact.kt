package com.sumian.sddoctor.service.publish.contract

import com.sumian.common.base.BaseShowLoadingView

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

}