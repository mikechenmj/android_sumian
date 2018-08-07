package com.sumian.sleepdoctor.doctor.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.reflect.TypeToken;
import com.sumian.sleepdoctor.advisory.activity.PublishAdvisoryRecordActivity;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.SdBaseWebViewActivity;
import com.sumian.sleepdoctor.doctor.bean.DoctorService;
import com.sumian.sleepdoctor.h5.H5Uri;
import com.sumian.sleepdoctor.record.SleepRecordActivity;
import com.sumian.sleepdoctor.utils.JsonUtil;
import com.sumian.sleepdoctor.widget.webview.SBridgeHandler;
import com.sumian.sleepdoctor.widget.webview.SBridgeResult;
import com.sumian.sleepdoctor.widget.webview.SWebView;

import java.util.Locale;

/**
 * Created by sm
 * on 2018/5/31 04:36
 * desc:
 **/
public class DoctorServiceWebActivity extends SdBaseWebViewActivity {
    public static final String ACTION_CLOSE_ACTIVE_ACTIVITY = "com.sumian.sleepdoctor.ACTION.close.active.activity";
    private static final String EXTRA_DOCTOR_SERVICE = "com.sumian.app.extra.doctor.service";
    private static final String EXTRA_FROM_RECORD = "com.sumian.doctorsleep.extra.from.record";
    private static final int REQUEST_CODE_BUY_SERVICE = 1000;

    private DoctorService mDoctorService;
    private BroadcastReceiver mBroadcastReceiver;

    private boolean mIsFromRecord;

    public static void show(Context context, DoctorService doctorService) {
        Bundle extras = new Bundle();
        extras.putParcelable(EXTRA_DOCTOR_SERVICE, doctorService);
        show(context, DoctorServiceWebActivity.class, extras);
    }

    public static void show(Context context, DoctorService doctorService, boolean isFromRecord) {
        Bundle extras = new Bundle();
        extras.putParcelable(EXTRA_DOCTOR_SERVICE, doctorService);
        extras.putBoolean(EXTRA_FROM_RECORD, isFromRecord);
        show(context, DoctorServiceWebActivity.class, extras);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        if (bundle != null) {
            this.mDoctorService = bundle.getParcelable(EXTRA_DOCTOR_SERVICE);
            this.mIsFromRecord = bundle.getBoolean(EXTRA_FROM_RECORD, false);
        }
        return super.initBundle(bundle);
    }

    @Override
    protected String initTitle() {
        return this.mDoctorService.getName();
    }

    @Override
    protected String getUrlContentPart() {
        return H5Uri.DOCTOR_SERVICE.replace("{id}", String.format(Locale.getDefault(), "%d", mDoctorService.getId()));
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        if (mBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    protected void registerHandler(@NonNull SWebView sWebView) {
        super.registerHandler(sWebView);
        sWebView.registerHandler("payDirect", new SBridgeHandler() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void handler(String data) {
                SBridgeResult<DoctorService> sBridgeResult = JsonUtil.fromJson(data, new TypeToken<SBridgeResult<DoctorService>>() {
                }.getType());
                //0：立即购买； -1：未绑定医生
                if (sBridgeResult.code == 0) {
                    PaymentActivity.startForResult(DoctorServiceWebActivity.this, sBridgeResult.result, REQUEST_CODE_BUY_SERVICE);
                } else {//未绑定医生
                    if (mIsFromRecord) {
                        ScanDoctorQrCodeActivity.show(DoctorServiceWebActivity.this, mDoctorService, mIsFromRecord);
                    } else {
                        String url = "doctorsleep://doctor?id=" + AppManager.getAccountViewModel().getUserInfo().doctor_id;
                        DoctorWebActivity.show(DoctorServiceWebActivity.this, url);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BUY_SERVICE && resultCode == RESULT_OK) {
            switch (mDoctorService.getType()) {
                case DoctorService.SERVICE_TYPE_ADVISORY:
                    Intent intent = new Intent(ACTION_CLOSE_ACTIVE_ACTIVITY);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    PublishAdvisoryRecordActivity.show(this, PublishAdvisoryRecordActivity.class);
                    break;
                case DoctorService.SERVICE_TYPE_SLEEP_REPORT:
                    SleepRecordActivity.Companion.launch(this);
                    break;
                default:
                    break;
            }
            finish();
        }
    }
}
