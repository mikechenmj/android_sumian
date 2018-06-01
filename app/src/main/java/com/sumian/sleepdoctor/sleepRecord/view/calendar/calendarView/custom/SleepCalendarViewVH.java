package com.sumian.sleepdoctor.sleepRecord.view.calendar.calendarView.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.sleepRecord.view.calendar.calendarView.CalendarViewVH;
import com.sumian.sleepdoctor.sleepRecord.view.calendar.calendarView.DayType;

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
    protected int getTextColor(DayType textType) {
        int colorRes;
        switch (textType) {
            case TYPE_1:
                colorRes = R.color.t1_color;
                break;
            case TYPE_2:
                colorRes = R.color.t1_color;
                break;
            case TYPE_3:
                colorRes = R.color.white;
                break;
            case TYPE_4:
                colorRes = R.color.b3_color;
                break;
            case TYPE_5:
                colorRes = R.color.t2_alpha_40_color;
                break;
            case TYPE_0:
            default:
                colorRes = R.color.t2_color;
                break;
        }
        return mContext.getResources().getColor(colorRes);
    }

    @Override
    protected Drawable getBgDrawable(DayType textType) {
        int drawableRes;
        switch (textType) {
            case TYPE_2:
                drawableRes = R.drawable.ring_blue_b5;
                break;
            case TYPE_3:
                drawableRes = R.drawable.circle_blue_b3;
                break;
            default:
                return null;
        }
        return mContext.getResources().getDrawable(drawableRes);
    }
}
