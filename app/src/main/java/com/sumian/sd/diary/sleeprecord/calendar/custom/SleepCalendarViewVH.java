package com.sumian.sd.diary.sleeprecord.calendar.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sumian.sd.R;
import com.sumian.sd.diary.sleeprecord.calendar.calendarView.CalendarViewVH;

/**
 * <pre>
 *     @author : Zhan Xuzhao
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
    public void setDay(int day, int dayType) {
        String text = day > 0 ? String.valueOf(day) : "";
        getMTextView().setText(text);
        getMTextView().setTextColor(getTextColor(dayType));
        getMTextView().setBackground(getBgDrawable(dayType));
        getMTextView().setTypeface(Typeface.DEFAULT, isBold(dayType) ? Typeface.BOLD : Typeface.NORMAL);
    }

    private boolean isBold(int dayType) {
        return false;
    }

    @Override
    protected int getTextColor(int dayType) {
        int textColor;
        switch (dayType) {
            case SleepDayType.HAS_DATA:
            case SleepDayType.NO_DATA:
                textColor = R.color.t1_color;
                break;
            case SleepDayType.SELECT_HAS_DATA:
            case SleepDayType.SELECT_NO_DATA:
                textColor = R.color.white;
                break;
            case SleepDayType.FEATURE:
                textColor = R.color.t1_color_40;
                break;
            default:
                textColor = R.color.t1_color;
                break;
        }
        return getMContext().getResources().getColor(textColor);
    }

    @Override
    protected Drawable getBgDrawable(int dayType) {
        int drawableRes;
        switch (dayType) {
            case SleepDayType.HAS_DATA:
                drawableRes = R.drawable.ic_calendar_date;
                break;
            case SleepDayType.SELECT_HAS_DATA:
                drawableRes = R.drawable.ic_calendar_selecteddate;
                break;
            case SleepDayType.SELECT_NO_DATA:
                drawableRes = R.drawable.ic_calendar_selected;
                break;
            default:
                return null;
        }
        return getMContext().getResources().getDrawable(drawableRes);
    }
}
