package com.sumian.sd.homepage

import android.view.View
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseWebViewActivity
import com.sumian.sd.h5.H5Uri
import kotlinx.android.synthetic.main.activity_sleep_diary_remind_setting.*

class RelaxationActivity : SdBaseWebViewActivity<SdBasePresenter<*>>() {

    override fun initWidget(root: View?) {
        super.initWidget(root)
        title_bar.openTopPadding(true)
    }

    override fun getUrlContentPart(): String {
        return H5Uri.CBTI_RELAXATIONS
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mTitleBar.visibility = View.VISIBLE
    }
}
