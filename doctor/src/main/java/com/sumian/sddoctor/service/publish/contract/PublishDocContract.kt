package com.sumian.sddoctor.service.publish.contract

import com.sumian.common.base.BaseShowLoadingView

/**
 * Created by dq
 *
 * on 2018/8/31
 *
 * desc:
 */
interface PublishDocContract {

    interface View : BaseShowLoadingView {

        fun onPublishSuccess(success: String)

        fun onPublishFailed(error: String)
    }


}