package com.sumian.sleepdoctor.cbti.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.View
import com.sumian.common.image.ImageLoader
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.SdBaseActivity
import com.sumian.sleepdoctor.cbti.bean.Course
import com.sumian.sleepdoctor.cbti.bean.CoursePlayAuth
import com.sumian.sleepdoctor.cbti.bean.CoursePlayLog
import com.sumian.sleepdoctor.cbti.contract.CBTIWeekPlayContract
import com.sumian.sleepdoctor.cbti.presenter.CBTICoursePlayAuthPresenter
import com.sumian.sleepdoctor.cbti.sheet.CBTICourseListBottomSheet
import com.sumian.sleepdoctor.cbti.video.NiceVideoPlayerManager
import com.sumian.sleepdoctor.cbti.video.NiceVideoView
import com.sumian.sleepdoctor.cbti.video.OnVideoViewEvent
import com.sumian.sleepdoctor.cbti.video.TxVideoPlayerController
import com.sumian.sleepdoctor.event.CBTIProgressChangeEvent
import com.sumian.sleepdoctor.event.EventBusUtil
import com.sumian.sleepdoctor.h5.H5Uri
import com.sumian.sleepdoctor.widget.TitleBar
import com.sumian.sleepdoctor.widget.dialog.SumianAlertDialog
import com.sumian.sleepdoctor.widget.dialog.SumianWebDialog
import kotlinx.android.synthetic.main.activity_main_cbti_lesson_detail_center.*


/**
 * Created by sm
 *
 * on 2018/7/10
 *
 * desc:CBTI 一个课时详情中心,包含播放视频,课程列表,以及课程总结等模块
 *
 */
class CBTICoursePlayActivity : SdBaseActivity<CBTIWeekPlayContract.Presenter>(), View.OnClickListener, TitleBar.OnBackClickListener, CBTIWeekPlayContract.View, CBTICourseListBottomSheet.OnCBTILessonListCallback, OnVideoViewEvent, TxVideoPlayerController.OnControllerCallback {

    private val TAG = CBTICoursePlayActivity::class.java.simpleName

    private var mCourse: Course? = null

    private var mCoursePlayAuth: CoursePlayAuth? = null

    private var mCurrentCourse: Course? = null
    private var mCurrentPosition = 0

    private val mController: TxVideoPlayerController by lazy {
        val controller = TxVideoPlayerController(this).setup(this@CBTICoursePlayActivity)
        controller.setControllerCallback(this@CBTICoursePlayActivity)
        controller
    }

    companion object {

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

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        EventBusUtil.postStickyEvent(CBTIProgressChangeEvent())
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_cbti_lesson_detail_center
    }

