package com.sumian.hw.widget.histogram;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.hw.common.util.TimeUtil;
import com.sumian.hw.network.response.SleepDetailReport;
import com.sumian.hw.tab.report.bean.SleepData;

import java.util.List;

/**
 * Created by jzz
 * on 2017/9/9
 * <p>
 * desc:日睡眠数据统计图表
 */

public class DaySleepHistogramView extends View implements Runnable {

    private static final String TAG = "DaySleepHistogramView";

    private Paint mCoordinatePaint;//坐标系画笔
    private TextPaint mTextPaint;//文本画笔
    private Paint mSquarePaint;//直方图画笔
    private Path mHorizontalPath;
    private RectF mFullRectF;
    private int mItemHeight;
    private int mItemWidth;

    private String[] mLabelText;
    private Rect mTextBounds;
    private int mFullTimeQuantum;//睡眠数据统计总时长
    private float mCellWidth;

    private float mProgress;//变化进度
    private int mLen;//总数据长度

    private List<SleepData> mSleepDataList;
    private SleepDetailReport mSleepDetailReport;


    public DaySleepHistogramView(Context context) {
        this(context, null);
    }

    public DaySleepHistogramView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DaySleepHistogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        //1.init  坐标系画笔
        Paint coordinatePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        coordinatePaint.setColor(getResources().getColor(R.color.light_content_bg_color));
        coordinatePaint.setStrokeCap(Paint.Cap.ROUND);
        float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1.0f, getResources().getDisplayMetrics());

        coordinatePaint.setStrokeWidth(width);
        coordinatePaint.setStyle(Paint.Style.STROKE);
        this.mCoordinatePaint = coordinatePaint;

        //2.init  文本画笔
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        textPaint.setColor(getResources().getColor(R.color.general_color));
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setStrokeJoin(Paint.Join.ROUND);
        textPaint.setTextAlign(Paint.Align.LEFT);
        float sp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16.0f, getResources().getDisplayMetrics());
        textPaint.setTextSize(sp);

        this.mTextPaint = textPaint;

        String[] labelText = getResources().getStringArray(R.array.label_text);

        Rect bounds = new Rect();
        textPaint.getTextBounds(labelText[0], 0, labelText[0].length(), bounds);

        this.mTextBounds = bounds;

        this.mLabelText = labelText;

        Paint squarePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        squarePaint.setColor(getResources().getColor(R.color.sleep_sober_color));
        squarePaint.setStyle(Paint.Style.FILL);
        squarePaint.setStrokeWidth(1.0f);

        this.mSquarePaint = squarePaint;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, measureHeight(heightMeasureSpec));
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

        // this.mCenterX = w >> 1;
        //this.mCenterY = h >> 1;

        int itemWidth = contentWidth >> 2;
        int itemHeight = (int) (contentHeight / 3.5);

        Path horizontalPath = new Path();
        horizontalPath.moveTo(paddingLeft, itemHeight << 1);
        horizontalPath.lineTo(w - paddingRight, itemHeight << 1);

        this.mHorizontalPath = horizontalPath;

        RectF sumRectF = new RectF();
        sumRectF.left = itemWidth * 0.75f;
        sumRectF.top = itemHeight << 1;
        sumRectF.right = w - paddingRight;
        sumRectF.bottom = itemHeight * 4;

        this.mFullRectF = sumRectF;

        this.mItemWidth = itemWidth;
        this.mItemHeight = itemHeight;

        this.mCellWidth = sumRectF.width() / mFullTimeQuantum;//每一分钟占所有睡眠数据的宽度

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCoordinate(canvas);
        drawSleep(canvas);
    }

    private void drawSleep(Canvas canvas) {

        TextPaint textPaint = this.mTextPaint;
        int itemWidth = this.mItemWidth;
        int itemHeight = this.mItemHeight;
        Rect textBounds = this.mTextBounds;

        List<SleepData> sleepDataList = this.mSleepDataList;

        if (sleepDataList == null || sleepDataList.isEmpty()) {
            return;
        }

        float cellWidth = this.mCellWidth;
        Paint squarePaint = this.mSquarePaint;


        Rect rect = new Rect();

        int left = (int) (itemWidth * 0.75f);
        int right;

        int len = this.mLen;

        SleepDetailReport sleepDetailReport = this.mSleepDetailReport;

        for (int i = 0; i < len; i++) {
            SleepData sleepData = sleepDataList.get(i);
            SleepDetailReport.SleepItem sleepItem = sleepData.getSleepItem();

            if (i == 0) {
                //draw fromTime
                int fromTime = sleepDetailReport.getFrom_time();//数据采集开始时间
                canvas.drawText(TimeUtil.formatTime(fromTime), itemWidth * 0.75f, itemHeight * 3.0f + 1.5f * textBounds.height(), textPaint);
            }

            if (i == sleepDataList.size() - 1) {
                int toTime = sleepDetailReport.getTo_time();//数据采集结束时间
                //draw toTime
                canvas.drawText(TimeUtil.formatTime(toTime), 4.0f * itemWidth - (textBounds.width() >> 1), itemHeight * 3.0f + 1.5f * textBounds.height(), textPaint);
            }

            rect.left = left;//最开始的左边
            float width = sleepData.getTimeQuantum() * cellWidth;//所占比例
            right = (int) (left + width);
            int state = sleepItem.getState();
            switch (state) {
                case 0x00://清醒
                    rect.top = 0;
                    rect.right = (int) (right * mProgress);
                    rect.bottom = itemHeight - 1;
                    squarePaint.setColor(getResources().getColor(R.color.sleep_sober_color));
                    break;
                case 0x01://快速眼动睡眠
                case 0x02://浅睡
                    rect.top = (int) (1 * itemHeight * mProgress) + 1;
                    rect.right = (int) (right * mProgress);
                    rect.bottom = 2 * itemHeight - 1;
                    squarePaint.setColor(getResources().getColor(R.color.sleep_light_color));
                    break;
                case 0x03://深睡
                    rect.top = (int) (2 * itemHeight * mProgress) + 1;
                    rect.right = (int) (right * mProgress);
                    rect.bottom = 3 * itemHeight - 1;
                    squarePaint.setColor(getResources().getColor(R.color.sleep_deep_color));
                    break;
                default:
                    break;
            }

            canvas.drawRect(rect, squarePaint);
            left += width;
        }

        if (this.mProgress <= 0.0f) {
            postDelayed(this, 16);
        }
    }

    private void drawCoordinate(Canvas canvas) {

        TextPaint textPaint = this.mTextPaint;
        Rect textBounds = this.mTextBounds;

        Paint coordinatePaint = this.mCoordinatePaint;
        Path soberPath = this.mHorizontalPath;
        int itemHeight = this.mItemHeight;

        String[] labelText = this.mLabelText;

        coordinatePaint.setStyle(Paint.Style.STROKE);
        coordinatePaint.setColor(getResources().getColor(R.color.light_content_bg_color));

        for (int i = 0; i < 3; i++) {
            canvas.drawText(labelText[i], getPaddingLeft(), itemHeight * (0.5f + i) + (textBounds.height() >> 1), textPaint);
            soberPath.reset();
            soberPath.moveTo(getPaddingLeft(), (1 + i) * itemHeight);
            soberPath.lineTo(getWidth() - getPaddingRight() - 8, (1 + i) * itemHeight);
            canvas.drawPath(soberPath, coordinatePaint);
        }
    }


    private int measureHeight(int heightMeasureSpec) {
        int size = MeasureSpec.getSize(heightMeasureSpec);
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.EXACTLY://match_parent  或者指定的大小
                int i = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240.0f, getResources().getDisplayMetrics());
                return Math.max(i, size);
            case MeasureSpec.AT_MOST:// wrap_content
                return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240.0f, getResources().getDisplayMetrics());
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240.0f, getResources().getDisplayMetrics());
    }

    @Override
    public void run() {

        List<SleepData> sleepDataList = this.mSleepDataList;

        if (sleepDataList != null && mLen == sleepDataList.size() && mProgress >= 1) {
            mProgress = 1;
            invalidate();
            return;
        }

        if (sleepDataList != null && mProgress >= 1 && mLen < sleepDataList.size()) {
            this.mLen += 1;
        }

        if (mProgress <= 1.0f) {
            mProgress += 0.05f;
        }
        invalidate();

        postDelayed(this, 16);
    }

    /**
     * @param sleepDetailReport sleepDetailReport
     * @param sleepDataList     sleepDataList
     */
    public void setData(SleepDetailReport sleepDetailReport, List<SleepData> sleepDataList) {
        this.mProgress = 0.0f;
        this.mLen = 0;
        this.mSleepDataList = sleepDataList;
        this.mSleepDetailReport = sleepDetailReport;

        int fullTimeQuantum = 0;
        if (sleepDataList != null && !sleepDataList.isEmpty()) {
            for (SleepData sleepData : sleepDataList) {
                fullTimeQuantum += sleepData.getTimeQuantum();
            }
            this.mCellWidth = mFullRectF.width() / fullTimeQuantum;
            this.mFullTimeQuantum = fullTimeQuantum;
        }

        post(this);
    }
}
