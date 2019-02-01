package com.sumian.sddoctor.patient.sleepdiary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sddoctor.R;

import androidx.annotation.Nullable;

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
    public TextView tvTitle;
    public TextView tvMenu;
    public ImageView ivRightArrow;

    public TitleView(Context context) {
        this(context, null);
    }

    public TitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View inflate = inflate(context, R.layout.view_title, this);
        tvTitle = inflate.findViewById(R.id.tv_title);
        tvMenu = inflate.findViewById(R.id.tv_menu);
        ivRightArrow = inflate.findViewById(R.id.iv_right_arrow);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleView);
        String title = typedArray.getString(R.styleable.TitleView_title_text);
        String menu = typedArray.getString(R.styleable.TitleView_content_text);
        boolean showRightArrow = typedArray.getBoolean(R.styleable.TitleView_tv_show_right_arrow, false);
        typedArray.recycle();
        tvTitle.setText(title);
        tvMenu.setText(menu);
        ivRightArrow.setVisibility(showRightArrow ? VISIBLE : GONE);
    }

    public void setOnMenuClickListener(OnClickListener listener) {
        tvMenu.setOnClickListener(listener);
    }

    public void setOnRightArrowClickListener(OnClickListener listener) {
        ivRightArrow.setOnClickListener(listener);
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
