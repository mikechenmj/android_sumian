@file:Suppress("DEPRECATION")

package com.sumian.sd.service.cbti.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.sumian.common.image.ImageLoader
import com.sumian.sd.R
import com.sumian.sd.base.SdBaseActivity
import com.sumian.sd.event.CBTIProgressChangeEvent
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.service.cbti.bean.Course
import com.sumian.sd.service.cbti.bean.CoursePlayAuth
import com.sumian.sd.service.cbti.bean.CoursePlayLog
import com.sumian.sd.service.cbti.contract.CBTIWeekPlayContract
import com.sumian.sd.service.cbti.dialog.CBTIQuestionDialog
import com.sumian.sd.service.cbti.presenter.CBTICoursePlayAuthPresenter
import com.sumian.sd.service.cbti.sheet.CBTICourseListBottomSheet
import com.sumian.sd.service.cbti.video.NiceVideoPlayerManager
import com.sumian.sd.service.cbti.video.OnVideoViewEvent
import com.sumian.sd.service.cbti.video.TxVideoPlayerController
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
class CBTICoursePlayActivity : SdBaseActivity<CBTIWeekPlayContract.Presenter>(), View.OnClickListener, TitleBar.OnBackClickListener, CBTIWeekPlayContract.View, CBTICourseListBottomSheet.OnCBTILessonListCallback, OnVideoViewEvent, TxVideoPlayerController.OnControllerCallback {

