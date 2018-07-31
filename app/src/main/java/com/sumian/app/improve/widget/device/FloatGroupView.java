package com.sumian.app.improve.widget.device;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.app.improve.assessment.AssessmentActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/3/15.
 * desc:小眠助手,悬浮容器
 */

public class FloatGroupView extends LinearLayout implements View.OnClickListener {

    TextView mTvLabel;

    private int mDefaultWidth;
    private float mToLeft;

    public FloatGroupView(Context context) {
        this(context, null);
    }

    public FloatGroupView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatGroupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setOnClickListener(this);
        View inflate = inflate(context, R.layout.hw_lay_float_container_view, this);
        mTvLabel = inflate.findViewById(R.id.tv_label);
        mTvLabel.setText("当前未匹配\n为默认模式");
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mDefaultWidth = w;
        this.mToLeft = (int) (mDefaultWidth / 1.5f);

        setTranslationX(mToLeft);
    }

    @Override
    public void onClick(View v) {

        if (mDefaultWidth == 0) {
            mDefaultWidth = getWidth();
        }

        if (mToLeft > 0) {
            mToLeft = -mToLeft;
            showDelayAnimate(100);
        } else {
            mToLeft = Math.abs(mToLeft);
            AssessmentActivity.show(getContext());
            showAnimate();
        }
    }

    private void showAnimate() {
        //animate().cancel();
        animate().translationX(0).translationXBy(mToLeft).setDuration(500).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //mToLeft = -mToLeft;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    public void rollBack() {
        showDelayAnimate(1000);
    }

    private void showDelayAnimate(long delayMills) {
        postDelayed(this::showAnimate, delayMills);
    }
}