    override fun initPresenter() {
        super.initPresenter()
        this.mPresenter = CBTICoursePlayAuthPresenter.init(this)
    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "finished") {
                onPlayNext()
            }
        }
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        title_bar.setOnBackClickListener(this)
        tv_lesson_list.setOnClickListener(this)
        nav_tab_lesson_practice.setOnClickListener(this)
        nav_tab_lesson_review_last_week.setOnClickListener(this)
        aliyun_player.apply {
            setPlayerType(NiceVideoView.TYPE_ALIYUN)
            setOnVideoViewEvent(this@CBTICoursePlayActivity)
            setController(mController)
        }
        nav_tab_lesson_review_last_week.setOnClickListener {
            SumianWebDialog.createWithPartUrl(H5Uri.CBTI_WEEK_REVIEW.replace("{last_chapter_summary}", mCoursePlayAuth!!.summary!!)).show(supportFragmentManager)
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, IntentFilter().apply {
            addAction("finished")
        })
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
            R.id.nav_tab_lesson_review_last_week -> {

            }
        }
    }

    override fun onBack(v: View?) {
        finish()
    }

    override fun onRestart() {
        super.onRestart()
        NiceVideoPlayerManager.instance().resumeNiceVideoPlayer()
    }

    override fun onStop() {
        super.onStop()
        NiceVideoPlayerManager.instance().suspendNiceVideoPlayer()
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

        tv_summary.text = mCurrentCourse?.summary

        mController.run {

            setTitle(mCurrentCourse?.title)

            setPlayAuth(mCoursePlayAuth!!)

            setChapterId(this@CBTICoursePlayActivity, mCourse?.cbti_chapter_id!!, mCurrentPosition)

            ImageLoader.loadImage(coursePlayAuth.banner, imageView(), R.mipmap.ic_img_cbti_banner, R.mipmap.ic_img_cbti_banner)

        }

        aliyun_player.setSourceData(coursePlayAuth.meta.video_id, coursePlayAuth.meta.play_auth)

        if (mIsSelect) {
            mIsSelect = false
        }

        aliyun_player.start()

        if (coursePlayAuth.meta.is_pop_questionnaire) {
            Log.e(TAG, "有调查问卷" + coursePlayAuth.meta.questionnaire.toString())
        } else {
            Log.e(TAG, "无调查问卷")
        }

        nav_tab_lesson_practice.visibility = if (coursePlayAuth.isHavePractice()) View.VISIBLE else View.GONE

        v_divider.visibility = if (coursePlayAuth.last_chapter_summary != null && coursePlayAuth.meta.exercise_is_filled) View.VISIBLE else View.GONE

        nav_tab_lesson_review_last_week.visibility = if (coursePlayAuth.last_chapter_summary != null) View.VISIBLE else View.GONE

        lay_lesson_tips.visibility = if (coursePlayAuth.isHavePractice() || coursePlayAuth.last_chapter_summary != null || coursePlayAuth.meta.exercise_is_filled) View.VISIBLE else View.GONE
    }

    override fun onGetCBTIPlayAuthFailed(error: String) {
        showCenterToast(error)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        Log.e(TAG, "onConfigurationChanged-------->")
    }

    override fun onUploadLessonLogSuccess(coursePlayLog: CoursePlayLog) {

    }

    override fun onUploadLessonLogFailed(error: String) {
        onGetCBTIPlayAuthFailed(error)
    }

    private var mIsSelect: Boolean = false

    override fun onSelectLesson(position: Int, course: Course): Boolean {
        this.mCurrentCourse = course
        this.mCurrentPosition = position
        this.mIsSelect = true
        this.mPresenter.getCBTIPlayAuthInfo(course.id)
        return true
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
        this.mPresenter.getCBTIPlayAuthInfo(mCurrentCourse?.id!!)
    }

    override fun onFrameChangeCallback(currentFrame: Long, oldFrame: Long, totalFrame: Long) {
        //PlayLog.e(TAG, "currentFrame=$currentFrame    oldFrame=$oldFrame   totalFrame=$totalFrame")

        mPresenter.calculatePlayFrame(mCurrentCourse?.id!!, currentFrame, oldFrame, totalFrame)
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

        tv_summary.text = mCurrentCourse?.summary

        mController.run {

            setTitle(mCurrentCourse?.title)

            setPlayAuth(mCoursePlayAuth!!)

            setChapterId(this@CBTICoursePlayActivity, mCourse?.cbti_chapter_id!!, mCurrentPosition)

            if (!isDestroyed) {
                ImageLoader.loadImage(coursePlayAuth.banner, imageView(), R.mipmap.ic_img_cbti_banner, R.mipmap.ic_img_cbti_banner)
            }

        }

        aliyun_player.setSourceData(coursePlayAuth.meta.video_id, coursePlayAuth.meta.play_auth)

        aliyun_player.start()

        nav_tab_lesson_practice.visibility = if (coursePlayAuth.isHavePractice()) View.VISIBLE else View.GONE

        v_divider.visibility = if (coursePlayAuth.last_chapter_summary != null && coursePlayAuth.meta.exercise_is_filled) View.VISIBLE else View.GONE

        nav_tab_lesson_review_last_week.visibility = if (coursePlayAuth.last_chapter_summary != null) View.VISIBLE else View.GONE

        lay_lesson_tips.visibility = if (coursePlayAuth.isHavePractice() || coursePlayAuth.last_chapter_summary != null || coursePlayAuth.meta.exercise_is_filled) View.VISIBLE else View.GONE

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
}