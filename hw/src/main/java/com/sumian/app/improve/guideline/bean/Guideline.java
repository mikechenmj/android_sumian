package com.sumian.app.improve.guideline.bean;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * Created by sm
 * on 2018/3/22.
 * desc:
 */

public class Guideline {

    public @StringRes
    int h1Label;
    public @StringRes
    int h2Label;
    public @DrawableRes
    int iconId;
    public int indicatorPosition;

    @Override
    public String toString() {
        return "Guideline{" +
            "h1Label='" + h1Label + '\'' +
            ", h2Label='" + h2Label + '\'' +
            ", iconId=" + iconId +
            ", indicatorPosition=" + indicatorPosition +
            '}';
    }
}
