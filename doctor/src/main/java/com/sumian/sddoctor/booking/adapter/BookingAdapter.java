package com.sumian.sddoctor.booking.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sumian.sddoctor.R;
import com.sumian.sddoctor.booking.bean.Booking;
import com.sumian.sddoctor.booking.bean.BookingSection;
import com.sumian.sddoctor.booking.widget.BookingItemView;
import com.sumian.sddoctor.util.TimeUtil;

import java.util.List;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/18 20:50
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class BookingAdapter extends BaseSectionQuickAdapter<BookingSection, BaseViewHolder> {

    private OnBookingItemClickListener mOnBookingItemClickListener;

    public BookingAdapter(List<BookingSection> data) {
        super(R.layout.item_booking, R.layout.item_booking_head, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, BookingSection item) {
        boolean isToday = TimeUtil.isInTheSameDay(item.dateInMillis, System.currentTimeMillis());
        String pattern = mContext.getResources().getString(isToday ? R.string.booking_item_date_pattern_today : R.string.booking_item_date_pattern);
        String dateStr = TimeUtil.formatDate(pattern, item.dateInMillis);
        TextView textView = helper.getView(R.id.tv_time);
        textView.setText(dateStr);
        textView.setActivated(isToday);
        helper.setVisible(R.id.tv_no_booking, item.bookingCount == 0);
        helper.setVisible(R.id.v_bottom_divider, item.bookingCount != 0);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookingSection item) {
        BookingItemView bookingItemView = helper.getView(R.id.booking_item_view);
        Booking booking = item.t;
        bookingItemView.setBookingData(booking);
        bookingItemView.showTimeLineTop(!item.isFirst);
        bookingItemView.showTimeLineBottom(!item.isLast);
        bookingItemView.showBottomDivider(!item.isLast);
        if (mOnBookingItemClickListener != null) {
            bookingItemView.setOnClickListener(v -> mOnBookingItemClickListener.onBookingItemClick(booking));
        }
    }

    public void setOnBookingItemClickListener(OnBookingItemClickListener onBookingItemClickListener) {
        mOnBookingItemClickListener = onBookingItemClickListener;
    }

    public interface OnBookingItemClickListener {
        void onBookingItemClick(Booking booking);
    }
}
