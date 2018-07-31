package com.sumian.hw.improve.report.calendar;

import com.sumian.hw.base.BaseNetView;
import com.sumian.hw.base.BasePresenter;

import java.util.List;

/**
 * Created by sm
 * on 2018/3/7.
 * desc:
 */

public interface CalendarReportContract {

    interface View extends BaseNetView<Presenter> {

        void onGetOneCalendarReportInfoSuccess(List<PagerCalendarItem> items);

    }


    interface Presenter extends BasePresenter {

        void getOneCalendarReportInfo(long monthInDayUnixTime, boolean isInclude);

    }
}
