package com.sumian.hw.report.presenter;

import com.sumian.common.network.response.ErrorResponse;
import com.sumian.hw.common.util.TimeUtil;
import com.sumian.hw.report.base.BaseResultResponse;
import com.sumian.hw.report.bean.WeekMeta;
import com.sumian.hw.report.contract.WeeklyReportContact;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.network.callback.BaseSdResponseCallback;
import com.sumian.sd.network.response.SleepDurationReport;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * 服务器返回的数据，时间是逆序的，5,4,3,2,1,adapter中的数据，时间是顺序的，1,2,3,4,5
 */
public class WeeklyReportPresenter implements WeeklyReportContact.Presenter {
    private static final int MIN_DATA_SIZE = 5;
    private static final int EMPTY_EARLIEST_START_TIME = 0;
    private static final int PRELOAD_DATA_SIZE = 5;
    private static final int INIT_DATA_SIZE = PRELOAD_DATA_SIZE * 2;
    private long mTargetDayTimeInMillis = 0;
    private static final int LOAD_DATA_TYPE_INIT = 1;
    private static final int LOAD_DATA_TYPE_REFRESH = 2;
    private static final int LOAD_DATA_TYPE_PRELOAD = 3;
    private static final int LOAD_DATA_TYPE_LOAD_AND_GOTO = 4;
    private int mLoadDataType = LOAD_DATA_TYPE_INIT;
    private WeeklyReportContact.View mView;
    private List<SleepDurationReport> mSleepDurationReports = new ArrayList<>(MIN_DATA_SIZE);   // 记录加载的数据，用于确定请求新数据所需要传入的时间。

    private WeeklyReportPresenter(WeeklyReportContact.View view) {
        view.setPresenter(this);
        mView = view;
    }

    public static void init(WeeklyReportContact.View view) {
        new WeeklyReportPresenter(view);
    }

