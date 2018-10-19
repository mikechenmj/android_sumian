package com.sumian.sd.base;

import android.support.annotation.Nullable;
import android.view.Gravity;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
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

    private static final String H5_ASSET_PATH = "h5/static";
    private static final String H5_ASSET_JS = H5_ASSET_PATH + "/js";
    private static final String H5_ASSET_CSS = H5_ASSET_PATH + "/css";
    private static final String H5_ASSET_IMG = H5_ASSET_PATH + "/img";

    protected Presenter mPresenter;

    @Override
    protected void initWidget() {
        super.initWidget();
        interceptH5LoadJs();
    }

    private void interceptH5LoadJs() {
        SWebView webView = getWebView();
        try {
            String[] jsFiles = getAssets().list(H5_ASSET_JS);
            String[] cssFiles = getAssets().list(H5_ASSET_CSS);
            String[] imgFiles = getAssets().list(H5_ASSET_IMG);
            if (jsFiles == null) {
                return;
            }
            boolean useLocalJs = Counter.INSTANCE.count() % 2 == 0;
            ToastUtils.showShort("h5 speed up");
            webView.setWebInterceptor((view, request) -> {
                String url = request.getUrl().toString();
                WebResourceResponse resourceResponse = null;
                if (!useLocalJs) { // test code
                    return null;
                }
                String dirPath = getLocalFileDirPath(url);
                if (dirPath == null) {
                    return null;
                }
                String localFile = getLocalFile(url, jsFiles, cssFiles, imgFiles);
                if (localFile == null) {
                    return null;
                }
                String mimeType = getMimeType(url);
                LogUtils.d(localFile);
                try {
                    resourceResponse = new WebResourceResponse(mimeType, "UTF-8", getAssets().open(dirPath + "/" + localFile));
                } catch (IOException e) {
                    e.printStackTrace();
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

    private String getLocalFileDirPath(String url) {
        String dir = null;
        if (url.endsWith(".js")) {
            dir = H5_ASSET_JS;
        } else if (url.endsWith(".css")) {
            dir = H5_ASSET_CSS;
        } else if (url.endsWith(".png") || url.endsWith(".jpg")) {
            dir = H5_ASSET_IMG;
        }
        if (dir == null) {
            return null;
        }
        return dir;
    }

    private String getLocalFile(String url, String[] js, String[] css, String[] image) {
        String[] files = null;
        if (url.endsWith(".js")) {
            files = js;
        } else if (url.endsWith(".css")) {
            files = css;
        } else if (url.endsWith(".png") || url.endsWith(".jpg")) {
            files = image;
        }
        if (files == null) {
            return null;
        }
        return getLocalFile(url, files);
    }

    @Nullable
    private String getLocalFile(String url, String[] files) {
        if (files == null) {
            return null;
        }
        for (String file : files) {
            if (url.endsWith(file)) {
                return file;
            }
        }
        return null;
    }

    /**
     * reference https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Complete_list_of_MIME_types
     *
     * @param url
     * @return
     */
    private String getMimeType(String url) {
        String mimeType = null;
        if (url.endsWith(".js")) {
            mimeType = "application/javascript";
        } else if (url.endsWith(".css")) {
            mimeType = "text/css";
        } else if (url.endsWith(".png")) {
            mimeType = "image/png";
        } else if (url.endsWith(".jpg")) {
            mimeType = "image/jpeg";
        }
        return mimeType;
    }

    class LocalFileData {
        String dirPath;
        String filePath;
        String mimeType;
    }

    private LocalFileData getLocalFileData(String url, String[] js, String[] css, String[] image) {
        LocalFileData fileData = new LocalFileData();
        if (url.endsWith(".js")) {
            fileData.dirPath = H5_ASSET_JS;
            fileData.mimeType = "application/javascript";
        } else if (url.endsWith(".css")) {
            fileData.dirPath = H5_ASSET_CSS;
            fileData.mimeType = "text/css";
        } else if (url.endsWith(".png")) {
            fileData.dirPath = H5_ASSET_IMG;
            fileData.mimeType = "image/png";
        } else if (url.endsWith(".jpg")) {
            fileData.dirPath = H5_ASSET_IMG;
            fileData.mimeType = "image/jpeg";
        }
        return fileData;
    }
}
