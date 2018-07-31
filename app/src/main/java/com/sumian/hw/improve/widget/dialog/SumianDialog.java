package com.sumian.hw.improve.widget.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/15 11:15
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SumianDialog {
    private Context mContext;
    private String mTitleText;
    private String mLeftText;
    private String mRightText;
    private View.OnClickListener mOnLeftClickListener;
    private View.OnClickListener mOnRightClickListener;
    private AlertDialog mAlertDialog;

    public SumianDialog setTitleText(@StringRes int titleTextRes) {
        mTitleText = getString(titleTextRes);
        return this;
    }

    public SumianDialog setLeftText(@StringRes int text, View.OnClickListener onClickListener) {
        mLeftText = getString(text);
        mOnLeftClickListener = onClickListener;
        return this;
    }

    public SumianDialog setRightText(@StringRes int text, View.OnClickListener onClickListener) {
        mRightText = getString(text);
        mOnRightClickListener = onClickListener;
        return this;
    }

    private SumianDialog(Context context) {
        mContext = context;
    }

    public static SumianDialog create(Context context) {
        return new SumianDialog(context);
    }

    public void show() {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.hw_lay_sumian_dialog, null, false);
        TextView titleTv = view.findViewById(R.id.tv_title);
        TextView leftTv = view.findViewById(R.id.tv_left);
        TextView rightTv = view.findViewById(R.id.tv_right);
        titleTv.setText(mTitleText);
        leftTv.setText(mLeftText);
        rightTv.setText(mRightText);
        leftTv.setOnClickListener(v -> {
            mAlertDialog.dismiss();
            if (mOnLeftClickListener != null) {
                mOnLeftClickListener.onClick(v);
            }
        });
        rightTv.setOnClickListener(v -> {
            mAlertDialog.dismiss();
            if (mOnRightClickListener != null) {
                mOnRightClickListener.onClick(v);
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.SumianDialog);
        mAlertDialog = builder.setView(view).create();
        mAlertDialog.show();
    }

    public void dismiss() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    private String getString(@StringRes int stringRes) {
        return mContext.getResources().getString(stringRes);
    }
}
