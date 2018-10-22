package com.sumian.sd.diary.sleeprecord;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sumian.common.network.response.ErrorResponse;
import com.sumian.common.utils.TimeUtilV2;
import com.sumian.sd.R;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.base.SdBaseFragment;
import com.sumian.sd.diary.sleeprecord.bean.SleepRecord;
import com.sumian.sd.diary.sleeprecord.bean.SleepRecordSummary;
import com.sumian.sd.diary.sleeprecord.calendar.calendarView.CalendarView;
import com.sumian.sd.diary.sleeprecord.calendar.custom.CalendarPopup;
import com.sumian.sd.diary.sleeprecord.widget.SleepRecordView;
import com.sumian.sd.h5.H5Uri;
import com.sumian.sd.h5.SimpleWebActivity;
import com.sumian.sd.network.callback.BaseSdResponseCallback;
import com.sumian.sd.utils.TimeUtil;
import com.sumian.sd.widget.dialog.SumianAlertDialog;
import com.sumian.sd.widget.dialog.SumianAlertDialogV2;
import com.sumian.sd.widget.dialog.theme.LightTheme;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class SleepRecordFragment extends SdBaseFragment implements CalendarView.OnDateClickListener {

    public static final int DATE_ARROW_CLICK_COLD_TIME = 300;
    public static final int REQUEST_CODE_FILL_SLEEP_RECORD = 1;
    public static final int PAGE_SIZE = 12;

    private static final String KEY_SLEEP_RECORD_TIME = "key_sleep_record_time";
    private static final String KEY_SCROLL_TO_BOTTOM = "key_scroll_to_bottom";

    @BindView(R.id.rl_toolbar)
    View mToolbar;
    @BindView(R.id.iv_date_arrow)
    ImageView mIvDateArrow;
    @BindView(R.id.tv_date)
    TextView mTvDate;
    @BindView(R.id.sleep_record)
    SleepRecordView mSleepRecordView;
    @BindView(R.id.scroll_view)
    ScrollView mScrollView;
    private long mSelectedTime = System.currentTimeMillis();
    private boolean mNeedScrollToBottom;
    private long mInitTime;
    private CalendarPopup mCalendarPopup;

    public static SleepRecordFragment newInstance(long scrollToTime, boolean needScrollToBottom) {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_SLEEP_RECORD_TIME, scrollToTime);
        bundle.putBoolean(KEY_SCROLL_TO_BOTTOM, needScrollToBottom);
        SleepRecordFragment sleepRecordFragment = new SleepRecordFragment();
        sleepRecordFragment.setArguments(bundle);
        return sleepRecordFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sleep_record;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mSleepRecordView.setOnClickRefillSleepRecordListener(v -> {
            boolean refillable = TimeUtilV2.Companion.getDayDistance(System.currentTimeMillis(), mSelectedTime) < 3;
            if (refillable) {
                launchFillSleepRecordActivity(mSelectedTime);
            } else {
                showRefillNotEnableDialog();
            }
        });
        mSleepRecordView.setOnClickFillSleepRecordBtnListener(v -> launchFillSleepRecordActivity(mSelectedTime));
    }

    private void showRefillNotEnableDialog() {
        new SumianAlertDialogV2(getActivity())
                .setMessageText(R.string.only_last_3_days_can_refill_sleep_diary)
                .setTopIcon(R.mipmap.ic_msg_icon_abnormal)
                .setOnBtnClickListener(R.string.hao_de, null)
                .show();
    }

    private void setTvDate(long timeInMillis) {
        mTvDate.setText(TimeUtil.formatDate("yyyy.MM.dd", timeInMillis));
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
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

    private void launchFillSleepRecordActivity(long time) {
        FillSleepRecordActivity.launchForResult(this, time, REQUEST_CODE_FILL_SLEEP_RECORD);
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
        Call<Map<String, List<SleepRecordSummary>>> call = AppManager.getSdHttpService().getSleepDiarySummaryList((int) (monthTime / 1000), 1, monthCount, 0);
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

    @OnClick({
            R.id.tv_date,
            R.id.iv_date_arrow,
            R.id.iv_weekly_report,
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_date:
            case R.id.iv_date_arrow:
                showDatePopup(!mIvDateArrow.isActivated());
                break;
            case R.id.iv_weekly_report:
                int selectTimeInSecond = (int) (mSelectedTime / 1000);
                String urlContentPart = H5Uri.SLEEP_RECORD_WEEKLY_REPORT.replace("{date}", String.valueOf(selectTimeInSecond));
                SimpleWebActivity.launch(getActivity(), urlContentPart);
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
        Call<SleepRecord> call = AppManager.getSdHttpService().getSleepDiaryDetail((int) (time / 1000L));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FILL_SLEEP_RECORD) {
            if (resultCode == Activity.RESULT_OK) {
                SleepRecord sleepRecord = FillSleepRecordActivity.resolveResultData(data);
                updateSleepRecordView(sleepRecord);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
