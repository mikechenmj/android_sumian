package com.sumian.sd.widget

import android.content.Context
import com.blankj.utilcode.util.ActivityUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.lay_share_bottom_sheet.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/13 09:42
 * desc   :
 * version: 1.0
 */
class ShareBottomSheet(context: Context) : BottomSheetDialog(context) {
    private var mUMShareListener: UMShareListener? = null
    private var mUrl: String = ""
    private var mTitle: String = ""
    private var mMessage: String = ""

    init {
        setContentView(R.layout.lay_share_bottom_sheet)
        tv_wechat_friend.setOnClickListener { share(SHARE_MEDIA.WEIXIN) }
        tv_wechat_circle.setOnClickListener { share(SHARE_MEDIA.WEIXIN_CIRCLE) }
        tv_cancel.setOnClickListener { dismiss() }
    }

    fun setUrl(url: String): ShareBottomSheet {
        mUrl = url
        return this
    }

    fun setTitle(title: String): ShareBottomSheet {
        mTitle = title
        return this
    }

    fun setMessage(message: String): ShareBottomSheet {
        mMessage = message
        return this
    }

    fun setListener(shareListener: UMShareListener?): ShareBottomSheet {
        mUMShareListener = shareListener
        return this
    }

    private fun share(shareMedia: SHARE_MEDIA) {
        AppManager
                .getOpenEngine()
                .shareUrl(ActivityUtils.getTopActivity(),
                        mUrl,
                        mTitle,
                        mMessage,
                        R.drawable.ic_share_launcher,
                        shareMedia,
                        mUMShareListener)
        dismiss()
    }
}