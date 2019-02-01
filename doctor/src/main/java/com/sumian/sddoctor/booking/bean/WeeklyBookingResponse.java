package com.sumian.sddoctor.booking.bean;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/21 13:48
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class WeeklyBookingResponse {

    // Map<WeekTime, Map<DayTime, List<Booking>>>
    public Map<Integer, Map<Integer, List<Booking>>> data;

    @NotNull
    @Override
    public String toString() {
        return "WeeklyBookingResponse{" +
                "data=" + data +
                '}';
    }

}
