package com.sumian.sleepdoctor.app.delegate;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.bumptech.glide.manager.SupportRequestManagerFragment;

/**
 * Created by sm
 * on 2018/1/23.
 * desc:
 */

public class FragmentLifecycleDelegate extends FragmentManager.FragmentLifecycleCallbacks {

    private static final String TAG = FragmentManagerDelegate.class.getSimpleName();

    private FragmentLifecycleCallback mCallback;

    public FragmentLifecycleDelegate(FragmentLifecycleCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
        super.onFragmentAttached(fm, f, context);

        if (f instanceof SupportRequestManagerFragment) {
            return;
        }
        Log.e(TAG, "onFragmentAttached: ---------->" + f.toString());
        mCallback.onFragmentAttached(fm, f, context);
    }

    @Override
    public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        super.onFragmentCreated(fm, f, savedInstanceState);
        if (f instanceof SupportRequestManagerFragment) {
            return;
        }
        //Log.e(TAG, "onFragmentCreated: ---------->" + f.toString());
        mCallback.onFragmentCreated(fm, f, savedInstanceState);
    }

    @Override
    public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
        super.onFragmentDestroyed(fm, f);
        if (f instanceof SupportRequestManagerFragment) {
            return;
        }
        Log.e(TAG, "onFragmentDestroyed: ---------->" + f.toString());
        mCallback.onFragmentDestroyed(fm, f);
    }

    @Override
    public void onFragmentDetached(FragmentManager fm, Fragment f) {
        super.onFragmentDetached(fm, f);
        if (f instanceof SupportRequestManagerFragment) {
            return;
        }
        Log.e(TAG, "onFragmentDetached: --------->" + f.toString());
        mCallback.onFragmentDetached(fm, f);
    }

}
