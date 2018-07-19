package com.sumian.sleepdoctor.cbti.activity

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import com.sumian.common.utils.ImageLoader
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseActivity
import com.sumian.sleepdoctor.cbti.bean.Course
import com.sumian.sleepdoctor.cbti.bean.CoursePlayAuth
import com.sumian.sleepdoctor.cbti.bean.CoursePlayLog
import com.sumian.sleepdoctor.cbti.contract.CBTIWeekPlayContract
import com.sumian.sleepdoctor.cbti.presenter.CBTICoursePlayAuthPresenter
import com.sumian.sleepdoctor.cbti.sheet.CBTICourseListBottomSheet
import com.sumian.sleepdoctor.event.CBTIProgressChangeEvent
import com.sumian.sleepdoctor.event.EventBusUtil
import com.sumian.sleepdoctor.h5.H5Uri
import com.sumian.sleepdoctor.widget.TitleBar
import com.sumian.sleepdoctor.widget.dialog.SumianWebDialog
import com.xiao.nicevideoplayer.NiceVideoPlayerManager
import com.xiao.nicevideoplayer.NiceVideoView
import com.xiao.nicevideoplayer.OnVideoViewEvent
import com.xiao.nicevideoplayer.TxVideoPlayerController
import kotlinx.android.synthetic.main.activity_main_cbti_lesson_detail_center.*


/**
 * Created by sm
 *
 * on 2018/7/10
 *
 * desc:CBTI 一个课时详情中心,包含播放视频,课程列表,以及课程总结等模块
 *
 */
class CBTICoursePlayActivity : BaseActivity<CBTIWeekPlayContract.Presenter>(), View.OnClickListener, TitleBar.OnBackClickListener, CBTIWeekPlayContract.View, CBTICourseListBottomSheet.OnCBTILessonListCallback, OnVideoViewEvent {

    private val TAG = CBTICoursePlayActivity::class.java.simpleName

    private var mCourse: Course? = null

    private var mCoursePlayAuth: CoursePlayAuth? = null

    private var mCurrentCourse: Course? = null
    private var mCurrentPosition = 0

    private val mController: TxVideoPlayerController by lazy {
        TxVideoPlayerController(this)
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
        EventBusUtil.postSticky(CBTIProgressChangeEvent())
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_cbti_lesson_detail_center
    }

    override fun initPresenter() {
        super.initPresenter()
        CBTICoursePlayAuthPresenter.init(this)
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        title_bar.setOnBackClickListener(this)
        tv_lesson_list.setOnClickListener(this)
        nav_tab_lesson_practice.setOnClickListener(this)
        nav_tab_lesson_review_last_week.setOnClickListener(this)
        aliyun_player.setOnVideoViewEvent(this)
        aliyun_player.apply {
            setPlayerType(NiceVideoView.TYPE_ALIYUN)
            //setUp(null, null)
            setController(mController)
        }
        nav_tab_lesson_review_last_week.setOnClickListener {
            SumianWebDialog.createWithPartUrl(H5Uri.CBTI_WEEK_REVIEW.replace("{last_chapter_summary}", mCoursePlayAuth!!.summary!!)).show(supportFragmentManager)
        }
    }

    override fun setPresenter(presenter: CBTIWeekPlayContract.Presenter?) {
        //    super.setPresenter(presenter)
        this.mPresenter = presenter
    }

    override fun initData() {
        super.initData()
        this.mPresenter.getCBTIDetailInfo(mCourse?.cbti_chapter_id!!)
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

    override fun onGetCBTIDetailSuccess(coursePlayAuth: CoursePlayAuth) {

        mCoursePlayAuth = coursePlayAuth
        mCurrentCourse = coursePlayAuth.courses[mCurrentPosition]

        title_bar.setTitle(mCurrentCourse?.title)

        tv_summary.text = coursePlayAuth.summary

        mController.setTitle(mCurrentCourse?.title)

        val titles = ArrayList<String>()
        coursePlayAuth.courses.forEach {
            titles.add(it.title)
        }

        mController.setLessonList(titles)

        ImageLoader.loadImage(this, mController.imageView(), coursePlayAuth.banner, R.mipmap.ic_img_cbti_banner, R.mipmap.ic_img_cbti_banner)
        aliyun_player?.apply {
            setPlayerType(NiceVideoView.TYPE_ALIYUN)
            setSourceData(coursePlayAuth.meta.video_id, coursePlayAuth.meta.play_auth)
        }

        nav_tab_lesson_review_last_week.visibility = if (coursePlayAuth.summary != null) View.VISIBLE else View.GONE
        v_divider.visibility = if (coursePlayAuth.summary != null) View.VISIBLE else View.GONE
    }

    override fun onGetCBTIDetailFailed(error: String) {
        showCenterToast(error)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        Log.e(TAG, "onConfigurationChanged-------->")
    }

    override fun onUploadLessonLogSuccess(coursePlayLog: CoursePlayLog) {

    }

    override fun onUploadLessonLogFailed(error: String) {
        onGetCBTIDetailFailed(error)
    }

    override fun onSelectLesson(position: Int, course: Course): Boolean {
        this.mCurrentCourse = course
        this.mCurrentPosition = position
        this.mPresenter.getCBTIDetailInfo(course.id)
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
        this.mPresenter.getCBTIDetailInfo(mCurrentCourse?.id!!)
    }

    override fun onFrameChangeCallback(currentFrame: Long, oldFrame: Long, totalFrame: Long) {
        Log.e(TAG, "currentFrame=$currentFrame    oldFrame=$oldFrame   totalFrame=$totalFrame")

        mPresenter.calculatePlayFrame(mCurrentCourse?.id!!, currentFrame, oldFrame, totalFrame)
        // Log.e(TAG, "finalFrame=$hexPlayFrame   fl=$fl")
    }

    override fun showExtraContent() {
        CBTIExerciseWebActivity.show(this, mCurrentCourse?.id!!)
    }
}