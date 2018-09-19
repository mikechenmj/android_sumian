package com.sumian.hw.report.presenter;

import com.sumian.hw.report.base.BaseResultResponse;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.report.bean.DailyMeta;
import com.sumian.hw.report.bean.DailyReport;
import com.sumian.hw.report.contract.DailyReportContract;
import com.sumian.sd.app.AppManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by sm
 * on 2018/3/7.
 * desc:
 */

public class DailyReportPresenter implements DailyReportContract.Presenter {

    private static final int PRELOAD_DATA_SIZE = 5;
    private static final int INIT_PAGE_SIZE = PRELOAD_DATA_SIZE * 2;

    private DailyReportContract.View mView;

    private long mTargetDayUnixTime = 0;
    private static final int LOAD_DATA_TYPE_INIT = 1;
    private static final int LOAD_DATA_TYPE_REFRESH = 2;
    private static final int LOAD_DATA_TYPE_PRELOAD = 3;
    private static final int LOAD_DATA_TYPE_LOAD_AND_GOTO = 4;
    private int mLoadDataType = LOAD_DATA_TYPE_INIT;

    private DailyReportPresenter(DailyReportContract.View view) {
        view.setPresenter(this);
        this.mView = view;
    }

    public static void init(DailyReportContract.View view) {
        new DailyReportPresenter(view);
    }

    private void getDayReports(long todayUnixTime, int pageSize, boolean include) {
        if (mView == null) return;
        if (mLoadDataType == LOAD_DATA_TYPE_INIT) {
            mView.onBegin();
        }
        Map<String, Object> map = new HashMap<>(0);
        map.put("date", todayUnixTime);
        map.put("page_size", pageSize);
        map.put("is_include", include ? 1 : 0);

        Call<BaseResultResponse<DailyReport, DailyMeta>> call = AppManager.getHwV1HttpService().getTodaySleepReport(map);
        mCalls.add(call);

        call.enqueue(new BaseResponseCallback<BaseResultResponse<DailyReport, DailyMeta>>() {
            @Override
            protected void onSuccess(BaseResultResponse<DailyReport, DailyMeta> response) {
                List<DailyReport> data = response.data;
                Collections.reverse(data);
                switch (mLoadDataType) {
                    case LOAD_DATA_TYPE_INIT:
                        mView.setReportsData(data);
                        mView.showReportAtTime(todayUnixTime);
                        break;
                    case LOAD_DATA_TYPE_REFRESH:
                        if (data.size() == 0) {
                            return;
                        }
                        mView.updateReportData(data.get(0));
                        break;
                    case LOAD_DATA_TYPE_PRELOAD:
                        mView.insertReportDataAtHead(data);
                        break;
                    case LOAD_DATA_TYPE_LOAD_AND_GOTO:
                        mView.insertReportDataAtHead(data);
                        mView.showReportAtTime(mTargetDayUnixTime);
                        break;

                }
            }

            @Override
            protected void onFailure(int code, String error) {
                mView.onFailure(error);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                mView.onFinish();
            }
        });
    }

    @Override
    public void getInitReports(long todayUnixTime) {
        mLoadDataType = LOAD_DATA_TYPE_INIT;
        getDayReports(todayUnixTime, INIT_PAGE_SIZE, true);
    }

    @Override
    public void getReportsAndGotoTargetTime(long startDayUnixTime, int pageSize, boolean include, long targetDayUnixTime) {
        mLoadDataType = LOAD_DATA_TYPE_LOAD_AND_GOTO;
        getDayReports(startDayUnixTime, pageSize + PRELOAD_DATA_SIZE, include);
        mTargetDayUnixTime = targetDayUnixTime;
    }

    @Override
    public void refreshReport(long unixTime) {
        mLoadDataType = LOAD_DATA_TYPE_REFRESH;
        getDayReports(unixTime, 1, true);
    }

    @Override
    public void getPreloadReports(long unixTime) {
        mLoadDataType = LOAD_DATA_TYPE_PRELOAD;
        getDayReports(unixTime, PRELOAD_DATA_SIZE, false);
    }
}
