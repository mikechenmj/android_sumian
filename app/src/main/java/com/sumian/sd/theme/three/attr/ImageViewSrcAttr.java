package com.sumian.sd.theme.three.attr;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;

import com.sumian.sd.theme.three.attr.base.SkinAttr;
import com.sumian.sd.theme.three.utils.SkinResourcesUtils;


/**
 * Created by _SOLID
 * Date:2017/2/15
 * Time:17:39
 * Desc:
 */

public class ImageViewSrcAttr extends SkinAttr {
    @Override
    protected void applySkin(View view) {
        if (view instanceof ImageView) {
            ImageView iv = (ImageView) view;
            if (isDrawable()) {
                iv.setImageDrawable(SkinResourcesUtils.getDrawable(attrValueRefId));
            } else if (isColor()) {
                iv.setImageDrawable(new ColorDrawable(SkinResourcesUtils.getColor(attrValueRefId)));
            }
        }
    }

    @Override
    protected void applyNightMode(View view) {
        super.applyNightMode(view);
        if (view instanceof ImageView) {
            ImageView iv = (ImageView) view;
            if (isDrawable()) {
                iv.setImageDrawable(SkinResourcesUtils.getNightDrawable(attrValueRefName));
            } else if (isColor()) {
                iv.setImageDrawable(new ColorDrawable(SkinResourcesUtils.getNightColor(attrValueRefId)));
            }
        }
    }
}
