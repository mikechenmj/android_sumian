package com.sumian.sddoctor.account.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.sddoctor.R;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;


/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/25 9:18
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class AppVersionUpgradeAlertDialog implements View.OnClickListener {

    private final Context mContext;
    private final Dialog mDialog;
    private ImageView mIvTop;
    private ImageView mIvClose;
    private TextView mTvTitle;
    private TextView mTvMessage;
    private Button mBtnLeft;
    private Button mBtnRight;
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
    private CharSequence mMessageCharSequence;
    private int mMessageGravity = Gravity.START;

    public AppVersionUpgradeAlertDialog(Context context) {
        mContext = context;
        mDialog = new Dialog(context, R.style.SumianDialog);
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(context).inflate(R.layout.lay_alert_app_version_upgrade_dialog, null, false);
        mIvTop = inflate.findViewById(R.id.iv_top);
        mIvClose = inflate.findViewById(R.id.iv_close);
        mIvClose.setOnClickListener(this);
        mTvTitle = inflate.findViewById(R.id.tv_title);
        mTvMessage = inflate.findViewById(R.id.tv_message);
        mBtnLeft = inflate.findViewById(R.id.btn_left);
        mBtnLeft.setOnClickListener(this);
        mBtnRight = inflate.findViewById(R.id.btn_right);
        mBtnRight.setOnClickListener(this);
        mDialog.setContentView(inflate);
    }

    private void updateView() {
        mIvClose.setVisibility(mIsCloseBtnVisible ? View.VISIBLE : View.GONE);
        mIvTop.setVisibility(mIconRes == 0 ? View.GONE : View.VISIBLE);
        mIvTop.setImageResource(mIconRes);
        mTvTitle.setVisibility(mTitleRes == 0 ? View.GONE : View.VISIBLE);
        mTvTitle.setText(mTitleRes);

        if (TextUtils.isEmpty(mMessage)) {
            mTvMessage.setText(mMessageCharSequence);
            mTvMessage.setMaxLines(10);
            mTvMessage.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            mTvMessage.setText(mMessage);
        }

        mTvMessage.setVisibility(TextUtils.isEmpty(mMessage) && TextUtils.isEmpty(mMessageCharSequence) ? View.GONE : View.VISIBLE);

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
        mTvMessage.setGravity(mMessageGravity);
    }

    public AppVersionUpgradeAlertDialog setCloseIconVisible(boolean visible) {
        mIsCloseBtnVisible = visible;
        return this;
    }

    public AppVersionUpgradeAlertDialog setTopIconResource(@DrawableRes int iconRes) {
        mIconRes = iconRes;
        return this;
    }

    public AppVersionUpgradeAlertDialog setTitle(@StringRes int titleRes) {
        mTitleRes = titleRes;
        return this;
    }

    public AppVersionUpgradeAlertDialog setMessage(@StringRes int messageRes) {
        mMessage = mContext.getString(messageRes);
        return this;
    }

    public AppVersionUpgradeAlertDialog setVersionMsg(String versionMsg) {
        mMessageCharSequence = versionMsg;
        return this;
    }

    public AppVersionUpgradeAlertDialog setMessageGravity(int gravity) {
        mMessageGravity = gravity;
        return this;
    }

    public AppVersionUpgradeAlertDialog setMessage(String message) {
        mMessage = message;
        return this;
    }

    public AppVersionUpgradeAlertDialog setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        mDialog.setOnKeyListener(onKeyListener);
        return this;
    }

    @SuppressWarnings("unused")
    public AppVersionUpgradeAlertDialog setLeftBtn(@StringRes int leftBtnTextRes, View.OnClickListener listener) {
        mLeftBtnTextRes = leftBtnTextRes;
        mLeftBtnClickListener = listener;
        return this;
    }


    public AppVersionUpgradeAlertDialog setRightBtn(@StringRes int rightBtnTextRes, View.OnClickListener listener) {
        mRightBtnTextRes = rightBtnTextRes;
        mRightBtnClickListener = listener;
        return this;
    }

    public void show() {
        updateView();
        mDialog.show();
    }

    public AppVersionUpgradeAlertDialog setCancelable(boolean cancelable) {
        mDialog.setCancelable(cancelable);
        return this;
    }

    public AppVersionUpgradeAlertDialog whitenLeft() {
        mWhitenLeft = true;
        return this;
    }

    public AppVersionUpgradeAlertDialog whitenRight() {
        mWhitenRight = true;
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                break;
            case R.id.btn_left:
                if (mLeftBtnClickListener != null) {
                    mLeftBtnClickListener.onClick(v);
                }
                break;
            case R.id.btn_right:
                if (mRightBtnClickListener != null) {
                    mRightBtnClickListener.onClick(v);
                }
                break;
            default:
                break;
        }
        mDialog.dismiss();
    }
}
