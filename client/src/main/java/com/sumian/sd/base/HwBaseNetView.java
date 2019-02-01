package com.sumian.sd.base;

/**
 * Created by jzz
 * on 2017/10/11.
 * desc:
 */

public interface HwBaseNetView<Presenter> extends HwBaseView<Presenter> {

    default void onFailure(String error) {
    }

    default void onBegin() {
    }

    default void onFinish() {
    }
}
