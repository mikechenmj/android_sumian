@file:Suppress("DEPRECATION")

package com.sumian.sddoctor.service.cbti.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.image.ImageLoader
import com.sumian.common.widget.TitleBar
import com.sumian.sddoctor.BuildConfig
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.StatusBarHelper
import com.sumian.sddoctor.service.advisory.onlinereport.SdBaseActivity
import com.sumian.sddoctor.service.cbti.bean.Course
import com.sumian.sddoctor.service.cbti.bean.CoursePlayAuth
import com.sumian.sddoctor.service.cbti.bean.CoursePlayLog
import com.sumian.sddoctor.service.cbti.contract.CBTIWeekPlayContract
import com.sumian.sddoctor.service.cbti.dialog.CBTIQuestionDialog
import com.sumian.sddoctor.service.cbti.presenter.CBTICoursePlayAuthPresenter
import com.sumian.sddoctor.service.cbti.sheet.CBTICourseListBottomSheet
import com.sumian.sddoctor.service.cbti.video.NiceVideoPlayerManager
import com.sumian.sddoctor.service.cbti.video.NiceVideoView
import com.sumian.sddoctor.service.cbti.video.OnVideoViewEvent
import com.sumian.sddoctor.service.cbti.video.TxVideoPlayerController
import com.sumian.sddoctor.widget.SumianAlertDialog
import kotlinx.android.synthetic.main.activity_main_cbti_lesson_detail_center.*

@Suppress("REDUNDANT_LABEL_WARNING")
/**
 * Created by sm
 *
 * on 2018/7/10
 *
 * desc:CBTI 一个课时详情中心,包含播放视频,课程列表,以及课程总结等模块
 *
 */
