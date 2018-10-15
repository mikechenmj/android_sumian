package com.sumian.sd.service.cbti.presenter

import android.util.Log
import com.alibaba.fastjson.JSON
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBasePresenter.mCalls
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.service.cbti.bean.CoursePlayAuth
import com.sumian.sd.service.cbti.bean.CoursePlayLog
import com.sumian.sd.service.cbti.contract.CBTIWeekPlayContract
import java.util.regex.Pattern

/**
 * Created by dq
 *
 * on 2018/7/16
 *
 * desc:
 */
class CBTICoursePlayAuthPresenter(view: CBTIWeekPlayContract.View) : CBTIWeekPlayContract.Presenter {

    private var mView: CBTIWeekPlayContract.View? = null

    private val TAG = CBTICoursePlayAuthPresenter::class.java.simpleName

    private val mBrowseFrame: StringBuilder by lazy {
        StringBuilder()
    }

    init {
        view.setPresenter(this)
        this.mView = view
    }

    companion object {

        fun init(view: CBTIWeekPlayContract.View): CBTIWeekPlayContract.Presenter {
            return CBTICoursePlayAuthPresenter(view)
        }
    }

    override fun getCBTIPlayAuthInfo(courseId: Int) {

        mView?.onBegin()

        val call = AppManager.getSdHttpService().getCBTIPLayAuth(id = courseId)
        mCalls.add(call)
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

    override fun uploadCBTIVideoLog(videoId: String, courseId: Int, videoProgress: String, endpoint: Int) {

        mView?.onBegin()

        val call = AppManager.getSdHttpService().uploadCBTICourseLogs(courseId, videoId, videoProgress.toUpperCase(), endpoint)
        mCalls.add(call)
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

    override fun calculatePlayFrame(videoId: String, currentCourseId: Int, currentFrame: Long, oldFrame: Long, totalFrame: Long) {

        if (currentFrame.toInt() == 0) {
            mBrowseFrame.delete(0, mBrowseFrame.length)
        }

        val jumpFrame = currentFrame - oldFrame
        if (jumpFrame > 1) {//补0,表示跳过了jumpFrame,未观看该帧数
            for (i in 0 until jumpFrame) {
                mBrowseFrame.append("0")
            }
        } else {
            mBrowseFrame.append("1")
        }
        //  PlayLog.e(TAG, "tmpFrame=$mBrowseFrame")

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

    override fun playNextCBTIVideo(courseId: Int) {

        mView?.onBegin()

        val call = AppManager.getSdHttpService().getCBTIPLayAuth(id = courseId)
        mCalls.add(call)
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

    override fun uploadCBTIQuestionnaires(courseId: Int, position: Int) {

        mView?.onBegin()

        val call = AppManager.getSdHttpService().uploadCBTIVideoQuestionnaires(courseId, JSON.toJSONString(position))
        mCalls.add(call)
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