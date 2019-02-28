package com.sumian.common.widget.panel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by jzz
 * <p>
 * on 2019/2/27
 * <p>
 * desc:  用于监听软键盘弹出的ViewGroup
 */
public class PanelFrameLayout extends FrameLayout {

    private static final String TAG = PanelFrameLayout.class.getSimpleName();

    private Rect mLastFrame = new Rect();
    private int mDisplayHeight;
    private int mDisplayDefaultHeight;

    private OnKeyboardListener onKeyboardListener;

    public PanelFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public PanelFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanelFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnKeyboardListener(OnKeyboardListener onKeyboardListener) {
        this.onKeyboardListener = onKeyboardListener;
    }

    public void setup(Activity activity) {
        registerDefaultDisplay(activity);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Context context = getContext();
        if (context instanceof Activity) {
            registerDefaultDisplay((Activity) context);
        }
    }

    private void registerDefaultDisplay(Activity activity) {
        // Get DisplayHeight
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        mDisplayDefaultHeight = metrics.heightPixels;
        //获取当前屏幕的实际真实高度  用该方式来解决刘海屏的相关问题
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        mDisplayHeight = metrics.heightPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        calculateHeightMeasureSpec();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void calculateHeightMeasureSpec() {
        Rect frame = new Rect();
        getWindowVisibleDisplayFrame(frame);

        int bottomChangeSize = 0;
        if (mLastFrame.bottom > 0) {
            bottomChangeSize = frame.bottom - mLastFrame.bottom;  //当前的bottom-lastBottom 计算当前窗口大小
        }

        mLastFrame.set(frame);

        if (bottomChangeSize == 0) {//窗口大小变化完毕,才进行软键盘事件分发
            // int changeSize = mLastFrame.bottom - mDisplayHeight;
            if (isOpenSoftKeyboard()) {
                notifyOpen();
            } else {
                notifyClose();
            }
        } //else {
        // if (bottomChangeSize > 0) {//正在变化窗口事件  关闭软键盘中  不进行事件分发
        //  notifyClose();
        //  } else {   //正在变化窗口事件 打开软键盘中  不进行事件分发
        //   notifyOpen();
        //  }
        //}
    }

    private boolean isOpenSoftKeyboard() {
        return mLastFrame.bottom != 0 && !(mLastFrame.bottom == mDisplayHeight || mLastFrame.bottom == mDisplayDefaultHeight);
    }

    private void notifyOpen() {
        if (onKeyboardListener != null) {
            onKeyboardListener.onKeyboardOpen();
        }
    }

    private void notifyClose() {
        if (onKeyboardListener != null) {
            onKeyboardListener.onKeyboardClose();
        }
    }

    public interface OnKeyboardListener {
        void onKeyboardOpen();

        void onKeyboardClose();
    }
}
