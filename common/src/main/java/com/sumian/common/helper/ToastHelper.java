package com.sumian.common.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.Toast;

import com.sumian.common.R;

/**
 * Created by jzz
 * on 2017/9/22
 * <p>
 * desc:toast helper
 */
public class ToastHelper {

    @SuppressLint("StaticFieldLeak")
    private static volatile ToastHelper INSTANCE;

    private Toast mToast;
    private int mYOffset;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());

    private Context mContext;

    private ToastHelper(Context context) {
        this.mContext = context;
        @SuppressLint("ShowToast") Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, context.getResources().getDimensionPixelSize(R.dimen.toast_space_100));
        this.mYOffset = toast.getYOffset();
        this.mToast = toast;
    }

    public static void init(@NonNull Context context) {
        if (INSTANCE == null) {
            synchronized (ToastHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ToastHelper(context);
                }
            }
        }
    }

    public static void show(String content) {
        show(content, Toast.LENGTH_SHORT);
    }

    public static void show(String content, int duration) {
        show(INSTANCE.mContext, content, Gravity.BOTTOM, duration);
    }

    public static void show(int rid) {
        Context context = INSTANCE.mContext;
        show(context, context.getResources().getString(rid));
    }

    public static void show(Context context, String content) {
        show(context, content, Gravity.BOTTOM);
    }

    public static void show(Context context, String content, int gravity) {
        show(context, content, gravity, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String content, int gravity, int duration) {
        show(context, content, gravity, duration, INSTANCE.mYOffset);
    }

    public static void show(Context context, String content, int gravity, int duration, int yOffset) {
        if (INSTANCE == null) init(context.getApplicationContext());
        Toast toast = INSTANCE.mToast;
        toast.setText(content);
        toast.setDuration(duration);
        toast.setGravity(gravity, 0, yOffset);
        INSTANCE.runUiThread(toast::show);
    }

    private void runUiThread(Runnable run) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            run.run();
        } else {
            INSTANCE.mUiHandler.post(run);
        }
    }
}
