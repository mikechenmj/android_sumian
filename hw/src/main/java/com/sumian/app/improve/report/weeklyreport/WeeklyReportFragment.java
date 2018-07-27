package com.sumian.app.improve.report.weeklyreport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.format.DateUtils;
import android.view.View;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.sumian.app.R;
import com.sumian.app.app.AppManager;
import com.sumian.app.base.BasePagerFragment;
import com.sumian.app.push.ReportPushManager;
import com.sumian.app.improve.report.calendar.CalendarDialog;
import com.sumian.app.improve.widget.report.LoadViewPagerRecyclerView;
import com.sumian.app.log.LogManager;
import com.sumian.app.network.response.SleepDurationReport;
import com.sumian.app.tab.report.sheet.SleepAdviceBottomSheet;
import com.sumian.app.widget.refresh.ActionLoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by jzz
 * on 2017/10/10.
 * desc:
 */

@SuppressWarnings("ConstantConditions")
public class WeeklyReportFragment extends BasePagerFragment<WeeklyReportPresenter> implements
    WeeklyReportContact.View, RecyclerViewPager.OnPageChangedListener, WeeklyReportAdapter.OnWeekReportCallback {

    @BindView(R.id.recycler)
    LoadViewPagerRecyclerView mRecycler;

    private static final int PRELOAD_THRESHOLD = 5;
    private SleepDurationReport mCurrentReport;
    private WeeklyReportAdapter mAdapter;
    private BroadcastReceiver mBroadcastReceiver;
    private boolean mNeedScrollToBottom;
    private int mCurrentPosition;
    private ActionLoadingDialog mActionLoadingDialog;
    private boolean mNeedUpdateWhenLoad;

    public static WeeklyReportFragment newInstance() {
        return new WeeklyReportFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_fragment_week_report;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecycler.addOnPageChangedListener(this);
        mRecycler.setAdapter(mAdapter = new WeeklyReportAdapter().setWeekReportCallback(this));
    }

    @Override
    protected void initData() {
        super.initData();
        initReceiver();
        long currentTimeMillis = System.currentTimeMillis();
        boolean showPushReport = showPushReportIfNeeded();
        if (!showPushReport) {
            mPresenter.getInitReports(currentTimeMillis);
        }
        initAdapter();
    }

    private void initAdapter() {
        SleepDurationReport sleepDurationReport = SleepDurationReport.createPlaceHoldData(System.currentTimeMillis());
        List<SleepDurationReport> list = new ArrayList<>();
        list.add(sleepDurationReport);
        mAdapter.addAllDataAtHead(list);
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CalendarDialog.ACTION_SELECT_DATE);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case CalendarDialog.ACTION_SELECT_DATE:
                        long selectUnixTime = intent.getLongExtra(CalendarDialog.EXTRA_DATE, 0);
                        scrollToTime(selectUnixTime * 1000L);
                        break;
                    default:
                        break;
                }
            }
        }, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        showPushReportIfNeeded();
    }

    private void scrollToTime(long time) {
        int position = mAdapter.getPosition(time);
        if (position == -1) {
            long firstItemTime;
            if (mAdapter.getItemCount() == 0) {
                firstItemTime = System.currentTimeMillis();
            } else {
                firstItemTime = mAdapter.getItem(0).getStart_date_show() * 1000L;
            }
            int pageCount = (int) ((firstItemTime - time) / (DateUtils.DAY_IN_MILLIS * 7));
            mPresenter.getReportsAndGotoTargetTime(firstItemTime, pageCount, false, time);
        } else {
            SleepDurationReport report = mAdapter.getItem(position);
            report.needScrollToBottom = mNeedScrollToBottom;
            mNeedScrollToBottom = false;
            mRecycler.scrollToPosition(position);
        }
    }

    private WeeklyReportAdapter.ViewHolder getViewHolder(int position) {
        return (WeeklyReportAdapter.ViewHolder)
            mRecycler.findViewHolderForAdapterPosition(position);
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        WeeklyReportPresenter.init(this);
    }

    @Override
    public void onEnterTab() {
        LogManager.appendUserOperationLog("点击 '周报告' 界面");
        showPushReportIfNeeded();
        AppManager.getJobScheduler().checkJobScheduler();
    }

    /**
     * 检查是否有推送消息，如果有，则显示对应数据，同时清空推送消息
     *
     * @return 是否有推送消息
     */
    private boolean showPushReportIfNeeded() {
        return ReportPushManager.getInstance().checkWeeklyPushReportAndRun(getContext(), pushReport -> {
            mNeedScrollToBottom = true;
            int pushDate = pushReport.getPushDate();
            // 这里有坑
            // 服务器获取的周报列表信息，start_date是从周六晚8点开始计算的，start_date_show是从周一0点开始计算的
            // "start_date":"2018-04-14 20:00:00",
            // "end_date":"2018-04-21 19:59:59",
            // "start_date_show":1523721600, => 2018-04-15 00:00:00:00
            // "end_date_show":1524240000，=> 2018-04-21 00:00:00:00
            // 推送的pushDate是2018-04-14 20:00:00:00从周六晚8点开始计算的，要换算成周一0点开始计算的格式，否则后续无法匹配。
            pushDate = pushDate + 3600 * 4; // 周六20:00 开始 => 周日00:00 开始
            if (mCurrentReport != null && mCurrentReport.getStart_date_show() == pushDate) {
                mPresenter.refreshReport(mCurrentReport.getStart_date_show() * 1000L);
            } else {
                scrollToTime(pushDate * 1000L);
            }
        });
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void setReportsData(List<SleepDurationReport> reports) {
        if (reports != null && !reports.isEmpty()) {
            mAdapter.addAllDataAtHead(reports);
        }
    }

    @Override
    public void updateReportData(SleepDurationReport reports) {
        mCurrentReport = reports;
        mCurrentReport.needScrollToBottom = mNeedScrollToBottom;
        mNeedScrollToBottom = false;
        mAdapter.updateItem(reports);
    }

    @Override
    public void insertReportDataAtHead(List<SleepDurationReport> reports) {
        if (reports == null || reports.isEmpty()) {
            showCenterToast("没有更多睡眠数据");
        } else {
            mAdapter.addAllDataAtHead(reports);
        }
    }

    @Override
    public void showReportAtTime(long time) {
        scrollToTime(time);
    }

    @Override
    public void setPresenter(WeeklyReportContact.Presenter presenter) {
        this.mPresenter = (WeeklyReportPresenter) presenter;
    }

    @Override
    public void onFailure(String error) {
        showToast(error);
    }

    private void preloadData() {
        mPresenter.getPreloadReports(mAdapter.getItem(0).getStart_date_show() * 1000L);
    }

    @Override
    public void OnPageChanged(int positionBeforeScroll, int position) {
        mCurrentPosition = position;
        this.mCurrentReport = mAdapter.getItem(position);
        if (mNeedUpdateWhenLoad) {
            mNeedUpdateWhenLoad = false;
            mPresenter.refreshReport(mCurrentReport.getStart_date_show() * 1000L);
            mNeedScrollToBottom = true;
        }

        if (position < PRELOAD_THRESHOLD) {
            preloadData();
        }
    }

    @Override
    public void onSwitchWeek(View v, int position, SleepDurationReport item) {
        switch (v.getId()) {
            case R.id.iv_pre:
                if (position > 0) {
                    mRecycler.scrollToPosition(position - 1);
                }
                break;
            case R.id.iv_next:
                mRecycler.scrollToPosition(position + 1);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefreshWeekReport(View v, int position, SleepDurationReport item) {
        mPresenter.refreshReport(item.getEnd_date_show() * 1000L);
    }

    @Override
    public void onShowSleepAdvice(View v, int position, SleepDurationReport item) {
        getFragmentManager()
            .beginTransaction()
            .add(SleepAdviceBottomSheet.newInstance(item.getAdvice()), SleepAdviceBottomSheet.class.getSimpleName())
            .commit();
    }

    @Override
    public void onBegin() {
        if (mActionLoadingDialog == null) {
            mActionLoadingDialog = new ActionLoadingDialog();
        }
        mActionLoadingDialog.show(getActivity().getSupportFragmentManager());
    }

    @Override
    public void onFinish() {
        dismissLoadingDialog();
        stopRefreshing();
    }

    private void dismissLoadingDialog() {
        if (mActionLoadingDialog != null) {
            mActionLoadingDialog.dismiss();
        }
    }

    private void stopRefreshing() {
        WeeklyReportAdapter.ViewHolder viewHolder = getViewHolder(mCurrentPosition);
        if (viewHolder != null) {
            viewHolder.setRefreshing(false);
        }
    }
}
