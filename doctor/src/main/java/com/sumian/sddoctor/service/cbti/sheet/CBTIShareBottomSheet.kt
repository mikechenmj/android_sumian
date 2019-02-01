package com.sumian.sddoctor.service.cbti.sheet

import android.view.Gravity
import android.view.View
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.helper.ToastHelper
import com.sumian.sddoctor.BuildConfig
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.App
import com.sumian.sddoctor.app.AppManager
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.layout_share_bottom_sheet.*

/**
 * Created by dq
 *
 * on 2018/7/17
 *
 * desc:  CBTI  分享
 */
class CBTIShareBottomSheet : BaseBottomSheetView(), UMShareListener, View.OnClickListener {

    companion object {

        @JvmStatic
        fun show(fragmentManager: FragmentManager) {
            val cbtiShareBottomSheet = CBTIShareBottomSheet()
            fragmentManager
                    .beginTransaction()
                    .add(cbtiShareBottomSheet, CBTIShareBottomSheet::class.java.simpleName)
                    .commitNowAllowingStateLoss()
        }
    }

    override fun getLayout(): Int {
        return R.layout.layout_share_bottom_sheet
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
                launchWechatShare(BuildConfig.CBTI_SHARE_WEBSITE,
                        "CBTI（失眠的认知行为治疗）",
                        "治疗失眠的非药物疗法，国内外睡眠医学领域推荐为治疗失眠的首选方案",
                        R.drawable.ic_share_launcher,
                        SHARE_MEDIA.WEIXIN)
            }
            R.id.tv_wechat_circle -> {
                launchWechatShare(BuildConfig.CBTI_SHARE_WEBSITE,
                        "CBTI（失眠的认知行为治疗），国内外睡眠医学领域首推的非药物治疗方案",
                        "治疗失眠的非药物疗法，国内外睡眠医学领域推荐为治疗失眠的首选方案",
                        R.drawable.ic_share_launcher,
                        SHARE_MEDIA.WEIXIN_CIRCLE)
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

    private fun launchWechatShare(shareUrl: String, title: String, description: String, @DrawableRes shareIcon: Int, shareType: SHARE_MEDIA) {
        if (!isWeChatAvailable()) {
            ToastUtils.showShort(getString(R.string.wechat_not_install))
            //return
        }
        AppManager
                .getOpenEngine()
                .shareUrl(activity,
                        shareUrl,
                        title,
                        description,
                        shareIcon,
                        shareType,
                        this@CBTIShareBottomSheet)

    }

    private fun isWeChatAvailable(): Boolean {
        val packageManager = App.getAppContext().packageManager
        val packageInfoList = packageManager.getInstalledPackages(0)
        packageInfoList.forEach {
            if (it.packageName == "com.tencent.mm") {
                return true
            }
        }
        return false
    }
}