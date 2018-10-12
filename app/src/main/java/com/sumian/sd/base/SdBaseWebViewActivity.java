package com.sumian.sd.base;

import android.net.Uri;
import android.view.Gravity;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blankj.utilcode.util.LogUtils;
import com.sumian.common.h5.widget.SWebView;
import com.sumian.common.helper.ToastHelper;
import com.sumian.common.h5.BaseWebViewActivity;
import com.sumian.sd.service.cbti.video.LogUtil;

import java.io.IOException;

import internal.org.apache.http.entity.mime.MIME;

/**
 * Created by sm
 * on 2018/5/25 10:03
 * desc:
 **/
public abstract class SdBaseWebViewActivity<Presenter extends SdBasePresenter> extends BaseWebViewActivity {

    protected Presenter mPresenter;

    @Override
    protected void initWidget() {
        super.initWidget();
        SWebView webView = getWebView();
        try {
            String[] files = getAssets().list("js");
            LogUtils.d(files);
            boolean useLocalJs = Counter.INSTANCE.count() % 2 == 0;
            webView.setWebInterceptor((view, request) -> {
                String url = request.getUrl().toString();
                WebResourceResponse resourceResponse = null;
                if (!useLocalJs) {
                    return null;
                }
                for (String file : files) {
                    if (url.endsWith(file)) {
                        try {
                            LogUtils.d("return", file);
                            resourceResponse = new WebResourceResponse("application/javascript", "UTF-8", getAssets().open("js/" + file));
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return resourceResponse;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void showCenterToast(String message) {
        ToastHelper.show(this, message, Gravity.CENTER);
    }

}
