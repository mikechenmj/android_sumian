package com.sumian.sd.base;

import android.view.Gravity;

import com.sumian.common.helper.ToastHelper;
import com.sumian.common.h5.BaseWebViewActivity;

/**
 * Created by sm
 * on 2018/5/25 10:03
 * desc:
 **/
public abstract class SdBaseWebViewActivity<Presenter extends SdBasePresenter> extends BaseWebViewActivity {

    protected Presenter mPresenter;

    protected void showCenterToast(String message) {
        ToastHelper.show(this, message, Gravity.CENTER);
    }

}
