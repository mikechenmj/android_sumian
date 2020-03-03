package com.sumian.sd.buz.homepage.sheet

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.fragment.app.FragmentManager
import com.sumian.common.h5.bean.WEIXIN
import com.sumian.common.h5.bean.WEIXIN_CIRCLE
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.widget.base.BaseBottomSheetView
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.lay_share_bottom_sheet.*

/**
 * Created by dq
 *
 * on 2018/7/17
 *
 * desc:  放松训练  分享
 */
class ShareBottomSheet : BaseBottomSheetView(), UMShareListener, View.OnClickListener {

    companion object {

        private const val ARGS_URL = "com.sumian.sd.args.url"
        private const val ARGS_TITLE = "com.sumian.sd.args.title"
        private const val ARGS_MOMENT_TITLE = "com.sumian.sd.args.moment_title"
        private const val ARGS_DESC = "com.sumian.sd.args.desc"
        private const val ARGS_THUMB_URL = "com.sumian.sd.args.thumb_url"
        private const val ARGS_WEIXIN = "com.sumian.sd.args.weixin"
        private const val ARGS_WEIXIN_CIRCLE = "com.sumian.sd.args.weixin_circle"

        @JvmStatic
        fun show(fragmentManager: FragmentManager, url: String, title: String, desc: String, momentTitle: String, thumbUrl: String,
                 umShareListener: UMShareListener? = null) {
            val relaxationShareBottomSheet = ShareBottomSheet()
            relaxationShareBottomSheet.mListener = umShareListener
            relaxationShareBottomSheet.arguments = Bundle().apply {
                putString(ARGS_URL, url)
                putString(ARGS_TITLE, title)
                putString(ARGS_DESC, desc)
                putString(ARGS_MOMENT_TITLE, momentTitle)
                putString(ARGS_THUMB_URL, thumbUrl)
            }

            fragmentManager
                    .beginTransaction()
                    .add(relaxationShareBottomSheet, ShareBottomSheet::class.java.simpleName)
                    .commitNowAllowingStateLoss()
        }

        @JvmStatic
        fun show(fragmentManager: FragmentManager, weixin: WEIXIN, weixinCircle: WEIXIN_CIRCLE,
                 umShareListener: UMShareListener? = null) {
            val relaxationShareBottomSheet = ShareBottomSheet()
            relaxationShareBottomSheet.mListener = umShareListener
            relaxationShareBottomSheet.arguments = Bundle().apply {
                putParcelable(ARGS_WEIXIN, weixin)
                putParcelable(ARGS_WEIXIN_CIRCLE, weixinCircle)
            }

            fragmentManager
                    .beginTransaction()
                    .add(relaxationShareBottomSheet, ShareBottomSheet::class.java.simpleName)
                    .commitNowAllowingStateLoss()
        }
    }

    private lateinit var shareUrl: String
    private lateinit var shareTitle: String
    private lateinit var shareDesc: String
    private lateinit var shareMomentTitle: String
    private lateinit var thumbUrl: String
    private var weixin: WEIXIN? = null
    private var weixinCircle: WEIXIN_CIRCLE? = null
    private var mListener: UMShareListener? = null

    override fun getLayout(): Int {
        return R.layout.lay_share_bottom_sheet
    }

    override fun initBundle(arguments: Bundle?) {
        super.initBundle(arguments)
        arguments?.let {
            this.shareUrl = it.getString(ARGS_URL, "")
            this.shareTitle = it.getString(ARGS_TITLE, "")
            this.shareDesc = it.getString(ARGS_DESC, "")
            this.shareMomentTitle = it.getString(ARGS_MOMENT_TITLE, "")
            this.thumbUrl = it.getString(ARGS_THUMB_URL, "")
            weixin = it.getParcelable(ARGS_WEIXIN)
            weixinCircle = it.getParcelable(ARGS_WEIXIN_CIRCLE)
        }
    }

    override fun initView(rootView: View?) {
        super.initView(rootView)
        tv_wechat_friend.setOnClickListener(this)
        tv_wechat_circle.setOnClickListener(this)
        tv_cancel.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_wechat_friend -> {
                if (weixin != null) {
                    var data = weixin!!
                    AppManager
                            .getOpenEngine()
                            .shareUrl(activity!!,
                                    data.link,
                                    data.title,
                                    data.desc,
                                    data.imgUrl,
                                    SHARE_MEDIA.WEIXIN,
                                    this@ShareBottomSheet)
                } else {
                    AppManager
                            .getOpenEngine()
                            .shareUrl(activity!!,
                                    shareUrl,
                                    shareTitle,
                                    shareDesc,
                                    thumbUrl,
                                    SHARE_MEDIA.WEIXIN,
                                    this@ShareBottomSheet)
                }
            }
            R.id.tv_wechat_circle -> {
                if (weixinCircle != null) {
                    var data = weixinCircle!!
                    AppManager
                            .getOpenEngine()
                            .shareUrl(activity!!,
                                    data.link,
                                    data.title,
                                    "",
                                    data.imgUrl,
                                    SHARE_MEDIA.WEIXIN_CIRCLE,
                                    this@ShareBottomSheet)
                } else {
                    AppManager
                            .getOpenEngine()
                            .shareUrl(activity!!,
                                    shareUrl,
                                    shareMomentTitle,
                                    shareDesc,
                                    thumbUrl,
                                    SHARE_MEDIA.WEIXIN_CIRCLE,
                                    this@ShareBottomSheet)
                }
            }
            R.id.tv_cancel -> {
            }
        }
        dismissAllowingStateLoss()
    }

    override fun onStart(shareMedia: SHARE_MEDIA?) {
        mListener?.onStart(shareMedia)
    }

    override fun onCancel(shareMedia: SHARE_MEDIA?) {
        ToastHelper.show(context, "分享已取消", Gravity.CENTER)
        mListener?.onCancel(shareMedia)
    }

    override fun onResult(shareMedia: SHARE_MEDIA?) {
        dismissAllowingStateLoss()
        mListener?.onResult(shareMedia)
    }

    override fun onError(shareMedia: SHARE_MEDIA?, throwable: Throwable?) {
        ToastHelper.show(context, "分享失败", Gravity.CENTER)
        mListener?.onError(shareMedia, throwable)
    }
}