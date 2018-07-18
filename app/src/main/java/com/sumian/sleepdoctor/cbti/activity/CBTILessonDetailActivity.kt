package com.sumian.sleepdoctor.cbti.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseActivity
import com.sumian.sleepdoctor.cbti.bean.LessonDetail
import com.sumian.sleepdoctor.cbti.contract.CBTIWeekLessonDetailContract
import com.sumian.sleepdoctor.cbti.presenter.CBTIWeekLessonDetailPresenter
import com.sumian.sleepdoctor.cbti.sheet.CBTILessonListBottomSheet
import com.sumian.sleepdoctor.widget.TitleBar
import com.xiao.nicevideoplayer.NiceVideoPlayer
import com.xiao.nicevideoplayer.NiceVideoPlayerManager
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
class CBTILessonDetailActivity : BaseActivity<CBTIWeekLessonDetailContract.Presenter>(), View.OnClickListener, TitleBar.OnBackClickListener, CBTIWeekLessonDetailContract.View {

    private val TAG = CBTILessonDetailActivity::class.java.simpleName

    private var mId: Int = 0

    private var mLessonDetail: LessonDetail? = null

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
        cbti_lesson_list_view.setShowLessonListBottomSheet(this)
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
            R.id.iv_lesson_list -> {
                mLessonDetail?.let {
                    CBTILessonListBottomSheet.show(supportFragmentManager, it.courses)
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

        title_bar.setTitle(lessonDetail.courses[0].title)

        tv_summary.text = lessonDetail.summary
        cbti_lesson_list_view.addAllItem(lessonDetail.courses)

        mController.setTitle(lessonDetail.courses[0].title)

        val titles = ArrayList<String>()
        lessonDetail.courses.forEach {
            titles.add(it.title)
        }

        mController.setLessonList(titles)
        Glide.with(this)
                .load(lessonDetail.banner).apply(object : RequestOptions() {

                }.placeholder(R.mipmap.ic_img_cbti_banner))
                .into(mController.imageView())
        aliyun_player?.apply {
            setPlayerType(NiceVideoPlayer.TYPE_ALIYUN)
            setSourceData(lessonDetail.meta.video_id, lessonDetail.meta.play_auth)
        }

    }

    override fun onGetCBTIDetailFailed(error: String) {
        showCenterToast(error)
    }

}