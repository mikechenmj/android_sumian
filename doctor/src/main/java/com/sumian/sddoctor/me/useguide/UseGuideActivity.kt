package com.sumian.sddoctor.me.useguide

import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.statistic.StatUtil
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.base.StatusBarHelper
import com.sumian.sddoctor.constants.StatConstants
import kotlinx.android.synthetic.main.activity_use_guide.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/13 16:16
 * desc   :
 * version: 1.0
 */
class UseGuideActivity : SddBaseActivity() {
    private val mVideoIds = intArrayOf(
            R.raw.add_patient,
            R.raw.patient_info,
            R.raw.open_service,
            R.raw.withdraw
    )

    private val mIndex by lazy {
        intent.getIntExtra(KEY_INDEX, 0)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_use_guide
    }

    override fun showBackNav(): Boolean {
        return false
    }

    override fun getPageName(): String {
        return StatConstants.page_use_guide
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { finish() }
        StatusBarHelper.initTitleBarUI(this, mTitleBar)
        tv_useful.setOnClickListener {
            onTvUsefulClick()
            StatUtil.event(StatConstants.click_use_guide_page_this_is_helpful)
        }
        val label = resources.getStringArray(R.array.use_guide_labels)[mIndex]
        setTitle(label)
        tv_use_guide_title.text = label
        tv_content.text = resources.getStringArray(R.array.use_guide_contents)[mIndex]
    }

    private fun playVideo() {
        val uri = Uri.parse("android.resource://" + getPackageName() + "/" + mVideoIds[mIndex])!!
        video_view.setVideoSize(1080, 1920)
        video_view.setVideoURI(uri)
        video_view.start()
        video_view.setOnCompletionListener { video_view.start() }
        // 解决视频初始化黑屏闪烁的问题
        video_view.setOnPreparedListener { mp ->
            mp.setOnInfoListener(
                    object : MediaPlayer.OnInfoListener {
                        override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
                            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                                video_view.setBackgroundColor(Color.TRANSPARENT)
                            }
                            return true
                        }
                    }
            )
        }
    }

    override fun onStart() {
        super.onStart()
        playVideo()
    }

    private fun onTvUsefulClick() {
        tv_useful.isSelected = true
        tv_useful.isEnabled = false
    }

    companion object {
        private const val KEY_INDEX = "KEY_INDEX"
        fun launch(index: Int) {
            val bundle = Bundle()
            bundle.putInt(KEY_INDEX, index)
            ActivityUtils.startActivity(bundle, UseGuideActivity::class.java)
        }
    }
}