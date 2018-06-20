package com.sumian.sleepdoctor.sleepRecord.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/31 20:17
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class TitleView extends LinearLayout {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_menu)
    TextView tvMenu;

    public TitleView(Context context) {
        this(context, null);
    }

    public TitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View inflate = inflate(context, R.layout.view_title, this);
        ButterKnife.bind(this, inflate);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleView);
        String title = typedArray.getString(R.styleable.TitleView_title_text);
        String menu = typedArray.getString(R.styleable.TitleView_content_text);
        typedArray.recycle();
        tvTitle.setText(title);
        tvMenu.setText(menu);
    }

    public void setOnMenuClickListener(OnClickListener listener) {
        tvMenu.setOnClickListener(listener);
    }

    public void setTvTitle(String title) {
        tvTitle.setText(title);
    }

    public void setTvMenu(String menu) {
        tvMenu.setText(menu);
    }

    public void setTvMenuVisibility(int visibility) {
        tvMenu.setVisibility(visibility);
    }
}
