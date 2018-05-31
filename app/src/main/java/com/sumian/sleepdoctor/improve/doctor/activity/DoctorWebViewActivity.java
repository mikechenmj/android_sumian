package com.sumian.sleepdoctor.improve.doctor.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseWebViewActivity;
import com.sumian.sleepdoctor.h5.H5Url;
import com.sumian.sleepdoctor.improve.doctor.bean.Doctor;
import com.sumian.sleepdoctor.improve.doctor.contract.BindDoctorContract;
import com.sumian.sleepdoctor.improve.doctor.presenter.BindDoctorPresenter;
import com.sumian.sleepdoctor.improve.widget.webview.SBridgeHandler;
import com.sumian.sleepdoctor.improve.widget.webview.SBridgeResult;
import com.sumian.sleepdoctor.improve.widget.webview.SWebView;
import com.sumian.sleepdoctor.utils.JsonUtil;

import org.jetbrains.annotations.NotNull;

/**
 * Created by sm
 * on 2018/5/28 11:40
 * desc:
 **/
public class DoctorWebViewActivity extends BaseWebViewActivity<BindDoctorPresenter> implements BindDoctorContract.View {
    private static final String ARGS_URL = "com.sumian.sleepdoctor.extra.args.url";

    private String mArgUrl;

    public static void launch(Context context, String url) {
        Bundle extras = new Bundle();
        //"https://sd-dev.sumian.com/doctor/1?scheme=" + uriQuery
        extras.putString(ARGS_URL, url);
        DoctorWebViewActivity.show(context, DoctorWebViewActivity.class, extras);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mArgUrl = bundle.getString(ARGS_URL);
        return super.initBundle(bundle);
    }

    @Override
    protected String getUrlContentPart() {
        Uri argUri = Uri.parse(mArgUrl);
        String originUrl = H5Url.H5_URI_DOCTOR_SERVICE;
        return originUrl.replace("{id}", argUri.getQueryParameter("id"));
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        BindDoctorPresenter.Companion.init(this);
    }

    @Override
    protected String h5HandlerName() {
        return "bindDoctorResult";
    }

    @Override
    protected int initTitle() {
        return R.string.bind_doctor;
    }

    @Override
    protected void registerHandler(SWebView sWebView) {
        sWebView.registerHandler(h5HandlerName(), new SBridgeHandler() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void handler(String data) {
                SBridgeResult<Doctor> sBridgeResult = JsonUtil.fromJson(data, new TypeToken<SBridgeResult<Doctor>>() {
                }.getType());

                mPresenter.checkBindDoctorState(sBridgeResult);
            }
        });
    }

    @Override
    public void setPresenter(BindDoctorContract.Presenter presenter) {
        this.mPresenter = (BindDoctorPresenter) presenter;
    }

    @Override
    public void onBindDoctorSuccess(@NotNull String message) {
        finish();
    }

    @Override
    public void onBindDoctorFailed(@NotNull String message) {
        showCenterToast(message);
    }

    @Override
    public void onIsSameDoctorCallback(@NotNull String message) {
        showCenterToast(message);
        finish();
    }
}
