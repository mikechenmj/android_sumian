package com.sumian.sd.widget

import android.content.Context
import android.text.TextUtils
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

    companion object {
        private const val INVALID_ICON_ID = -1
    }

    private var mUMShareListener: UMShareListener? = null
    private var mUrl: String = ""
    private var mTitle: String = ""
    private var mMessage: String = ""
    private var mIconUrl: String = ""
    private var mIconId = INVALID_ICON_ID

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

    fun setIcon(iconUrl: String): ShareBottomSheet {
        mIconUrl = iconUrl
        return this
    }

    fun setIcon(iconId: Int): ShareBottomSheet {
        mIconId = iconId
        return this
    }

    fun setListener(shareListener: UMShareListener?): ShareBottomSheet {
        mUMShareListener = shareListener
        return this
    }

    private fun share(shareMedia: SHARE_MEDIA) {
        if (mIconId != INVALID_ICON_ID) {
            AppManager
                    .getOpenEngine()
                    .shareUrl(ActivityUtils.getTopActivity(),
                            mUrl,
                            mTitle,
                            mMessage,
                            mIconId,
                            shareMedia,
                            mUMShareListener)
        } else if (!TextUtils.isEmpty(mIconUrl)) {
            AppManager
                    .getOpenEngine()
                    .shareUrl(ActivityUtils.getTopActivity(),
                            mUrl,
                            mTitle,
                            mMessage,
                            mIconUrl,
                            shareMedia,
                            mUMShareListener)
        } else {
            AppManager
                    .getOpenEngine()
                    .shareUrl(ActivityUtils.getTopActivity(),
                            mUrl,
                            mTitle,
                            mMessage,
                            R.mipmap.ic_launcher,
                            shareMedia,
                            mUMShareListener)
        }
        dismiss()
    }
}