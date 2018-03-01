package com.sumian.sleepdoctor.app.delegate;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by sm
 * on 2018/1/23.
 * desc:
 */

public interface FragmentLifecycleCallback {

    void onFragmentAttached(FragmentManager fm, Fragment f, Context context);

    //void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState);

    //void onFragmentDestroyed(FragmentManager fm, Fragment f);

    //void onFragmentDetached(FragmentManager fm, Fragment f);
}
