package com.sumian.sd.homepage

import android.content.Intent
import android.view.ViewGroup
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.base.BaseDialogPresenterActivity
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.R
import com.sumian.sd.sleepguide.SleepGuideActivity
import kotlinx.android.synthetic.main.activity_sleep_guide_dialog.*

class SleepGuideDialogActivity : BaseDialogPresenterActivity<IPresenter>() {

    companion object {
        private const val KEY_BUTTON_Y_POSITION = "KEY_BUTTON_Y_POSITION"
        fun start(buttonYPosition: Int) {
            LogUtils.d(buttonYPosition)
            val intent = Intent(ActivityUtils.getTopActivity(), SleepGuideDialogActivity::class.java)
            intent.putExtra(KEY_BUTTON_Y_POSITION, buttonYPosition)
            ActivityUtils.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_sleep_guide_dialog
    }

    override fun initWidget() {
        super.initWidget()
        iv_sleep_guide.setOnClickListener {
            SleepGuideActivity.start()
            finish()
        }
        iv_close.setOnClickListener { finish() }

        intent.getIntExtra(KEY_BUTTON_Y_POSITION, 0)
        val layoutParams = iv_sleep_guide.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin = intent.getIntExtra(KEY_BUTTON_Y_POSITION, 0)
        iv_sleep_guide.layoutParams = layoutParams
    }

}
