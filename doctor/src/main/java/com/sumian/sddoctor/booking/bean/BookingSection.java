package com.sumian.sddoctor.booking.bean;

import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/18 20:52
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class BookingSection extends SectionEntity<Booking> {
    public long dateInMillis;
    public int bookingCount;
    public boolean isFirst;
    public boolean isLast;

    private BookingSection(Booking booking, boolean isFirst, boolean isLast) {
        super(booking);
        this.isFirst = isFirst;
        this.isLast = isLast;
    }

    private BookingSection() {
        super(null);
    }

    public static BookingSection createHeaderSection(long dateInMillis, int bookingCount) {
        BookingSection bookingSection = new BookingSection();
        bookingSection.isHeader = true;
        bookingSection.bookingCount = bookingCount;
        bookingSection.dateInMillis = dateInMillis;
        return bookingSection;
    }

    public static BookingSection createItemSection(Booking booking, boolean isFirst, boolean isLast) {
        return new BookingSection(booking, isFirst, isLast);
    }
}
