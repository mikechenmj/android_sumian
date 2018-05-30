package com.sumian.sleepdoctor.widget.calendar.calendarView.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.widget.calendar.calendarView.CalendarViewVH;
import com.sumian.sleepdoctor.widget.calendar.calendarView.DayType;

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

//    DAY_TYPE_NORMAL 正常日期 t2 text color
//    DAY_TYPE_EMPHASIZE_1 有睡眠记录 t1 text color
//    TEXT_TYPE_EMPHASIZE_2 有医生评论 t1 text color, blue ring bg
//    TEXT_TYPE_EMPHASIZE_3 当前选择日期 white text color, blue circle bg
//    DAY_TYPE_EMPHASIZE_4 今天 blue text color
//    DAY_TYPE_EMPHASIZE_5
//    DAY_TYPE_DE_EMPHASIZE_1 未来时间，t2_alpha_40 text color
//    TEXT_TYPE_DE_EMPHASIZE_2
//    TEXT_TYPE_DE_EMPHASIZE_3

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
            case DE_EMPHASIZE_1:
                colorRes = R.color.t2_alpha_40_color;
                break;
            case EMPHASIZE_1:
                colorRes = R.color.t1_color;
                break;
            case EMPHASIZE_2:
                colorRes = R.color.t1_color;
                break;
            case EMPHASIZE_3:
                colorRes = R.color.white;
                break;
            case EMPHASIZE_4:
                colorRes = R.color.b3_color;
                break;
            case NORMAL:
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
            case EMPHASIZE_2:
                drawableRes = R.drawable.ring_blue_b5;
                break;
            case EMPHASIZE_3:
                drawableRes = R.drawable.circle_blue_b3;
                break;
            default:
                return null;
        }
        return mContext.getResources().getDrawable(drawableRes);
    }
}
