package com.sumian.sd.theme.three.attr;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import com.sumian.hw.report.widget.text.CountSleepDurationTextView;
import com.sumian.sd.theme.three.attr.base.SkinAttr;
import com.sumian.sd.theme.three.utils.SkinResourcesUtils;

/**
 * Created by dq
 * <p>
 * on 2018/9/18
 * <p>
 * desc:
 */
public class CountSleepDurationTextViewAttr extends SkinAttr {

    @Override
    protected void applySkin(View view) {
        if (view instanceof CountSleepDurationTextView) {
            CountSleepDurationTextView countSleepDurationTextView = (CountSleepDurationTextView) view;
            if (isDrawable()) {
                Drawable drawable = SkinResourcesUtils.getDrawable(attrValueRefId);
                CharSequence text = countSleepDurationTextView.getText();
                if (TextUtils.isEmpty(text)) {
                    countSleepDurationTextView.setDuration(0);
                }
            }
        }
    }

    @Override
    protected void applyNightMode(View view) {
        if (view instanceof CountSleepDurationTextView) {
            CountSleepDurationTextView countSleepDurationTextView = (CountSleepDurationTextView) view;
            if (isDrawable()) {
                Drawable nightDrawable = SkinResourcesUtils.getNightDrawable(attrValueRefName);
                CharSequence text = countSleepDurationTextView.getText();
                if (TextUtils.isEmpty(text)) {
                    countSleepDurationTextView.setDuration(0);
                }
            }
        }
    }
}
