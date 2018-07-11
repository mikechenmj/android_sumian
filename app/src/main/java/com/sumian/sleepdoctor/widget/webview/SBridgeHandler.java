package com.sumian.sleepdoctor.widget.webview;

import android.util.Log;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.sumian.sleepdoctor.BuildConfig;

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
        function.onCallBack(data);
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "handler: ----hello---->" + data);
        }
        //  SBridgeResult<Result> sBridgeResult = null;// JSON.parseObject(data, new TypeToken<SBridgeResult<Result>>() {
        //   }.getType());//JsonUtil.fromJson(data, new TypeToken<SBridgeResult<Result>>() {
        //}.getType());
//        TypeAdapter<SBridgeResult<Result>> adapter = new GsonBuilder().create().getAdapter(new TypeToken<SBridgeResult<Result>>() {
//        });
//        try {
//            sBridgeResult = adapter.fromJson(data);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //JsonReader jsonReader = new GsonBuilder().create().newJsonReader(value.charStream());
//        try {
//            T result = adapter.read(jsonReader);
//            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
//                throw new JsonIOException("JSON document was not fully consumed.");
//            }
//            return result;
//        } finally {
//            value.close();
//        }


        // Log.e(TAG, "handler: --------->" + sBridgeResult.toString());
        // if (sBridgeResult != null) {
        handler(data);
        //}
    }

    public void handler(String data) {
    }
}
