package com.sumian.sleepdoctor.widget.dialog;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/25 9:18
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SumianAlertDialog extends DialogFragment {

    @BindView(R.id.iv_top)
    ImageView mIvTop;
    @BindView(R.id.iv_close)
    ImageView mIvClose;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_message)
    TextView mTvMessage;
    @BindView(R.id.btn_left)
    AppCompatButton mBtnLeft;
    @BindView(R.id.space)
    Space mSpace;
    @BindView(R.id.btn_right)
    AppCompatButton mBtnRight;

    private Unbinder mBind;
    private boolean mIsCloseBtnVisible;
    private int mIconRes;
    private int mTitleRes;
    private int mMessageRes;
    private int mLeftBtnTextRes;
    private View.OnClickListener mLeftBtnClickListener;
    private int mRightBtnTextRes;
    private View.OnClickListener mRightBtnClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.SumianDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.lay_alert_dialog, container, false);
        mBind = ButterKnife.bind(this, inflate);
        return inflate;
    }

    @Override
    public void onDestroyView() {
        mBind.unbind();
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIvClose.setVisibility(mIsCloseBtnVisible ? View.VISIBLE : View.GONE);
        mIvTop.setVisibility(mIconRes == 0 ? View.GONE : View.VISIBLE);
        mIvTop.setImageResource(mIconRes);
        mTvTitle.setVisibility(mTitleRes == 0 ? View.GONE : View.VISIBLE);
        mTvTitle.setText(mTitleRes);
        mTvMessage.setVisibility(mMessageRes == 0 ? View.GONE : View.VISIBLE);
        mTvMessage.setText(mMessageRes);
        mBtnLeft.setVisibility(mLeftBtnTextRes == 0 ? View.GONE : View.VISIBLE);
        if (mLeftBtnTextRes != 0) mBtnLeft.setText(mLeftBtnTextRes);
        mBtnRight.setVisibility(mRightBtnTextRes == 0 ? View.GONE : View.VISIBLE);
        if (mRightBtnTextRes != 0) mBtnRight.setText(mRightBtnTextRes);
        mSpace.setVisibility(mLeftBtnTextRes != 0 && mRightBtnTextRes != 0 ? View.VISIBLE : View.GONE);
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
        mMessageRes = messageRes;
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

    public static SumianAlertDialog create() {
        return new SumianAlertDialog();
    }

    public void show(android.support.v4.app.FragmentManager fragmentManager) {
        show(fragmentManager, getClass().getSimpleName());
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
        }
        dismiss();
    }
}
