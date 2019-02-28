package com.sumian.common.widget.panel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

/**
 * Created by jzz
 * <p>
 * on 2019/2/28
 * <p>
 * desc:
 */
@SuppressWarnings("ALL")
public class PanelNestScrollView extends NestedScrollView {

    public PanelNestScrollView(@NonNull Context context) {
        this(context, null);
    }

    public PanelNestScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanelNestScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 封装反射调用 滑动到子View
     *
     * @param child child
     */
    public void invokeSuperScrollToChild(View child) {
        try {
            Method scrollToChild = this.getClass().getSuperclass().getDeclaredMethod("scrollToChild", View.class);
            scrollToChild.setAccessible(true);
            scrollToChild.invoke(this, child);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
