package com.sumian.sleepdoctor.widget.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <pre>
 *     @@author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/25 9:18
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SumianAlertDialog {

    private final Context mContext;
    private final Dialog mDialog;
    @BindView(R.id.iv_top)
    ImageView mIvTop;
    @BindView(R.id.iv_close)
    ImageView mIvClose;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_message)
    TextView mTvMessage;
    @BindView(R.id.btn_left)
    Button mBtnLeft;
    @BindView(R.id.btn_right)
    Button mBtnRight;
    private boolean mIsCloseBtnVisible;
    private int mIconRes;
    private int mTitleRes;
    private int mLeftBtnTextRes;
    private View.OnClickListener mLeftBtnClickListener;
    private int mRightBtnTextRes;
    private View.OnClickListener mRightBtnClickListener;
    private boolean mWhitenLeft;
    private boolean mWhitenRight;
    private String mMessage;

    public SumianAlertDialog(Context context) {
        mContext = context;
        mDialog = new Dialog(context, R.style.SumianDialog);
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(context).inflate(R.layout.lay_alert_dialog, null, false);
        ButterKnife.bind(this, inflate);
        mDialog.setContentView(inflate);
    }

    private void updateView() {
        mIvClose.setVisibility(mIsCloseBtnVisible ? View.VISIBLE : View.GONE);
        mIvTop.setVisibility(mIconRes == 0 ? View.GONE : View.VISIBLE);
        mIvTop.setImageResource(mIconRes);
        mTvTitle.setVisibility(mTitleRes == 0 ? View.GONE : View.VISIBLE);
        mTvTitle.setText(mTitleRes);
        mTvMessage.setVisibility(TextUtils.isEmpty(mMessage) ? View.GONE : View.VISIBLE);
        mTvMessage.setText(mMessage);
        mBtnLeft.setVisibility(mLeftBtnTextRes == 0 ? View.GONE : View.VISIBLE);
        if (mLeftBtnTextRes != 0) {
            mBtnLeft.setText(mLeftBtnTextRes);
        }
        mBtnRight.setVisibility(mRightBtnTextRes == 0 ? View.GONE : View.VISIBLE);
        if (mRightBtnTextRes != 0) {
            mBtnRight.setText(mRightBtnTextRes);
        }
        if (mWhitenLeft) {
            mBtnLeft.setBackgroundResource(R.drawable.bg_btn_white);
            mBtnLeft.setTextColor(mContext.getResources().getColor(R.color.t5_color));
        }
        if (mWhitenRight) {
            mBtnRight.setBackgroundResource(R.drawable.bg_btn_white);
            mBtnRight.setTextColor(mContext.getResources().getColor(R.color.t5_color));
        }
    }

    public SumianAlertDialog setCloseIconVisible(boolean visible) {
        mIsCloseBtnVisible = visible;
        return this;
    }

    public SumianAlertDialog setTopIconResource(@DrawableRes int iconRes) {
        mIconRes = iconRes;
        return this;
    }

    public SumianAlertDialog setTitle(@StringRes int titleRes) {
        mTitleRes = titleRes;
        return this;
    }

    public SumianAlertDialog setMessage(@StringRes int messageRes) {
        mMessage = mContext.getString(messageRes);
        return this;
    }

    public SumianAlertDialog setMessage(String message) {
        mMessage = message;
        return this;
    }

    @SuppressWarnings("unused")
    public SumianAlertDialog setLeftBtn(@StringRes int leftBtnTextRes, View.OnClickListener listener) {
        mLeftBtnTextRes = leftBtnTextRes;
        mLeftBtnClickListener = listener;
        return this;
    }


    public SumianAlertDialog setRightBtn(@StringRes int rightBtnTextRes, View.OnClickListener listener) {
        mRightBtnTextRes = rightBtnTextRes;
        mRightBtnClickListener = listener;
        return this;
    }

    public void show() {
        updateView();
        mDialog.show();
    }

    public SumianAlertDialog setCancelable() {
        mDialog.setCancelable(false);
        return this;
    }

    public SumianAlertDialog setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        mDialog.setOnKeyListener(onKeyListener);
        return this;
    }

    @OnClick({R.id.iv_close, R.id.btn_left, R.id.btn_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                break;
            case R.id.btn_left:
                if (mLeftBtnClickListener != null) {
                    mLeftBtnClickListener.onClick(view);
                }
                break;
            case R.id.btn_right:
                if (mRightBtnClickListener != null) {
                    mRightBtnClickListener.onClick(view);
                }
                break;
            default:
                break;
        }
        mDialog.dismiss();
    }

    public SumianAlertDialog whitenLeft() {
        mWhitenLeft = true;
        return this;
    }

    public SumianAlertDialog whitenRight() {
        mWhitenRight = true;
        return this;
    }
}
