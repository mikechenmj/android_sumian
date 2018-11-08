package com.sumian.sd.service.cbti.job

import android.content.Intent
import android.support.v4.app.JobIntentService
import android.text.TextUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.NetUtil
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.service.cbti.bean.CBTIWatchLog

class CBTICourseWatchLogJobService : JobIntentService() {
    companion object {
        /**
         * Unique job ID for this service.
         */
        private const val JOB_ID = 1002

        private const val EXTRAS_CBTI_COURSE_ID = "com.sumian.sd.extras.cbti_course_id"
        private const val EXTRAS_CBTI_VIDEO_ID = "com.sumian.sd.extras.cbti_video_id"
        private const val EXTRAS_CBTI_WATCH_LENGTH = "com.sumian.sd.extras.cbti_watch_length"

        @JvmStatic
        fun execute(cbtiCourseId: Int, videoId: String, cbtiCourseWatchLength: Int) {
            val intent = Intent(App.getAppContext(), CBTICourseWatchLogJobService::class.java).apply {
                putExtra(EXTRAS_CBTI_COURSE_ID, cbtiCourseId)
                putExtra(EXTRAS_CBTI_VIDEO_ID, videoId)
                putExtra(EXTRAS_CBTI_WATCH_LENGTH, cbtiCourseWatchLength)

            }
            JobIntentService.enqueueWork(App.getAppContext(), CBTICourseWatchLogJobService::class.java, JOB_ID, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        val cbtiCourseId = intent.getIntExtra(EXTRAS_CBTI_COURSE_ID, 0)
        val videoId = intent.getStringExtra(EXTRAS_CBTI_VIDEO_ID)
        val cbtiCourseWatchLength = intent.getIntExtra(EXTRAS_CBTI_WATCH_LENGTH, 0)

        if (cbtiCourseId == 0 && TextUtils.isEmpty(videoId) && cbtiCourseWatchLength <= 0) {
            return
        }
        uploadCBTICourseWatchLog(cbtiCourseId, videoId, cbtiCourseWatchLength)
    }

    private fun uploadCBTICourseWatchLog(cbtiCourseId: Int, videoId: String, cbtiCourseWatchLength: Int) {
        if (!NetUtil.isHaveNet(App.getAppContext())) {
            uploadCBTICourseWatchLog(cbtiCourseId, videoId, cbtiCourseWatchLength)
        }
        AppManager
                .getSdHttpService()
                .uploadCBTICourseWatchLog(cbtiCourseId,
                        videoId,
                        cbtiCourseWatchLength)
                .enqueue(object : BaseSdResponseCallback<CBTIWatchLog>() {
                    override fun onSuccess(response: CBTIWatchLog?) {
                    }

                    override fun onFailure(errorResponse: ErrorResponse) {

                    }
                })
    }
}