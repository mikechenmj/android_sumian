package com.sumian.sleepdoctor.widget;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sumian.sleepdoctor.R;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/25 15:25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class IconToast {
    public static void show(Context context, @StringRes int textRes, boolean success) {
        show(context, textRes, success ? R.mipmap.ic_success_white : R.mipmap.ic_failed_white);
    }

    public static void show(Context context, @StringRes int textRes, @DrawableRes int iconRes) {
        Toast toast = new Toast(context);
        View inflate = LayoutInflater.from(context).inflate(R.layout.toast_icon_text, null, false);
        TextView textView = inflate.findViewById(R.id.tv);
        ImageView imageView = inflate.findViewById(R.id.iv);
        textView.setText(textRes);
        imageView.setImageResource(iconRes);
        toast.setView(inflate);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
