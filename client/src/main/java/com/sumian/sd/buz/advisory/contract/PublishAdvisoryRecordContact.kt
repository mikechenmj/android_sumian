package com.sumian.sd.buz.advisory.contract

import com.sumian.sd.base.SdBaseView
import com.sumian.sd.buz.advisory.bean.Advisory
import com.sumian.sd.buz.advisory.presenter.PublishAdvisoryRecordPresenter

/**
 *
 *Created by sm
 * on 2018/6/8 10:42
 * desc:
 **/
interface PublishAdvisoryRecordContact {

    interface View : SdBaseView<PublishAdvisoryRecordPresenter> {

        fun onPublishAdvisoryRecordSuccess(advisory: Advisory)

        fun onPublishAdvisoryRecordFailed(error: String)

        fun onGetLastAdvisorySuccess(advisory: Advisory)

        fun onGetLastAdvisoryFailed(error: String)

        fun onGetPublishUploadStsSuccess(successMsg: String)

        fun onGetPublishUploadStsFailed(error: String)

        fun onStartUploadImagesCallback()

        fun onEndUploadImagesCallback()

    }


}