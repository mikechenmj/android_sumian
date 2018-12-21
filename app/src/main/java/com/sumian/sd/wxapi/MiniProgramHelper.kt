package com.sumian.sd.wxapi

import android.content.Context
import com.sumian.sd.BuildConfig
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.umeng.socialize.utils.DeviceConfig.context

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
}