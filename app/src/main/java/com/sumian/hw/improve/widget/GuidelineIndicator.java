package com.sumian.hw.improve.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.sumian.sleepdoctor.R;

/**
 * Created by sm
 * on 2018/3/22.
 * desc:
 */

public class GuidelineIndicator extends View {

    private Paint mIndicatorPaint;

    private @ColorInt
    int mIndicatorColor;
    private @ColorInt
    int mUnIndicatorColor;

    private float mRadius = 0.0f;

    private int mCenterX = 0;
    private int mCenterY = 0;

    private float mMargin = 0;

    private int mFirstColor;
    private int mSecondColor;
    private int mThirdColor;
    private int mFourthColor;

    public GuidelineIndicator(Context context) {
        this(context, null);
    }

    public GuidelineIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuidelineIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIndicatorColor = getResources().getColor(R.color.bt_hole_color);
        this.mUnIndicatorColor = getResources().getColor(R.color.light_line_bg_color);

        this.mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        this.mIndicatorPaint.setStyle(Paint.Style.FILL);
        this.mIndicatorPaint.setColor(mIndicatorColor);

        this.mRadius = getResources().getDimension(R.dimen.space_4);
        this.mMargin = getResources().getDimension(R.dimen.space_10);

        this.mFirstColor = mIndicatorColor;
        this.mSecondColor = mUnIndicatorColor;
        this.mThirdColor = mUnIndicatorColor;
        this.mFourthColor = mUnIndicatorColor;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = w - paddingLeft - paddingRight;
        int contentHeight = h - paddingTop - paddingBottom;

        this.mCenterX = contentWidth >> 1;
        this.mCenterY = contentHeight >> 1;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mIndicatorPaint.setColor(mFirstColor);
        canvas.drawCircle(mCenterX - mMargin * 1.5f - mRadius, mCenterY, mRadius, mIndicatorPaint);

        mIndicatorPaint.setColor(mSecondColor);
        canvas.drawCircle(mCenterX - mMargin / 2 - (mRadius / 2.0f), mCenterY, mRadius, mIndicatorPaint);

        mIndicatorPaint.setColor(mThirdColor);
        canvas.drawCircle(mCenterX + mMargin / 2 + (mRadius / 2.0f), mCenterY, mRadius, mIndicatorPaint);

        mIndicatorPaint.setColor(mFourthColor);
        canvas.drawCircle(mCenterX + mMargin * 1.5f + mRadius, mCenterY, mRadius, mIndicatorPaint);
    }

    public void showIndicator(int position) {
        switch (position) {
            case 1:
                this.mFirstColor = mIndicatorColor;
                this.mSecondColor = mUnIndicatorColor;
                this.mThirdColor = mUnIndicatorColor;
                this.mFourthColor = mUnIndicatorColor;
                break;
            case 2:
                this.mFirstColor = mUnIndicatorColor;
                this.mSecondColor = mIndicatorColor;
                this.mThirdColor = mUnIndicatorColor;
                this.mFourthColor = mUnIndicatorColor;
                break;
            case 3:
                this.mFirstColor = mUnIndicatorColor;
                this.mSecondColor = mUnIndicatorColor;
                this.mThirdColor = mIndicatorColor;
                this.mFourthColor = mUnIndicatorColor;
                break;
            case 4:
                this.mFirstColor = mUnIndicatorColor;
                this.mSecondColor = mUnIndicatorColor;
                this.mThirdColor = mUnIndicatorColor;
                this.mFourthColor = mIndicatorColor;
                break;

        }
        postInvalidateDelayed(16);
    }
}
