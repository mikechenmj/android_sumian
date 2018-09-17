package com.sumian.sd.theme;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by dq
 */

public class ViewAttributeUtil {

    public static int getAttributeValue(AttributeSet attr, int paramInt) {
        int value = -1;
        int count = attr.getAttributeCount();
        for (int i = 0; i < count; i++) {
            if (attr.getAttributeNameResource(i) == paramInt) {
                String str = attr.getAttributeValue(i);
                if (!TextUtils.isEmpty(str) && str.startsWith("?")) {
                    value = Integer.valueOf(str.substring(1, str.length()));
                    return value;
                }
            }
        }
        return value;
    }

    public static int getBackgroundAttribute(AttributeSet attr) {
        return getAttributeValue(attr, android.R.attr.background);
    }

    public static int getTextColorAttribute(AttributeSet attr) {
        return getAttributeValue(attr, android.R.attr.textColor);
    }

    public static int getImageViewAttribute(AttributeSet attr) {
        return getAttributeValue(attr, android.R.attr.src);
    }

    public static void applyimageViewDrawable(ITheme ci, Resources.Theme theme, int paramInt) {
        TypedArray ta = theme.obtainStyledAttributes(new int[]{paramInt});
        int res = ta.getResourceId(0, 0);
        if (null != ci) {
            ((ImageView) ci.getView()).setImageResource(res);
        }
        ta.recycle();
    }

    public static void applyBackgroundDrawable(ITheme ci, Resources.Theme theme, int paramInt) {
        TypedArray ta = theme.obtainStyledAttributes(new int[]{paramInt});
        Drawable drawable = ta.getDrawable(0);
        if (null != ci) {
            (ci.getView()).setBackground(drawable);
        }
        ta.recycle();
    }

    public static void applyCardViewBgColor(CardView cardView, Resources.Theme theme, int paramInt) {
        TypedArray ta = theme.obtainStyledAttributes(new int[]{paramInt});
        int resourceId = ta.getColor(0, 0);
        if (null != cardView) {
            cardView.setCardBackgroundColor(resourceId);
        }
        ta.recycle();
    }

    public static void applyCardViewBackgroundDrawable(CardView cardView, Resources.Theme theme, int paramInt) {
        TypedArray ta = theme.obtainStyledAttributes(new int[]{paramInt});
        Drawable drawable = ta.getDrawable(0);
        if (null != cardView) {
            cardView.setBackground(drawable);
        }
        ta.recycle();
    }

    public static void applyTextDrawable(ITheme ci, Resources.Theme theme, int paramInt) {
        TypedArray ta = theme.obtainStyledAttributes(new int[]{paramInt});
        int resourceId = ta.getColor(0, 0);
        if (null != ci && ci instanceof TextView) {
            ((TextView) ci.getView()).setTextColor(resourceId);
        }
        ta.recycle();
    }
}
