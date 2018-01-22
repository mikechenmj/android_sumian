package com.sumian.sleepdoctor.widget.divider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

public class SettingDividerView extends LinearLayout implements View.OnClickListener {

    @BindView(R.id.iv_type)
    ImageView mIvType;
    @BindView(R.id.tv_type_desc)
    TextView mTvTypeDesc;
    @BindView(R.id.v_dot)
    View mVDot;
    @BindView(R.id.tv_setting_content)
    TextView mTvSettingContent;
    @BindView(R.id.lay_my_msg_notice)
    LinearLayout mLayMyMsgNotice;
    @BindView(R.id.v_divider_line)
    View mVDividerLine;
    @BindView(R.id.iv_more)
    ImageView mIvMore;

    private OnShowMoreListener mOnShowMoreListener;

    public void setOnShowMoreListener(OnShowMoreListener onShowMoreListener) {
        mOnShowMoreListener = onShowMoreListener;
    }

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
        ButterKnife.bind(inflate(context, R.layout.lay_setting_divider_item, this));
        setGravity(Gravity.CENTER);
        setHorizontalGravity(LinearLayout.VERTICAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingDividerView);

        @DrawableRes int iconId = typedArray.getResourceId(R.styleable.SettingDividerView_type_icon, -1);
        String typeDesc = typedArray.getString(R.styleable.SettingDividerView_type_desc);
        @ColorInt int typeDescColor = typedArray.getColor(R.styleable.SettingDividerView_type_desc_text_color, getResources().getColor(R.color.t1_color));
        // @Dimension float typeDescTextSize = typedArray.getDimension(R.styleable.SettingDividerView_type_desc_text_size, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics()));

        @ColorInt int dotColor = typedArray.getColor(R.styleable.SettingDividerView_dot_color, Color.RED);

        String typeContent = typedArray.getString(R.styleable.SettingDividerView_type_content);
        @ColorInt int typeContentColor = typedArray.getColor(R.styleable.SettingDividerView_type_content_text_color, getResources().getColor(R.color.t1_color));
        // float typeContentTextSize = typedArray.getDimension(R.styleable.SettingDividerView_type_content_text_size, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics()));

        @ColorInt int dividerLineColor = typedArray.getColor(R.styleable.SettingDividerView_divider_line_color, getResources().getColor(R.color.b1_color));
        @Dimension float dividerLineSize = typedArray.getDimension(R.styleable.SettingDividerView_divider_line_size, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));

        int dividerGone = typedArray.getInt(R.styleable.SettingDividerView_divider_line_visible, View.VISIBLE);

        int moreGone = typedArray.getInt(R.styleable.SettingDividerView_divider_more_visible, View.VISIBLE);

        typedArray.recycle();

        if (iconId <= -1) {
            mIvType.setVisibility(GONE);
        } else {
            mIvType.setVisibility(VISIBLE);
            mIvType.setImageResource(iconId);
        }

        mTvTypeDesc.setTextColor(typeDescColor);
        // mTvTypeDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, typeDescTextSize);
        mTvTypeDesc.setText(typeDesc);

        mVDot.setBackgroundColor(dotColor);

        mTvSettingContent.setText(typeContent);
        mTvSettingContent.setTextColor(typeContentColor);
        mTvSettingContent.setVisibility(TextUtils.isEmpty(typeContent) ? GONE : VISIBLE);
        //mTvSettingContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, typeContentTextSize);

        mVDividerLine.setBackgroundColor(dividerLineColor);
        ViewGroup.LayoutParams layoutParams = mVDividerLine.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = (int) dividerLineSize;
        mVDividerLine.setLayoutParams(layoutParams);
        mVDividerLine.setVisibility(dividerGone);

        mIvMore.setVisibility(moreGone);

        setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mOnShowMoreListener != null) {
            mOnShowMoreListener.onShowMore();
        }
    }


    public interface OnShowMoreListener {

        void onShowMore();
    }
}
