package com.sumian.sddoctor.patient.sleepdiary;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sumian.common.base.BaseFragment;
import com.sumian.common.network.response.ErrorResponse;
import com.sumian.common.utils.TimeUtilV2;
import com.sumian.sddoctor.R;
import com.sumian.sddoctor.app.AppManager;
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback;
import com.sumian.sddoctor.patient.sleepdiary.bean.SleepRecord;
import com.sumian.sddoctor.patient.sleepdiary.bean.SleepRecordSummary;
import com.sumian.sddoctor.patient.sleepdiary.widget.SleepRecordView;
import com.sumian.sddoctor.service.report.widget.calendar.calendarView.CalendarView;
import com.sumian.sddoctor.service.report.widget.calendar.custom.CalendarPopup;
import com.sumian.sddoctor.util.TimeUtil;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;

@SuppressWarnings("ALL")
public class SleepRecordFragment extends BaseFragment implements CalendarView.OnDateClickListener, View.OnClickListener {

    public static final int DATE_ARROW_CLICK_COLD_TIME = 300;
    public static final int REQUEST_CODE_FILL_SLEEP_RECORD = 1;
    public static final int PAGE_SIZE = 12;

    private static final String KEY_USER_ID = "key_user_id";
    private static final String KEY_SLEEP_RECORD_TIME = "key_sleep_record_time";
    private static final String KEY_SCROLL_TO_BOTTOM = "key_scroll_to_bottom";

    View mToolbar;
    ImageView mIvDateArrow;
    TextView mTvDate;
    SleepRecordView mSleepRecordView;
    ScrollView mScrollView;
    private long mSelectedTime = System.currentTimeMillis();
    private boolean mNeedScrollToBottom;
    private long mInitTime;
    private CalendarPopup mCalendarPopup;
    private int mUserId;

    public static SleepRecordFragment newInstance(int userId, long scrollToTime) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_USER_ID, userId);
        bundle.putLong(KEY_SLEEP_RECORD_TIME, scrollToTime);
        bundle.putBoolean(KEY_SCROLL_TO_BOTTOM, false);
        SleepRecordFragment sleepRecordFragment = new SleepRecordFragment();
        sleepRecordFragment.setArguments(bundle);
        return sleepRecordFragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_sleep_record;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mToolbar = findView(R.id.rl_toolbar);
        mIvDateArrow = findView(R.id.iv_date_arrow);
        mIvDateArrow.setOnClickListener(this);
        mTvDate = findView(R.id.tv_date);
        mTvDate.setOnClickListener(this);
        mSleepRecordView = findView(R.id.sleep_record);
        mScrollView = findView(R.id.scroll_view);
    }

    private boolean isRefillable() {
        return TimeUtilV2.Companion.getDayDistance(System.currentTimeMillis(), mSelectedTime) < 3;
    }

    private void setTvDate(long timeInMillis) {
        mTvDate.setText(TimeUtil.formatDate("yyyy.MM.dd", timeInMillis));
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        mUserId = bundle.getInt(KEY_USER_ID, 0);
        long defaultTime = TimeUtil.getDayStartTime(System.currentTimeMillis());
        if (bundle == null) {
            mInitTime = defaultTime;
        } else {
            mNeedScrollToBottom = bundle.getBoolean(KEY_SCROLL_TO_BOTTOM, false);
            mInitTime = bundle.getLong(KEY_SLEEP_RECORD_TIME);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        changeSelectTime(mInitTime);
    }

    private void showDatePopup(boolean show) {
        mIvDateArrow.setActivated(show);
        if (show) {
            mCalendarPopup = new CalendarPopup(getActivity(), this::querySleepReportSummaryList);
            mCalendarPopup.setOnDateClickListener(this);
            mCalendarPopup.setOnDismissListener(() -> mIvDateArrow.setActivated(false));
            mCalendarPopup.setSelectDayTime(mSelectedTime);
            mCalendarPopup.showAsDropDown(mToolbar, 0, (int) getResources().getDimension(R.dimen.space_10));
        }
    }

    private void querySleepReportSummaryList(long monthTime, int monthCount, boolean isInit) {
        int userId = getArguments().getInt(KEY_USER_ID);
        int unixTime = (int) (monthTime / 1000);
        Call<Map<String, List<SleepRecordSummary>>> call = AppManager.getHttpService()
                .getSleepDiarySummaryList(userId, unixTime, 1, monthCount, 0);
        addCall(call);
        call.enqueue(new BaseSdResponseCallback<Map<String, List<SleepRecordSummary>>>() {
            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
            }

            @Override
            protected void onSuccess(Map<String, List<SleepRecordSummary>> response) {
                if (response == null) {
                    return;
                }
                Set<Long> hasDataDays = new HashSet<>();
                for (Map.Entry<String, List<SleepRecordSummary>> entry : response.entrySet()) {
                    for (SleepRecordSummary summary : entry.getValue()) {
                        long summaryDate = summary.getDateInMillis();
                        hasDataDays.add(summaryDate);
                    }
                }
                mCalendarPopup.addMonthAndData(monthTime, hasDataDays, isInit);
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_date:
            case R.id.iv_date_arrow:
                showDatePopup(!mIvDateArrow.isActivated());
                break;
            default:
                break;
        }
    }

    @Override
    public void onDateClick(long time) {
        if (time > TimeUtil.getStartTimeOfTheDay(System.currentTimeMillis())) {
            return;
        }
        changeSelectTime(time);
    }

    private void changeSelectTime(long time) {
        mSelectedTime = time;
        setTvDate(time);
        queryAndShowSleepReportAtTime(time);
    }

    private void queryAndShowSleepReportAtTime(long time) {
        mSleepRecordView.setTime(time);
        Call<SleepRecord> call = AppManager.getHttpService().getUserDiary(mUserId, (int) (time / 1000L));
        addCall(call);
        call.enqueue(new BaseSdResponseCallback<SleepRecord>() {
            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                updateSleepRecordView(null);
            }

            @Override
            protected void onSuccess(SleepRecord response) {
                updateSleepRecordView(response);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
            }
        });
    }

    private void updateSleepRecordView(SleepRecord response) {
        mSleepRecordView.setSleepRecord(response);
        if (mNeedScrollToBottom) {
            mScrollView.post(() -> mScrollView.fullScroll(View.FOCUS_DOWN));
            mNeedScrollToBottom = false;
        }
    }
}
