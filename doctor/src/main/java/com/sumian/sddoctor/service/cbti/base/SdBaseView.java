package com.sumian.sddoctor.service.cbti.base;

/**
 * Created by jzz
 * on 2017/4/4.
 * <p>
 * desc:
 */

public interface SdBaseView<Presenter extends SdBasePresenter> {

    default void setPresenter(Presenter presenter) {

    }

    default void onFailure(String error) {

    }

    default void onBegin() {

    }

    default void onFinish() {

    }
}
