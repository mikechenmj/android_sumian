package com.sumian.sd.base;

import android.view.Gravity;
import android.webkit.WebResourceResponse;

import com.sumian.common.h5.widget.SWebView;
import com.sumian.common.helper.ToastHelper;
import com.sumian.common.h5.BaseWebViewActivity;

import java.io.IOException;

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
        interceptH5LoadJs();
    }

    private void interceptH5LoadJs() {
        SWebView webView = getWebView();
        try {
            String[] files = getAssets().list("h5");
            if (files == null) {
                return;
            }
            boolean useLocalJs = Counter.INSTANCE.count() % 2 == 0;
            webView.setWebInterceptor((view, request) -> {
                String url = request.getUrl().toString();
                WebResourceResponse resourceResponse = null;
//                if (!useLocalJs) {
//                    return null;
//                }
                for (String file : files) {
                    if (url.endsWith(file)) {
                        try {
                            resourceResponse = new WebResourceResponse("application/javascript", "UTF-8", getAssets().open("h5/" + file));
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
