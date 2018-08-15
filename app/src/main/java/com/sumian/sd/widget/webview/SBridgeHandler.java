package com.sumian.sd.widget.webview;

import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.sumian.sd.BuildConfig;

/**
 * Created by sm
 * on 2018/5/29 19:07
 * desc:  可直接使用该handler 处理 webview 的数据,因为回调过来的都是json
 **/
public abstract class SBridgeHandler implements BridgeHandler {

    private static final String TAG = SBridgeHandler.class.getSimpleName();

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Override
    public void handler(String data, CallBackFunction function) {
        LogUtils.d(data);
        function.onCallBack(data);
        handler(data);
    }

    public void handler(String data) {
    }
}
