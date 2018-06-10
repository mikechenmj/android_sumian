package com.sumian.sleepdoctor.base;

/**
 * Created by jzz
 * on 2017/4/4.
 * <p>
 * desc:
 */

public interface BaseView<Presenter extends BasePresenter> {

    default void setPresenter(Presenter presenter) {

    }

    default void onFailure(String error) {

    }

    default void onBegin() {

    }

    default void onFinish() {

    }
}
