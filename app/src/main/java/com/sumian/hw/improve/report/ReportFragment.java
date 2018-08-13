package com.sumian.hw.improve.report;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.sumian.hw.base.HwBasePagerFragment;
import com.sumian.hw.improve.guideline.dialog.ReportGuidelineDialog;
import com.sumian.hw.improve.guideline.utils.GuidelineUtils;
import com.sumian.hw.improve.main.bean.PushReport;
import com.sumian.hw.improve.report.calendar.CalendarDialog;
import com.sumian.hw.improve.report.dailyreport.DailyReportFragment;
import com.sumian.hw.improve.report.weeklyreport.WeeklyReportFragment;
import com.sumian.hw.improve.widget.TabIndicatorView;
import com.sumian.hw.log.LogManager;
import com.sumian.hw.push.ReportPushManager;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;

/**
 * Created by sm
 * on 2018/3/6.
 * desc:
 */

public class ReportFragment extends HwBasePagerFragment implements TabIndicatorView.OnSwitchIndicatorCallback {

    TabIndicatorView mTabIndicatorView;

    private HwBasePagerFragment[] mBaseFragments;
    private int mCurrentPosition = 0;

    private BroadcastReceiver mBroadcastReceiver;
    private ReportGuidelineDialog mReportGuidelineDialog;

    public static ReportFragment newInstance() {
        return new ReportFragment();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnterTab() {
        LogManager.appendUserOperationLog("点击进入 '报告' 页面");
        // check job scheduler
        AppManager.getJobScheduler().checkJobScheduler();
        // check guideline
        if (GuidelineUtils.needShowDailyUserGuide()) {
            mReportGuidelineDialog = new ReportGuidelineDialog(getContext());
            mReportGuidelineDialog.show();
        }
        // check push report
        boolean showPushReport = showPushReportInNeeded();
        if (!showPushReport && mCurrentPosition != -1 && mTabIndicatorView != null) {
            mTabIndicatorView.selectTabByPosition(mCurrentPosition);
        }
    }

    /**
     * 如果有push report，则切换到对应的tab
     *
     * @return 是否有push report存在
     */
    private boolean showPushReportInNeeded() {
        PushReport pushReport = ReportPushManager.getInstance().getPushReport();
        if (pushReport != null) {
            if (pushReport.getPushType() == PushReport.PUSH_TYPE_DAILY_REPORT) {
                mTabIndicatorView.selectTabByPosition(0);
            } else {
                mTabIndicatorView.selectTabByPosition(1);
            }
            return true;
        }
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_fragment_main_report;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mBaseFragments = new HwBasePagerFragment[]{DailyReportFragment.newInstance(), WeeklyReportFragment.newInstance()};
        mTabIndicatorView = root.findViewById(R.id.tab_indicator_view);
        mTabIndicatorView.setOnSwitchIndicatorCallback(this);
        mTabIndicatorView.selectTabByPosition(0);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void initData() {
        super.initData();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CalendarDialog.ACTION_SELECT_DATE);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case CalendarDialog.ACTION_SELECT_DATE:
                        mTabIndicatorView.updateTabsUiBySelectPosition(0);
                        onSwitchIndicator(null, 0);
                        break;
                    default:
                        break;
                }
            }
        }, filter);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (mReportGuidelineDialog != null && mReportGuidelineDialog.isShowing()) {
                mReportGuidelineDialog.dismiss();
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onRelease() {
        super.onRelease();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mBroadcastReceiver);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onSwitchIndicator(View v, int position) {
//        if (mCurrentPosition == position) return;
        LogManager.appendPhoneLog("com.sumian.app.improve.report.ReportFragment.onSwitchIndicator position: " + position);
        HwBasePagerFragment pagerFragment;
        String tag;
        HwBasePagerFragment fragmentByTag;
        for (int i = 0, len = mBaseFragments.length; i < len; i++) {
            pagerFragment = mBaseFragments[i];
            tag = pagerFragment.getClass().getSimpleName();
            if (position == i) {
                fragmentByTag = (HwBasePagerFragment) getFragmentManager().findFragmentByTag(tag);
                if (fragmentByTag != null && fragmentByTag.isAdded()) {
                    getFragmentManager().beginTransaction().show(fragmentByTag).runOnCommit(fragmentByTag::onEnterTab).commit();
                } else {
                    getFragmentManager().beginTransaction().add(R.id.report_container, pagerFragment, tag).runOnCommit(pagerFragment::onEnterTab).commit();
                }
            } else {
                fragmentByTag = (HwBasePagerFragment) getFragmentManager().findFragmentByTag(tag);
                if (fragmentByTag != null) {
                    getFragmentManager().beginTransaction().hide(fragmentByTag).commit();
                }
            }
        }
        mCurrentPosition = position;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onShowCalendar(View v) {
        CalendarDialog.create(getCurrentShowReportTime()).show(getFragmentManager(), CalendarDialog.class.getSimpleName());
    }

    private long getCurrentShowReportTime() {
        DailyReportFragment dailyReportFragment = (DailyReportFragment) mBaseFragments[0];
        if (dailyReportFragment == null) {
            return 0;
        }
        return dailyReportFragment.getCurrentReportTime();
    }
}
