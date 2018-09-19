package com.sumian.sd.theme.three.attr;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.sumian.sd.theme.three.attr.base.SkinAttr;
import com.sumian.sd.theme.three.utils.SkinResourcesUtils;


/**
 * Created by _SOLID
 * Date:2016/4/13
 * Time:22:53
 */
public class TextDrawableAttr extends SkinAttr {

    @Override
    protected void applySkin(View view) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            Drawable leftDrawable = SkinResourcesUtils.getDrawable(attrValueRefId);
            tv.setCompoundDrawablesRelativeWithIntrinsicBounds(leftDrawable, null, null, null);
        }
    }

    @Override
    protected void applyNightMode(View view) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            Drawable leftDrawable = SkinResourcesUtils.getNightDrawable(attrValueRefName);
            tv.setCompoundDrawablesRelativeWithIntrinsicBounds(leftDrawable, null, null, null);
        }
    }
}
