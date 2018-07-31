package com.sumian.hw.improve.guideline.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sumian.sleepdoctor.R;
import com.sumian.hw.improve.guideline.utils.GuidelineUtils;

import static com.sumian.hw.improve.guideline.utils.GuidelineUtils.SP_KEY_NEED_SHOW_DAILY_REPORT_USER_GUIDE;

/**
 * Created by jzz
 * on 2018/1/8.
 * desc:
 */

public class ReportGuidelineDialog extends AppCompatDialog implements View.OnClickListener {

    private int mStep = 0;

    private LinearLayout mStepOne;
    private LinearLayout mStepTwo;
    private LinearLayout mStepThree;

    private Button mBtStep;

    public ReportGuidelineDialog(@NonNull Context context) {
        this(context, R.style.full_screen_dialog);
    }

    @SuppressLint("ObsoleteSdkInt")
    @SuppressWarnings("ConstantConditions")
    private ReportGuidelineDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {//4.4 全透明状态栏
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @SuppressWarnings({"ConstantConditions", "TrivialMethodReference"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置一个布局
        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(getContext()).inflate(R.layout.hw_lay_user_guide, null, false);
        rootView.setOnClickListener(this);
        setContentView(rootView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.mBtStep = rootView.findViewById(R.id.bt_action);
        this.mBtStep.setText(R.string.go_next_step);
        this.mBtStep.setOnClickListener(this);

        this.mStepOne = rootView.findViewById(R.id.lay_guide_one);
        this.mStepTwo = rootView.findViewById(R.id.lay_guide_two);
        this.mStepThree = rootView.findViewById(R.id.lay_guide_three);


        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onClick(View v) {
        mStep++;
        switch (mStep) {
            case 1:
                mStepOne.setVisibility(View.GONE);
                mStepTwo.setVisibility(View.VISIBLE);
                mStepThree.setVisibility(View.GONE);
                this.mBtStep.setText(R.string.go_next_step);
                break;
            case 2:
                mStepOne.setVisibility(View.GONE);
                mStepTwo.setVisibility(View.GONE);
                mStepThree.setVisibility(View.VISIBLE);
                this.mBtStep.setText(R.string.oh_i_see);
                break;
            default:
                GuidelineUtils.putBoolean(SP_KEY_NEED_SHOW_DAILY_REPORT_USER_GUIDE, false);
                cancel();
                break;
        }
    }
}
