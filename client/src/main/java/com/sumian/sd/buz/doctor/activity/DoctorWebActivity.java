package com.sumian.sd.buz.doctor.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.reflect.TypeToken;
import com.sumian.common.h5.bean.SBridgeResult;
import com.sumian.common.h5.handler.SBridgeHandler;
import com.sumian.common.h5.widget.SWebView;
import com.sumian.common.statistic.StatUtil;
import com.sumian.common.utils.JsonUtil;
import com.sumian.sd.R;
import com.sumian.sd.base.SdBaseWebViewActivity;
import com.sumian.sd.buz.doctor.bean.Doctor;
import com.sumian.sd.buz.doctor.bean.DoctorService;
import com.sumian.sd.buz.doctor.contract.BindDoctorContract;
import com.sumian.sd.buz.doctor.presenter.BindDoctorPresenter;
import com.sumian.sd.common.h5.H5Uri;
import com.sumian.sd.main.MainActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import androidx.annotation.NonNull;

/**
 * Created by sm
 * on 2018/5/28 11:40
 * desc:
 **/
@SuppressWarnings("ALL")
public class DoctorWebActivity extends SdBaseWebViewActivity implements BindDoctorContract.View {

    private static final String ARGS_URL = "com.sumian.sleepdoctor.extra.args.url";

    private String mArgUrl;
    private boolean mIsFromRecord;
    private DoctorService mDoctorService;
    protected BindDoctorPresenter mPresenter;

    public static void show(DoctorServiceWebActivity context, String url) {
        Bundle extras = new Bundle();
        //"https://sd-dev.sumian.com/doctor/1?scheme=" + uriQuery
        extras.putString(ARGS_URL, url);

        Intent intent = new Intent(context, DoctorWebActivity.class);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    public static void show(Context context, String url, DoctorService doctorService, boolean isFromRecord) {
        Bundle extras = new Bundle();
        //"https://sd-dev.sumian.com/doctor/1?scheme=" + uriQuery
        extras.putString(ARGS_URL, url);
        extras.putParcelable(ScanDoctorQrCodeActivity.EXTRAS_DOCTOR_SERVICE, doctorService);
        extras.putBoolean(ScanDoctorQrCodeActivity.EXTRAS_FROM_RECORD, isFromRecord);

        Intent intent = new Intent(context, DoctorWebActivity.class);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatUtil.INSTANCE.event("page_binding_doctor", null);
    }

    @Override
    protected void initBundle(@NonNull Bundle bundle) {
        mPresenter = BindDoctorPresenter.init(this);
        this.mIsFromRecord = bundle.getBoolean(ScanDoctorQrCodeActivity.EXTRAS_FROM_RECORD, false);
        this.mArgUrl = bundle.getString(ARGS_URL);
        this.mDoctorService = bundle.getParcelable(ScanDoctorQrCodeActivity.EXTRAS_DOCTOR_SERVICE);
    }

    @Override
    protected String getUrlContentPart() {
        Uri argUri = Uri.parse(mArgUrl);
        String originUrl = H5Uri.BIND_DOCTOR;
        //https://sd-dev.sumian.com/doctor/null?url=wxxxxxxxxxxx
        return originUrl.replace("{url}", argUri.toString());
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
            MainActivity.launch(MainActivity.TAB_2, null);
        }
        StatUtil.INSTANCE.event("e_binding_success", null);
    }

    @Override
    public void onBindDoctorFailed(@NotNull String message) {
        ToastUtils.showShort(message);
    }

    @Override
    public void onIsSameDoctorCallback(@NotNull String message) {
        ToastUtils.showShort(message);
        finish();
    }
}
