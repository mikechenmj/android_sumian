package com.sumian.sd.service.advisory.contract

import com.alibaba.sdk.android.oss.callback.OSSProgressCallback
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseView
import com.sumian.sd.service.advisory.bean.Advisory

/**
 *
 *Created by sm
 * on 2018/6/8 10:42
 * desc:
 **/
interface PublishAdvisoryRecordContact {

    interface View : SdBaseView<Presenter> {

        fun onPublishAdvisoryRecordSuccess(advisory: Advisory)

        fun onPublishAdvisoryRecordFailed(error: String)

        fun onGetLastAdvisorySuccess(advisory: Advisory)

        fun onGetLastAdvisoryFailed(error: String)

        fun onGetPublishUploadStsSuccess(successMsg: String)

        fun onGetPublishUploadStsFailed(error: String)

        fun onStartUploadImagesCallback()

        fun onEndUploadImagesCallback()

    }


    interface Presenter : SdBasePresenter<Any> {

        fun publishAdvisoryRecord(advisoryId: Int, content: String, onlineReportIds: ArrayList<Int>?)

        fun publishPictureAdvisoryRecord(advisoryId: Int, content: String, onlineReportIds: ArrayList<Int>?, pictureCount: Int)

        fun publishImages(localFilePaths: Array<String>, oSSProgressCallback: OSSProgressCallback<PutObjectRequest>)

        fun getLastAdvisory()
    }
}