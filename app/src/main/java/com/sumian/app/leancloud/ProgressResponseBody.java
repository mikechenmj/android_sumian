package com.sumian.app.leancloud;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

/**
 * Created by jzz
 * on 2018/1/7.
 * desc:
 */

public class ProgressResponseBody extends ResponseBody {

    private static final String TAG = ProgressResponseBody.class.getSimpleName();

    private String url;
    private ResponseBody responseBody;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(String url, ResponseBody responseBody) {
        this.url = url;
        this.responseBody = responseBody;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }


    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(new ProgressSource(responseBody, url));
        }
        return bufferedSource;
    }

    private static class ProgressSource extends ForwardingSource {

        private long totalBytesRead = 0L;
        private long contentLength;

        private final String url;

        ProgressSource(ResponseBody responseBody, String url) {
            super(responseBody.source());
            this.contentLength = responseBody.contentLength();
            this.url = url;
        }

        @Override
        public long read(@NonNull Buffer sink, long byteCount) throws IOException {
            // read() returns the number of bytes read, or -1 if this source is exhausted.
            long bytesRead = super.read(sink, byteCount);
            totalBytesRead += bytesRead != -1 ? bytesRead : 0;   //不断统计当前下载好的数据

            int progress = Math.round(100.0f * totalBytesRead / contentLength + 0.5f);

//            ProgressListener progressListener = MyGlideModule.getProgressListener(url);
            // Log.e(TAG, "download progress is " + progress + " " + " contentLength=" + contentLength + " bytesRead=" + bytesRead + " totalBytesRead=" + totalBytesRead + " " + progressListener);
//            if (progressListener != null) {
//                progressListener.onProgress(url, progress);
//            }

            return bytesRead;
        }
    }

}
