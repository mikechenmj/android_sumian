package com.sumian.sddoctor.service.publish.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter
import com.sumian.sddoctor.service.publish.bean.Publish

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


    interface Presenter : IPresenter {

        fun publishDoc(publishType: Int = Publish.PUBLISH_ADVISORY_TYPE, publishId: Int, content: String)
    }
}