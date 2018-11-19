package com.sumian.sd.h5;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.sumian.common.h5.handler.SBridgeHandler;
import com.sumian.common.h5.widget.SWebView;
import com.sumian.common.utils.StreamUtil;
import com.sumian.common.utils.SumianExecutor;
import com.sumian.sd.base.SdBaseWebViewActivity;
import com.sumian.sd.h5.bean.ImageCount;
import com.sumian.sd.utils.JsonUtil;
import com.sumian.sd.widget.sheet.SelectPictureBottomSheet;

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
public class SleepFileWebActivity extends SdBaseWebViewActivity {

    private static final String TAG = SleepFileWebActivity.class.getSimpleName();

    private String[] selectedImages;

    public static void show(Context context) {
        context.startActivity(new Intent(context, SleepFileWebActivity.class));
    }

    @Override
    protected String getUrlContentPart() {
        return H5Uri.MY_MEDICAL_RECORD;
    }

    @Override
    protected String h5HandlerName() {
        return "getImgUrl";
    }

    @Override
    protected void registerHandler(@NonNull SWebView sWebView) {
        super.registerHandler(sWebView);
        sWebView.registerHandler(h5HandlerName(), new SBridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                //super.handler(data, function);
                ImageCount imageCount = JsonUtil.fromJson(data, ImageCount.class);

                if (imageCount == null) {
                    return;
                }

                SelectPictureBottomSheet.show(getSupportFragmentManager(), images -> {
                    selectedImages = images;
                    Log.e(TAG, "handler: -------->");
                    enCodeBase64(images, (base64Images) -> {
                        String toJson = JsonUtil.toJson(base64Images);
                        function.onCallBack(toJson);
                    });
                    return images == null || images.length <= 0;
                }, imageCount.selectQuantity, selectedImages);
            }
        });
    }

    private void enCodeBase64(String[] images, OnDecodeBase64Callback onDecodeBase64Callback) {
        SumianExecutor.INSTANCE.runOnBackgroundThread(() -> {
            //image array ------> base64  array
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            // options.inTempStorage = new byte[16 * 1024 * 1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            String image;
            byte[] bytes;

            Bitmap bitmap;

            StringBuilder imageBase64 = new StringBuilder();
            List<String> tmp = new ArrayList<>(images.length);
            for (String tmpImage : images) {
                bos.reset();
                imageBase64.delete(0, imageBase64.length());
                image = tmpImage;
                // options.inJustDecodeBounds = true;
                bitmap = BitmapFactory.decodeFile(image, options);
                //options.inJustDecodeBounds = false;
                if (bitmap == null) {
                    continue;
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos);
                try {
                    bos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    StreamUtil.close(bos);
                }

                bytes = bos.toByteArray();
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                imageBase64.append("data:image/png;base64,").append(Base64.encodeToString(bytes, Base64.NO_WRAP));

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
