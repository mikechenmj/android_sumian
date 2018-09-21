package com.sumian.hw.report.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.sumian.hw.common.util.TimeUtil;
import com.sumian.sd.R;
import com.sumian.sd.app.App;
import com.sumian.sd.theme.three.SkinConfig;

import java.util.Calendar;

/**
 * Created by jzz
 * on 2017/6/1.
 * <p>
 * desc: CalendarView 按照常见日历格式，是7列（一周7点）7行（第1行，星期几，2-7行日期）的结构
 */

public class CalendarView extends View implements View.OnClickListener {

    public static final String TAG = CalendarView.class.getSimpleName();
    public static final int DAY_COUNT_OF_WEEK = 7;

    private int mItemWidth;
    private int mItemHeight;
    private Rect mTextBound = new Rect();
    private float mRadius;
    private String[] mWeekdayNames = {"日", "一", "二", "三", "四", "五", "六"};
    private TextPaint mTextPaint;
    private Paint mBgPaint;
    private Paint mDotPaint;
    private Paint mLinePaint;
    private Rect mItemRect = new Rect();
    private float mDownX;
    private float mDownY;
    // colors
    private int mDefaultTextColor;
    private int mHighlightTextColor;
    private int mWithBgTextColor;
    private int mBgCircleColor;
    private int mDotColor;
    private int mTodayColor;

    private int mToday = -1;//今天的position

