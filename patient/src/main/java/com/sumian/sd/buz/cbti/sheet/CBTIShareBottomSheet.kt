package com.sumian.sd.buz.cbti.sheet

import android.view.Gravity
import android.view.View
import androidx.fragment.app.FragmentManager
import com.sumian.common.helper.ToastHelper
import com.sumian.common.statistic.StatUtil
import com.sumian.sd.BuildConfig
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.widget.base.BaseBottomSheetView
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.lay_share_bottom_sheet.*

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
        return R.layout.lay_share_bottom_sheet
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
                        .shareUrl(activity!!,
                                BuildConfig.CBTI_SHARE_WEBSITE,
                                "CBTI（失眠的认知行为治疗）", "治疗失眠的非药物疗法，国内外睡眠医学领域推荐为治疗失眠的首选方案",
                                R.drawable.ic_share_launcher,
                                SHARE_MEDIA.WEIXIN,
                                this@CBTIShareBottomSheet)
            }
            R.id.tv_wechat_circle -> {
                AppManager
                        .getOpenEngine()
                        .shareUrl(activity!!,
                                BuildConfig.CBTI_SHARE_WEBSITE,
                                "CBTI（失眠的认知行为治疗），国内外睡眠医学领域首推的非药物治疗方案",
                                "治疗失眠的非药物疗法，国内外睡眠医学领域推荐为治疗失眠的首选方案",
                                R.drawable.ic_share_launcher,
                                SHARE_MEDIA.WEIXIN_CIRCLE,
                                this@CBTIShareBottomSheet)
            }
            R.id.tv_cancel -> {
                dismissAllowingStateLoss()
            }
        }

    }

    override fun onStart(shareMedia: SHARE_MEDIA?) {
        StatUtil.event(StatConstants.on_cbti_introduction_page_share_success)
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