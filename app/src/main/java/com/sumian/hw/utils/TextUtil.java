package com.sumian.hw.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;

import com.sumian.sd.R;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/10 14:17
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class TextUtil {

    @NonNull
    public static SpannableString getSpannableString(int number, int unitSize) {
        String text = String.valueOf(number);
        return getSpannableString(text, unitSize);
    }

    @NonNull
    public static SpannableString getSpannableString(String text, int unitSize) {
        SpannableString span = new SpannableString(text);
        span.setSpan(new AbsoluteSizeSpan(unitSize), 0, text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return span;
    }

    public static CharSequence getPercentNumberCharSequence(int percent, int numberSize, int percentSize) {
        SpannableString numberSs = getSpannableString(percent, numberSize);
        SpannableString percentSs = getSpannableString("%", percentSize);
        return TextUtils.concat(numberSs, percentSs);
    }

    public static CharSequence getPercentNumberCharSequence(Context context, int percent) {
        int numberSize = context.getResources().getDimensionPixelSize(R.dimen.font_22);
        int percentSize = context.getResources().getDimensionPixelSize(R.dimen.font_12);
        return getPercentNumberCharSequence(percent, numberSize, percentSize);
    }
}