    // special draw times
    private int[] highlightDays; // 有睡眠日记的天
    private int[] highlightDays2;   // 今天
    private int[] drawUnderlineDays;
    private int[] drawDotDays;
    private int[] drawBgDays;
    private Calendar mMonthCalendar;
    private int mX;
    private int mY;
    private OnDateClickListener mOnDateClickListener;

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initColors();
        initDotPaint();
        initBgPaint();
        initLinePaint();
        initTextPaint();
        mMonthCalendar = TimeUtil.getStartDayOfMonth(System.currentTimeMillis());
        setOnClickListener(this);
    }

    private void initDotPaint() {
        mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mDotPaint.setColor(mDotColor);
        mDotPaint.setStyle(Paint.Style.FILL);
    }

    private void initBgPaint() {
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBgPaint.setColor(getResources().getColor(android.R.color.white));
        mBgPaint.setStyle(Paint.Style.FILL);
    }

    private void initLinePaint() {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mLinePaint.setColor(getResources().getColor(R.color.weekend_font_color));
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setStrokeJoin(Paint.Join.ROUND);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(1.0f);
    }

    private void initTextPaint() {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mTextPaint.setColor(mDefaultTextColor);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(px);
        mTextPaint.setStrokeCap(Paint.Cap.ROUND);
        mTextPaint.setStrokeJoin(Paint.Join.ROUND);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        String text = mWeekdayNames[0];
        mTextPaint.getTextBounds(text, 0, text.length(), mTextBound);
    }

    private void initColors() {
        if (SkinConfig.isInNightMode(App.getAppContext())) {//夜间模式
            mDefaultTextColor = getResources().getColor(R.color.t2_color_night);
            mHighlightTextColor = getResources().getColor(R.color.t5_color_night);
            mWithBgTextColor = getResources().getColor(R.color.t5_color_night);
            mBgCircleColor = getResources().getColor(R.color.b1_color_night);
            mDotColor = getResources().getColor(R.color.b8_color_night);
            mTodayColor = getResources().getColor(R.color.b2_color_day);
        } else {//正常模式
            mDefaultTextColor = getResources().getColor(R.color.t2_color_day);
            mHighlightTextColor = getResources().getColor(R.color.t5_color_day);
            mWithBgTextColor = getResources().getColor(R.color.t5_color_day);
            mBgCircleColor = getResources().getColor(R.color.b1_color_day);
            mDotColor = getResources().getColor(R.color.b8_color_day);
            mTodayColor = getResources().getColor(R.color.b3_color_day);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, measureHeight(heightMeasureSpec));
    }

    private int measureHeight(int heightMeasureSpec) {
        int size = MeasureSpec.getSize(heightMeasureSpec);
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.EXACTLY://match_parent  或者指定的大小
                int i = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
                return Math.max(i, size);
            case MeasureSpec.AT_MOST:// wrap_content
                return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
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
        mItemWidth = contentWidth / 7;
        mItemHeight = contentHeight / 7;
        mRadius = Math.min(mItemWidth, mItemHeight) * 0.9f * 0.5f;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int dayCountOfTheMonth = TimeUtil.getDayCountInTheMonth(mMonthCalendar.getTimeInMillis());
        int dayOfWeekOfTheFirstDayOfTheMonth = mMonthCalendar.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = 1;
        int index = 0;
        while (dayOfMonth <= dayCountOfTheMonth) {
            updateItemRect(index);
            if (index < DAY_COUNT_OF_WEEK) {
                // draw weekday name
                drawText(canvas, mWeekdayNames[index], false);
            } else if (index < DAY_COUNT_OF_WEEK + dayOfWeekOfTheFirstDayOfTheMonth - 1) {
                // shift position， draw nothing
            } else {
                // draw date
                drawDate(canvas, dayOfMonth, index);
                dayOfMonth++;
            }
            index++;
        }
    }

    private void updateItemRect(int index) {
        int compare;
        int mod;
        compare = index / DAY_COUNT_OF_WEEK;
        mod = index % DAY_COUNT_OF_WEEK;
        mX = mItemWidth * (mod + 1) - (mItemWidth >> 1);
        mY = (compare + 1) * mItemHeight - (mItemHeight >> 1);
        mItemRect.left = mItemWidth * mod;
        mItemRect.right = mItemWidth * (mod + 1);
        mItemRect.top = mItemHeight * (compare);
        mItemRect.bottom = mItemHeight * (compare + 1);
    }

    private void drawText(Canvas canvas, String text, boolean highlight) {
        int color = highlight ? mHighlightTextColor : mDefaultTextColor;
        drawText(canvas, text, color);
    }

    private void drawText(Canvas canvas, String text, int textColor) {
        mTextPaint.setColor(textColor);
        canvas.drawText(text, mX, mY + (mTextBound.height() >> 1), mTextPaint);
    }

    private void drawDot(Canvas canvas) {
        mDotPaint.setColor(mDotColor);
        canvas.drawCircle(mX + mTextBound.width(), mY - mTextBound.height() / 2, 10, mDotPaint);
    }

    private void drawUnderline(Canvas canvas) {
        mLinePaint.setColor(mBgCircleColor);
        canvas.drawLine(mX - mTextBound.width() / 1.5f, mY + mTextBound.height(),
                mX + mTextBound.width() / 1.5f, mY + mTextBound.height(), mLinePaint);
    }

    private void drawBgCircle(Canvas canvas) {
        mBgPaint.setColor(mBgCircleColor);
        canvas.drawCircle(mItemRect.centerX(), mItemRect.centerY(), mRadius, mBgPaint);
    }

    private void drawDate(Canvas canvas, int dayOfMonth, int index) {
        if (dayIn(dayOfMonth, drawDotDays)) {
            drawDot(canvas);
        }
        if (dayIn(dayOfMonth, drawUnderlineDays)) {
            drawUnderline(canvas);
        }
        String text = String.valueOf(dayOfMonth);
        boolean drawBg = dayIn(dayOfMonth, drawBgDays);
        if (drawBg) {
            drawBgCircle(canvas);
            drawText(canvas, text, mWithBgTextColor);
        } else {
            if (dayIn(dayOfMonth, highlightDays2)) {
                drawText(canvas, text, mTodayColor);
            } else {
                drawText(canvas, text, dayIn(dayOfMonth, highlightDays));
            }
        }
    }

    /**
     * 获取点击区域对应7*7矩阵中的位置
     */
    private int getClickPosition(float eventX, float eventY) {
        int remainderX = (int) (eventX / mItemWidth);
        int remainderY = (int) (eventY / mItemHeight);
        return (remainderY) * DAY_COUNT_OF_WEEK + remainderX;
    }

    @Override
    public void onClick(View v) {
        float downX = mDownX;
        float downY = mDownY;
        int position = getClickPosition(downX, downY);

        int dayOfMonth = getDayOfMonthFromCalendarViewPosition(position);
        if (!isDayOfMonthValid(dayOfMonth)) {
            return;
        }
        if (mOnDateClickListener != null) {
            Calendar calendar = TimeUtil.getDayStartCalendar(mMonthCalendar.getTimeInMillis());
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mOnDateClickListener.onDateClick(calendar.getTimeInMillis());
        }
    }

    private boolean isDayOfMonthValid(int dayOfMonth) {
        return dayOfMonth >= 1 && dayOfMonth <= mMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private int getDayOfMonthFromCalendarViewPosition(int position) {
        int dayOfWeek = mMonthCalendar.get(Calendar.DAY_OF_WEEK);
        return position - DAY_COUNT_OF_WEEK - dayOfWeek + 2;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public interface OnCalenderListener {

        void showCalender(long unixTime);
    }

    /**
     * @param time time in millis
     */
    public void setTime(long time) {
        mMonthCalendar = TimeUtil.getStartDayOfMonth(time);
    }

    public boolean dayIn(int day, int[] days) {
        if (days == null || days.length == 0) {
            return false;
        }
        for (int d : days) {
            if (day == d) {
                return true;
            }
        }
        return false;
    }

    public CalendarView setHighlightDays(int[] highlightDays) {
        this.highlightDays = highlightDays;
        return this;
    }

    public CalendarView setHighlightDays2(int[] highlightDays2) {
        this.highlightDays2 = highlightDays2;
        return this;
    }

    public CalendarView setDrawUnderlineDays(int[] drawUnderlineDays) {
        this.drawUnderlineDays = drawUnderlineDays;
        return this;
    }

    public CalendarView setDrawDotDays(int[] drawDotDays) {
        this.drawDotDays = drawDotDays;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public CalendarView setDrawBgDays(int[] drawBgDays) {
        this.drawBgDays = drawBgDays;
        return this;
    }

    public interface OnDateClickListener {
        void onDateClick(long timeInMillis);
    }

    public void setOnDateClickListener(OnDateClickListener onDateClickListener) {
        mOnDateClickListener = onDateClickListener;
    }
}
