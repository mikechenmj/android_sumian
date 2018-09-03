package com.sumian.sd.homepage

import android.view.View
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseWebViewActivity
import com.sumian.sd.h5.H5Uri

class RelaxationActivity : SdBaseWebViewActivity<SdBasePresenter<*>>() {

    override fun getUrlContentPart(): String {
        return H5Uri.CBTI_RELAXATIONS
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mTitleBar.visibility = View.VISIBLE
    }
}