    private var mCourse: Course? = null
    private var mCoursePlayAuth: CoursePlayAuth? = null
    private var mCurrentCourse: Course? = null
    private var mCurrentPosition = 0
    private var mCBTIQuestionDialog: CBTIQuestionDialog? = null
    private var mPendingRestart = false

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
        title_bar.setOnBackClickListener(this)
        tv_lesson_list.setOnClickListener(this)
        nav_tab_lesson_practice.setOnClickListener(this)
        //nav_tab_lesson_review_last_week.setOnClickListener(this)
        aliyun_player.apply {
            setPlayerType(com.sumian.sd.service.cbti.video.NiceVideoView.TYPE_ALIYUN)
            setOnVideoViewEvent(this@CBTICoursePlayActivity)
            setController(mController)
        }
    }

    override fun initData() {
        super.initData()
        this.mPresenter.getCBTIPlayAuthInfo(mCourse?.id!!)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_lesson_list -> {
                mCoursePlayAuth?.let {
                    CBTICourseListBottomSheet.show(supportFragmentManager, mCourse?.cbti_chapter_id!!, mCurrentPosition, this)
                }
            }
            R.id.nav_tab_lesson_practice -> {
                CBTIExerciseWebActivity.show(v.context, mCurrentCourse?.id!!)
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

        mCoursePlayAuth = coursePlayAuth
        mCurrentCourse = coursePlayAuth.courses[mCurrentPosition]

        title_bar.setTitle(mCurrentCourse?.title)

        tv_summary.loadData(mCurrentCourse?.summary_rtf, "text/html", "utf-8")

        mController.run {

            setTitle(mCurrentCourse?.title)

            setPlayAuth(mCoursePlayAuth!!)

            setChapterId(this@CBTICoursePlayActivity, mCourse?.cbti_chapter_id!!, mCurrentPosition)

            if (!isDestroyed) {
                ImageLoader.loadImage(coursePlayAuth.banner, imageView(), R.mipmap.ic_img_cbti_banner, R.mipmap.ic_img_cbti_banner)
            }
        }

        aliyun_player.setSourceData(coursePlayAuth.meta.video_id, coursePlayAuth.meta.play_auth)

        if (mIsSelect) {
            mIsSelect = false
        }

        nav_tab_lesson_practice.visibility = if (coursePlayAuth.isHavePractice()) View.VISIBLE else View.GONE

        //v_divider.visibility = if (coursePlayAuth.last_chapter_summary != null && coursePlayAuth.meta.exercise_is_filled) View.VISIBLE else View.GONE

        //nav_tab_lesson_review_last_week.visibility = if (coursePlayAuth.last_chapter_summary != null) View.VISIBLE else View.GONE

        lay_lesson_tips.visibility = if (coursePlayAuth.isHavePractice() || coursePlayAuth.last_chapter_summary != null || coursePlayAuth.meta.exercise_is_filled) View.VISIBLE else View.GONE

        if (coursePlayAuth.meta.is_pop_questionnaire) {
            Log.e(TAG, "有调查问卷" + coursePlayAuth.meta.questionnaire.toString())

            mCBTIQuestionDialog = CBTIQuestionDialog(CBTICoursePlayActivity@ this)
                    .setOnSubmitQuestionCallback(object : CBTIQuestionDialog.OnSubmitQuestionCallback {

                        override fun submitQuestionCallback(position: Int) {
                            // coursePlayAuth.courses.forEach {
                            //if (it.current_course) {
                            mPresenter?.uploadCBTIQuestionnaires(mCurrentCourse!!.id, position)
                            aliyun_player.start()
                            // return@forEach
                            //}
                            // }
                        }

                        override fun onShowQuestionDialog() {
                            autoPause()
                        }

                    }).setQuestionnaire(coursePlayAuth.meta.questionnaire[0])
            mCBTIQuestionDialog?.ownerActivity = this@CBTICoursePlayActivity
            mCBTIQuestionDialog?.show()
        } else {
            Log.e(TAG, "无调查问卷")
            aliyun_player.start()
        }
    }

    override fun onGetCBTIPlayAuthFailed(error: String) {
        showCenterToast(error)
    }

    override fun onUploadLessonLogSuccess(coursePlayLog: CoursePlayLog) {

    }

    override fun onUploadLessonLogFailed(error: String) {
        onGetCBTIPlayAuthFailed(error)
    }

    override fun onUploadCBTIQuestionnairesSuccess(coursePlayAuth: CoursePlayAuth) {
        if (mCBTIQuestionDialog?.isShowing!!) {
            mCBTIQuestionDialog?.cancel()
        }
    }

    override fun onUploadCBTIQuestionnairesFailed(error: String) {
        showCenterToast(error)
    }

    private var mIsSelect: Boolean = false

    override fun onSelectLesson(position: Int, course: Course): Boolean {
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

    }

    override fun onRePlayCallbck() {
        aliyun_player.replay()
    }

    override fun onPlayErrorCallback() {
        mCurrentCourse?.let {
            this.mPresenter.getCBTIPlayAuthInfo(it.id)
        }
    }

    override fun onFrameChangeCallback(currentFrame: Long, oldFrame: Long, totalFrame: Long) {
        //PlayLog.e(TAG, "currentFrame=$currentFrame    oldFrame=$oldFrame   totalFrame=$totalFrame")

        mPresenter.calculatePlayFrame(mCoursePlayAuth?.meta?.video_id!!, mCurrentCourse?.id!!, currentFrame, oldFrame, totalFrame)
        // PlayLog.e(TAG, "finalFrame=$hexPlayFrame   fl=$fl")
    }

    override fun showExtraContent() {
        CBTIExerciseWebActivity.show(this, mCurrentCourse?.id!!)
    }

    override fun showPracticeDialog() {
        SumianAlertDialog(this).setTitle(R.string.practice_dialog_title).setMessage("完成本节课程学习后自动解锁下节内容").setRightBtn(R.string.good) { aliyun_player.replay() }.show()
    }

    override fun onGetCBTINextPlayAuthSuccess(coursePlayAuth: CoursePlayAuth) {

        mCoursePlayAuth = coursePlayAuth
        mCurrentCourse = coursePlayAuth.courses[mCurrentPosition]

        title_bar.setTitle(mCurrentCourse?.title)

        tv_summary.loadData(mCurrentCourse?.summary_rtf, "text/html", "utf-8")

        mController.run {

            setTitle(mCurrentCourse?.title)

            setPlayAuth(mCoursePlayAuth!!)

            setChapterId(this@CBTICoursePlayActivity, mCourse?.cbti_chapter_id!!, mCurrentPosition)

            if (!isDestroyed) {
                ImageLoader.loadImage(coursePlayAuth.banner, imageView(), R.mipmap.ic_img_cbti_banner, R.mipmap.ic_img_cbti_banner)
            }

        }

        aliyun_player.setSourceData(coursePlayAuth.meta.video_id, coursePlayAuth.meta.play_auth)

        nav_tab_lesson_practice.visibility = if (coursePlayAuth.isHavePractice()) View.VISIBLE else View.GONE

        // v_divider.visibility = if (coursePlayAuth.last_chapter_summary != null && coursePlayAuth.meta.exercise_is_filled) View.VISIBLE else View.GONE

        // nav_tab_lesson_review_last_week.visibility = if (coursePlayAuth.last_chapter_summary != null) View.VISIBLE else View.GONE

        lay_lesson_tips.visibility = if (coursePlayAuth.isHavePractice() || coursePlayAuth.last_chapter_summary != null || coursePlayAuth.meta.exercise_is_filled) View.VISIBLE else View.GONE

        if (coursePlayAuth.meta.is_pop_questionnaire) {
            mCBTIQuestionDialog = CBTIQuestionDialog(CBTICoursePlayActivity@ this)
                    .setOnSubmitQuestionCallback(object : CBTIQuestionDialog.OnSubmitQuestionCallback {

                        override fun submitQuestionCallback(position: Int) {
                            // coursePlayAuth.courses.forEach {
                            // if (it.current_course) {
                            mPresenter?.uploadCBTIQuestionnaires(mCurrentCourse?.id!!, position)
                            aliyun_player.start()
                            //      return@forEach
                            // }
                            //  }
                        }

                        override fun onShowQuestionDialog() {
                            autoPause()
                        }

                    }).setQuestionnaire(coursePlayAuth.meta.questionnaire[0])
            mCBTIQuestionDialog?.ownerActivity = this@CBTICoursePlayActivity
            mCBTIQuestionDialog?.show()
        } else {
            Log.e(TAG, "无调查问卷")
            aliyun_player.start()
        }
    }

    override fun onGetCBTINextPlayAuthFailed(error: String) {
        mCurrentPosition -= 1
        showPracticeDialog()
    }

    override fun onPlayNext() {
        if (mCurrentPosition < mCoursePlayAuth?.courses?.size!! - 1) {
            mCurrentPosition += 1
            val course = mCoursePlayAuth?.courses?.get(mCurrentPosition)
            mPresenter.playNextCBTIVideo(course?.id!!)
        }
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
}