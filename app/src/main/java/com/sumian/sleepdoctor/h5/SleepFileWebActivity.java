package com.sumian.sleepdoctor.h5;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.sumian.common.media.SelectImageActivity;
import com.sumian.common.media.config.SelectOptions;
import com.sumian.common.operator.AppOperator;
import com.sumian.common.utils.StreamUtil;
import com.sumian.sleepdoctor.base.BaseWebViewActivity;
import com.sumian.sleepdoctor.h5.bean.ImageCount;
import com.sumian.sleepdoctor.improve.widget.webview.SBridgeHandler;
import com.sumian.sleepdoctor.improve.widget.webview.SWebView;
import com.sumian.sleepdoctor.utils.JsonUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     @author : sm
 *
 *     e-mail : yaoqi.y@sumian.com
 *     time: 2018/7/2 11:47
 *
 *     version: 1.0
 *
 *     desc:睡眠档案
 *
 * </pre>
 */
public class SleepFileWebActivity extends BaseWebViewActivity {

    private static final String TAG = SleepFileWebActivity.class.getSimpleName();

    private String[] selectedImages;

    @Override
    protected String getUrlContentPart() {
        return H5Uri.MY_MEDICAL_RECORD;
    }

    @Override
    protected String h5HandlerName() {
        return "getImgUrl";
    }

    @Override
    protected void registerHandler(SWebView sWebView) {
        super.registerHandler(sWebView);
        sWebView.registerHandler(h5HandlerName(), new SBridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                //super.handler(data, function);
                ImageCount imageCount = JsonUtil.fromJson(data, ImageCount.class);

                if (imageCount == null) {
                    return;
                }

                Log.e(TAG, "handler: ----1---->" + imageCount.toString());

                SelectImageActivity.show(SleepFileWebActivity.this, new SelectOptions
                        .Builder()
                        .setHasCam(true)
                        .setCallback(images -> {
                            selectedImages = images;
                            enCodeBase64(images, (base64Images) -> {
                                String toJson = JsonUtil.toJson(base64Images);
                                function.onCallBack(toJson);
                            });

                            Log.e(TAG, "handler: ------>" + "123");
                        }).setSelectCount(imageCount.selectQuantity).setSelectedImages(selectedImages).build());
            }
        });
    }

    private void enCodeBase64(String[] images, OnDecodeBase64Callback onDecodeBase64Callback) {

        AppOperator.runOnThread(() -> {
            //image array ------> base64  array
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            //options.inTempStorage = new byte[16 * 1024 * 1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            String image;
            byte[] bytes;

            StringBuilder imageBase64 = new StringBuilder();
            List<String> tmp = new ArrayList<>(0);
            for (int i = 0; i < images.length; i++) {
                bos.reset();
                imageBase64.delete(0, imageBase64.length());
                image = images[i];
                Log.e(TAG, "handler: ----------->position=" + i + "    image=" + image);

                Bitmap bitmap = BitmapFactory.decodeFile(image, options);
                if (bitmap == null) continue;
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos);
                try {
                    bos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    StreamUtil.close(bos);
                }

                bytes = bos.toByteArray();
                bitmap.recycle();
                imageBase64.append("data:image/png;base64,").append(Base64.encodeToString(bytes, Base64.NO_WRAP));

                Log.e(TAG, "handler: ------imageBase64------>" + imageBase64);
                tmp.add(imageBase64.toString());
            }

            runOnUiThread(() -> {
                if (onDecodeBase64Callback != null) {
                    onDecodeBase64Callback.onDecode(tmp);
                }
            });
        });
    }


    interface OnDecodeBase64Callback {

        void onDecode(List<String> base64Images);
    }

}
