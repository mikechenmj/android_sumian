package com.sumian.sddoctor.widget.calendar.calendarview.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumian.sddoctor.R;

import androidx.recyclerview.widget.RecyclerView;


/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/29 15:59
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CalendarViewVH extends RecyclerView.ViewHolder {

    public TextView mTextView;
    protected Context mContext;

    protected CalendarViewVH(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        mTextView = itemView.findViewById(R.id.tv);
    }

    public static CalendarViewVH create(ViewGroup parent) {
        Context context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_calendar, parent, false);
        return new CalendarViewVH(inflate);
    }

    public void setDay(int day, int dayType) {
        String text = day > 0 ? String.valueOf(day) : "";
        mTextView.setText(text);
        mTextView.setTextColor(getTextColor(dayType));
        mTextView.setBackground(getBgDrawable(dayType));
    }

    protected int getTextColor(int textType) {
        return Color.DKGRAY;
    }

    protected Drawable getBgDrawable(int textType) {
        return null;
    }
}
