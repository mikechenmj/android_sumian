package com.sumian.app.improve.report.calendar;

import com.sumian.app.base.BaseNetView;
import com.sumian.app.base.BasePresenter;
import com.sumian.app.improve.report.calendar.PagerCalendarItem;

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
