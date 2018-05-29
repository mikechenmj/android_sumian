package com.sumian.sleepdoctor.widget.calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/29 15:59
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CalendarVH extends RecyclerView.ViewHolder{

    Context mContext;
    @BindView(R.id.tv)
    TextView mTextView;

    public static CalendarVH create(ViewGroup parent) {
        Context context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_calendar, parent, false);
        return new CalendarVH(inflate);
    }

    CalendarVH(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
    }

    public void setDay(int day, @CalendarView.DayType int textType) {
        String text = day > 0 ? String.valueOf(day) : "";
        mTextView.setText(text);
        mTextView.setTextColor
                (getTextColor(textType));
        mTextView.setBackground(getBgDrawable(textType));
    }

    protected int getTextColor(int textType) {
        return Color.DKGRAY;
    }

    protected Drawable getBgDrawable(int textType) {
        return null;
    }
}
