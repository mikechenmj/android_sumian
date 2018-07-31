package com.sumian.app.improve.widget.device;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.sumian.app.R;


/**
 * Created by sm
 * on 2018/3/22.
 * desc:
 * 1.设备连接中的状态变化,
 * 2.设备连接成功时的状态变化
 * 3.设备同步数据时的状态变化
 */
public class DeviceRippleConnectingView extends FrameLayout {

    public static final String TAG = DeviceRippleConnectingView.class.getSimpleName();

    private int mMaxRadius;
    private int mInterval = 100;
    private int count = 0;

    private Paint mRipplePaint;
    private Paint mIdlePaint;
    private Paint mSyncPaint;
    private Paint mMaskPaint;
    private Paint mPathArrowPaint;

    private int mCenterX;
    private int mCenterY;

    private boolean mIsShowIdleStatus = true;

    private boolean mIsShowConnectingAnim;

    private ImageView mCenterImageView;

    private int mProgress = 360;
    private Runnable mConnectingAnimationAction = () -> {
        count += 2;
        count %= mInterval;
        postInvalidateOnAnimation();
    };

    public DeviceRippleConnectingView(Context context) {
        this(context, null);
    }

    public DeviceRippleConnectingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeviceRippleConnectingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRipplePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mRipplePaint.setStyle(Paint.Style.STROKE);
        mRipplePaint.setColor(Color.parseColor("#FF7EA5F4"));
        mRipplePaint.setStrokeWidth(getContext().getResources().getDimensionPixelSize(R.dimen.space_2));

        mIdlePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mIdlePaint.setStyle(Paint.Style.FILL);
        mIdlePaint.setColor(Color.parseColor("#FF2C2F37"));
        mIdlePaint.setStrokeWidth(getContext().getResources().getDimensionPixelSize(R.dimen.space_2));

        mSyncPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mSyncPaint.setStyle(Paint.Style.STROKE);
        mSyncPaint.setColor(Color.parseColor("#FF7D8FB3"));
        mSyncPaint.setStrokeCap(Paint.Cap.ROUND);
        mSyncPaint.setStrokeJoin(Paint.Join.ROUND);
        mSyncPaint.setStrokeWidth(getContext().getResources().getDimensionPixelSize(R.dimen.space_2));

        mMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mMaskPaint.setStyle(Paint.Style.STROKE);
        //Color.parseColor("#FF212329")
        mMaskPaint.setColor(Color.parseColor("#FF212329"));
        // mMaskPaint.setStrokeCap(Paint.Cap.ROUND);
        //  mMaskPaint.setStrokeJoin(Paint.Join.ROUND);
        mMaskPaint.setStrokeWidth(getContext().getResources().getDimensionPixelSize(R.dimen.space_3));

        mPathArrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPathArrowPaint.setStyle(Paint.Style.STROKE);
        mPathArrowPaint.setColor(Color.parseColor("#ff7ea4f4"));
        mPathArrowPaint.setStrokeCap(Paint.Cap.ROUND);
        mPathArrowPaint.setStrokeJoin(Paint.Join.ROUND);
        mPathArrowPaint.setStrokeWidth(getContext().getResources().getDimensionPixelSize(R.dimen.space_2));
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

        mCenterImageView = (ImageView) getChildAt(0);

        SweepGradient sweepGradientShader = new SweepGradient(mCenterX, mCenterY, Color.parseColor("#807ea5f4"), Color.parseColor("#ff7ea4f4"));
        mSyncPaint.setShader(sweepGradientShader);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        drawIdle(canvas);
        drawConnectingAnim(canvas);
        super.dispatchDraw(canvas);
    }

    private void drawIdle(Canvas canvas) {
        mRipplePaint.setColor(Color.TRANSPARENT);
        mSyncPaint.setColor(Color.TRANSPARENT);
        mMaskPaint.setColor(Color.TRANSPARENT);
        mPathArrowPaint.setColor(Color.TRANSPARENT);

        if (mIsShowIdleStatus) {
            mIdlePaint.setColor(Color.parseColor("#FF2C2F37"));
            int width = mCenterImageView.getWidth();
            int height = mCenterImageView.getHeight();

            int mMaxRadius = (Math.min(width, height) >> 1) - 25;

            int save = canvas.save();

            mIdlePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(mCenterX, mCenterY, mMaxRadius, mIdlePaint);
            mIdlePaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(mCenterX, mCenterY, mMaxRadius + 35, mIdlePaint);

            canvas.restoreToCount(save);
        }

    }

    private void drawConnectingAnim(Canvas canvas) {
        mIdlePaint.setColor(Color.TRANSPARENT);
        mSyncPaint.setColor(Color.TRANSPARENT);
        mMaskPaint.setColor(Color.TRANSPARENT);
        mPathArrowPaint.setColor(Color.TRANSPARENT);

        if (mIsShowConnectingAnim) {
            mRipplePaint.setColor(Color.parseColor("#FF7EA5F4"));
            int save = canvas.save();
            for (int step = count; step < mMaxRadius; step += mInterval) {
                mRipplePaint.setAlpha(255 * (mMaxRadius - step) / mMaxRadius);
                canvas.drawCircle(mCenterX, mCenterY, (float) (step >= mMaxRadius ? mMaxRadius - 3 : step), mRipplePaint);
            }
            canvas.restoreToCount(save);
            postDelayed(mConnectingAnimationAction, 0);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mConnectingAnimationAction);
    }

    public void showIdleStatus() {
        mIsShowIdleStatus = true;
        mIsShowConnectingAnim = false;
        removeCallbacks(mConnectingAnimationAction);
        postInvalidateDelayed(16);
    }

    public void startConnectingAnimation() {
        if (mIsShowConnectingAnim) {
            return;
        }
        removeCallbacks(mConnectingAnimationAction);
        mIsShowIdleStatus = false;
        mIsShowConnectingAnim = true;
        post(mConnectingAnimationAction);
    }
}
