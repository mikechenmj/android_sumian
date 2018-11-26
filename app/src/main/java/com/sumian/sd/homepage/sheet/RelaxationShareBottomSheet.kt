package com.sumian.sd.homepage.sheet

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.Gravity
import android.view.View
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
class RelaxationShareBottomSheet : BaseBottomSheetView(), UMShareListener, View.OnClickListener {

    companion object {

        private const val ARGS_URL = "com.sumian.sd.args.url"
        private const val ARGS_TITLE = "com.sumian.sd.args.title"
        private const val ARGS_DESC = "com.sumian.sd.args.desc"


        @JvmStatic
        fun show(fragmentManager: FragmentManager, url: String, title: String, desc: String) {
            val relaxationShareBottomSheet = RelaxationShareBottomSheet()
            relaxationShareBottomSheet.arguments = Bundle().apply {
                putString(ARGS_URL, url)
                putString(ARGS_TITLE, title)
                putString(ARGS_DESC, desc)
            }

            fragmentManager
                    .beginTransaction()
                    .add(relaxationShareBottomSheet, RelaxationShareBottomSheet::class.java.simpleName)
                    .commitNowAllowingStateLoss()
        }
    }

    private lateinit var shareUrl: String
    private lateinit var shareTitle: String
    private lateinit var shareDesc: String

    override fun getLayout(): Int {
        return R.layout.lay_share_bottom_sheet
    }

    override fun initBundle(arguments: Bundle?) {
        super.initBundle(arguments)
        arguments?.let {
            this.shareUrl = it.getString(ARGS_URL, "")
            this.shareTitle = it.getString(ARGS_TITLE, "放松训练")
            this.shareDesc = it.getString(ARGS_DESC, "放松训练")
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
                AppManager
                        .getOpenEngine()
                        .shareWebForCallback(activity,
                                shareUrl,
                                shareTitle, shareDesc,
                                R.drawable.ic_share_launcher,
                                SHARE_MEDIA.WEIXIN,
                                this@RelaxationShareBottomSheet)
            }
            R.id.tv_wechat_circle -> {
                AppManager
                        .getOpenEngine()
                        .shareWebForCallback(activity,
                                shareUrl,
                                shareTitle,
                                shareDesc,
                                R.drawable.ic_share_launcher,
                                SHARE_MEDIA.WEIXIN_CIRCLE,
                                this@RelaxationShareBottomSheet)
            }
            R.id.tv_cancel -> {
                dismissAllowingStateLoss()
            }
        }

    }

    override fun onStart(shareMedia: SHARE_MEDIA?) {
    }

    override fun onCancel(shareMedia: SHARE_MEDIA?) {
        ToastHelper.show(context, "分享已取消", Gravity.CENTER)
    }

    override fun onResult(shareMedia: SHARE_MEDIA?) {
        dismissAllowingStateLoss()
    }

    override fun onError(shareMedia: SHARE_MEDIA?, throwable: Throwable?) {
        ToastHelper.show(context, "分享失败", Gravity.CENTER)

    }
}