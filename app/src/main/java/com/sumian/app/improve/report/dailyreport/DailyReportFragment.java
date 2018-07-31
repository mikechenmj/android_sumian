package com.sumian.app.improve.report.dailyreport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.sumian.app.R;
import com.sumian.app.app.HwAppManager;
import com.sumian.app.base.BasePagerFragment;
import com.sumian.app.push.ReportPushManager;
import com.sumian.app.improve.report.calendar.CalendarDialog;
import com.sumian.app.improve.report.note.NoteDialog;
import com.sumian.app.improve.report.note.SleepNote;
import com.sumian.app.improve.report.viewModel.ReportModel;
import com.sumian.app.improve.widget.SwitchDateView;
import com.sumian.app.improve.widget.report.LoadViewPagerRecyclerView;
import com.sumian.app.improve.widget.report.SyncingReportView;
import com.sumian.app.log.LogManager;
import com.sumian.app.widget.refresh.ActionLoadingDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sm
 * on 2018/3/6.
 * desc:
 */

@SuppressWarnings("ConstantConditions")
public class DailyReportFragment extends BasePagerFragment<DailyReportPresenter> implements DailyReportContract.View,
        SwitchDateView.OnSwitchDateListener, View.OnClickListener, RecyclerViewPager.OnPageChangedListener,
        NoteDialog.OnWriteNoteCallback, DailyAdapter.OnRefreshCallback, ReportModel.OnSyncCallback {

    public static final String EXTRA_SCROLL = "com.sumian.app.extra.SCROLL";
    private static final int PRELOAD_THRESHOLD = 5;

    SyncingReportView mSyncingReportView;
    SwitchDateView mSwitchDateView;
    LoadViewPagerRecyclerView mRecycler;
    ImageView mIvFloatDiary;

    private DailyAdapter mDailyAdapter;
    private BroadcastReceiver mBroadcastReceiver;
    private DailyReport mCurrentDailyReport;
    private boolean mNeedScrollToBottom;
    private ActionLoadingDialog mActionLoadingDialog;
    private int mCurrentPosition;

    private boolean mIsSyncing;

    public static DailyReportFragment newInstance() {
        return new DailyReportFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_fragment_daily_report;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mSyncingReportView = root.findViewById(R.id.syncing_report_view);
        mSwitchDateView = root.findViewById(R.id.sdv);
        mRecycler = root.findViewById(R.id.recycler);
        mIvFloatDiary = root.findViewById(R.id.iv_float_diary);
        root.findViewById(R.id.iv_float_diary).setOnClickListener(this);

        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecycler.setAdapter(mDailyAdapter = new DailyAdapter().setOnSwitchDateListener(this).setOnClickListener(v -> initNoteDialog()).setOnRefreshCallback(this));
        mRecycler.addOnPageChangedListener(this);
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        DailyReportPresenter.init(this);
    }

    @Override
    protected void initData() {
        super.initData();
        initReceiver();
        initAdapter();
        boolean showDailyPushReport = showPushReportIfNeeded();
        if (!showDailyPushReport) {
            mPresenter.getInitReports(mSwitchDateView.getTodayUnixTime());
        }
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
                        mNeedScrollToBottom = intent.getBooleanExtra(EXTRA_SCROLL, false);
                        scrollToTime(selectUnixTime);
                        break;
                    default:
                        break;
                }
            }
        }, filter);
    }

    private void initAdapter() {
        DailyReport dailyReport;
        if (HwAppManager.getReportModel().isHaveTodayCache()) {
            dailyReport = HwAppManager.getReportModel().getCacheDailyReport();
        } else {
            dailyReport = new DailyReport();
            dailyReport.date = (int) mSwitchDateView.getTodayUnixTime();
            dailyReport.bedtime_state = new ArrayList<>(0);
            dailyReport.packages = new ArrayList<>(0);
        }
        mDailyAdapter.addItem(dailyReport);
    }

    @Override
    public void setPresenter(DailyReportContract.Presenter presenter) {
        this.mPresenter = (DailyReportPresenter) presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        showPushReportIfNeeded();
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void setReportsData(List<DailyReport> dailyReports) {
        if (dailyReports == null || dailyReports.isEmpty()) {
            return;
        }
        mDailyAdapter.initAddAll(dailyReports);
    }

    @Override
    public void showReportAtTime(long unixTime) {
        scrollToTime(unixTime);
    }

    @Override
    public void updateReportData(DailyReport dailyReport) {
        dailyReport.needScrollToBottom = mNeedScrollToBottom;
        mNeedScrollToBottom = false;
        int updateItemPosition = mDailyAdapter.updateItem(dailyReport);
        if (updateItemPosition == mCurrentPosition) {
            mCurrentDailyReport = dailyReport;
        }
        updateFloatDiaryVisibility();
    }

    @Override
    public void insertReportDataAtHead(List<DailyReport> dailyReports) {
        mDailyAdapter.insertDataToHead(dailyReports);
    }

    @Override
    public void scrollToTime(long unixTime) {
        int position = mDailyAdapter.getPosition((int) unixTime);
        if (position == -1) {
            int date = mDailyAdapter.getItem(0).date;
            long intervalUnixTime = 24 * 60 * 60;//86400 一天的间隔时间戳
            long distance = date - unixTime;
            int pageCount = (int) (distance / intervalUnixTime);
            mPresenter.getReportsAndGotoTargetTime(date, pageCount, false, unixTime);
        } else {
            mCurrentDailyReport = mDailyAdapter.getItem(position);
            mCurrentDailyReport.needScrollToBottom = mNeedScrollToBottom;
            mNeedScrollToBottom = false;
            mRecycler.scrollToPosition(position);
        }
    }

    private DailyAdapter.ViewHolder getViewHolder(int position) {
        return (DailyAdapter.ViewHolder) mRecycler.findViewHolderForAdapterPosition(position);
    }

    private void preloadData() {
        mPresenter.getPreloadReports(mDailyAdapter.getItemDate(0));
    }


    @Override
    public void onClick(View v) {
        initNoteDialog();
    }

    private void initNoteDialog() {
        SleepNote sleepNote = new SleepNote();
        sleepNote.wakeUpMood = TextUtils.isEmpty(mCurrentDailyReport.wrote_diary_at) ? -1 : mCurrentDailyReport.wake_up_mood;
        sleepNote.sleepId = mCurrentDailyReport.id;
        sleepNote.bedtimeState = mCurrentDailyReport.bedtime_state;
        sleepNote.remark = mCurrentDailyReport.remark;
        NoteDialog noteDialog = NoteDialog.newInstance(sleepNote);
        noteDialog.setOnWriteNoteCallback(this);
        noteDialog.show(getFragmentManager(), NoteDialog.class.getSimpleName());
    }

    @Override
    public void OnPageChanged(int positionBeforeScroll, int position) {
        mCurrentPosition = position;
        mCurrentDailyReport = mDailyAdapter.getItem(position);
        if ((mCurrentDailyReport == null || mCurrentDailyReport.sleep_duration <= 0) && mIsSyncing) {
            mSyncingReportView.showSyncing();
        } else {
            mSyncingReportView.hide();
        }
        updateFloatDiaryVisibility();
        if (position < PRELOAD_THRESHOLD) {
            preloadData();
        }
    }

    private void updateFloatDiaryVisibility() {
        mIvFloatDiary.setVisibility(mCurrentDailyReport.packages != null
                && mCurrentDailyReport.packages != null && mCurrentDailyReport.packages.size() > 0
                && mCurrentDailyReport.light_duration_percent != 0
                && TextUtils.isEmpty(mCurrentDailyReport.wrote_diary_at)
                ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onWrite(DailyReport dailyReport) {
        mIvFloatDiary.setVisibility(View.GONE);
        this.mCurrentDailyReport = dailyReport;
        int position = mDailyAdapter.getPosition(dailyReport.date);
        mDailyAdapter.updateItem(position, dailyReport);
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

    @Override
    public void onFailure(String error) {
        showToast(error);
    }

    private void dismissLoadingDialog() {
        if (mActionLoadingDialog != null) {
            mActionLoadingDialog.dismiss();
        }
    }

    private void stopRefreshing() {
        DailyAdapter.ViewHolder viewHolder = getViewHolder(mCurrentPosition);
        if (viewHolder != null) {
            viewHolder.setRefreshing(false);
        }
    }

    @Override
    public void onEnterTab() {
        // check monitor sync sleep data status
        HwAppManager.getReportModel().setOnSyncCallback(this).checkSyncStatus();
        showPushReportIfNeeded();
        LogManager.appendUserOperationLog("点击 '日报告' 界面");
        HwAppManager.getJobScheduler().checkJobScheduler();
    }

    @Override
    public void onSyncingCallback() {
        this.mIsSyncing = true;
        DailyReport dailyReport = mDailyAdapter.getItem(mCurrentPosition);
        if (dailyReport == null || dailyReport.sleep_duration > 0) {
            return;
        }
        mSyncingReportView.showSyncing();
    }

    @Override
    public void onSyncingErrorCallback() {
        this.mIsSyncing = false;
        mSyncingReportView.showSyncError();
    }

    @Override
    public void onSyncFinishedCallback() {
        this.mIsSyncing = false;
        mSyncingReportView.hide();
    }

    /**
     * 检查是否有推送消息，如果有，则显示对应数据，同时清空推送消息
     *
     * @return 是否有推送消息
     */
    private boolean showPushReportIfNeeded() {
        return ReportPushManager.getInstance().checkDailyPushReportAndRun(getContext(), pushReport -> {
            mNeedScrollToBottom = true;
            int pushDate = pushReport.getPushDate();
            if (mCurrentDailyReport != null && mCurrentDailyReport.date == pushDate) {
                mPresenter.refreshReport(mCurrentDailyReport.date);
            } else {
                scrollToTime(pushDate);
            }
        });
    }

    @Override
    public void onRefresh(int position, DailyReport dailyReport) {
        mPresenter.refreshReport(dailyReport.date);
    }

    public long getCurrentReportTime() {
        if (mCurrentDailyReport == null) {
            return 0;
        }
        return mCurrentDailyReport.getDateInMillis();
    }
}
