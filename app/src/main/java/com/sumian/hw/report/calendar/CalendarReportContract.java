package com.sumian.hw.report.calendar;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;

import java.util.List;

/**
 * Created by sm
 * on 2018/3/7.
 * desc:
 */

public interface CalendarReportContract {

    interface View extends HwBaseNetView<Presenter> {

        void onGetOneCalendarReportInfoSuccess(List<PagerCalendarItem> items);

    }


    interface Presenter extends HwBasePresenter {

        void getOneCalendarReportInfo(long monthInDayUnixTime, boolean isInclude);

    }
}
