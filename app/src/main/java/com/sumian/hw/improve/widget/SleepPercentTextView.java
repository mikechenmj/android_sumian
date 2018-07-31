package com.sumian.hw.improve.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.sumian.hw.common.util.TextUtil;

/**
 * Created by sm
 * on 2018/3/8.
 * desc:
 */

public class SleepPercentTextView extends AppCompatTextView {

    private Paint mProgressPaint;
    private Paint mThumbPaint;
    private int mRadius;
    private int mWidth;
    private int mHeight;

    private RectF mArcRect;

    private int mPercent;
    private float mSweepAngle = 0.0f;

    public SleepPercentTextView(Context context) {
        this(context, null);
    }

    public SleepPercentTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SleepPercentTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(12.0f);
        mProgressPaint.setColor(Color.parseColor("#ff7ea5f4"));

        mThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mThumbPaint.setStyle(Paint.Style.STROKE);
        mThumbPaint.setStrokeWidth(12.0f);
        mThumbPaint.setColor(Color.parseColor("#ff434b5b"));

        mArcRect = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        this.mWidth = w;
        this.mHeight = h;

        int contentX = (w - paddingLeft - paddingRight) >> 1;
        int contentY = (h - paddingTop - paddingBottom) >> 1;

        this.mRadius = (int) ((Math.min(w, h) >> 1) - (mProgressPaint.getStrokeWidth() / 2));

        mArcRect.left = contentX - mRadius;
        mArcRect.top = contentY - mRadius;
        mArcRect.right = contentX + mRadius;
        mArcRect.bottom = contentY + mRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mWidth >> 1, mHeight >> 1, mRadius, mThumbPaint);
        canvas.drawArc(mArcRect, -90, mSweepAngle, false, mProgressPaint);
    }

    public void setPercent(int percent) {
        this.mPercent = percent;
        this.mSweepAngle = percent * (360.0f / 100);
        String text;
        if (percent <= 0) {
            text = "--";
            setText(text);
        } else {
            CharSequence percentNumberCharSequence = TextUtil.getPercentNumberCharSequence(getContext(), percent);
            setText(percentNumberCharSequence);
        }
    }

    public int getPercent() {
        return mPercent;
    }
}
