package com.sumian.hw.improve.device.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;

/**
 * Created by sm
 * on 2018/3/27.
 * <p>
 * desc:
 */

@SuppressWarnings("ConstantConditions")
public class PaModeDialog extends AppCompatDialog implements View.OnClickListener {

    private ImageView mIvIcon;
    private TextView mTvContent;
    private View mDivider;
    private TextView mTvSubmit;

    private int mType;
    private @StringRes
    int mContentId = R.string.turning_on_pa_mode;

    public PaModeDialog(Context context) {
        this(context, R.style.full_screen_dialog);
    }

    private PaModeDialog(Context context, int theme) {
        super(context, theme);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            if (window != null) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        } else {//4.4 全透明状态栏
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public PaModeDialog setType(int type) {
        this.mType = type;
        return this;
    }

    public PaModeDialog setTvContent(@StringRes int contentId) {
        this.mContentId = contentId;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        setCanceledOnTouchOutside(false);

        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(getContext()).inflate(R.layout.hw_lay_dialog_pa_mode, null, false);
        setContentView(rootView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mIvIcon = rootView.findViewById(R.id.iv_icon);
        mTvContent = rootView.findViewById(R.id.tv_content);
        mDivider = rootView.findViewById(R.id.v_divider);
        mTvSubmit = rootView.findViewById(R.id.tv_submit);

        rootView.findViewById(R.id.tv_submit).setOnClickListener(this);
        showError();
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    public void showError() {
        switch (mType) {
            case 0x01:
                mIvIcon.setVisibility(View.VISIBLE);
                mDivider.setVisibility(View.GONE);
                mTvSubmit.setVisibility(View.GONE);
                break;
            case 0x02:
                mIvIcon.setVisibility(View.GONE);
                mDivider.setVisibility(View.VISIBLE);
                mTvSubmit.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        mTvContent.setText(mContentId);
    }

}
