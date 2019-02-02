package com.sumian.sd.buz.cbti.presenter

import android.util.Log
import com.alibaba.fastjson.JSON
import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.cbti.bean.CoursePlayAuth
import com.sumian.sd.buz.cbti.bean.CoursePlayLog
import com.sumian.sd.buz.cbti.contract.CBTIWeekPlayContract
import com.sumian.sd.buz.cbti.job.CBTICourseWatchLogJobService
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import java.util.regex.Pattern

/**
 * Created by dq
 *
 * on 2018/7/16
 *
 * desc:
 */
class CBTICoursePlayAuthPresenter(view: CBTIWeekPlayContract.View) : BaseViewModel() {

    private var mView: CBTIWeekPlayContract.View? = null

    private var mCurrentCourseId = -1
    private var mCurrentFrame: Long = 0
    private val mBrowseFrame: StringBuilder by lazy {
        StringBuilder()
    }

    init {
        view.setPresenter(this)
        this.mView = view
    }

    companion object {

        private val TAG = CBTICoursePlayAuthPresenter::class.java.simpleName

        @JvmStatic
        fun init(view: CBTIWeekPlayContract.View): CBTICoursePlayAuthPresenter {
            return CBTICoursePlayAuthPresenter(view)
        }
    }

    fun getCBTIPlayAuthInfo(courseId: Int) {

        mView?.onBegin()

        val call = AppManager.getSdHttpService().getCBTIPLayAuth(id = courseId)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<CoursePlayAuth>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onGetCBTIPlayAuthFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: CoursePlayAuth?) {
                response?.let {
                    mView?.onGetCBTIPlayAuthSuccess(response)
                }
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }

        })
    }

    fun uploadCBTIVideoLog(videoId: String, courseId: Int, videoProgress: String, endpoint: Int) {

        mView?.onBegin()

        val call = AppManager.getSdHttpService().uploadCBTICourseLogs(courseId, videoId, videoProgress.toUpperCase(), endpoint)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<CoursePlayLog>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onUploadLessonLogFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: CoursePlayLog?) {
                response?.let {
                    mView?.onUploadLessonLogSuccess(response)
                }
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }

        })
    }

    fun calculatePlayFrame(videoId: String, currentCourseId: Int, currentFrame: Long, oldFrame: Long, totalFrame: Long) {
        this.mCurrentFrame = currentFrame
        if (mCurrentCourseId != currentCourseId) {//不一致说明是第一次播放，或者不是同一个视频,需要重新初始化
            mBrowseFrame.clear()
            for (i in 0 until totalFrame) {
                mBrowseFrame.append("0")
            }
            mCurrentCourseId = currentCourseId
        }

        if (currentFrame.toInt() >= 0 && currentFrame < totalFrame) {
            if (mBrowseFrame[currentFrame.toInt()] == '0') {
                mBrowseFrame[currentFrame.toInt()] = '1'
            }
        }

        val jumpFrame = currentFrame - oldFrame
        val hexPlayFrame = mBrowseFrame.toString().toBigInteger(2).toString(16)
        val appearNumber = appearNumber(mBrowseFrame.toString(), "1")
        val fl = appearNumber * 1.0f / totalFrame
        if (fl == 0.7f) {
            Log.e(TAG, "看超过了70%")
        }
        // playFrame=0.7f/jump Frame/ 60frame/s /play finished 都上传一次
        if (currentFrame.toInt() <= 1 || (fl > 0.68f && fl <= 0.70f) || jumpFrame > 1 || currentFrame.toInt() % 60 == 0 || currentFrame == totalFrame) {
            uploadCBTIVideoLog(videoId, currentCourseId, hexPlayFrame, currentFrame.toInt())
        }
    }

    fun playNextCBTIVideo(courseId: Int) {
        mView?.onBegin()
        val call = AppManager.getSdHttpService().getCBTIPLayAuth(id = courseId)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<CoursePlayAuth>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onGetCBTINextPlayAuthFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: CoursePlayAuth?) {
                response?.let {
                    mView?.onGetCBTINextPlayAuthSuccess(response)
                }
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }
        })
    }

    fun uploadCBTIQuestionnaires(courseId: Int, position: Int) {
        mView?.onBegin()
        val call = AppManager.getSdHttpService().uploadCBTIVideoQuestionnaires(courseId, JSON.toJSONString(position))
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<CoursePlayAuth>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onUploadCBTIQuestionnairesFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: CoursePlayAuth?) {
                response?.let {
                    mView?.onUploadCBTIQuestionnairesSuccess(it)
                }
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }
        })
    }

    fun uploadCBTICourseWatchLog(courseId: Int, videoId: String) {
        val hexWatchLength = appearNumber(mBrowseFrame.toString(), "1")
        if (hexWatchLength <= 0) {
            return
        }
        val hexPlayFrameProgress = mBrowseFrame.toString().toBigInteger(2).toString(16)
        if (mCurrentCourseId != courseId) {
            mCurrentCourseId = courseId
        }
        CBTICourseWatchLogJobService.execute(mCurrentCourseId, videoId, hexPlayFrameProgress, mCurrentFrame.toInt(), hexWatchLength)
    }

    /**
     * 获取指定字符串出现的次数
     *
     * @param srcText 源字符串
     * @param findText 要查找的字符串
     * @return
     */
    private fun appearNumber(srcText: String, findText: String): Int {
        var count = 0
        val p = Pattern.compile(findText)
        val m = p.matcher(srcText)
        while (m.find()) {
            count++
        }
        return count
    }
}