package com.sumian.hw.improve.report;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.sumian.hw.base.HwBaseFragment;
import com.sumian.hw.improve.guideline.dialog.ReportGuidelineDialog;
import com.sumian.hw.improve.guideline.utils.GuidelineUtils;
import com.sumian.hw.improve.main.bean.PushReport;
import com.sumian.hw.improve.report.calendar.CalendarDialog;
import com.sumian.hw.improve.report.dailyreport.DailyReportFragment;
import com.sumian.hw.improve.report.weeklyreport.WeeklyReportFragment;
import com.sumian.hw.improve.widget.TabIndicatorView;
import com.sumian.hw.log.LogManager;
import com.sumian.hw.push.ReportPushManager;
import com.sumian.hw.utils.FragmentUtil;
import com.sumian.sd.R;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.main.OnEnterListener;

/**
 * Created by sm
 * on 2018/3/6.
 * desc:
 */

public class ReportFragment extends HwBaseFragment implements TabIndicatorView.OnSwitchIndicatorCallback, OnEnterListener {

    TabIndicatorView mTabIndicatorView;

    private String[] mFTags = {DailyReportFragment.class.getSimpleName(), WeeklyReportFragment.class.getSimpleName()};
    private int mCurrentPosition = 0;

    private BroadcastReceiver mBroadcastReceiver;
    private ReportGuidelineDialog mReportGuidelineDialog;

    public static ReportFragment newInstance() {
        return new ReportFragment();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnter() {
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
        LogUtils.d(mTabIndicatorView);
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
        LogUtils.d();
        mTabIndicatorView = root.findViewById(R.id.tab_indicator_view);
        mTabIndicatorView.setOnSwitchIndicatorCallback(this);
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
        LogManager.appendPhoneLog("com.sumian.app.improve.report.ReportFragment.onSwitchIndicator position: " + position);
        showFragmentByPosition(position);
        mCurrentPosition = position;
    }

    private void showFragmentByPosition(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null) {
            return;
        }
        FragmentUtil.Companion.switchFragment(R.id.report_fragment_container, fragmentManager, mFTags, position,
                p -> {
                    if (p == 0) {
                        return DailyReportFragment.newInstance();
                    } else {
                        return WeeklyReportFragment.newInstance();
                    }
                }, new FragmentUtil.DefaultRunOnCommitCallbackImpl()
        );
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onShowCalendar(View v) {
        CalendarDialog.create(getCurrentShowReportTime()).show(getFragmentManager(), CalendarDialog.class.getSimpleName());
    }

    private long getCurrentShowReportTime() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null) {
            return 0;
        }
        DailyReportFragment dailyReportFragment = (DailyReportFragment) fragmentManager.findFragmentByTag(mFTags[0]);
        if (dailyReportFragment == null) {
            return 0;
        }
        return dailyReportFragment.getCurrentReportTime();
    }
}
