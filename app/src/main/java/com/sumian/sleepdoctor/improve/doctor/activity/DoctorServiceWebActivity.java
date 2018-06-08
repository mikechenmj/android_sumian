package com.sumian.sleepdoctor.improve.doctor.activity;

import android.content.Context;
import android.os.Bundle;

import com.google.gson.reflect.TypeToken;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseWebViewActivity;
import com.sumian.sleepdoctor.h5.H5Url;
import com.sumian.sleepdoctor.improve.doctor.bean.DoctorService;
import com.sumian.sleepdoctor.improve.widget.webview.SBridgeHandler;
import com.sumian.sleepdoctor.improve.widget.webview.SBridgeResult;
import com.sumian.sleepdoctor.improve.widget.webview.SWebView;
import com.sumian.sleepdoctor.utils.JsonUtil;

import java.util.Locale;

/**
 * Created by sm
 * on 2018/5/31 04:36
 * desc:
 **/
public class DoctorServiceWebActivity extends BaseWebViewActivity {

    private static final String EXTRA_DOCTOR_SERVICE = "com.sumian.app.extra.doctor.service";

    private DoctorService mDoctorService;

    public static void show(Context context, DoctorService doctorService) {
        Bundle extras = new Bundle();
        extras.putParcelable(EXTRA_DOCTOR_SERVICE, doctorService);
        show(context, DoctorServiceWebActivity.class, extras);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        if (bundle != null) {
            this.mDoctorService = bundle.getParcelable(EXTRA_DOCTOR_SERVICE);
        }
        return super.initBundle(bundle);
    }

    @Override
    protected String initTitle() {
        return this.mDoctorService.getName();
    }

    @Override
    protected String getUrlContentPart() {
        return H5Url.H5_URI_DOCTOR_SERVICE.replace("{id}", String.format(Locale.getDefault(), "%d", mDoctorService.getId()));
    }

    @Override
    protected String h5HandlerName() {
        return "payDirect";
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
                    String url = "doctorsleep://doctor?id=" + AppManager.getAccountViewModel().getUserProfile().doctor_id;
                    DoctorWebActivity.launch(DoctorServiceWebActivity.this, url);
                }
            }
        });

    }
}
