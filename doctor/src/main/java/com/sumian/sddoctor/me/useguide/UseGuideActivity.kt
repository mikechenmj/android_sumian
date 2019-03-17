package com.sumian.sddoctor.me.useguide

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.base.StatusBarHelper
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
            R.raw.inspect_patient_info,
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

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { finish() }
        StatusBarHelper.initTitleBarUI(this, mTitleBar)
        tv_useful.setOnClickListener { onTvUsefulClick() }
        val label = resources.getStringArray(R.array.use_guide_labels)[mIndex]
        setTitle(label)
        tv_use_guide_title.text = label
        tv_content.text = resources.getStringArray(R.array.use_guide_contents)[mIndex]

        video_view.setMediaController(MediaController(this))
        val uri = Uri.parse("android.resource://" + getPackageName() + "/" + mVideoIds[mIndex])!!
        video_view.setVideoURI(uri)
        video_view.start()
        video_view.setOnCompletionListener { video_view.start() }
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