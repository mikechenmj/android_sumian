package com.sumian.sd.buz.cbti.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.sumian.sd.R;

import androidx.annotation.Nullable;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/4 9:26
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class RingProgressView extends View {

    private static final int DEFAULT_MAX_PROGRESS = 100;
    private Paint mProgressPaint;
    private Paint mBgRingPaint;
    private RectF mArcRect;
    private int mProgress;
    private int mMaxProgress;

    public RingProgressView(Context context) {
        this(context, null);
    }

    public RingProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        Resources resources = context.getResources();
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.RingProgressView);
        float ringWidth = attributes.getDimension(R.styleable.RingProgressView_rpv_ring_width, resources.getDimension(R.dimen.space_10));
        int ringBgColor = attributes.getColor(R.styleable.RingProgressView_rpv_ring_bg_color, resources.getColor(R.color.b1_color));
        int progressColor = attributes.getColor(R.styleable.RingProgressView_rpv_progress_color, resources.getColor(R.color.b3_color));
        mProgress = attributes.getInt(R.styleable.RingProgressView_rpv_progress, 0);
        mMaxProgress = attributes.getInt(R.styleable.RingProgressView_rpv_max_progress, DEFAULT_MAX_PROGRESS);
        attributes.recycle();

        mArcRect = new RectF();
        // init background ring paint
        mBgRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBgRingPaint.setStyle(Paint.Style.STROKE);
        mBgRingPaint.setStrokeWidth(ringWidth);
        mBgRingPaint.setColor(ringBgColor);
        // init progress paint
        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(ringWidth);
        mProgressPaint.setColor(progressColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateSizeInfo(w, h);
    }

    private void updateSizeInfo(int w, int h) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int mWidth = w;
        int mHeight = h;
        int contentX = (w - paddingLeft - paddingRight) >> 1;
        int contentY = (h - paddingTop - paddingBottom) >> 1;
        int mRadius = (int) ((Math.min(w, h) >> 1) - (mProgressPaint.getStrokeWidth() / 2));
        mArcRect.left = contentX - mRadius;
        mArcRect.top = contentY - mRadius;
        mArcRect.right = contentX + mRadius;
        mArcRect.bottom = contentY + mRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw ring bg
        canvas.drawArc(mArcRect, -90, 360.0f, false, mBgRingPaint);
        // draw progress
        canvas.drawArc(mArcRect, -90, getSweepAngle(), false, mProgressPaint);
    }

    private float getSweepAngle() {
        return 360.0f * mProgress / mMaxProgress;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
    }
}
