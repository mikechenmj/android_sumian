package com.sumian.hw.improve.feedback;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.sumian.hw.base.BaseActivity;
import com.sumian.hw.log.LogManager;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.oss.bean.OssResponse;
import com.sumian.hw.widget.adapter.OnTextWatcherAdapter;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.app.HwAppManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sm
 * on 2018/3/22.
 * desc:
 */

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FeedbackActivity extends BaseActivity {

    private static final String TAG = FeedbackActivity.class.getSimpleName();

    ImageView mTitleBar;
    EditText mEtInput;
    TextView mTvContentLength;
    TextView mTvSubmit;

    private int mRetryCount;

    public static void show(Context context) {
        context.startActivity(new Intent(context, FeedbackActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_feedback;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleBar = findViewById(R.id.iv_back);
        mEtInput = findViewById(R.id.et_input);
        mTvContentLength = findViewById(R.id.tv_content_length);
        mTvSubmit = findViewById(R.id.tv_submit);

        mTitleBar.setOnClickListener(v -> finish());
        mEtInput.addTextChangedListener(new OnTextWatcherAdapter() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                mTvContentLength.setText(String.format(Locale.getDefault(), "%d%s%d", s.length(), "/", 200));
                if (s.length() <= 0) {
                    mTvContentLength.setVisibility(View.GONE);
                } else {
                    if (s.length() >= 200) {
                        mTvContentLength.setTextColor(getResources().getColor(R.color.warn_color));
                    } else {
                        mTvContentLength.setTextColor(getResources().getColor(R.color.full_general_color));
                    }
                    mTvContentLength.setVisibility(View.VISIBLE);
                }
            }
        });

        mTvSubmit.setOnClickListener(v -> {
            String input = mEtInput.getText().toString().trim();
            if (TextUtils.isEmpty(input)) {
                showCenterToast("您未填写反馈意见及建议");
                return;
            }

            LogManager.appendPhoneUSerAgentLog();

            HwAppManager.getHwV1HttpService()
                    .feedback(input, "txt")
                    .enqueue(new BaseResponseCallback<OssResponse>() {

                        @Override
                        protected void onSuccess(OssResponse response) {
                            showCenterToast("您的反馈已提交成功");
                            finish();
                            // File logFile = new File(App.Companion.getAppContext().getCacheDir(), LogManager.LOG_FILE_NAME);
                            // uploadFile(response, logFile.getAbsolutePath());
                        }

                        @Override
                        protected void onFailure(String error) {
                            showToast(error);
                        }
                    });
        });

    }

    public void uploadFile(OssResponse ossResponse, String localUploadFilePath) {
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(ossResponse.getAccess_key_id(), ossResponse.getAccess_key_secret(), ossResponse.getSecurity_token());
        OSSClient ossClient = new OSSClient(App.Companion.getAppContext(), ossResponse.getEndpoint(), credentialProvider);
        // 构造上传请求
        PutObjectRequest putObjectRequest = new PutObjectRequest(ossResponse.getBucket(), ossResponse.getObject(), localUploadFilePath);
        // 异步上传时可以设置进度回调

        Map<String, String> callbackParam = new HashMap<>();
        callbackParam.put("callbackUrl", ossResponse.getCallback_url());
        callbackParam.put("callbackBody", ossResponse.getCallback_body());
        putObjectRequest.setCallbackParam(callbackParam);

        putObjectRequest.setProgressCallback((request, currentSize, totalSize) -> Log.i(TAG, "currentSize=" + currentSize + " totalSize=" + totalSize));
        ossClient.asyncPutObject(putObjectRequest, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                String serverCallbackReturnBody = result.getServerCallbackReturnBody();
                String avatarUrl = null;
                if (TextUtils.isEmpty(serverCallbackReturnBody)) {
                    avatarUrl = "http://" + ossResponse.getBucket() + "." + ossResponse.getEndpoint() + "/" + ossResponse.getObject();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(serverCallbackReturnBody);
                        Log.e(TAG, "onSuccess: ------log  url---->" + jsonObject.toString());

                        avatarUrl = jsonObject.getString("url");
                        if (TextUtils.isEmpty(avatarUrl)) {
                            avatarUrl = "http://" + ossResponse.getBucket() + "." + ossResponse.getEndpoint() + "/" + ossResponse.getObject();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.e(TAG, "onSuccess: ------log  url---->" + avatarUrl);
                File logFile = new File(localUploadFilePath);
                if (logFile.exists()) {
                    boolean delete = logFile.delete();
                    if (delete) {
                        try {
                            boolean newFile = logFile.createNewFile();
                            showToast("您的反馈已提交成功");
                            finish();
                            Log.e(TAG, "onSuccess: -------->newFile=" + newFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                // 请求异常
                if (clientException != null) {
                    LogManager.appendUserOperationLog("log 文件上传失败  clientException=" + clientException.toString());
                }
                if (serviceException != null) {
                    LogManager.appendUserOperationLog("log 文件上传失败  serviceException=" + serviceException.toString());

                }
                if (mRetryCount <= 3) {
                    mRetryCount++;
                    uploadFile(ossResponse, localUploadFilePath);
                } else {
                    showToast("您的反馈提交失败,请稍后重试");
                }
            }
        });
        // task.waitUntilFinished(); // 可以等待任务完成
    }
}
