@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package com.sumian.sd.homepage

import android.content.Context
import android.content.Intent
import android.view.View
import com.sumian.common.h5.widget.SWebView
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseWebViewActivity
import com.sumian.sd.h5.H5Uri
import com.sumian.sd.homepage.sheet.RelaxationShareBottomSheet

class RelaxationActivity : SdBaseWebViewActivity<SdBasePresenter<*>>() {

    companion object {
        @JvmStatic
        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, RelaxationActivity::class.java)
        }
    }

    override fun initWidget() {
        super.initWidget()
        getTitleBar().openTopPadding(true)
    }

    override fun getUrlContentPart(): String {
        return H5Uri.CBTI_RELAXATIONS
    }

    override fun registerHandler(sWebView: SWebView) {
        super.registerHandler(sWebView)
        sWebView.registerHandler("shareRelaxation") { data, function ->
            RelaxationShareBottomSheet.show(supportFragmentManager, getCompleteUrl(), "放松训练", getTitleBar().mTvTitle.text.toString())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        getTitleBar().visibility = View.VISIBLE
    }
}
