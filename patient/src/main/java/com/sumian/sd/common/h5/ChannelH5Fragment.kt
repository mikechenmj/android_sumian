package com.sumian.sd.common.h5

import com.sumian.common.h5.BaseWebViewFragment
import com.sumian.common.h5.widget.SWebViewLayout
import com.sumian.sd.BuildConfig
import com.sumian.sd.R
import kotlinx.android.synthetic.main.channel_h5_fragment.*

class ChannelH5Fragment : BaseWebViewFragment() {

    override fun getLayoutId(): Int {
        return R.layout.channel_h5_fragment
    }

    override fun getSWebViewLayout(): SWebViewLayout {
        return sm_webview_container
    }

    override fun getCompleteUrl(): String {
        val token = "?token=" + getToken()
        val completeUrl = BuildConfig.CHANNEL_H5_URL + token
        return completeUrl
    }
}