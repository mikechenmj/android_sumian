package com.sumian.sleepdoctor.h5;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.sumian.common.media.SelectImageActivity;
import com.sumian.common.media.config.SelectOptions;
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

    private List<String> selectedImages = new ArrayList<>(0);
    private List<String> mBase64Data = new ArrayList<>(0);

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

                if (imageCount != null) {
                    Log.e(TAG, "handler: ----1---->" + imageCount.toString());
                }

                SelectImageActivity.show(SleepFileWebActivity.this, new SelectOptions
                        .Builder()
                        .setHasCam(true)
                        .setCallback(images -> {
                            //image array ------> base64  array
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 2;
                            //options.inTempStorage = new byte[16 * 1024 * 1024];
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            String image;
                            byte[] bytes;
                            String imageBase64 = "data:image/png;base64,";
                            for (int i = 0; i < images.length; i++) {
                                image = images[i];
                                Log.e(TAG, "handler: ----------->position=" + i + "    image=" + image);

                                Bitmap bitmap = BitmapFactory.decodeFile(image, options);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, bos);
                                try {
                                    bos.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    StreamUtil.close(bos);
                                }

                                bytes = bos.toByteArray();
                                bitmap.recycle();
                                imageBase64 += Base64.encodeToString(bytes, Base64.NO_WRAP);

                                Log.e(TAG, "handler: ------imageBase64------>" + imageBase64);
                                int indexOf = mBase64Data.indexOf(imageBase64);
                                if (indexOf == -1) {
                                    mBase64Data.add(imageBase64);
                                    Log.e(TAG, "handler: ----------未在数组中,直接加入进去--->");
                                } else {
                                    Log.e(TAG, "handler: ------已存在-->");
                                }
                            }

                            String toJson = JsonUtil.toJson(mBase64Data);
                            Log.e(TAG, "handler: ------>" + "123");

                            function.onCallBack(toJson);

                        }).setSelectCount(9).setSelectedImages(selectedImages).build());
            }
        });
    }

}
