package com.sumian.sd.widget.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDialog;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.sd.R;
import com.sumian.sd.widget.dialog.theme.ITheme;
import com.sumian.sd.widget.dialog.theme.LightTheme;
import com.sumian.sd.widget.dialog.theme.ThemeFactory;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/25 9:18
 *     desc   :
 *     version: 1.0
 *
 *     updated by jzz
 *
 *     on 2018/08/17
 *
 *     desc:加入黑白主题
 * </pre>
 */
public class SumianAlertDialog implements View.OnClickListener {

    private final Context mContext;

    private final Dialog mDialog;

    private CardView mCardView;
    private ImageView mIvClose;
    private ImageView mIvTop;
    private TextView mTvTitle;
    private TextView mTvMessage;
    private Button mBtnLeft;
    private Button mBtnRight;

    private boolean mIsCloseBtnVisible;

    private View.OnClickListener mLeftBtnClickListener;
    private View.OnClickListener mRightBtnClickListener;

    private boolean mWhitenLeft;
    private boolean mWhitenRight;

    private ITheme mITheme;

    private int mBgColorRes;

    private int mDismissRes;

    private int mIconRes;

    private int mTitleFontColor;
    private int mTitleRes;


    private int mMessageFontColorRes;
    private String mMessage;
    private CharSequence mMessageCharSequence;

    private int mLeftBgRes;
    private int mLeftFontColorRes;
    private int mLeftBtnTextRes;

    private int mRightBgRes;
    private int mRightFontColorRes;
    private int mRightBtnTextRes;

    private boolean mIsHideTopIcon;


    public SumianAlertDialog(Context context) {
        mContext = context;
        mITheme = ThemeFactory.create(LightTheme.class);
        mDialog = new AppCompatDialog(context, R.style.SumianDialog);
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(context).inflate(R.layout.lay_alert_dialog, null, false);

        mCardView = inflate.findViewById(R.id.card_view);
        mIvClose = inflate.findViewById(R.id.iv_close);
        mIvClose.setOnClickListener(this);
        mIvTop = inflate.findViewById(R.id.iv_top);
        mTvTitle = inflate.findViewById(R.id.tv_title);
        mTvMessage = inflate.findViewById(R.id.tv_message);
        mBtnLeft = inflate.findViewById(R.id.btn_left);
        mBtnLeft.setOnClickListener(this);
        mBtnRight = inflate.findViewById(R.id.btn_right);
        mBtnRight.setOnClickListener(this);

        mDialog.setContentView(inflate);
    }

    public SumianAlertDialog setTheme(ITheme iTheme) {
        this.mITheme = iTheme;
        return this;
    }

    private void updateView() {
        LightTheme lightTheme = (LightTheme) mITheme;

        setupTheme(lightTheme.getBgColorRes(), lightTheme.getDismissImageRes(),
                lightTheme.getNoticeImageRes(), lightTheme.getTitleColorRes(),
                lightTheme.getMessageColorRes(), lightTheme.getLeftButtonBgRes(),
                lightTheme.getLeftButtonFontColorRes(), lightTheme.getRightButtonBgRes(), lightTheme.getRightButtonFontColorRes());

        mCardView.setCardBackgroundColor(mContext.getResources().getColor(mBgColorRes));

        mIvClose.setImageResource(mDismissRes);
        mIvClose.setVisibility(mIsCloseBtnVisible ? View.VISIBLE : View.GONE);

        mIvTop.setVisibility(mIconRes == 0 || mIsHideTopIcon ? View.GONE : View.VISIBLE);
        mIvTop.setImageResource(mIconRes);

        mTvTitle.setVisibility(mTitleRes == 0 ? View.GONE : View.VISIBLE);
        if (mTitleRes != 0) {
            mTvTitle.setText(mTitleRes);
        }
        mTvTitle.setTextColor(getColor(mTitleFontColor));

        mTvMessage.setTextColor(getColor(mMessageFontColorRes));
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
        mBtnLeft.setTextColor(getColor(mLeftFontColorRes));
        mBtnLeft.setBackgroundResource(mLeftBgRes);

        mBtnRight.setVisibility(mRightBtnTextRes == 0 ? View.GONE : View.VISIBLE);
        if (mRightBtnTextRes != 0) {
            mBtnRight.setText(mRightBtnTextRes);
        }
        mBtnRight.setTextColor(getColor(mRightFontColorRes));
        mBtnRight.setBackgroundResource(mRightBgRes);

        if (mWhitenLeft) {
            mBtnLeft.setBackgroundResource(R.drawable.bg_btn_white);
            mBtnLeft.setTextColor(getColor(R.color.t5_color));
        }

        if (mWhitenRight) {
            mBtnRight.setBackgroundResource(R.drawable.bg_btn_white);
            mBtnRight.setTextColor(getColor(R.color.t5_color));
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

    public SumianAlertDialog setVersionMsg(String versionMsg) {
        mMessageCharSequence = versionMsg;
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

    public void hide() {
        if (mDialog.isShowing()) {
            mDialog.cancel();
        }
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }

    public SumianAlertDialog setCancelable(boolean cancelable) {
        mDialog.setCancelable(cancelable);
        return this;
    }

    public SumianAlertDialog setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        mDialog.setOnKeyListener(onKeyListener);
        return this;
    }

    public SumianAlertDialog setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        mDialog.setOnCancelListener(onCancelListener);
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

    public SumianAlertDialog whitenLeft() {
        mWhitenLeft = true;
        return this;
    }

    public SumianAlertDialog whitenRight() {
        mWhitenRight = true;
        return this;
    }

    public SumianAlertDialog hideTopIcon(boolean isGone) {
        this.mIsHideTopIcon = isGone;
        return this;
    }

    private int getColor(@ColorRes int colorRes) {
        return mContext.getResources().getColor(colorRes);
    }

    private void setupTheme(int bgColor, int dismissImageResource, int noticeImageResource, int titleColor,
                            int messageColor, int leftButtonBg, int leftButtonFontColor, int rightButtonBg,
                            int rightButtonFontColor) {

        this.mBgColorRes = bgColor;

        this.mDismissRes = dismissImageResource;
        this.mIconRes = noticeImageResource;

        this.mTitleFontColor = titleColor;
        this.mMessageFontColorRes = messageColor;

        this.mLeftBgRes = leftButtonBg;
        this.mLeftFontColorRes = leftButtonFontColor;

        this.mRightBgRes = rightButtonBg;
        this.mRightFontColorRes = rightButtonFontColor;
    }
}
