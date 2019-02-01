package com.sumian.sddoctor.service.report.widget.progress;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import com.sumian.sddoctor.R;

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
public class ColorfulProgressView extends View {

    private Paint mProgressPaint;
    private Paint mBgRingPaint;
    private int mRadius;
    private int mWidth;
    private int mHeight;
    private RectF mArcRect;
    private int mPercent;
    private float mSweepAngle = 0.0f;
    private int[][] mProgressColors;
    private Matrix mMatrix;
    private SweepGradient mSweepGradient;
    private int mProgressLevel;
    private int mContentX;
    private int mContentY;

    public ColorfulProgressView(Context context) {
        this(context, null);
    }

    public ColorfulProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
//        inflate(context, R.layout.lay_progress_view, this);

        Resources resources = context.getResources();
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ColorfulProgressView);

        float ringWidth = attributes.getDimension(R.styleable.ColorfulProgressView_cpv_ring_width, resources.getDimension(R.dimen.space_10));
        int ringBgColor = attributes.getColor(R.styleable.ColorfulProgressView_cpv_ring_bg_color, resources.getColor(R.color.b1_color));

        int highStartColor = attributes.getColor(R.styleable.ColorfulProgressView_cpv_high_start_color, resources.getColor(R.color.sleep_record_progress_high_start));
        int highEndColor = attributes.getColor(R.styleable.ColorfulProgressView_cpv_high_end_color, resources.getColor(R.color.sleep_record_progress_high_end));
        int middleStartColor = attributes.getColor(R.styleable.ColorfulProgressView_cpv_middle_start_color, resources.getColor(R.color.sleep_record_progress_middle_start));
        int middleEndColor = attributes.getColor(R.styleable.ColorfulProgressView_cpv_middle_end_color, resources.getColor(R.color.sleep_record_progress_middle_end));
        int lowStartColor = attributes.getColor(R.styleable.ColorfulProgressView_cpv_low_start_color, resources.getColor(R.color.sleep_record_progress_low_start));
        int lowEndColor = attributes.getColor(R.styleable.ColorfulProgressView_cpv_low_end_color, resources.getColor(R.color.sleep_record_progress_low_end));

        attributes.recycle();

        mProgressColors = new int[][]{{highStartColor, highEndColor}, {middleStartColor, middleEndColor}, {lowStartColor, lowEndColor}};

        mArcRect = new RectF();
        mMatrix = new Matrix();
        // init background ring paint
        mBgRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBgRingPaint.setStyle(Paint.Style.STROKE);
        mBgRingPaint.setColor(ringBgColor);
        // init progress paint
        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        setRingWidth(ringWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateSizeInfo(w, h);
        updateSweepGradient();
    }

    private void updateSizeInfo(int w, int h) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        mWidth = w;
        mHeight = h;
        mContentX = (w - paddingLeft - paddingRight) >> 1;
        mContentY = (h - paddingTop - paddingBottom) >> 1;
        mRadius = (int) ((Math.min(w, h) >> 1) - (mProgressPaint.getStrokeWidth() / 2));
        mArcRect.left = mContentX - mRadius;
        mArcRect.top = mContentY - mRadius;
        mArcRect.right = mContentX + mRadius;
        mArcRect.bottom = mContentY + mRadius;
    }

    private void updateSweepGradient() {
        int[] startEndColors = getStartEndColors(mPercent);
        mSweepGradient = new SweepGradient(mContentX, mContentY, startEndColors[0], startEndColors[1]);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw ring bg
        canvas.drawCircle(mWidth >> 1, mHeight >> 1, mRadius, mBgRingPaint);
        // draw progress
        mMatrix.setRotate(-90f, canvas.getWidth() / 2, canvas.getHeight() / 2);
        mSweepGradient.setLocalMatrix(mMatrix);
        mProgressPaint.setShader(mSweepGradient);
        canvas.drawArc(mArcRect, -90, mSweepAngle, false, mProgressPaint);
    }

    public void setProgress(int progress) {
        mPercent = progress;
        mSweepAngle = progress * (360.0f / 100);
        int progressLevel = getProgressLevelByPercent(progress);
        if (mProgressLevel != progressLevel) {
            mProgressLevel = progressLevel;
            updateSweepGradient();
        }
        invalidate();
    }

    public int[] getStartEndColors(int percent) {
        int progressLevelByPercent = getProgressLevelByPercent(percent);
        return mProgressColors[progressLevelByPercent];
    }

    public int getProgressLevelByPercent(int percent) {
        if (percent >= 85) {
            return 0;
        } else if (percent >= 70) {
            return 1;
        } else {
            return 2;
        }
    }

    public int getPercent() {
        return mPercent;
    }

    public void setRingWidth(float ringWidth) {
        mBgRingPaint.setStrokeWidth(ringWidth);
        mProgressPaint.setStrokeWidth(ringWidth);
    }

    public void setRingBgColor(int color) {
        mBgRingPaint.setColor(color);
        requestLayout();
        invalidate();
    }

    public void setProgressColors(int[][] progressColors) {
        mProgressColors = progressColors;
        invalidate();
    }
}