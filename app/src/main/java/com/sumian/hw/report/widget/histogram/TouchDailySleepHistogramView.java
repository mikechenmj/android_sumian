package com.sumian.hw.report.widget.histogram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.TextPaint;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.sumian.hw.report.bean.DailyReport;
import com.sumian.hw.report.bean.SleepPackage;
import com.sumian.hw.report.widget.bean.SleepSegment;
import com.sumian.sd.R;
import com.sumian.sd.theme.three.SkinConfig;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

/**
 * Created by jzz
 * on 2018/4/5.
 * desc:可触摸查看睡眠时间段的日睡眠数据统计报表
 */

public class TouchDailySleepHistogramView extends View implements View.OnLongClickListener {

    // private static final String TAG = TouchDailySleepHistogramView.class.getSimpleName();

    private Paint mCoordinatePaint;//坐标系画笔
    private TextPaint mTextPaint;//文本画笔
    private Paint mSquarePaint;//直方图画笔

    private Path mHorizontalPath = new Path();

    private Rect mTextBounds = new Rect();
    private RectF mContentRectF = new RectF();
    private Rect mIndicatorBounds = new Rect();

    private String[] mLabelText = getResources().getStringArray(R.array.duration);

    private ArrayMap<Integer, SleepSegment> mSegmentArrayMap = new ArrayMap<>(0);

    private int mLightSleepHeight;
    private int mDeepSleepHeight;
    private int mAwakeSleepHeight;

    private int mDefaultIndicatorHeight;

    private int mContentWidth;
    private int mContentHeight;
    private int mCenterX;
    private int mCenterY;

    private float mFrameWidth;

    private int mFromUnixTime;
    private int mToUnixTime;

    private WindowManager mWindowManager;
    private View popView;
    private TextView tvTop;
    private TextView tvBottom;
    private WindowManager.LayoutParams popLayoutParams;

    private boolean mIsLongClick;
    private int mDefaultPadding = 0;
    private int mDefaultIndicatorMargin;

    private SleepSegment mCurrentSleepSegment;

    private float mDownX;
    private float mDownY;

    private int mCoordinateColor = Color.TRANSPARENT;

    private int mTextColor = Color.TRANSPARENT;
    private int mEmptyLabelTextColor = Color.TRANSPARENT;

    private int mSoberColor = Color.TRANSPARENT;
    private int mLightColor = Color.TRANSPARENT;
    private int mEogColor = Color.TRANSPARENT;
    private int mDeepColor = Color.TRANSPARENT;


    public TouchDailySleepHistogramView(Context context) {
        this(context, null);
    }

    public TouchDailySleepHistogramView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchDailySleepHistogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TouchDailySleepHistogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        setOnLongClickListener(this);
        initAttrs(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TouchDailySleepHistogramView, defStyleAttr, defStyleRes);

        this.mCoordinateColor = attributes.getColor(R.styleable.TouchDailySleepHistogramView_tdshv_coordinate_color, getResources().getColor(R.color.l3_color_day));

        this.mTextColor = attributes.getColor(R.styleable.TouchDailySleepHistogramView_tdshv_label_text_color, getResources().getColor(R.color.t2_color_day));
        this.mEmptyLabelTextColor = attributes.getColor(R.styleable.TouchDailySleepHistogramView_tdshv_empty_text_color, getResources().getColor(R.color.t5_color_day));

        this.mSoberColor = attributes.getColor(R.styleable.TouchDailySleepHistogramView_tdshv_sober_color, getResources().getColor(R.color.g3_color_day));
        this.mLightColor = attributes.getColor(R.styleable.TouchDailySleepHistogramView_tdshv_light_color, getResources().getColor(R.color.g1_color_day));
        this.mEogColor = attributes.getColor(R.styleable.TouchDailySleepHistogramView_tdshv_eog_color, getResources().getColor(R.color.g1_color_day));
        this.mDeepColor = attributes.getColor(R.styleable.TouchDailySleepHistogramView_tdshv_deep_color, getResources().getColor(R.color.g2_color_day));


