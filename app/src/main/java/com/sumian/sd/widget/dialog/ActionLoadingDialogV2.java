package com.sumian.sd.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;

import com.sumian.sd.R;

/**
 * Created by jzz
 * on 2017/11/3.
 * <p>
 * desc:
 */

@SuppressWarnings("deprecation")
public class ActionLoadingDialogV2 extends Dialog {

    public ActionLoadingDialogV2(@NonNull Context context) {
        super(context, android.R.style.Theme_Holo_Light_DialogWhenLarge_NoActionBar);
        setContentView(R.layout.lay_action_loading);
    }
}
