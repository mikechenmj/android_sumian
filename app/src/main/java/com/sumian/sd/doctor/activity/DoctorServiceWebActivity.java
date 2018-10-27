package com.sumian.sd.doctor.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.reflect.TypeToken;
import com.sumian.common.h5.bean.SBridgeResult;
import com.sumian.common.h5.handler.SBridgeHandler;
import com.sumian.common.h5.widget.SWebView;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.base.SdBaseWebViewActivity;
import com.sumian.sd.doctor.bean.DoctorService;
import com.sumian.sd.doctor.bean.H5DoctorServiceShoppingResult;
import com.sumian.sd.h5.H5Uri;
import com.sumian.sd.pay.activity.PaymentActivity;
import com.sumian.sd.service.advisory.activity.PublishAdvisoryRecordActivity;
import com.sumian.sd.service.diary.DiaryEvaluationDetailActivity;
import com.sumian.sd.service.tel.activity.TelBookingPublishActivity;
import com.sumian.sd.utils.JsonUtil;

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
    private boolean mIsFromRecord;

    public static void show(Context context, DoctorService doctorService) {
        Bundle extras = new Bundle();
        extras.putParcelable(EXTRA_DOCTOR_SERVICE, doctorService);
        initLauncherIntent(context, extras);
    }

    private static void initLauncherIntent(Context context, Bundle extras) {
        Intent intent = new Intent(context, DoctorServiceWebActivity.class);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    public static void show(Context context, DoctorService doctorService, boolean isFromRecord) {
        Bundle extras = new Bundle();
        extras.putParcelable(EXTRA_DOCTOR_SERVICE, doctorService);
        extras.putBoolean(EXTRA_FROM_RECORD, isFromRecord);
        initLauncherIntent(context, extras);
    }

    @Override
    protected void initBundle(@NonNull Bundle bundle) {
        this.mDoctorService = bundle.getParcelable(EXTRA_DOCTOR_SERVICE);
        this.mIsFromRecord = bundle.getBoolean(EXTRA_FROM_RECORD, false);
    }

    @Override
    protected String initTitle() {
        return this.mDoctorService.getName();
    }

    @Override
    protected String getUrlContentPart() {
        return H5Uri.DOCTOR_SERVICE.replace("{id}", String.format(Locale.getDefault(), "%d", mDoctorService.getType()));
    }

    @Override
    protected void registerHandler(@NonNull SWebView sWebView) {
        super.registerHandler(sWebView);
        sWebView.registerHandler("buyService", new SBridgeHandler() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void handler(String data) {
                SBridgeResult<H5DoctorServiceShoppingResult> sBridgeResult = JsonUtil.fromJson(data, new TypeToken<SBridgeResult<H5DoctorServiceShoppingResult>>() {
                }.getType());
                //0：立即购买； -1：未绑定医生
                if (sBridgeResult.code == 0) {
                    PaymentActivity.startForResult(DoctorServiceWebActivity.this, sBridgeResult.result.getService(), sBridgeResult.result.getPackageId(), REQUEST_CODE_BUY_SERVICE);
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
        if (requestCode == REQUEST_CODE_BUY_SERVICE) {
            switch (resultCode) {
                case RESULT_OK:
                    switch (mDoctorService.getType()) {
                        case DoctorService.SERVICE_TYPE_ADVISORY:
                            Intent intent = new Intent(ACTION_CLOSE_ACTIVE_ACTIVITY);
                            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                            PublishAdvisoryRecordActivity.show(this, PublishAdvisoryRecordActivity.class);
                            break;
                        case DoctorService.SERVICE_TYPE_SLEEP_REPORT:
                            DiaryEvaluationDetailActivity.launchLatestEvaluation(this);
                            break;
                        case DoctorService.SERVICE_TYPE_PHONE_ADVISORY:
                            TelBookingPublishActivity.show();
                        default:
                            break;
                    }
                    break;
                case RESULT_CANCELED:

                    break;
                default:
                    break;
            }
            finish();
        }
    }
}
