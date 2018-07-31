package com.sumian.app.improve.report;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.sumian.app.R;
import com.sumian.app.app.HwAppManager;
import com.sumian.app.base.BasePagerFragment;
import com.sumian.app.improve.guideline.dialog.ReportGuidelineDialog;
import com.sumian.app.improve.guideline.utils.GuidelineUtils;
import com.sumian.app.improve.main.bean.PushReport;
import com.sumian.app.push.ReportPushManager;
import com.sumian.app.improve.report.calendar.CalendarDialog;
import com.sumian.app.improve.report.dailyreport.DailyReportFragment;
import com.sumian.app.improve.report.weeklyreport.WeeklyReportFragment;
import com.sumian.app.improve.widget.TabIndicatorView;
import com.sumian.app.log.LogManager;

/**
 * Created by sm
 * on 2018/3/6.
 * desc:
 */

public class ReportFragment extends BasePagerFragment implements TabIndicatorView.OnSwitchIndicatorCallback {

    TabIndicatorView mTabIndicatorView;

    private BasePagerFragment[] mBaseFragments;
    private int mCurrentPosition = 0;

    private BroadcastReceiver mBroadcastReceiver;

    public static ReportFragment newInstance() {
        return new ReportFragment();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnterTab() {
        LogManager.appendUserOperationLog("点击进入 '报告' 页面");
        // check job scheduler
        HwAppManager.getJobScheduler().checkJobScheduler();
        // check guideline
        if (GuidelineUtils.needShowDailyUserGuide()) {
            new ReportGuidelineDialog(getContext()).show();
        }
        // check push report
        boolean showPushReport = showPushReportInNeeded();
        if (!showPushReport && mCurrentPosition != -1) {
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
        mTabIndicatorView = root.findViewById(R.id.tab_indicator_view);
        mTabIndicatorView.setOnSwitchIndicatorCallback(this);
        mBaseFragments = new BasePagerFragment[]{DailyReportFragment.newInstance(), WeeklyReportFragment.newInstance()};
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
        BasePagerFragment pagerFragment;
        String tag;
        BasePagerFragment fragmentByTag;
        for (int i = 0, len = mBaseFragments.length; i < len; i++) {
            pagerFragment = mBaseFragments[i];
            tag = pagerFragment.getClass().getSimpleName();
            if (position == i) {
                fragmentByTag = (BasePagerFragment) getFragmentManager().findFragmentByTag(tag);
                if (fragmentByTag != null && fragmentByTag.isAdded()) {
                    getFragmentManager().beginTransaction().show(fragmentByTag).runOnCommit(fragmentByTag::onEnterTab).commit();
                } else {
                    getFragmentManager().beginTransaction().add(R.id.report_container, pagerFragment, tag).runOnCommit(pagerFragment::onEnterTab).commit();
                }
            } else {
                fragmentByTag = (BasePagerFragment) getFragmentManager().findFragmentByTag(tag);
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
