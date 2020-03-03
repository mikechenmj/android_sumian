package com.sumian.sd.common.h5

import android.view.View
import com.sumian.common.h5.BaseWebViewFragment
import com.sumian.common.h5.bean.H5BindShareData
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.h5.widget.SWebViewLayout
import com.sumian.common.statistic.StatUtil
import com.sumian.sd.BuildConfig
import com.sumian.sd.R
import com.sumian.sd.buz.homepage.sheet.ShareBottomSheet
import com.sumian.sd.buz.stat.StatConstants
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
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

    override fun registerHandler(sWebView: SWebView) {
        sWebView.registerHandler("bindShare") { data, function ->
            val shareData = H5BindShareData.fromJson(data)
            getSWebViewLayout().shareView.visibility = View.VISIBLE
            getSWebViewLayout().shareView.setOnClickListener {
                if (shareData.weixin == null || shareData.weixinCircle == null) {
                    return@setOnClickListener
                }
                ShareBottomSheet.show(childFragmentManager, shareData.weixin!!, shareData.weixinCircle!!,
                        object : UMShareListener {
                            override fun onResult(p0: SHARE_MEDIA?) {

                            }

                            override fun onCancel(p0: SHARE_MEDIA?) {
                            }

                            override fun onError(p0: SHARE_MEDIA?, p1: Throwable?) {
                            }

                            override fun onStart(p0: SHARE_MEDIA?) {
                                StatUtil.event(StatConstants.on_relaxation_detail_page_share_success)
                            }
                        })
            }
        }
    }
}