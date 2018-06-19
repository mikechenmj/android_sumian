package com.sumian.sleepdoctor.improve.doctor.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseWebViewActivity;
import com.sumian.sleepdoctor.h5.H5Uri;
import com.sumian.sleepdoctor.improve.doctor.bean.DoctorService;
import com.sumian.sleepdoctor.improve.widget.webview.SBridgeHandler;
import com.sumian.sleepdoctor.improve.widget.webview.SBridgeResult;
import com.sumian.sleepdoctor.improve.widget.webview.SWebView;
import com.sumian.sleepdoctor.utils.JsonUtil;

import java.util.Locale;
import java.util.Objects;

/**
 * Created by sm
 * on 2018/5/31 04:36
 * desc:
 **/
public class DoctorServiceWebActivity extends BaseWebViewActivity {

    private static final String EXTRA_DOCTOR_SERVICE = "com.sumian.app.extra.doctor.service";
    private static final String EXTRA_SCAN_2_BIND = "com.sumian.doctorsleep.extra.scan.2.bind";

    private DoctorService mDoctorService;
    private BroadcastReceiver mBroadcastReceiver;

    private boolean mIsScan2Bind;

    public static void show(Context context, DoctorService doctorService) {
        Bundle extras = new Bundle();
        extras.putParcelable(EXTRA_DOCTOR_SERVICE, doctorService);
        show(context, DoctorServiceWebActivity.class, extras);
    }

    public static void show(Context context, DoctorService doctorService, boolean isScan2Bind) {
        Bundle extras = new Bundle();
        extras.putParcelable(EXTRA_DOCTOR_SERVICE, doctorService);
        extras.putBoolean(EXTRA_SCAN_2_BIND, isScan2Bind);
        show(context, DoctorServiceWebActivity.class, extras);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        if (bundle != null) {
            this.mDoctorService = bundle.getParcelable(EXTRA_DOCTOR_SERVICE);
            this.mIsScan2Bind = bundle.getBoolean(EXTRA_SCAN_2_BIND, false);
        }
        return super.initBundle(bundle);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        registerFinishBroadcastReceiver();
    }

    private void registerFinishBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ShoppingCarActivity.ACTION_CLOSE_ACTIVE_ACTIVITY);

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (Objects.requireNonNull(intent.getAction())) {
                    case ShoppingCarActivity.ACTION_CLOSE_ACTIVE_ACTIVITY:
                        finish();
                        break;
                    default:
                        break;
                }
            }
        }, filter);
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
    protected String h5HandlerName() {
        return "payDirect";
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        if (mBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    protected void registerHandler(SWebView sWebView) {
        super.registerHandler(sWebView);
        sWebView.registerHandler(h5HandlerName(), new SBridgeHandler() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void handler(String data) {
                SBridgeResult<DoctorService> sBridgeResult = JsonUtil.fromJson(data, new TypeToken<SBridgeResult<DoctorService>>() {
                }.getType());
                if (sBridgeResult.code == 0) {//立即去购买
                    ShoppingCarActivity.launch(DoctorServiceWebActivity.this, sBridgeResult.result);
                } else {//未绑定医生
                    if (mIsScan2Bind) {
                        ScanDoctorQrCodeActivity.show(DoctorServiceWebActivity.this, ScanDoctorQrCodeActivity.class);
                    } else {
                        String url = "doctorsleep://doctor?id=" + AppManager.getAccountViewModel().getUserProfile().doctor_id;
                        DoctorWebActivity.launch(DoctorServiceWebActivity.this, url);
                    }
                }
            }
        });

    }
}
