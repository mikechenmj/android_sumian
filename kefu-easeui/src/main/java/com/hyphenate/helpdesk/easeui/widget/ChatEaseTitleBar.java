package com.hyphenate.helpdesk.easeui.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.helpdesk.R;

/**
 * 聊天界面标题栏
 */
public class ChatEaseTitleBar extends RelativeLayout {

    protected RelativeLayout leftLayout;
    protected ImageView leftImage;
    protected RelativeLayout rightLayout;
    protected ImageView rightImage;
    protected TextView titleView;
    protected RelativeLayout titleLayout;
    protected ProgressBar mLoading;

    public ChatEaseTitleBar(Context context) {
        this(context, null);
    }

    public ChatEaseTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatEaseTitleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.light_hd_widget_chat_title_bar, this);
        leftLayout = findViewById(R.id.left_layout);
        leftImage = findViewById(R.id.left_image);
        rightLayout = findViewById(R.id.right_layout);
        rightImage = findViewById(R.id.right_image);
        titleView = findViewById(R.id.title);
        titleLayout = findViewById(R.id.root);
        mLoading = findViewById(R.id.iv_loading);
        parseStyle(context, attrs);
    }

    private void parseStyle(Context context, AttributeSet attrs) {
        if (attrs != null) {
            @SuppressLint("CustomViewStyleable") TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseTitleBar);
            String title = ta.getString(R.styleable.EaseTitleBar_titleBarTitle);
            titleView.setText(title);

            int textColor = ta.getColor(R.styleable.EaseTitleBar_titleBarTextColor, context.getResources().getColor(R.color.bt_hole_color));
            titleView.setTextColor(textColor);

            Drawable leftDrawable = ta.getDrawable(R.styleable.EaseTitleBar_titleBarLeftImage);
            if (null != leftDrawable) {
                leftImage.setImageDrawable(leftDrawable);
            }
            Drawable rightDrawable = ta.getDrawable(R.styleable.EaseTitleBar_titleBarRightImage);
            if (null != rightDrawable) {
                rightImage.setImageDrawable(rightDrawable);
            }

            Drawable background = ta.getDrawable(R.styleable.EaseTitleBar_titleBarBackground);
            if (null != background) {
                titleLayout.setBackgroundDrawable(background);
            }

            ta.recycle();
        }
    }

    public void showLoading() {
        mLoading.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        mLoading.setVisibility(View.GONE);
    }

    public void setLeftImageResource(int resId) {
        leftImage.setImageResource(resId);
    }

    public void setRightImageResource(int resId) {
        rightImage.setImageResource(resId);
    }

    public void setLeftLayoutClickListener(OnClickListener listener) {
        leftLayout.setOnClickListener(listener);
    }

    public void setRightLayoutClickListener(OnClickListener listener) {
        rightLayout.setOnClickListener(listener);
    }

    public void setLeftLayoutVisibility(int visibility) {
        leftLayout.setVisibility(visibility);
    }

    public void setRightLayoutVisibility(int visibility) {
        rightLayout.setVisibility(visibility);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setBackgroundColor(int color) {
        titleLayout.setBackgroundColor(color);
    }

    public RelativeLayout getLeftLayout() {
        return leftLayout;
    }

    public RelativeLayout getRightLayout() {
        return rightLayout;
    }
}
