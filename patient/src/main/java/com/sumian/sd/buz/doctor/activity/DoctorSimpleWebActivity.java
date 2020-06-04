package com.sumian.sd.buz.doctor.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.reflect.TypeToken;
import com.sumian.common.h5.bean.SBridgeResult;
import com.sumian.common.h5.handler.SBridgeHandler;
import com.sumian.common.h5.widget.SWebView;
import com.sumian.common.statistic.StatUtil;
import com.sumian.common.utils.JsonUtil;
import com.sumian.sd.buz.doctor.bean.Doctor;
import com.sumian.sd.buz.doctor.presenter.BindDoctorPresenter;
import com.sumian.sd.buz.doctor.presenter.DoctorWebContainerView;
import com.sumian.sd.buz.stat.StatConstants;
import com.sumian.sd.common.h5.SimpleWebActivity;
import com.sumian.sd.main.MainActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by sm
 * on 2018/5/28 11:40
 * desc:
 **/
@SuppressWarnings("ALL")
public class DoctorSimpleWebActivity extends SimpleWebActivity implements DoctorWebContainerView {

    private static final String ARGS_URL = "com.sumian.sleepdoctor.extra.args.url";

    protected BindDoctorPresenter mPresenter;

    public static void launch(Context context, String urlContentPart) {
        Intent intent = new Intent(context, DoctorSimpleWebActivity.class);
        intent.putExtra(SimpleWebActivity.Companion.getKEY_URL_CONTENT_PART(), urlContentPart);
        ActivityUtils.startActivity(intent);
    }

    @NotNull
    @Override
    public String getPageName() {
        return StatConstants.page_doctor_introduction;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatUtil.INSTANCE.event(StatConstants.page_binding_doctor, null);
    }

    @Override
    protected void initBundle(@NonNull Bundle bundle) {
        super.initBundle(bundle);
        mPresenter = BindDoctorPresenter.init(this);
    }

    @Override
    protected void registerHandler(@NonNull SWebView sWebView) {
        super.registerHandler(sWebView);
        sWebView.registerHandler("bindDoctorResult", new SBridgeHandler() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void handler(String data) {
                StatUtil.INSTANCE.event(StatConstants.click_doctor_introduction_page_bind_btn);
                SBridgeResult<Doctor> sBridgeResult = JsonUtil.fromJson(data, new TypeToken<SBridgeResult<Doctor>>() {
                }.getType());

                mPresenter.checkBindDoctorState(sBridgeResult);
            }
        });
    }


    public void setPresenter(BindDoctorPresenter presenter) {
        this.mPresenter = (BindDoctorPresenter) presenter;
    }


    public void onBindDoctorSuccess(@NotNull String message) {
        MainActivity.launch(MainActivity.TAB_2, null);
        StatUtil.INSTANCE.event(StatConstants.e_binding_success, null);
    }


    public void onBindDoctorFailed(@NotNull String message) {
        ToastUtils.showShort(message);
    }


    public void onIsSameDoctorCallback(@NotNull String message) {
        ToastUtils.showShort(message);
        finish();
    }
}
