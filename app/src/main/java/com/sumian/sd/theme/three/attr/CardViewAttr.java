package com.sumian.sd.theme.three.attr;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.view.View;

import com.sumian.sd.theme.three.attr.base.SkinAttr;
import com.sumian.sd.theme.three.utils.SkinResourcesUtils;

/**
 * Created by dq
 * <p>
 * on 2018/9/18
 * <p>
 * desc:
 */
public class CardViewAttr extends SkinAttr {

    @Override
    protected void applySkin(View view) {
        if (view instanceof CardView) {
            CardView cardView = (CardView) view;
            if (isColor()) {
                int color = SkinResourcesUtils.getColor(attrValueRefId);
                cardView.setCardBackgroundColor(color);
            } else if (isDrawable()) {
                Drawable bg = SkinResourcesUtils.getDrawable(attrValueRefId);
                cardView.setBackground(bg);
            }
        }
    }

    @Override
    protected void applyNightMode(View view) {
        if (view instanceof CardView) {
            CardView cardView = (CardView) view;
            if (isColor()) {
                cardView.setCardBackgroundColor(SkinResourcesUtils.getNightColor(attrValueRefId));
            } else if (isDrawable()) {
                cardView.setBackground(SkinResourcesUtils.getNightDrawable(attrValueRefName));
            }
        }
    }
}