        attributes.recycle();
    }

    private WindowManager.LayoutParams createPopupLayout(IBinder token) {
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        p.gravity = Gravity.START | Gravity.TOP;
        p.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        p.format = PixelFormat.TRANSLUCENT;
        p.flags = computeFlags(p.flags);
        p.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        p.token = token;
        p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
        p.setTitle("DiscreteTouchDaySleepHistogramView Indicator:" + Integer.toHexString(hashCode()));
        return p;
    }

    /**
     * @param curFlags Cur Flags
     * @return Flags
     */
    private int computeFlags(int curFlags) {
        curFlags &= ~(
                WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        curFlags |= WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES;
        curFlags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        curFlags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        return curFlags;
    }

    private void init() {

        //1.init  坐标系画笔
        mCoordinatePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mCoordinatePaint.setColor(mCoordinateColor);
        mCoordinatePaint.setStrokeCap(Paint.Cap.ROUND);
        float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, getResources().getDisplayMetrics());

        mCoordinatePaint.setStrokeWidth(width);
        mCoordinatePaint.setStyle(Paint.Style.STROKE);

        //2.init  文本画笔
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mTextPaint.setColor(getResources().getColor(R.color.full_general_color));
        mTextPaint.setStrokeCap(Paint.Cap.ROUND);
        mTextPaint.setStrokeJoin(Paint.Join.ROUND);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        float sp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12.0f, getResources().getDisplayMetrics());
        mTextPaint.setTextSize(sp);

        mTextPaint.getTextBounds(mLabelText[0], 0, mLabelText[0].length(), mTextBounds);

        mSquarePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mSquarePaint.setColor(getResources().getColor(R.color.transparent));
        mSquarePaint.setStyle(Paint.Style.FILL);
        mSquarePaint.setStrokeWidth(1.0f);

        this.mLightSleepHeight = getResources().getDimensionPixelSize(R.dimen.space_100);
        this.mDeepSleepHeight = getResources().getDimensionPixelSize(R.dimen.space_80);
        this.mAwakeSleepHeight = getResources().getDimensionPixelSize(R.dimen.space_120);

        this.mDefaultPadding = getResources().getDimensionPixelSize(R.dimen.space_10);
        this.mDefaultIndicatorHeight = getResources().getDimensionPixelSize(R.dimen.space_130);
        this.mDefaultIndicatorMargin = getResources().getDimensionPixelSize(R.dimen.space_2);
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

        this.mContentWidth = contentWidth;
        this.mContentHeight = contentHeight;

        this.mCenterX = w >> 1;
        this.mCenterY = h >> 1;

        String indicator = "20:00";
        this.mTextPaint.getTextBounds(indicator, 0, indicator.length(), mIndicatorBounds);

        this.mContentRectF.left = paddingLeft + mDefaultPadding;
        this.mContentRectF.top = paddingTop;
        this.mContentRectF.right = paddingLeft + mDefaultPadding + mContentWidth;
        this.mContentRectF.bottom = paddingTop + mContentHeight - 2.5f * mTextBounds.height() - (mCoordinatePaint.getStrokeWidth() / 2);

        this.mFrameWidth = (mContentWidth - mDefaultPadding * 2) / (60 * 60 * 24.0f);//20:00-20:00 24小时 每1s占所有睡眠数据的宽度
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCoordinate(canvas);
        drawSleepSegment(canvas);
    }

    private void drawSleepSegment(Canvas canvas) {

        if (mSegmentArrayMap == null || mSegmentArrayMap.isEmpty()) {
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16.0f, getResources().getDisplayMetrics()));
            mTextPaint.setColor(mEmptyLabelTextColor);
            canvas.drawText(getContext().getString(R.string.no_have_sleep_data), mCenterX, mCenterY - 40, mTextPaint);
            mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13.0f, getResources().getDisplayMetrics()));
            mTextPaint.setColor(mTextColor);
            canvas.drawText(getContext().getString(R.string.no_have_sleep_data_low_label), mCenterX, mCenterY + 40, mTextPaint);
            return;
        }

        String indicator;

        float startX = getPaddingLeft() + mDefaultPadding;

        float indicatorOffset;

        float indicatorStartX;
        float indicatorStartY = 0;

        //记录上一个 indicator 的 x 坐标
        float preToTimeStartX = 0;

        mCoordinatePaint.setStrokeWidth(1.0f);

        SleepSegment tmpSegment;
        ArrayList<SleepPackage> tmpPackage;
        int fromTime;
        int toTime;
        for (Map.Entry<Integer, SleepSegment> segmentEntry : mSegmentArrayMap.entrySet()) {
            tmpSegment = segmentEntry.getValue();
            tmpPackage = tmpSegment.sleepPackage;
            for (int i = 0, len = tmpPackage.size(); i < len; i++) {//1.draw sleep segment
                SleepPackage p = tmpPackage.get(i);
                fromTime = p.from_time;
                toTime = p.to_time;
                switch (p.state) {
                    case 0://清醒
                        mContentRectF.top = mContentRectF.bottom - mAwakeSleepHeight;
                        mSquarePaint.setColor(mSoberColor);
                        break;
                    case 1://快速眼动睡眠
                        mSquarePaint.setColor(mEogColor);
                    case 2://浅睡
                        mContentRectF.top = mContentRectF.bottom - mLightSleepHeight;
                        mSquarePaint.setColor(mLightColor);
                        break;
                    case 3://深睡
                        mContentRectF.top = mContentRectF.bottom - mDeepSleepHeight;
                        mSquarePaint.setColor(mDeepColor);
                        break;
                    default:
                        break;
                }

                //计算偏移量

                if (toTime < mFromUnixTime) {
                    continue;
                } else if (fromTime < mFromUnixTime && toTime >= mFromUnixTime) {
                    mContentRectF.left = startX;
                    mContentRectF.right = startX + (toTime - mFromUnixTime) * mFrameWidth;
                } else if (fromTime >= mFromUnixTime && toTime <= mToUnixTime) {
                    mContentRectF.left = startX + (fromTime - mFromUnixTime) * mFrameWidth;
                    mContentRectF.right = startX + (toTime - mFromUnixTime) * mFrameWidth;
                } else if (fromTime > mFromUnixTime && fromTime <= mToUnixTime && toTime > mToUnixTime) {
                    mContentRectF.left = startX + (fromTime - mFromUnixTime) * mFrameWidth;
                    mContentRectF.right = startX + (mToUnixTime - mFromUnixTime) * mFrameWidth;
                } else if (fromTime > mToUnixTime || toTime > mToUnixTime) {
                    continue;
                }
                canvas.drawRect(mContentRectF, mSquarePaint);
            }

            fromTime = tmpSegment.showFromTimeIndicator;//2.draw from time segment  indicator
            if (fromTime > mFromUnixTime) {
                indicator = formatIndicator(fromTime);
                if (tmpSegment.isClick) {
                    mTextPaint.setColor(mTextColor);
                    mTextPaint.setTextAlign(Paint.Align.CENTER);
                    mCoordinatePaint.setColor(mCoordinateColor);
                } else {
                    mTextPaint.setColor(Color.TRANSPARENT);
                    mCoordinatePaint.setColor(Color.TRANSPARENT);
                }

                indicatorStartX = startX + (fromTime - mFromUnixTime) * mFrameWidth;

                preToTimeStartX = indicatorStartX;

                switch (tmpSegment.fromTimeState) {
                    case 0://清醒
                        indicatorStartY = mContentRectF.bottom - mAwakeSleepHeight;
                        break;
                    case 1://快速眼动睡眠
                    case 2://浅睡
                        indicatorStartY = mContentRectF.bottom - mLightSleepHeight;
                        break;
                    case 3://深睡
                        indicatorStartY = mContentRectF.bottom - mDeepSleepHeight;
                        break;
                    default:
                        break;
                }

                canvas.drawLine(indicatorStartX, indicatorStartY, indicatorStartX, mContentRectF.bottom - mDefaultIndicatorHeight + mDefaultIndicatorMargin, mCoordinatePaint);

                if (indicatorStartX - mTextBounds.width() / 2 <= 0) {
                    mTextPaint.setTextAlign(Paint.Align.LEFT);
                } else {
                    mTextPaint.setTextAlign(Paint.Align.CENTER);
                }

                canvas.drawText(indicator, indicatorStartX, mContentRectF.bottom - mDefaultIndicatorHeight, mTextPaint);
            }

            toTime = tmpSegment.showToTimeIndicator;//3.draw to time segment indicator
            if (toTime < mToUnixTime) {
                indicator = formatIndicator(toTime);
                if (tmpSegment.isClick) {
                    mTextPaint.setColor(mTextColor);
                    mTextPaint.setTextAlign(Paint.Align.CENTER);
                    mCoordinatePaint.setColor(mCoordinateColor);
                } else {
                    mTextPaint.setColor(Color.TRANSPARENT);
                    mCoordinatePaint.setColor(Color.TRANSPARENT);
                }

                indicatorStartX = startX + (toTime - mFromUnixTime) * mFrameWidth;

                switch (tmpSegment.toTimeState) {
                    case 0://清醒
                        indicatorStartY = mContentRectF.bottom - mAwakeSleepHeight;
                        break;
                    case 1://快速眼动睡眠
                    case 2://浅睡
                        indicatorStartY = mContentRectF.bottom - mLightSleepHeight;
                        break;
                    case 3://深睡
                        indicatorStartY = mContentRectF.bottom - mDeepSleepHeight;
                        break;
                    default:
                        break;
                }

                //计算上一个 indicator 和当前 indicator 是否相交.如果大于一个indicator width,不偏移
                if (indicatorStartX - preToTimeStartX > mIndicatorBounds.width()) {
                    indicatorOffset = 0;
                } else {//2个 indicator 相交,需要偏移1.2个 indicator.height() 高度
                    indicatorOffset = mIndicatorBounds.height() * 1.2f;
                }

                canvas.drawLine(indicatorStartX, indicatorStartY, indicatorStartX, mContentRectF.bottom - mDefaultIndicatorHeight + mDefaultIndicatorMargin, mCoordinatePaint);

                if (getWidth() - indicatorStartX <= mTextBounds.width() / 2) {
                    mTextPaint.setTextAlign(Paint.Align.RIGHT);
                } else {
                    mTextPaint.setTextAlign(Paint.Align.CENTER);
                }

                canvas.drawText(indicator, indicatorStartX, mContentRectF.bottom - mDefaultIndicatorHeight - indicatorOffset, mTextPaint);
            }
        }
    }

    private String formatIndicator(int unixTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixTime * 1000L);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return String.format(Locale.getDefault(), "%02d%s%02d", hour, ":", minute);
    }

    private void drawCoordinate(Canvas canvas) {
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12.0f, getResources().getDisplayMetrics()));
        mTextPaint.getTextBounds(mLabelText[0], 0, mLabelText[0].length(), mTextBounds);

        int segmentStartX = getPaddingLeft() + mDefaultPadding;
        int segmentEndY = mContentHeight - mTextBounds.height();

        canvas.drawText(mLabelText[0], segmentStartX, segmentEndY, mTextPaint);
        canvas.drawText(mLabelText[1], segmentStartX + mFrameWidth * 4 * 60 * 60, segmentEndY, mTextPaint);
        canvas.drawText(mLabelText[2], segmentStartX + mFrameWidth * 8 * 60 * 60, segmentEndY, mTextPaint);
        canvas.drawText(mLabelText[3], segmentStartX + mFrameWidth * 16 * 60 * 60, segmentEndY, mTextPaint);
        mTextPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(mLabelText[0], segmentStartX + mFrameWidth * 24 * 60 * 60, segmentEndY, mTextPaint);

        mHorizontalPath.rewind();
        mHorizontalPath.moveTo(segmentStartX, mContentHeight - 2.5f * mTextBounds.height());
        mHorizontalPath.lineTo(getPaddingLeft() + mContentWidth - mDefaultPadding, mContentHeight - 2.5f * mTextBounds.height());
        mCoordinatePaint.setStyle(Paint.Style.STROKE);
        float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, getResources().getDisplayMetrics());
        mCoordinatePaint.setStrokeWidth(width);
        mCoordinatePaint.setColor(mCoordinateColor);
        canvas.drawPath(mHorizontalPath, mCoordinatePaint);
    }


    private int measureHeight(int heightMeasureSpec) {
        int size = MeasureSpec.getSize(heightMeasureSpec);
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.EXACTLY://match_parent  或者指定的大小
                int i = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200.0f, getResources().getDisplayMetrics());
                return Math.max(i, size);
            case MeasureSpec.AT_MOST:// wrap_content
                return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200.0f, getResources().getDisplayMetrics());
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200.0f, getResources().getDisplayMetrics());
    }

    public void setCoordinateColor(int coordinateColor) {
        mCoordinateColor = coordinateColor;
        invalidate();
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        invalidate();
    }

    public void setEmptyLabelTextColor(int emptyLabelTextColor) {
        mEmptyLabelTextColor = emptyLabelTextColor;
        requestLayout();
        invalidate();
    }

    public void setSoberColor(int soberColor) {
        mSoberColor = soberColor;
        requestLayout();
        invalidate();
    }

    public void setLightColor(int lightColor) {
        mLightColor = lightColor;
        requestLayout();
        invalidate();
    }

    public void setEogColor(int eogColor) {
        mEogColor = eogColor;
        requestLayout();
        invalidate();
    }

    public void setDeepColor(int deepColor) {
        mDeepColor = deepColor;
        requestLayout();
        invalidate();
    }

    /**
     * @param dailyReport dailyReport
     */
    public void setData(DailyReport dailyReport) {
        long todayUnixTime = dailyReport.date;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((todayUnixTime - (24 * 60 * 60)) * 1000L);//等于当天的前一天
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 20, 0, 0);

        this.mFromUnixTime = (int) (calendar.getTimeInMillis() / 1000L);

        calendar.setTimeInMillis(todayUnixTime * 1000L);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 20, 0, 0);

        this.mToUnixTime = (int) (calendar.getTimeInMillis() / 1000L);

        transformSleepSegments(dailyReport.packages);

        postInvalidateOnAnimation();

    }

    private void transformSleepSegments(ArrayList<SleepPackage> packages) {
        if (!mSegmentArrayMap.isEmpty()) {
            mSegmentArrayMap.clear();
        }

        if (packages == null || packages.isEmpty()) return;

        SleepSegment tmpSegment;
        ArrayList<SleepPackage> tmpPackages;

        for (SleepPackage p : packages) {
            p.calculateDuration();//计算每一个睡眠特征的时间跨度

            if (!mSegmentArrayMap.containsKey(p.sleep_id)) {
                tmpSegment = new SleepSegment();
                tmpSegment.id = p.sleep_id;
                tmpSegment.fromTimeState = p.state;
                tmpSegment.showFromTimeIndicator = p.from_time;
                tmpPackages = new ArrayList<>(0);
                tmpPackages.add(p);
                tmpSegment.sleepPackage = tmpPackages;
                mSegmentArrayMap.put(p.sleep_id, tmpSegment);
            } else {//已存在的睡眠特征区间
                tmpSegment = mSegmentArrayMap.get(p.sleep_id);
                tmpSegment.id = p.sleep_id;
                tmpSegment.sleepPackage.add(p);
                //tmpSegment.showToTimeIndicator = p.to_time;
            }
        }

        for (Map.Entry<Integer, SleepSegment> entry : mSegmentArrayMap.entrySet()) {
            tmpSegment = entry.getValue();
            tmpPackages = tmpSegment.sleepPackage;

            for (int i = 0; i < tmpPackages.size(); i++) {
                SleepPackage p = tmpPackages.get(i);
                tmpSegment.totalDuration += p.duration;
                if (i == tmpPackages.size() - 1) {
                    tmpSegment.toTimeState = p.state;
                    tmpSegment.showToTimeIndicator = p.to_time;
                }
            }
        }

        tmpSegment = null;
        for (SleepSegment segment : mSegmentArrayMap.values()) {
            if (tmpSegment == null) {
                tmpSegment = segment;
            } else {
                if (segment.showFromTimeIndicator >= mFromUnixTime && segment.showToTimeIndicator <= mToUnixTime
                        && segment.totalDuration > tmpSegment.totalDuration) {
                    tmpSegment = segment;
                }
            }
        }

        if (tmpSegment != null) {
            this.mCurrentSleepSegment = tmpSegment;
            mSegmentArrayMap.get(tmpSegment.id).isClick = true;
        }

    }

    @SuppressLint({"ClickableViewAccessibility"})
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                this.mDownX = event.getX();
                this.mDownY = event.getY();
                drawSegmentLabel(mDownX, mDownY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPopView(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (popView != null) {
                    popView.setVisibility(GONE);
                }
                mIsLongClick = false;
                if (popView != null && popView.isAttachedToWindow()) {
                    mWindowManager.removeViewImmediate(popView);
                    popView = null;
                }
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.onTouchEvent(event);
    }

    private void drawSegmentLabel(float downX, float downY) {

        int fromTime;
        int toTime;
        long startX;
        long endX;

        for (SleepSegment segment : mSegmentArrayMap.values()) {
            fromTime = segment.showFromTimeIndicator;
            toTime = segment.showToTimeIndicator;

            startX = fromTime - mFromUnixTime > 0 ? (long) ((fromTime - mFromUnixTime) * mFrameWidth) + mDefaultPadding : mDefaultPadding;
            endX = (long) ((toTime - mFromUnixTime) * mFrameWidth) + mDefaultPadding;
            if ((downX >= startX && downX <= endX) && (downY > mContentRectF.bottom - mContentRectF.height() && downY < mContentRectF.bottom)) {
                segment.isClick = true;
                mCurrentSleepSegment = segment;
                break;
            }
        }

        for (SleepSegment segment : mSegmentArrayMap.values()) {
            if (segment.id != mCurrentSleepSegment.id) {
                segment.isClick = false;
            }
        }

        postInvalidateOnAnimation();
    }

    private void drawPopView(float moveX, float moveY) {
        //fromUnixTime -----> toUnixTime
        if (!mIsLongClick) return;

        int fromTime;
        int toTime;
        long startX;
        long endX;

        String sleepState = null;
        String indicatorTopText;
        String indicatorBottomText;

        int duration;

        drawSegmentLabel(moveX, moveY);

        if (mCurrentSleepSegment != null && mCurrentSleepSegment.isClick) {
            fromTime = mCurrentSleepSegment.showFromTimeIndicator;
            toTime = mCurrentSleepSegment.showToTimeIndicator;

            startX = fromTime - mFromUnixTime > 0 ? (long) ((fromTime - mFromUnixTime) * mFrameWidth) + mDefaultPadding : mDefaultPadding;
            endX = (long) ((toTime - mFromUnixTime) * mFrameWidth) + mDefaultPadding;
            if ((moveX >= startX && moveX <= endX) && (moveY > getPaddingTop() && moveY < mContentRectF.bottom)) {
                for (SleepPackage p : mCurrentSleepSegment.sleepPackage) {
                    fromTime = p.from_time;
                    toTime = p.to_time;
                    duration = p.duration / 60;
                    switch (p.state) {
                        case 0:
                            sleepState = "清醒: ";
                            break;
                        case 1:
                        case 2:
                            sleepState = "浅睡: ";
                            break;
                        case 3:
                            sleepState = "深睡: ";
                            break;
                    }

                    startX = fromTime - mFromUnixTime > 0 ? (long) ((fromTime - mFromUnixTime) * mFrameWidth) : 0;
                    endX = (long) ((toTime - mFromUnixTime) * mFrameWidth);

                    if (moveX >= startX && moveX <= endX) {
                        indicatorTopText = String.format(Locale.getDefault(), "%s%d%s", sleepState, duration, " min");
                        indicatorBottomText = formatDuration(fromTime, toTime);

                        tvTop.setText(indicatorTopText);
                        tvBottom.setText(indicatorBottomText);

//                        if (popView.getWidth() / 2 - moveX > 0) {
//                            moveX = moveX - (popView.getWidth() >> 1);
//                        } else if (getWidth() - moveX > popView.getWidth()) {
//                            moveX = popView.getWidth() - popView.getWidth() / 2;
//                        } else if (moveX > getPaddingLeft() + mDefaultPadding + popView.getWidth() / 2 && popView.getWidth() / 2 < getWidth() - moveX) {
//                            moveX = moveX - (popView.getWidth() >> 1);
//                        }

                        popLayoutParams.x = (int) (moveX - (popView.getWidth() >> 1) + 0.5f);
                        popLayoutParams.y = (int) (mContentRectF.bottom - (mContentRectF.bottom - mDefaultIndicatorHeight));
                        popView.setVisibility(VISIBLE);
                        mWindowManager.updateViewLayout(popView, popLayoutParams);
                        break;
                    }

                }
            } else {
                popView.setVisibility(GONE);
            }
        } else {
            popView.setVisibility(GONE);
        }

    }

    private String formatDuration(int fromTime, int toTime) {
        return String.format(Locale.getDefault(), "%s%s%s", formatUnixTime(fromTime), " - ", formatUnixTime(toTime));
    }

    private String formatUnixTime(int unixTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixTime * 1000L);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return String.format(Locale.getDefault(), "%02d%s%02d", hour, ":", minute);
    }

    @SuppressLint("InflateParams")
    @Override
    public boolean onLongClick(View v) {
        if (mSegmentArrayMap == null || mSegmentArrayMap.isEmpty()) return false;
        mIsLongClick = true;

        getParent().requestDisallowInterceptTouchEvent(true);

        if (popView == null) {
            popView = LayoutInflater.from(getContext()).inflate(R.layout.hw_lay_sleep_data_indicator_pop, null, false);
            CardView popCardView = popView.findViewById(R.id.pop_container);
            popCardView.setCardBackgroundColor(SkinConfig.isInNightMode(v.getContext()) ? getResources().getColor(R.color.b2_color_night) : getResources().getColor(R.color.b2_color_day));
            tvTop = popView.findViewById(R.id.tv_indicator_top);
            tvTop.setTextColor(SkinConfig.isInNightMode(v.getContext()) ? getResources().getColor(R.color.t5_color_night) : getResources().getColor(R.color.t5_color_day));
            tvBottom = popView.findViewById(R.id.tv_indicator_bottom);
            tvBottom.setTextColor(SkinConfig.isInNightMode(v.getContext()) ? getResources().getColor(R.color.t5_color_night) : getResources().getColor(R.color.t5_color_day));
            popLayoutParams = createPopupLayout(getWindowToken());
        }

        if (popView != null && !popView.isAttachedToWindow()) {
            mWindowManager.addView(popView, popLayoutParams);
        }

        drawPopView(mDownX, mDownY);
        return true;
    }

}
