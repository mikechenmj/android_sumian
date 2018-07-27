package com.sumian.app.tab.report.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;

import com.sumian.app.R;
import com.sumian.app.base.BaseActivity;
import com.sumian.app.common.helper.ToastHelper;
import com.sumian.app.common.util.TimeUtil;
import com.sumian.app.common.util.UiUtil;
import com.sumian.app.network.response.SleepDetailReport;
import com.sumian.app.tab.report.bean.SleepData;
import com.sumian.app.tab.report.contract.DaySleepDetailContract;
import com.sumian.app.tab.report.presenter.DaySleepDetailPresenter;
import com.sumian.app.widget.SleepNoteView;
import com.sumian.app.widget.TitleBar;
import com.sumian.app.widget.histogram.DaySleepHistogramView;
import com.sumian.app.widget.refresh.BlueRefreshView;

import java.util.List;

import butterknife.BindView;

/**
 * Created by jzz
 * on 2017/10/11.
 * desc:睡眠数据详情
 */

public class DaySleepDetailReportActivity extends BaseActivity implements DaySleepDetailContract.View,
    SwipeRefreshLayout.OnRefreshListener, TitleBar.OnBackListener {

    private static final String ARGS_SLEEP_DETAIL_REPORT = "args_sleep_detail_report";

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.refresh)
    BlueRefreshView mRefresh;

    @BindView(R.id.day_sleep_histogram_view)
    DaySleepHistogramView mDaySleepHistogramView;

    @BindView(R.id.tv_sleep_duration_hour)
    TextView mTvSleepDurationHour;
    @BindView(R.id.tv_sleep_duration_min)
    TextView mTvSleepDurationMin;

    @BindView(R.id.tv_sleep_awake_duration_hour)
    TextView mTvSleepAwakeDurationHour;
    @BindView(R.id.tv_sleep_awake_duration_min)
    TextView mTvSleepAwakeDurationMin;

    @BindView(R.id.tv_sleep_from_time)
    TextView mTvGoToSleepTime;
    @BindView(R.id.tv_sleep_awake_up_time)
    TextView mTvSleepWakedUpTime;

    @BindView(R.id.tv_sleep_deep_duration_hour)
    TextView mTvSleepDeepDurationHour;
    @BindView(R.id.tv_sleep_deep_duration_min)
    TextView mTvSleepDeepDurationMin;

    @BindView(R.id.tv_sleep_light_duration_hour)
    TextView mTvSleepLightDurationHour;
    @BindView(R.id.tv_sleep_light_duration_min)
    TextView mTvSleepLightDurationMin;

    @BindView(R.id.sleepNoteView)
    SleepNoteView mSleepNoteView;


    private long mSleepId;

    private DaySleepDetailContract.Presenter mPresenter;

    public static void show(Context context, long sleepId) {
        Intent intent = new Intent(context, DaySleepDetailReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ARGS_SLEEP_DETAIL_REPORT, sleepId);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        this.mSleepId = bundle.getLong(ARGS_SLEEP_DETAIL_REPORT);
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_detail_sleep;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        this.mTitleBar.addOnBackListener(this);
        this.mRefresh.setOnRefreshListener(this);
        this.mTvSleepDurationHour.setTypeface(UiUtil.getTypeface());
        this.mTvSleepDurationMin.setTypeface(UiUtil.getTypeface());

        this.mTvSleepAwakeDurationHour.setTypeface(UiUtil.getTypeface());
        this.mTvSleepAwakeDurationMin.setTypeface(UiUtil.getTypeface());

        this.mTvGoToSleepTime.setTypeface(UiUtil.getTypeface());
        this.mTvSleepWakedUpTime.setTypeface(UiUtil.getTypeface());

        this.mTvSleepDeepDurationHour.setTypeface(UiUtil.getTypeface());
        this.mTvSleepDeepDurationMin.setTypeface(UiUtil.getTypeface());

        this.mTvSleepLightDurationHour.setTypeface(UiUtil.getTypeface());
        this.mTvSleepLightDurationMin.setTypeface(UiUtil.getTypeface());

        DaySleepDetailPresenter.init(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.doSyncDaySleepDetailReport(mSleepId);
    }

    @Override
    public void setPresenter(DaySleepDetailContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onFailure(String error) {
        runOnUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onBegin() {
        runOnUiThread(() -> this.mRefresh.setRefreshing(true));
    }

    @Override
    public void onFinish() {
        this.mRefresh.setRefreshing(false);
    }

    @Override
    public void onSyncDaySleepDetailReportSuccess(SleepDetailReport sleepDetailReport) {
        runUiThread(() -> {
            List<SleepData> sleepData = mPresenter.transform2SleepData(sleepDetailReport);
            this.mDaySleepHistogramView.setData(sleepDetailReport, sleepData);

            this.mTitleBar.setText(TimeUtil.formatSlashDate(sleepDetailReport.getTo_time()));

            int sleepDuration = sleepDetailReport.getSleep_duration();
            this.mTvSleepDurationHour.setText(TimeUtil.calculateHour(sleepDuration));
            this.mTvSleepDurationMin.setText(TimeUtil.calculateMin(sleepDuration));
            int awakeDuration = sleepDetailReport.getAwake_duration();
            this.mTvSleepAwakeDurationHour.setText(TimeUtil.calculateHour(awakeDuration));
            this.mTvSleepAwakeDurationMin.setText(TimeUtil.calculateMin(awakeDuration));

            int sleepAt = sleepDetailReport.getSleep_at();
            this.mTvGoToSleepTime.setText(sleepAt > 0 ? TimeUtil.formatTime(sleepAt) : "----");
            int wakedUpAtTime = sleepDetailReport.getWaked_up_at();
            this.mTvSleepWakedUpTime.setText(wakedUpAtTime > 0 ? TimeUtil.formatTime(wakedUpAtTime) : "----");


            int deepDuration = sleepDetailReport.getDeep_duration();
            this.mTvSleepDeepDurationHour.setText(TimeUtil.calculateHour(deepDuration));
            this.mTvSleepDeepDurationMin.setText(TimeUtil.calculateMin(deepDuration));

            int lightDuration = sleepDetailReport.getLight_duration();
            this.mTvSleepLightDurationHour.setText(TimeUtil.calculateHour(lightDuration));
            this.mTvSleepLightDurationMin.setText(TimeUtil.calculateMin(lightDuration));
            this.mSleepNoteView.addSleepNoteData(sleepDetailReport);
        });

    }

    @Override
    public void onSyncDaySleepDetailReportFailed(String error) {
        runOnUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void onBack(View v) {
        finish();
    }
}
