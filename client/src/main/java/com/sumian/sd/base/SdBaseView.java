package com.sumian.sd.base;

import com.sumian.common.base.BaseViewModel;

/**
 * Created by jzz
 * on 2017/4/4.
 * <p>
 * desc:
 */
@Deprecated
public interface SdBaseView<Presenter extends BaseViewModel> {

    default void setPresenter(Presenter presenter) {

    }

    default void onFailure(String error) {

    }

    default void onBegin() {

    }

    default void onFinish() {

    }
}
