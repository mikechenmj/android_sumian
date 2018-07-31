package com.sumian.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.sumian.sleepdoctor.R;


/**
 * Created by jzz
 * on 2017/5/5.
 * <p>
 * desc:
 */

public class VoiceProgress extends View implements Runnable {

    private Paint mPaint;
    private int centerX;
    private int centerY;

    private int width;
    private int height;

    private int progress;

    private boolean isPlay;

    private Path p1;
    private Path p2;
    private Path p3;

    public VoiceProgress(Context context) {
        this(context, null);
    }

    public VoiceProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        //paint.setStrokeWidth(1.0f);
        paint.setColor(getResources().getColor(R.color.general_color));
        paint.setStrokeWidth(10.0f);

        this.mPaint = paint;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        int contentWidth = w - paddingLeft - paddingRight;
        int contentHeight = h - paddingTop - paddingBottom;

        centerX = contentWidth >> 1;
        centerY = contentHeight >> 1;

        width = contentWidth / 8;
        height = contentHeight / 8;
        Path p1 = new Path();
        p1.moveTo(width, height * 3);
        p1.quadTo(width * 2, centerY, width, height * 5);
        p1.lineTo(width, height * 5);
        this.p1 = p1;

        Path p2 = new Path();
        p2.moveTo(width * 2, height * 2);
        p2.quadTo(width * 4, centerY, width * 2, height * 6);
        p2.lineTo(width * 2, height * 6);
        this.p2 = p2;

        Path p3 = new Path();
        p3.moveTo(width * 3, height);
        p3.quadTo(width * 6, centerY, width * 3, height * 7);
        p3.lineTo(width * 3, height * 7);
        this.p3 = p3;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        // 画坐标系
//        Path path1 = new Path();
//        mPaint.setStrokeWidth(1.0f);
//        mPaint.setColor(Color.GRAY);
//
//        path1.moveTo(0, centerY);
//        path1.lineTo(getWidth(), centerY);
//
//        //画 x 轴
//        canvas.drawPath(path1, mPaint);
//
//        Path path2 = new Path();
//        path2.moveTo(centerX, 0);
//        path2.lineTo(centerX, getHeight());
//
//        //画 y 轴
//        canvas.drawPath(path2, mPaint);

        if (progress == 0 || progress == 1 || progress == 2 || progress == 3) {
            canvas.drawPath(p1, mPaint);
        }

        if (progress == 0 || progress == 2 || progress == 3) {
            canvas.drawPath(p2, mPaint);
        }

        if (progress == 0 || progress == 3) {
            canvas.drawPath(p3, mPaint);
        }

        if (isPlay)
            play();
    }

    public void play() {
        isPlay = true;
        post(this);
    }

    public void stop() {
        isPlay = false;
        if (progress == 3) {
            removeCallbacks(this);
        }
        progress = 0;
        removeCallbacks(this);
    }

    @Override
    public void run() {
        progress++;
        if (progress == 3) {
            progress = 0;
        }
        postInvalidateDelayed(250);
    }

}
