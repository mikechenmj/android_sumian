package com.sumian.app.base;

/**
 * Created by jzz
 * on 2017/10/11.
 * desc:
 */

public interface BaseNetView<Presenter> extends BaseView<Presenter> {

    default void onFailure(String error) {
    }

    default void onBegin() {
    }

    default void onFinish() {
    }
}
