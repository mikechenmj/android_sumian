package com.sumian.sd.doctor.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.sumian.sd.R;
import com.sumian.sd.base.SdBaseWebViewActivity;
import com.sumian.sd.doctor.bean.Doctor;
import com.sumian.sd.doctor.bean.DoctorService;
import com.sumian.sd.doctor.contract.BindDoctorContract;
import com.sumian.sd.doctor.presenter.BindDoctorPresenter;
import com.sumian.sd.h5.H5Uri;
import com.sumian.sd.main.MainActivity;
import com.sumian.sd.utils.JsonUtil;
import com.sumian.sd.widget.webview.SBridgeHandler;
import com.sumian.sd.widget.webview.SBridgeResult;
import com.sumian.sd.widget.webview.SWebView;

import org.jetbrains.annotations.NotNull;

/**
 * Created by sm
 * on 2018/5/28 11:40
 * desc:
 **/
public class DoctorWebActivity extends SdBaseWebViewActivity<BindDoctorPresenter> implements BindDoctorContract.View {

    private static final String ARGS_URL = "com.sumian.sleepdoctor.extra.args.url";

    private String mArgUrl;
    private boolean mIsFromRecord;
    private DoctorService mDoctorService;

    public static void show(DoctorServiceWebActivity context, String url) {
        Bundle extras = new Bundle();
        //"https://sd-dev.sumian.com/doctor/1?scheme=" + uriQuery
        extras.putString(ARGS_URL, url);
        DoctorWebActivity.show(context, DoctorWebActivity.class, extras);
    }

    public static void show(Context context, String url, DoctorService doctorService, boolean isFromRecord) {
        Bundle extras = new Bundle();
        //"https://sd-dev.sumian.com/doctor/1?scheme=" + uriQuery
        extras.putString(ARGS_URL, url);
        extras.putParcelable(ScanDoctorQrCodeActivity.EXTRAS_DOCTOR_SERVICE, doctorService);
        extras.putBoolean(ScanDoctorQrCodeActivity.EXTRAS_FROM_RECORD, isFromRecord);
        DoctorWebActivity.show(context, DoctorWebActivity.class, extras);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        if (bundle != null) {
            this.mIsFromRecord = bundle.getBoolean(ScanDoctorQrCodeActivity.EXTRAS_FROM_RECORD, false);
            this.mArgUrl = bundle.getString(ARGS_URL);
            this.mDoctorService = bundle.getParcelable(ScanDoctorQrCodeActivity.EXTRAS_DOCTOR_SERVICE);
        }
        return super.initBundle(bundle);
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
    protected String getUrlContentPart() {
        Uri argUri = Uri.parse(mArgUrl);
        String originUrl = H5Uri.BIND_DOCTOR;
        return originUrl.replace("{id}", argUri.getQueryParameter("id"));
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        BindDoctorPresenter.Companion.init(this);
    }

    @Override
    protected String initTitle() {
        return getString(R.string.bind_doctor);
    }

    @Override
    protected void registerHandler(@NonNull SWebView sWebView) {
        sWebView.registerHandler("bindDoctorResult", new SBridgeHandler() {
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
        if (mIsFromRecord) {
            DoctorServiceWebActivity.show(this, mDoctorService, true);
        } else {
            MainActivity.Companion.launch(MainActivity.TAB_SD_1, null);
        }
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
