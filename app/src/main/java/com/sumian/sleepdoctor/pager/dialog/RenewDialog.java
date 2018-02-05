package com.sumian.sleepdoctor.pager.dialog;

import android.content.Context;
import android.support.annotation.LayoutRes;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.sumian.sleepdoctor.R;

import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/2/2.
 * desc:
 */

public class RenewDialog extends QMUIDialog {

    public RenewDialog(Context context) {
        this(context, R.style.QMUI_Dialog);
    }

    public RenewDialog(Context context, int styleRes) {
        super(context, styleRes);
    }


    public RenewDialog bindContentView(@LayoutRes int id) {
        setContentView(id);
        ButterKnife.bind(this);
        return this;
    }

}
