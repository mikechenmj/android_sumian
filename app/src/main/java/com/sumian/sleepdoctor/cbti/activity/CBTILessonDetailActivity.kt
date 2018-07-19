package com.sumian.sleepdoctor.cbti.activity

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import com.sumian.common.utils.ImageLoader
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseActivity
import com.sumian.sleepdoctor.cbti.bean.Lesson
import com.sumian.sleepdoctor.cbti.bean.LessonDetail
import com.sumian.sleepdoctor.cbti.bean.LessonLog
import com.sumian.sleepdoctor.cbti.contract.CBTIWeekLessonDetailContract
import com.sumian.sleepdoctor.cbti.presenter.CBTIWeekLessonDetailPresenter
import com.sumian.sleepdoctor.cbti.sheet.CBTILessonListBottomSheet
import com.sumian.sleepdoctor.widget.TitleBar
import com.xiao.nicevideoplayer.NiceVideoPlayer
import com.xiao.nicevideoplayer.NiceVideoPlayerManager
import com.xiao.nicevideoplayer.OnVideoViewEvent
import com.xiao.nicevideoplayer.TxVideoPlayerController
import kotlinx.android.synthetic.main.activity_main_cbti_lesson_detail_center.*
import java.util.regex.Pattern


/**
 * Created by sm
 *
 * on 2018/7/10
 *
 * desc:CBTI 一个课时详情中心,包含播放视频,课程列表,以及课程总结等模块
 *
 */
class CBTILessonDetailActivity : BaseActivity<CBTIWeekLessonDetailContract.Presenter>(), View.OnClickListener, TitleBar.OnBackClickListener, CBTIWeekLessonDetailContract.View, CBTILessonListBottomSheet.OnCBTILessonListCallback, OnVideoViewEvent {

    private val TAG = CBTILessonDetailActivity::class.java.simpleName

    private var mId: Int = 0

    private var mLessonDetail: LessonDetail? = null

    private var mCurrentLesson: Lesson? = null

    private val mBrowseFrame: StringBuilder by lazy {
        StringBuilder()
    }

    private val mController: TxVideoPlayerController by lazy {
        TxVideoPlayerController(this)
    }

    companion object {

        private const val EXTRA_CBTI_LESSON_ID = "com.sumian.sleepdoctor.extras.cbti.lesson.id"

        fun show(context: Context, id: Int) {

            val extras = Bundle().apply {
                putInt(EXTRA_CBTI_LESSON_ID, id)
            }
            show(context, CBTILessonDetailActivity::class.java, extras)
        }
    }

    override fun initBundle(bundle: Bundle?): Boolean {
        bundle?.let {
            this.mId = it.getInt(EXTRA_CBTI_LESSON_ID, 0)
        }

        return super.initBundle(bundle)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_cbti_lesson_detail_center
    }

    override fun initPresenter() {
        super.initPresenter()
        CBTIWeekLessonDetailPresenter.init(this)
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        title_bar.setOnBackClickListener(this)
        tv_lesson_list.setOnClickListener(this)
        aliyun_player.setOnVideoViewEvent(this)
        aliyun_player.apply {
            setPlayerType(NiceVideoPlayer.TYPE_ALIYUN)
            //setUp(null, null)
            setController(mController)
        }
    }

    override fun setPresenter(presenter: CBTIWeekLessonDetailContract.Presenter?) {
        //    super.setPresenter(presenter)
        this.mPresenter = presenter
    }

    override fun initData() {
        super.initData()
        this.mPresenter.getCBTIDetailInfo(mId)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_lesson_list -> {
                mLessonDetail?.let {
                    CBTILessonListBottomSheet.show(supportFragmentManager, it.courses, this)
                }
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

    override fun onGetCBTIDetailSuccess(lessonDetail: LessonDetail) {

        mLessonDetail = lessonDetail
        mCurrentLesson = lessonDetail.courses[0]

        title_bar.setTitle(mCurrentLesson?.title)

        tv_summary.text = lessonDetail.summary

        mController.setTitle(mCurrentLesson?.title)

        val titles = ArrayList<String>()
        lessonDetail.courses.forEach {
            titles.add(it.title)
        }

        mController.setLessonList(titles)

        ImageLoader.loadImage(this, mController.imageView(), lessonDetail.banner, R.mipmap.ic_img_cbti_banner, R.mipmap.ic_img_cbti_banner)
        aliyun_player?.apply {
            setPlayerType(NiceVideoPlayer.TYPE_ALIYUN)
            setSourceData(lessonDetail.meta.video_id, lessonDetail.meta.play_auth)
        }

    }

    override fun onGetCBTIDetailFailed(error: String) {
        showCenterToast(error)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        Log.e(TAG, "onConfigurationChanged-------->")
    }

    override fun onUploadLessonLogSuccess(lessonLog: LessonLog) {
        Log.e(TAG, lessonLog.toString())
    }

    override fun onUploadLessonLogFailed(error: String) {
        onGetCBTIDetailFailed(error)
    }

    override fun onSelectLesson(position: Int, lesson: Lesson): Boolean {
        this.mCurrentLesson = lesson
        this.mPresenter.getCBTIDetailInfo(lesson.id)
        return true
    }

    override fun onPlayReadyCallback() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPauseCallback() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlayPositionCallback(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onResetPlayCallback() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlayErrorCallback() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFrameChangeCallback(currentFrame: Long, oldFrame: Long, totalFrame: Long) {
        Log.e(TAG, "currentFrame=$currentFrame    oldFrame=$oldFrame   totalFrame=$totalFrame")

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
        //  Log.e(TAG, "tmpFrame=$mBrowseFrame")

        val hexPlayFrame = mBrowseFrame.toString().toBigInteger(2).toString(16)

        val appearNumber = appearNumber(mBrowseFrame.toString(), "1")

        val fl = appearNumber * 1.0f / totalFrame

        if (fl >= 0.7f) {
            Log.e(TAG, "看超过了70%")
            mPresenter.uploadCBTIVideoLog(mCurrentLesson?.id!!, hexPlayFrame, currentFrame.toInt())
        }
        // Log.e(TAG, "finalFrame=$hexPlayFrame   fl=$fl")
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