class CBTICoursePlayActivity : SdBaseActivity<CBTIWeekPlayContract.Presenter>(), View.OnClickListener,
        TitleBar.OnBackClickListener, CBTIWeekPlayContract.View,
        CBTICourseListBottomSheet.OnCBTILessonListCallback, OnVideoViewEvent,
        TxVideoPlayerController.OnControllerCallback {

    private var mCourse: Course? = null
    private var mCoursePlayAuth: CoursePlayAuth? = null
    private var mCurrentCourse: Course? = null
    private var mCurrentPosition = 0
    private var mCBTIQuestionDialog: CBTIQuestionDialog? = null
    private var mCurrentQuestionPosition = 0
    private var mPendingRestart = false
    private var mIsSelect: Boolean = false

    private val mController: TxVideoPlayerController by lazy {
        val controller = TxVideoPlayerController(this).setup(this@CBTICoursePlayActivity)
        controller.setControllerCallback(this@CBTICoursePlayActivity)
        controller
    }

    companion object {

        private val TAG = CBTICoursePlayActivity::class.java.simpleName

        private const val EXTRA_CBTI_COURSE = "com.sumian.sleepdoctor.extras.cbti.course"
        private const val EXTRA_SELECT_POSITION = "com.sumian.sleepdoctor.extras.select.position"

        fun show(context: Context, course: Course, position: Int) {

            val extras = Bundle().apply {
                putParcelable(EXTRA_CBTI_COURSE, course)
                putInt(EXTRA_SELECT_POSITION, position)
            }
            show(context, CBTICoursePlayActivity::class.java, extras)
        }
    }

    override fun initBundle(bundle: Bundle?): Boolean {
        bundle?.let {
            this.mCourse = it.getParcelable(EXTRA_CBTI_COURSE)
            this.mCurrentCourse = mCourse
            this.mCurrentPosition = it.getInt(EXTRA_SELECT_POSITION, 0)
        }

        return super.initBundle(bundle)
    }

    override fun getLayoutId(): Int {
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        return R.layout.activity_main_cbti_lesson_detail_center
    }

    override fun initPresenter() {
        super.initPresenter()
        this.mPresenter = CBTICoursePlayAuthPresenter.init(this)
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        StatusBarHelper.initTitleBarUI(this, title_bar)
        title_bar.setOnBackClickListener(this)
        tv_lesson_list.setOnClickListener(this)
        //nav_tab_lesson_review_last_week.setOnClickListener(this)
        aliyun_player.apply {
            setPlayerType(NiceVideoView.TYPE_ALIYUN)
            setOnVideoViewEvent(this@CBTICoursePlayActivity)
            setController(mController)
        }
    }

    override fun initData() {
        super.initData()
        this.mPresenter.getCBTIPlayAuthInfo(mCurrentCourse?.id!!)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_lesson_list -> {
                mCoursePlayAuth?.let {
                    CBTICourseListBottomSheet.show(supportFragmentManager, mCourse?.cbti_chapter_id!!, mCurrentPosition, this)
                }
            }
            R.id.nav_tab_lesson_practice -> {
                mCurrentCourse?.let {
                    CBTIExerciseWebActivity.show(v.context, it.id)
                }
            }
        }
    }

    override fun onBack(v: View?) {
        finish()
    }

    override fun onRestart() {
        super.onRestart()
        autoPlay()
    }

    override fun onStop() {
        super.onStop()
        autoPause()
    }

    override fun onBackPressed() {
        if (NiceVideoPlayerManager.instance().onBackPressed()) {
            return
        }
        super.onBackPressed()
    }

    override fun onGetCBTIPlayAuthSuccess(coursePlayAuth: CoursePlayAuth) {
        if (mIsSelect) {
            mIsSelect = false
        }
        updateView(coursePlayAuth)
    }

    override fun onGetCBTIPlayAuthFailed(error: String) {
        showCenterToast(error)
    }

    override fun onUploadLessonLogSuccess(coursePlayLog: CoursePlayLog) {

    }

    override fun onUploadLessonLogFailed(error: String) {
        //onGetCBTIPlayAuthFailed(error)
    }

    override fun onUploadCBTIQuestionnairesSuccess(coursePlayAuth: CoursePlayAuth) {
        mCBTIQuestionDialog?.updateQuestionResult()
    }

    override fun onUploadCBTIQuestionnairesFailed(error: String) {
        showCenterToast(error)
    }

    override fun onSelectLesson(position: Int, course: Course): Boolean {
        uploadCBTICourseWatchLog()
        this.mCurrentCourse = course
        this.mCurrentPosition = position
        this.mIsSelect = true
        this.mPresenter.getCBTIPlayAuthInfo(course.id)
        return true
    }

    override fun onShow() {
        autoPause()
    }

    override fun onDismiss() {
        autoPlay()
    }

    override fun onPlayReadyCallback() {

    }

    override fun onPauseCallback() {

    }

    override fun onPlayPositionCallback(position: Int) {

    }

    override fun onResetPlayCallback() {
        // Log.e(TAG, "onResetPlayCallback: --------->")
    }

    override fun onRePlayCallbck() {
        aliyun_player.replay()
    }

    override fun onPlayErrorCallback() {
        // Log.e(TAG, "onPlayErrorCallback: -------->error")
    }

    override fun onFrameChangeCallback(currentFrame: Long, oldFrame: Long, totalFrame: Long) {
        //PlayLog.e(TAG, "currentFrame=$currentFrame    oldFrame=$oldFrame   totalFrame=$totalFrame")
        mCurrentCourse?.video_id?.let {
            mPresenter.calculatePlayFrame(it, mCurrentCourse?.id!!, currentFrame, oldFrame, totalFrame)
        }
        // PlayLog.e(TAG, "finalFrame=$hexPlayFrame   fl=$fl")
    }

    override fun showExtraContent() {
        CBTIExerciseWebActivity.show(this, mCurrentCourse?.id!!)
    }

    override fun showPracticeDialog() {
        SumianAlertDialog(this).setTitle(R.string.practice_dialog_title).setMessage(getString(R.string.dialog_finish_practice_msg)).setRightBtn(R.string.good) { aliyun_player.replay() }.show()
    }

    override fun onGetCBTINextPlayAuthSuccess(coursePlayAuth: CoursePlayAuth) {
        updateView(coursePlayAuth)
    }

    override fun onGetCBTINextPlayAuthFailed(error: String) {
        mCurrentPosition -= 1
        showPracticeDialog()
    }

    override fun onPlayNext() {
        uploadCBTICourseWatchLog()
        if (mCurrentPosition < mCoursePlayAuth?.courses?.size!! - 1) {
            mCurrentPosition += 1
            mCurrentCourse = mCoursePlayAuth?.courses?.get(mCurrentPosition)
            mCurrentCourse?.let {
                mPresenter.playNextCBTIVideo(it.id)
            }
        }
    }

    override fun onPlayRetry() {
        //遇到错误后,尝试重新播放
        this.mPresenter.getCBTIPlayAuthInfo(mCurrentCourse?.id!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0x01) {//有练习的练习完成,那么直接播放下一课时
                onPlayNext()
            }
        }
    }

    override fun onRelease() {
        uploadCBTICourseWatchLog()
        super.onRelease()
        aliyun_player.release()
    }

    private fun autoPlay() {
        if (mPendingRestart) {
            NiceVideoPlayerManager.instance().resumeNiceVideoPlayer()
        }
    }

    private fun autoPause() {
        mPendingRestart = aliyun_player.isPlaying || aliyun_player.isBufferingPlaying
        NiceVideoPlayerManager.instance().suspendNiceVideoPlayer()
    }

    private fun updateView(coursePlayAuth: CoursePlayAuth) {
        mCoursePlayAuth = coursePlayAuth
        mCurrentCourse = coursePlayAuth.courses[mCurrentPosition]
        mCurrentCourse?.video_id = coursePlayAuth.meta.video_id

        title_bar.setTitle(mCurrentCourse?.title)
        formatWebViewString(mCurrentCourse?.summary_rtf, tv_summary)
        mController.run {

            setTitle(mCurrentCourse?.title)

            setPlayAuth(mCoursePlayAuth!!)

            setChapterId(this@CBTICoursePlayActivity, mCourse?.cbti_chapter_id!!, mCurrentPosition)

            if (!isDestroyed) {
                ImageLoader.loadImage(coursePlayAuth.banner, imageView(), R.drawable.ic_img_cbti_banner,
                        R.drawable.ic_img_cbti_banner)
            }
        }

        aliyun_player.setSourceData(mCurrentCourse?.id
                ?: 0, coursePlayAuth.meta.video_id, coursePlayAuth.meta.play_auth)

        nav_tab_lesson_practice.visibility = if (coursePlayAuth.isHavePractice()) {
            nav_tab_lesson_practice.setOnClickListener(this)
            View.VISIBLE
        } else {
            nav_tab_lesson_practice.setOnClickListener(null)
            View.GONE
        }
        lay_lesson_tips.visibility = if (coursePlayAuth.isHavePractice() || coursePlayAuth.last_chapter_summary != null || coursePlayAuth.meta.exercise_is_filled) View.VISIBLE else View.GONE

        if (coursePlayAuth.meta.is_pop_questionnaire) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "有调查问卷" + coursePlayAuth.meta.questionnaire.toString())
            }
            mCBTIQuestionDialog = CBTIQuestionDialog(CBTICoursePlayActivity@ this)
                    .setOnSubmitQuestionCallback(object : CBTIQuestionDialog.OnSubmitQuestionCallback {

                        override fun submitQuestionCallback(position: Int) {
                            mCurrentQuestionPosition = position
                            mPresenter?.uploadCBTIQuestionnaires(mCurrentCourse!!.id, position)
                        }

                        override fun dismissQuestionDialog(): Boolean {
                            aliyun_player.start()
                            return true
                        }
                    }).setQuestionnaire(coursePlayAuth.meta.questionnaire[0])
            mCBTIQuestionDialog?.ownerActivity = this@CBTICoursePlayActivity
            mCBTIQuestionDialog?.show()
        } else {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "无调查问卷")
            }
            aliyun_player.start()
        }
    }

    private fun formatWebViewString(summaryRtf: String?, webView: SWebView) {
        summaryRtf?.let {
            webView.loadDataWithBaseURL(null, it, "text/html", "utf-8", null)
        }
    }

    private fun uploadCBTICourseWatchLog() {
        mCurrentCourse?.video_id?.let {
            mPresenter.uploadCBTICourseWatchLog(mCurrentCourse!!.id, it)
        }
    }
}