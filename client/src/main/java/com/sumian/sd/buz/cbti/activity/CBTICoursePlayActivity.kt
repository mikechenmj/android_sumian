@file:Suppress("DEPRECATION")

package com.sumian.sd.buz.cbti.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.image.ImageLoader
import com.sumian.sd.BuildConfig
import com.sumian.sd.R
import com.sumian.sd.base.SdBaseActivity
import com.sumian.sd.buz.cbti.bean.Course
import com.sumian.sd.buz.cbti.bean.CoursePlayAuth
import com.sumian.sd.buz.cbti.bean.CoursePlayLog
import com.sumian.sd.buz.cbti.dialog.CBTIQuestionDialog
import com.sumian.sd.buz.cbti.event.CBTIProgressChangeEvent
import com.sumian.sd.buz.cbti.presenter.CBTICoursePlayAuthPresenter
import com.sumian.sd.buz.cbti.sheet.CBTICourseListBottomSheet
import com.sumian.sd.buz.cbti.video.NiceVideoPlayerManager
import com.sumian.sd.buz.cbti.video.NiceVideoView
import com.sumian.sd.buz.cbti.video.OnVideoViewEvent
import com.sumian.sd.buz.cbti.video.TxVideoPlayerController
import com.sumian.sd.common.utils.EventBusUtil
import com.sumian.sd.widget.TitleBar
import com.sumian.sd.widget.dialog.SumianAlertDialog
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
class CBTICoursePlayActivity : SdBaseActivity<CBTICoursePlayAuthPresenter>(), View.OnClickListener, TitleBar.OnBackClickListener, CBTICourseListBottomSheet.OnCBTILessonListCallback, OnVideoViewEvent, TxVideoPlayerController.OnControllerCallback {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBusUtil.postStickyEvent(CBTIProgressChangeEvent())
    }

    companion object {
        private val TAG = CBTICoursePlayActivity::class.java.simpleName

        private const val EXTRA_CBTI_COURSE = "com.sumian.sleepdoctor.extras.cbti.course"
        private const val EXTRA_SELECT_POSITION = "com.sumian.sleepdoctor.extras.select.position"

        @JvmStatic
        fun show(context: Context, course: Course, position: Int) {
            val extras = Bundle().apply {
                putParcelable(EXTRA_CBTI_COURSE, course)
                putInt(EXTRA_SELECT_POSITION, position)
            }
            ActivityUtils.startActivity(extras, CBTICoursePlayActivity::class.java)
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
        this.mViewModel = CBTICoursePlayAuthPresenter.init(this)
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
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
        this.mViewModel.getCBTIPlayAuthInfo(mCurrentCourse?.id!!)
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

    fun onGetCBTIPlayAuthSuccess(coursePlayAuth: CoursePlayAuth) {
        if (mIsSelect) {
            mIsSelect = false
        }
        updateView(coursePlayAuth)
    }

    fun onGetCBTIPlayAuthFailed(error: String) {
        ToastUtils.showShort(error)
    }

    fun onUploadLessonLogSuccess(coursePlayLog: CoursePlayLog) {

    }

    fun onUploadLessonLogFailed(error: String) {
        //onGetCBTIPlayAuthFailed(error)
    }

    fun onUploadCBTIQuestionnairesSuccess(coursePlayAuth: CoursePlayAuth) {
        mCBTIQuestionDialog?.updateQuestionResult()
    }

    fun onUploadCBTIQuestionnairesFailed(error: String) {
        ToastUtils.showShort(error)
    }

    override fun onSelectLesson(position: Int, course: Course): Boolean {
        uploadCBTICourseWatchLog(mCurrentCourse?.id, mCurrentCourse?.video_id)
        this.mCurrentPosition = position
        this.mIsSelect = true
        this.mViewModel.getCBTIPlayAuthInfo(course.id)
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
            mViewModel.calculatePlayFrame(it, mCurrentCourse?.id!!, currentFrame, oldFrame, totalFrame)
        }
        // PlayLog.e(TAG, "finalFrame=$hexPlayFrame   fl=$fl")
    }

    override fun showExtraContent() {
        CBTIExerciseWebActivity.show(this, mCurrentCourse?.id!!)
    }

    override fun showPracticeDialog() {
        SumianAlertDialog(this).setTitle(R.string.practice_dialog_title)
                .setMessage(getString(R.string.dialog_finish_practice_msg))
                .setRightBtn(R.string.good) {
                    this.mViewModel.getCBTIPlayAuthInfo(mCurrentCourse?.id!!)
                }.show()
    }

    fun onGetCBTINextPlayAuthSuccess(coursePlayAuth: CoursePlayAuth) {
        uploadCBTICourseWatchLog(mCurrentCourse?.id, mCurrentCourse?.video_id)
        mCurrentPosition += 1
        updateView(coursePlayAuth)
    }

    fun onGetCBTINextPlayAuthFailed(error: String) {
        mCurrentCourse = mCoursePlayAuth?.courses?.get(mCurrentPosition)
        uploadCBTICourseWatchLog(mCurrentCourse?.id, mCurrentCourse?.video_id)
        showPracticeDialog()
    }

    override fun onPlayNext() {
        if (mCurrentPosition < mCoursePlayAuth?.courses?.size!! - 1) {
            val nextPosition = mCurrentPosition + 1
            val nextCourse = mCoursePlayAuth?.courses?.get(nextPosition)
            nextCourse?.let {
                mViewModel.playNextCBTIVideo(it.id)
            }
        }
    }

    override fun onPlayRetry() {
        //遇到错误后,尝试重新播放
        this.mViewModel.getCBTIPlayAuthInfo(mCurrentCourse?.id!!)
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
        uploadCBTICourseWatchLog(mCurrentCourse?.id, mCurrentCourse?.video_id)
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
                ImageLoader.loadImage(coursePlayAuth.banner, imageView(), R.mipmap.ic_img_cbti_banner, R.mipmap.ic_img_cbti_banner)
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
                            mViewModel?.uploadCBTIQuestionnaires(mCurrentCourse!!.id, position)
                        }

                        override fun dismissQuestionDialog(): Boolean {
                            aliyun_player.start()
                            uploadCBTICourseWatchLog(mCurrentCourse?.id, mCurrentCourse?.video_id)
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
            uploadCBTICourseWatchLog(mCurrentCourse?.id, mCurrentCourse?.video_id)
        }
    }

    private fun formatWebViewString(summaryRtf: String?, webView: SWebView) {
        summaryRtf?.let {
            webView.loadDataWithBaseURL(null, it, "text/html", "utf-8", null)
        }
    }

    private fun uploadCBTICourseWatchLog(courseId: Int?, videoId: String?) {
        videoId?.let {
            mViewModel.uploadCBTICourseWatchLog(courseId ?: 0, it)
        }
    }

    fun onBegin() {

    }

    fun onFinish() {

    }
}