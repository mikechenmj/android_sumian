package com.sumian.sd.theme.three.attr;

import android.view.View;

import com.sumian.hw.widget.refresh.BlueRefreshView;
import com.sumian.sd.theme.three.attr.base.SkinAttr;
import com.sumian.sd.theme.three.utils.SkinResourcesUtils;


/**
 *
 */
public class SwipeRefreshLayoutBgAttr extends SkinAttr {

    @Override
    protected void applySkin(View view) {
        if (view instanceof BlueRefreshView) {
            BlueRefreshView blueRefreshView = (BlueRefreshView) view;
            int color = SkinResourcesUtils.getColor(attrValueRefId);
            blueRefreshView.setProgressBackgroundColorSchemeColor(color);
        }
    }

    @Override
    protected void applyNightMode(View view) {
        if (view instanceof BlueRefreshView) {
            BlueRefreshView blueRefreshView = (BlueRefreshView) view;
            int color = SkinResourcesUtils.getNightColor(attrValueRefId);
            blueRefreshView.setProgressBackgroundColorSchemeColor(color);
        }
    }
}
