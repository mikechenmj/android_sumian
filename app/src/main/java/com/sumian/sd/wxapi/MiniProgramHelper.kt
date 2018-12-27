package com.sumian.sd.wxapi

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.BuildConfig
import com.sumian.sd.R
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/21 10:55
 * desc   :
 * version: 1.0
 */
object MiniProgramHelper {
    fun launchYouZan(context: Context) {
        val appId = BuildConfig.WECHAT_APP_ID;// Enter the AppId of the mobile app
        val api = WXAPIFactory.createWXAPI(context, appId);
        val req = WXLaunchMiniProgram.Req();
        req.userName = BuildConfig.WECHAT_MINI_PROGRAM_ID_YOUZAN;// Enter the original id of the Mini Program
        //req.path = path;                 //Page path to redirect to the Mini Program. The path parameter is optional, and defaults to the homepage of the Mini Program if it is not provided
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE// Developer version, trial version, and official version are available for selection
        api.sendReq(req);
    }

    fun launchYouZanOrWeb(context: Context) {
        if (AppUtils.isAppInstalled("com.tencent.mm")) {
            SumianDialog(context)
                    .setTitleText(R.string.notice)
                    .setMessageText(R.string.sumian_doctor_want_to_open_wechat)
                    .setLeftBtn(R.string.cancel, null)
                    .whitenLeft()
                    .setRightBtn(R.string.open, View.OnClickListener { launchYouZan(ActivityUtils.getTopActivity()) })
                    .show()
        } else {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.YOUZAN_URL))
            ActivityUtils.getTopActivity().startActivity(browserIntent)
        }
    }
}