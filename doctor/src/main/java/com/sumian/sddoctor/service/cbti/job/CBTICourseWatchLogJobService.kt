package com.sumian.sddoctor.service.cbti.job

import android.content.Intent
import android.text.TextUtils
import androidx.core.app.JobIntentService
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.NetUtil
import com.sumian.sddoctor.service.cbti.bean.CoursePlayLog
import com.sumian.sddoctor.app.App
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback

class CBTICourseWatchLogJobService : JobIntentService() {
    companion object {
        /**
         * Unique job ID for this service.
         */
        private const val JOB_ID = 1002

        private const val EXTRAS_CBTI_COURSE_ID = "com.sumian.sd.extras.cbti_course_id"
        private const val EXTRAS_CBTI_VIDEO_ID = "com.sumian.sd.extras.cbti_video_id"
        private const val EXTRAS_CBTI_VIDEO_PROGRESS = "com.sumian.sd.extras.cbti_video_progress"
        private const val EXTRAS_CBTI_ENDPOINT = "com.sumian.sd.extras.cbti_endpoint"
        private const val EXTRAS_CBTI_WATCH_LENGTH = "com.sumian.sd.extras.cbti_watch_length"

        @JvmStatic
        fun execute(cbtiCourseId: Int, videoId: String, hexVideoProgress: String, endpoint: Int, cbtiCourseWatchLength: Int) {
            val intent = Intent(App.getAppContext(), CBTICourseWatchLogJobService::class.java).apply {
                putExtra(EXTRAS_CBTI_COURSE_ID, cbtiCourseId)
                putExtra(EXTRAS_CBTI_VIDEO_ID, videoId)
                putExtra(EXTRAS_CBTI_VIDEO_PROGRESS, hexVideoProgress)
                putExtra(EXTRAS_CBTI_ENDPOINT, endpoint)
                putExtra(EXTRAS_CBTI_WATCH_LENGTH, cbtiCourseWatchLength)

            }
            JobIntentService.enqueueWork(App.getAppContext(), CBTICourseWatchLogJobService::class.java, JOB_ID, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        val cbtiCourseId = intent.getIntExtra(EXTRAS_CBTI_COURSE_ID, 0)
        val videoId = intent.getStringExtra(EXTRAS_CBTI_VIDEO_ID)
        val hexVideoProgress = intent.getStringExtra(EXTRAS_CBTI_VIDEO_PROGRESS)
        val videoEndpoint = intent.getIntExtra(EXTRAS_CBTI_ENDPOINT, 0)
        val cbtiCourseWatchLength = intent.getIntExtra(EXTRAS_CBTI_WATCH_LENGTH, 0)

        if (cbtiCourseId == 0 && TextUtils.isEmpty(videoId) && cbtiCourseWatchLength <= 0) {
            return
        }
        uploadCBTICourseWatchLog(cbtiCourseId, videoId, hexVideoProgress, videoEndpoint, cbtiCourseWatchLength)
    }

    private fun uploadCBTICourseWatchLog(cbtiCourseId: Int, videoId: String, hexVideoProgress: String, endpoint: Int, cbtiCourseWatchLength: Int) {
        if (!NetUtil.isHaveNet(App.getAppContext())) {
            uploadCBTICourseWatchLog(cbtiCourseId, videoId, hexVideoProgress, endpoint, cbtiCourseWatchLength)
            return
        }

        val call = AppManager.getHttpService().uploadCBTICourseWatchLengthLogs(cbtiCourseId, videoId, hexVideoProgress.toUpperCase(), endpoint, cbtiCourseWatchLength)
        call.enqueue(object : BaseSdResponseCallback<CoursePlayLog>() {
            override fun onFailure(errorResponse: ErrorResponse) {
            }

            override fun onSuccess(response: CoursePlayLog?) {
            }

        })
    }
}