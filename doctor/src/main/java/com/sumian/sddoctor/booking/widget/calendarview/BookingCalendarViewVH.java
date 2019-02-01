package com.sumian.sddoctor.booking.widget.calendarview;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sumian.sddoctor.R;
import com.sumian.sddoctor.widget.calendar.calendarview.base.CalendarViewVH;

import androidx.annotation.NonNull;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/29 15:59
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class BookingCalendarViewVH extends CalendarViewVH {
    private BookingCalendarViewVH(View itemView) {
        super(itemView);
    }

    @NonNull
    public static CalendarViewVH create(ViewGroup parent) {
        Context context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_calendar, parent, false);
        return new BookingCalendarViewVH(inflate);
    }

    @Override
    public void setDay(int day, int dayType) {
        String text = day > 0 ? String.valueOf(day) : "";
        mTextView.setText(text);
        mTextView.setTextColor(getTextColor(dayType));
        mTextView.setBackground(getBgDrawable(dayType));
        mTextView.setTypeface(Typeface.DEFAULT, isBold(dayType) ? Typeface.BOLD : Typeface.NORMAL);
    }

    private boolean isBold(int dayType) {
        switch (dayType) {
            case BookingDayType.TYPE_HAS_BOOKING:
            case BookingDayType.TYPE_TODAY:
                return true;
            default:
                return false;
        }
    }

    @Override
    protected int getTextColor(int dayType) {
        int textColor;
        switch (dayType) {
            case BookingDayType.TYPE_NORMAL:
                textColor = R.color.t2_color;
                break;
            case BookingDayType.TYPE_TODAY:
                textColor = R.color.b3_color;
                break;
            case BookingDayType.TYPE_FEATURE:
                textColor = R.color.t2_alpha_40_color;
                break;
            case BookingDayType.TYPE_HAS_BOOKING:
                textColor = R.color.t1_color;
                break;
            case BookingDayType.TYPE_SELECTED_DAY:
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
            case BookingDayType.TYPE_SELECTED_DAY:
                drawableRes = R.drawable.circle_b3;
                break;
            default:
                return null;
        }
        return mContext.getResources().getDrawable(drawableRes);
    }
}
