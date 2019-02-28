package com.sumian.sddoctor.widget.divider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.sumian.sddoctor.R;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;


/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

@SuppressWarnings("unused")
public class SettingDividerView extends LinearLayout {

    public static final int INVALID_RES_ID = -1;

    private ImageView mIvType;
    private ImageView mIvTypeCircle;
    private TextView mTvLabel;

    private View mDot;

    private TextView mTvContent;

    private ImageView mIvRightArrow;
    private Switch mSwitch;

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;
    private boolean mUseCircleTypeIv;

    public SettingDividerView(Context context) {
        this(context, null);
    }

    public SettingDividerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingDividerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View inflate = inflate(context, R.layout.lay_setting_divider_item, this);
        mIvType = inflate.findViewById(R.id.iv_type);
        mIvTypeCircle = inflate.findViewById(R.id.iv_type_circle);
        mTvLabel = inflate.findViewById(R.id.tv_type_label);
        mDot = inflate.findViewById(R.id.v_dot);
        mTvContent = inflate.findViewById(R.id.tv_content);
        View VDividerLine = inflate.findViewById(R.id.v_divider_line);
        mIvRightArrow = inflate.findViewById(R.id.iv_right_arrow);
        mSwitch = inflate.findViewById(R.id.sw);

        setHorizontalGravity(LinearLayout.VERTICAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingDividerView);
        @DrawableRes int iconId = typedArray.getResourceId(R.styleable.SettingDividerView_type_icon, INVALID_RES_ID);
        String typeDesc = typedArray.getString(R.styleable.SettingDividerView_type_desc);
        @ColorInt int typeDescColor = typedArray.getColor(R.styleable.SettingDividerView_type_desc_text_color, getResources().getColor(R.color.t1_color));
        @ColorInt int dotColor = typedArray.getColor(R.styleable.SettingDividerView_dot_color, Color.RED);
        String typeContent = typedArray.getString(R.styleable.SettingDividerView_type_content);
        @ColorInt int typeContentColor = typedArray.getColor(R.styleable.SettingDividerView_type_content_text_color, getResources().getColor(R.color.t1_color));
        @ColorInt int dividerLineColor = typedArray.getColor(R.styleable.SettingDividerView_divider_line_color, getResources().getColor(R.color.b1_color));
        @Dimension float dividerLineSize = typedArray.getDimension(R.styleable.SettingDividerView_divider_line_size, getResources().getDimension(R.dimen.space_1));
        int dividerGone = typedArray.getInt(R.styleable.SettingDividerView_divider_line_visible, View.VISIBLE);
        int moreGone = typedArray.getInt(R.styleable.SettingDividerView_divider_more_visible, View.VISIBLE);
        boolean showSwitch = typedArray.getBoolean(R.styleable.SettingDividerView_show_switch, false);
        mUseCircleTypeIv = typedArray.getBoolean(R.styleable.SettingDividerView_use_circle_type_iv, false);
        boolean showIv = typedArray.getBoolean(R.styleable.SettingDividerView_show_iv, false);
        typedArray.recycle();

        useCircleIv(mUseCircleTypeIv);
        getTypeIv().setVisibility(iconId != INVALID_RES_ID || showIv ? VISIBLE : GONE);
        if (iconId != INVALID_RES_ID) {
            getTypeIv().setImageResource(iconId);
        }
        mTvLabel.setTextColor(typeDescColor);
        mTvLabel.setText(typeDesc);
        // mDot.setBackgroundColor(dotColor);
        mTvContent.setText(typeContent);
        mTvContent.setTextColor(typeContentColor);
        mTvContent.setVisibility(TextUtils.isEmpty(typeContent) ? GONE : VISIBLE);
        VDividerLine.setBackgroundColor(dividerLineColor);
        VDividerLine.setVisibility(dividerGone);
        mIvRightArrow.setVisibility(moreGone);
        mSwitch.setVisibility(showSwitch ? VISIBLE : GONE);
    }

    public void showRightArrow(boolean show) {
        mIvRightArrow.setVisibility(show ? INVISIBLE : GONE);
    }

    public void setContentText(String content) {
        mTvContent.setText(content);
        mTvContent.setVisibility(TextUtils.isEmpty(content) ? GONE : VISIBLE);
    }

    public String getContextText() {
        return mTvContent.getText().toString();
    }

    public String getLabelText() {
        return mTvLabel.getText().toString();
    }

    public void setLabelText(String label) {
        mTvLabel.setText(label);
    }

    public void setOnCheckedChangeListener(@Nullable CompoundButton.OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
        mSwitch.setOnCheckedChangeListener(listener);
    }

    public void setSwitchChecked(boolean checked) {
        mSwitch.setChecked(checked);
    }

    public void showRedDot(boolean isShow) {
        mDot.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 控件初始化数据的时候，可能不需要触发回调，可以调用该方法
     */
    public void setSwitchCheckedWithoutCallback(boolean checked) {
        mSwitch.setOnCheckedChangeListener(null);
        mSwitch.setChecked(checked);
        mSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

    public void useCircleIv(boolean useCircleIv) {
        mIvTypeCircle.setVisibility(useCircleIv ? VISIBLE : GONE);
        mIvType.setVisibility(!useCircleIv ? VISIBLE : GONE);
    }

    public ImageView getTypeIv() {
        return mUseCircleTypeIv ? mIvTypeCircle : mIvType;
    }

    public void setTvContentColor(int color) {
        mTvContent.setTextColor(color);
    }
}
