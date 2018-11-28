package com.sumian.common.h5.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.common.R;


/**
 * Created by sm
 * on 2018/5/24 17:08
 * desc:
 **/
public class EmptyErrorView extends LinearLayout implements View.OnClickListener {

    private ImageView mIvEmptyIcon;
    private TextView mTvEmptyMsgTitle;
    private TextView mTvEmptyMsgDesc;
    private boolean mAutoHide;

    private OnEmptyCallback mOnEmptyCallback;

    public EmptyErrorView(Context context) {
        this(context, null);
    }

    public EmptyErrorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        setOrientation(VERTICAL);
        setBackgroundColor(getResources().getColor(R.color.b1_color));
        setOnClickListener(this);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EmptyErrorView);
        Drawable icon = a.getDrawable(R.styleable.EmptyErrorView_eev_icon);
        String title = a.getString(R.styleable.EmptyErrorView_eev_title);
        String desc = a.getString(R.styleable.EmptyErrorView_eev_desc);
        mAutoHide = a.getBoolean(R.styleable.EmptyErrorView_eev_auto_hide, true);
        a.recycle();
        mIvEmptyIcon.setImageDrawable(icon);
        mTvEmptyMsgTitle.setText(title);
        mTvEmptyMsgDesc.setText(desc);
    }

    public static EmptyErrorView create(Context context, @DrawableRes int icon, @StringRes int msgTitle, @StringRes int msgDesc) {
        EmptyErrorView emptyErrorView = new EmptyErrorView(context);
        emptyErrorView.setIvEmptyIcon(icon);
        emptyErrorView.setTvEmptyMsgTitle(msgTitle);
        emptyErrorView.setTvEmptyMsgDesc(msgDesc);
        return emptyErrorView;
    }

    public static EmptyErrorView createNormalEmptyView(Context context) {
        return EmptyErrorView.create(context,
                R.mipmap.ic_empty_state_report,
                0,
                R.string.no_data_hint);
    }

    private void initView(Context context) {
        View rootView = inflate(context, R.layout.common_lay_empty_view, this);
        this.mIvEmptyIcon = rootView.findViewById(R.id.iv_empty_icon);
        this.mTvEmptyMsgTitle = rootView.findViewById(R.id.tv_empty_msg_title);
        this.mTvEmptyMsgDesc = rootView.findViewById(R.id.tv_empty_msg_desc);
    }

    public void setOnEmptyCallback(OnEmptyCallback onEmptyCallback) {
        mOnEmptyCallback = onEmptyCallback;
    }

    /**
     * desc: 网络请求返回403/500等code,或者访问的页面不存在
     */
    public void invalidRequestError() {
        invalid(R.mipmap.ic_empty_state_404, R.string.empty_404_msg, R.string.empty_404_desc);
    }

    /**
     * desc:网络环境异常.
     */
    public void invalidNetworkError() {
        invalid(R.mipmap.ic_empty_state_network_anomaly, R.string.empty_network_error_msg, R.string.empty_network_error_desc);
    }

    private void invalid(@DrawableRes int emptyId, @StringRes int emptyTitleId, @StringRes int emptyDescId) {
        this.mIvEmptyIcon.setImageResource(emptyId);
        this.mTvEmptyMsgTitle.setText(emptyTitleId);
        this.mTvEmptyMsgDesc.setText(emptyDescId);
        show();
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    @Override
    public void onClick(View v) {
        if (mOnEmptyCallback != null) {
            this.mOnEmptyCallback.reload();
        }
        if (mAutoHide) {
            hide();
        }
    }

    public void setIvEmptyIcon(@DrawableRes int icon) {
        mIvEmptyIcon.setImageResource(icon);
    }

    public void setTvEmptyMsgTitle(@StringRes int msgTitle) {
        if (msgTitle == 0) {
            mTvEmptyMsgTitle.setVisibility(GONE);
        } else {
            mTvEmptyMsgTitle.setVisibility(VISIBLE);
            mTvEmptyMsgTitle.setText(msgTitle);
        }
    }

    public void setTvEmptyMsgDesc(@StringRes int msgDesc) {
        mTvEmptyMsgDesc.setText(msgDesc);
    }

    public interface OnEmptyCallback {

        void reload();
    }
}
