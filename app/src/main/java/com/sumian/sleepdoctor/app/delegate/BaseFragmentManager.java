package com.sumian.sleepdoctor.app.delegate;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by sm
 * on 2018/1/23.
 * desc:
 */

public interface BaseFragmentManager<Fragment> {

    void replaceFragment(@NonNull Class<? extends Fragment> clx);

    void replaceFragment(@NonNull Class<? extends Fragment> clx, @Nullable Bundle args);

    void replaceFragment(@IdRes int containerId, Class<? extends Fragment> clx, @Nullable Bundle args);

    void showNavTab();

    void hideNavTab();

    void onBackPressedDelegate();

    void goHome();

    void onRelease();

}
