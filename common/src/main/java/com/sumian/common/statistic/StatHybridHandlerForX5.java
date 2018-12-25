package com.sumian.common.statistic;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.mid.util.Util;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatSpecifyReportedInfo;
import com.tencent.stat.common.StatCommonHelper;
import com.tencent.stat.common.StatLogger;
import com.tencent.stat.hybrid.StatHybridBridge;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.URLDecoder;

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/25 14:18
 * desc   : StatHybridHandler 不支持x5 webview，copy 其代码，修改webview的包名，使其支持x5 webview
 * version: 1.0
 */
public class StatHybridHandlerForX5 {
    public static final int HYBRID_VERSION = 1;
    public static final String MTA_HYBRID_UA_FLAG = " TencentMTA/1";
    private static StatLogger a = StatCommonHelper.getLogger();
    private static StatSpecifyReportedInfo b = null;
    private static Context c = null;
    private static StatHybridBridge d = new StatHybridBridge();

    public StatHybridHandlerForX5() {
    }

    public static void init(Context var0) {
        b = new StatSpecifyReportedInfo();
        b.setAppKey(StatConfig.getAppKey(var0));
        b.setInstallChannel(StatConfig.getInstallChannel(var0));
        b.setFromH5(1);
    }

    public static void init(Context var0, String var1, String var2) {
        b = new StatSpecifyReportedInfo();
        b.setAppKey(var1);
        b.setInstallChannel(var2);
        b.setFromH5(1);
    }

    public static StatSpecifyReportedInfo getH5reportInfo() {
        return b;
    }

    public static void setH5reportInfo(StatSpecifyReportedInfo var0) {
        b = var0;
    }

    public static Context getContext() {
        return c;
    }

    public static void initWebSettings(WebSettings var0) {
        if (var0 != null) {
            String var1 = var0.getUserAgentString();
            a.d("org ua:" + var1);
            if (!TextUtils.isEmpty(var1) && !var1.contains(" TencentMTA/1")) {
                var0.setUserAgentString(var1 + " TencentMTA/1");
                a.d("new ua:" + var0.getUserAgentString());
            }
        }

    }

    public static boolean handleWebViewUrl(WebView var0, String var1) {
        try {
            String var2 = URLDecoder.decode(var1, "UTF-8");
            if (var0 != null && !Util.isEmpty(var2) && var1.toLowerCase().startsWith("tencentMtaHyb:".toLowerCase())) {
                if (c == null) {
                    c = var0.getContext().getApplicationContext();
                }

                a.d("decodedURL:" + var2);
                a(var0, var2.substring("tencentMtaHyb:".length()));
                return true;
            }
        } catch (Throwable var3) {
            a.e(var3);
        }

        return false;
    }

    private static void a(WebView var0, String var1) throws Exception {
        JSONObject var2 = new JSONObject(var1);
        String var3 = var2.getString("methodName");
        JSONObject var4 = var2.getJSONObject("args");
        a.d("invoke method:" + var3 + ",args:" + var4);
        Method var5 = d.getClass().getMethod(var3, JSONObject.class);
        var5.invoke(d, var4);
    }
}
