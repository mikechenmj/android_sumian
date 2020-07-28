package com.sumian.sd.common.h5

import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils

open class TranslucentStatusWebActivity : SimpleWebActivity() {
    companion object {
        fun launch(context: Context, urlContentPart: String, pageNameForStat: String? = null) {
            val intent = getLaunchIntentWithPartUrl(context, urlContentPart, pageNameForStat)
            ActivityUtils.startActivity(intent)
        }

        private fun getLaunchIntentWithPartUrl(context: Context, urlContentPart: String, pageNameForStat: String? = null): Intent {
            val intent = Intent(context, TranslucentStatusWebActivity::class.java)
            intent.putExtra(KEY_URL_CONTENT_PART, urlContentPart)
            intent.putExtra(KEY_PAGE_NAME, pageNameForStat)
            return intent
        }
    }
}