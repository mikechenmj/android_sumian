package com.sumian.sd.service.cbti.sheet

import android.support.v4.app.FragmentManager
import android.view.Gravity
import android.view.View
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.BuildConfig
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
                AppManager.getOpenEngine().shareWebForCallback(activity, BuildConfig.CBTI_SHARE_WEBSITE, "CBTI简介", "针对失眠的认知行为治疗 (CBTI)是治疗失眠的非药物疗法。因其疗效已经得到大量的临床研究证实，CBTI已经被国内外睡眠医学领域推荐为治疗失眠的首选方案。CBTI的原理旨在改变失眠人群的不良睡眠行为、习惯以及与失眠有关的不良认知等多方面因素，以达到改善失眠的效果。", R.drawable.ic_share_launcher, SHARE_MEDIA.WEIXIN, this@CBTIShareBottomSheet)
            }
            R.id.tv_wechat_circle -> {
                AppManager.getOpenEngine().shareWebForCallback(activity, BuildConfig.CBTI_SHARE_WEBSITE, "CBTI简介", "针对失眠的认知行为治疗 (CBTI)是治疗失眠的非药物疗法。因其疗效已经得到大量的临床研究证实，CBTI已经被国内外睡眠医学领域推荐为治疗失眠的首选方案。CBTI的原理旨在改变失眠人群的不良睡眠行为、习惯以及与失眠有关的不良认知等多方面因素，以达到改善失眠的效果。", R.drawable.ic_share_launcher, SHARE_MEDIA.WEIXIN_CIRCLE, this@CBTIShareBottomSheet)
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