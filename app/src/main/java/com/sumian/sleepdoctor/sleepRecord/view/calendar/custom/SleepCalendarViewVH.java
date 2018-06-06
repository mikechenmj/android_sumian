package com.sumian.sleepdoctor.sleepRecord.view.calendar.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.sleepRecord.view.calendar.calendarView.CalendarViewVH;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/29 15:59
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SleepCalendarViewVH extends CalendarViewVH {
    private SleepCalendarViewVH(View itemView) {
        super(itemView);
    }

    @NonNull
    public static CalendarViewVH create(ViewGroup parent) {
        Context context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_calendar, parent, false);
        return new SleepCalendarViewVH(inflate);
    }

    @Override
    protected int getTextColor(int dayType) {
        int textColor;
        switch (dayType) {
            case SleepDayType.TYPE_NORMAL:
                textColor = R.color.t2_color;
                break;
            case SleepDayType.TYPE_TODAY:
                textColor = R.color.b3_color;
                break;
            case SleepDayType.TYPE_FEATURE:
                textColor = R.color.t2_alpha_40_color;
                break;
            case SleepDayType.TYPE_HAS_RECORD_NO_DOCTOR_EVALUATION:
                textColor = R.color.t1_color;
                break;
            case SleepDayType.TYPE_HAS_RECORD_HAS_DOCTOR_EVALUATION:
                textColor = R.color.t1_color;
                break;
            case SleepDayType.TYPE_SELECTED_DAY:
                textColor = R.color.white;
                break;
            default:
                textColor = R.color.t2_color;
                break;
        }
        return mContext.getResources().getColor(textColor);
    }

    @Override
    protected Drawable getBgDrawable(int dayType) {
        int drawableRes;
        switch (dayType) {
            case SleepDayType.TYPE_HAS_RECORD_HAS_DOCTOR_EVALUATION:
                drawableRes = R.drawable.ring_blue_b5;
                break;
            case SleepDayType.TYPE_SELECTED_DAY:
                drawableRes = R.drawable.circle_b3;
                break;
            default:
                return null;
        }
        return mContext.getResources().getDrawable(drawableRes);
    }
}