    private void loadReports(long time, int pageSize, boolean isInclude) {
        if (mLoadDataType == LOAD_DATA_TYPE_INIT) {
            mView.onBegin();
        }
        Map<String, Object> map = new HashMap<>(0);
        map.put("date", time / 1000);
        map.put("page_size", pageSize);
        map.put("is_include", isInclude ? 1 : 0);
        map.put("direction", 0);
        Call<BaseResultResponse<SleepDurationReport, WeekMeta>> call = AppManager.getHwHttpService().getWeeksSleepReport(map);
        mCalls.add(call);
        call.enqueue(new BaseSdResponseCallback<BaseResultResponse<SleepDurationReport, WeekMeta>>() {
            @Override
            protected void onSuccess(BaseResultResponse<SleepDurationReport, WeekMeta> response) {
                List<SleepDurationReport> weekReports = response.data;
                // 加载服务器的数据是，先做插空处理
                switch (mLoadDataType) {
                    case LOAD_DATA_TYPE_INIT:
                    case LOAD_DATA_TYPE_LOAD_AND_GOTO:
                    case LOAD_DATA_TYPE_PRELOAD:
                        long newReportsEarliestStartTime = getNewReportsEarliestStartTime();
                        weekReports = resolveDataFromServer(newReportsEarliestStartTime, weekReports, MIN_DATA_SIZE);
                        Collections.reverse(weekReports);
                        mSleepDurationReports.addAll(0, weekReports);
                        break;
                }
                switch (mLoadDataType) {
                    case LOAD_DATA_TYPE_INIT:
                        mView.setReportsData(weekReports);
                        mView.showReportAtTime(weekReports.get(weekReports.size() - 1).getStartDateShowInMillis());
                        break;
                    case LOAD_DATA_TYPE_LOAD_AND_GOTO:
                        mView.insertReportDataAtHead(weekReports);
                        if (weekReports.size() == 0) {
                            return;
                        }
                        for (SleepDurationReport report : weekReports) {
                            if (report.isTimeBetweenStartAndEnd(mTargetDayTimeInMillis)) {
                                mView.showReportAtTime(mTargetDayTimeInMillis);
                                break;
                            }
                        }
                        break;
                    case LOAD_DATA_TYPE_PRELOAD:
                        mView.insertReportDataAtHead(weekReports);
                        break;
                    case LOAD_DATA_TYPE_REFRESH:
                        SleepDurationReport weekReport;
                        if (weekReports == null || weekReports.isEmpty()) {
                            weekReport = SleepDurationReport.createFromTime(time);
                        } else {
                            weekReport = weekReports.get(0);
                        }
                        mView.updateReportData(weekReport);
                        break;
                }
            }

            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                super.onFailure(errorResponse);
                mView.onFailure(errorResponse.getMessage());
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                mView.onFinish();
            }
        });
    }

    /**
     * @return 当前数据最早的start time，如果当前数据为空，返回EMPTY_EARLIEST_START_TIME
     */
    private long getCurrentReportsEarliestStartTime() {
        long earliestStartTime;
        if (mSleepDurationReports == null || mSleepDurationReports.size() == 0) {
            earliestStartTime = EMPTY_EARLIEST_START_TIME;
        } else {
            earliestStartTime = mSleepDurationReports.get(0).getStartDateShowInMillis();
        }
        return earliestStartTime;
    }

    /**
     * @return 新添加的数据应该具有的最早的start time。
     * 如果当前数据集不为空，则取当前最早数据start time往前移动一周的时间，
     * 如果为空，则取System.currentTimeMillis()所对应的时间
     */
    private long getNewReportsEarliestStartTime() {
        long currentReportsEarliestStartTime = getCurrentReportsEarliestStartTime();
        if (currentReportsEarliestStartTime == EMPTY_EARLIEST_START_TIME) {
            return TimeUtil.getStartTimeOfWeek(System.currentTimeMillis());
        } else {
            long weekStartDayTime = TimeUtil.getStartTimeOfWeek(currentReportsEarliestStartTime);
            Calendar calendar = TimeUtil.getCalendar(weekStartDayTime);
            TimeUtil.rollDay(calendar, -7);
            return calendar.getTimeInMillis();
        }
    }

    @Override
    public void getInitReports(long todayTime) {
        mLoadDataType = LOAD_DATA_TYPE_INIT;
        loadReports(todayTime, INIT_DATA_SIZE, true);
    }

    @Override
    public void getReportsAndGotoTargetTime(long startDayTime, int pageSize, boolean include, long targetDayTime) {
        mLoadDataType = LOAD_DATA_TYPE_LOAD_AND_GOTO;
        mTargetDayTimeInMillis = targetDayTime;
        loadReports(startDayTime, pageSize + PRELOAD_DATA_SIZE, include);
    }

    @Override
    public void refreshReport(long time) {
        mLoadDataType = LOAD_DATA_TYPE_REFRESH;
        loadReports(time, 1, true);
    }

    @Override
    public void getPreloadReports(long time) {
        mLoadDataType = LOAD_DATA_TYPE_PRELOAD;
        loadReports(time, PRELOAD_DATA_SIZE, false);
    }

    /**
     * 如果某些周没有数据，那么服务器就不返回那些周的数据，
     * 而客户端要显示所有周的数据，即使它是空的，所以这里要多数据做填充处理。
     * 如果入参集合为空，则生产minDataSize周的数据；
     * 如果入参集合不为空，则遍历入参集合，填充其中空缺的部分；
     * 如果所有空缺填充完后，数量不足minDataSize个，则额外补充数据，直至够minDataSize个；
     * 返回最终数据(size>=minDataSize）
     *
     * @param newReportsEarliestStartTime 新增数据应该具备的最早的start time
     * @param data                        服务器返回的原始数据
     * @param minDataSize                 需要的数据的最小个数
     * @return 处理后的数据
     */
    private static List<SleepDurationReport> resolveDataFromServer(long newReportsEarliestStartTime, List<SleepDurationReport> data, int minDataSize) {
        Calendar calendar = TimeUtil.getCalendar(newReportsEarliestStartTime);
        ArrayList<SleepDurationReport> resolvedData = new ArrayList<>();
        // 如果数据为空，则新建minDataSize个空数据
        if (data == null || data.size() == 0) {
            for (int i = 0; i < minDataSize; i++) {
                SleepDurationReport fromTime = SleepDurationReport.createFromTime(calendar.getTimeInMillis());
                resolvedData.add(fromTime);
                TimeUtil.rollDay(calendar, -7);
            }
        } else {
            Iterator<SleepDurationReport> iterator = data.iterator();
            while ((iterator.hasNext())) {
                // 填补空缺数据
                SleepDurationReport next = iterator.next();
                long startDateShowInMillis = next.getStartDateShowInMillis();
                while (startDateShowInMillis < calendar.getTimeInMillis()) {
                    resolvedData.add(SleepDurationReport.createFromTime(calendar.getTimeInMillis()));
                    TimeUtil.rollDay(calendar, -7);
                }
                resolvedData.add(next);
                TimeUtil.rollDay(calendar, -7);
            }
            // 如果补空完后，数据不足 minDataSize 个则继续生产几个数据
            while (resolvedData.size() < minDataSize) {
                resolvedData.add(SleepDurationReport.createFromTime(calendar.getTimeInMillis()));
                TimeUtil.rollDay(calendar, -7);
            }
        }
        return resolvedData;
    }
}
