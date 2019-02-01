package com.sumian.sd.base;

import android.view.Gravity;

import com.sumian.common.h5.BaseWebViewActivity;
import com.sumian.common.helper.ToastHelper;

/**
 * Created by sm
 * on 2018/5/25 10:03
 * desc:
 **/
public abstract class SdBaseWebViewActivity<Presenter extends SdBasePresenter> extends BaseWebViewActivity {

    private static final String H5_ASSET_PATH = "h5/static";

    protected Presenter mPresenter;

    @Override
    protected void initWidget() {
        super.initWidget();
    }

    protected void showCenterToast(String message) {
        ToastHelper.show(this, message, Gravity.CENTER);
    }
}
