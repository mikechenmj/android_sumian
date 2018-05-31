package com.sumian.sleepdoctor.improve.doctor.activity;

import android.net.Uri;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseWebViewActivity;
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

    private static final String TAG = DoctorWebViewActivity.class.getSimpleName();

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
    }

    @Override
    protected void initData() {
        super.initData();
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
    protected String queryParameter() {
        return "id";
    }

    @Override
    protected String appendUri() {
        return BuildConfig.H5_URI_DOCTOR;
    }

    @Override
    protected void parseUrl(String url) {
        Uri parseUrl = Uri.parse(url);
        String appendUrl = appendUri().replace("{id}", parseUrl.getQueryParameter(queryParameter()));
        super.parseUrl(appendUrl);
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
