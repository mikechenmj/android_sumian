package com.sumian.hw.widget.ripple;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.sumian.sd.R;


/**
 * Created by sm
 * on 2018/3/22.
 * desc:
 */
public class RippleScanningView extends RelativeLayout implements Runnable {

    private int mMaxRadius;
    private int mInterval = 100;
    private int count = 0;

    private Paint mRipplePaint;

    private int mCenterX;
    private int mCenterY;

    public RippleScanningView(Context context) {
        this(context, null);
    }

    public RippleScanningView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleScanningView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRipplePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mRipplePaint.setAntiAlias(true);
        mRipplePaint.setStyle(Paint.Style.STROKE);
        mRipplePaint.setColor(getResources().getColor(R.color.t1_color_day));
        mRipplePaint.setStrokeWidth(2.f);
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

        mMaxRadius = Math.min(w, h) >> 1;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int save = canvas.save();
        for (int step = count; step < mMaxRadius; step += mInterval) {
            mRipplePaint.setAlpha(255 * (mMaxRadius - step) / mMaxRadius);
            canvas.drawCircle(mCenterX, mCenterY, (float) (step >= mMaxRadius ? mMaxRadius - 3 : step), mRipplePaint);
        }
        canvas.restoreToCount(save);

        postDelayed(this, 0);

        super.dispatchDraw(canvas);
    }

    @Override
    public void run() {
        count += 2;
        count %= mInterval;
        postInvalidateOnAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    public void stopAnimation() {
        removeCallbacks(this);
    }

    public void startAnimation() {
        stopAnimation();
        postOnAnimationDelayed(this, 16);
    }
}
