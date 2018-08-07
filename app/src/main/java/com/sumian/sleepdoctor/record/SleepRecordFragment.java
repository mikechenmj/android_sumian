package com.sumian.sleepdoctor.record;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.SdBaseFragment;
import com.sumian.sleepdoctor.doctor.bean.DoctorService;
import com.sumian.sleepdoctor.h5.H5Uri;
import com.sumian.sleepdoctor.h5.SimpleWebActivity;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.record.bean.SleepRecord;
import com.sumian.sleepdoctor.record.bean.SleepRecordSummary;
import com.sumian.sleepdoctor.record.widget.SleepRecordView;
import com.sumian.sleepdoctor.utils.TimeUtil;
import com.sumian.sleepdoctor.widget.dialog.ActionLoadingDialog;
import com.sumian.sleepdoctor.record.calendar.calendarView.CalendarView;
import com.sumian.sleepdoctor.record.calendar.custom.SleepCalendarViewWrapper;

import java.util.List;
import java.util.Map;

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
    private PopupWindow mPopupWindow;
    private long mPopupDismissTime;
    private SleepCalendarViewWrapper mCalendarViewWrapper;
    private long mSelectedTime = System.currentTimeMillis();
    private ActionLoadingDialog mActionLoadingDialog;
    private boolean mNeedScrollToBottom;
    private long mInitTime;

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
        mSleepRecordView.setOnClickRefillSleepRecordListener(v -> launchFillSleepRecordActivity(mSelectedTime));
        mSleepRecordView.setOnClickFillSleepRecordBtnListener(v -> launchFillSleepRecordActivity(mSelectedTime));
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

    private void queryServices() {
        Call<DoctorService> call = AppManager.getHttpService().getServiceByType(DoctorService.SERVICE_TYPE_ADVISORY);
        addCall(call);
        call.enqueue(new BaseResponseCallback<DoctorService>() {
            @Override
            protected void onSuccess(DoctorService response) {
                boolean hasSleepReportService = response != null && response.getLast_count() > 0;
                if (mSleepRecordView != null) {
                    mSleepRecordView.setForceShowDoctorAdvice(hasSleepReportService);
                }
            }

            @Override
            protected void onFailure(int code, @NonNull String message) {

            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        changeSelectTime(mInitTime);
        queryServices();
    }

    private void launchFillSleepRecordActivity(long time) {
        FillSleepRecordActivity.launchForResult(this, time, REQUEST_CODE_FILL_SLEEP_RECORD);
    }

    private void showDatePopup(boolean show) {
        long currentTimeMillis = System.currentTimeMillis();
        long timeGap = currentTimeMillis - mPopupDismissTime;
        if (timeGap < DATE_ARROW_CLICK_COLD_TIME) {
            return;
        }
        mIvDateArrow.setActivated(show);
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mCalendarViewWrapper = new SleepCalendarViewWrapper(getContext());
            mCalendarViewWrapper.setOnDateClickListener(this);
            mCalendarViewWrapper.setTodayTime(currentTimeMillis);
            mCalendarViewWrapper.setOnBgClickListener(v -> mPopupWindow.dismiss());
            mCalendarViewWrapper.setLoadMoreListener(time -> querySleepReportSummaryList(time, false));
            mPopupWindow.setContentView(mCalendarViewWrapper);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(null);
            mPopupWindow.setAnimationStyle(0);
            mPopupWindow.setOnDismissListener(() -> {
                mPopupDismissTime = System.currentTimeMillis();
                mIvDateArrow.setActivated(false);
            });
        }
        if (show) {
            mPopupWindow.showAsDropDown(mToolbar, 0, (int) getResources().getDimension(R.dimen.space_10));
            querySleepReportSummaryList(System.currentTimeMillis(), true);
        } else {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    private void querySleepReportSummaryList(long time, boolean isInit) {
        List<Long> monthTimes = TimeUtil.createMonthTimes(time, PAGE_SIZE, isInit);
        if (isInit) {
            mCalendarViewWrapper.setMonthTimes(monthTimes);
            mCalendarViewWrapper.setSelectDayTime(mSelectedTime);
        } else {
            mCalendarViewWrapper.addMonthTimes(monthTimes);
        }
        Call<Map<String, List<SleepRecordSummary>>> call = AppManager.getHttpService().getSleepDiarySummaryList((int) (time / 1000), 1, PAGE_SIZE, 0);
        addCall(call);
        call
                .enqueue(new BaseResponseCallback<Map<String, List<SleepRecordSummary>>>() {
                    @Override
                    protected void onSuccess(Map<String, List<SleepRecordSummary>> response) {
                        mCalendarViewWrapper.addSleepRecordSummaries(response);
                    }

                    @Override
                    protected void onFailure(int code, @NonNull String message) {

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
        mPopupWindow.dismiss();
        changeSelectTime(time);
    }

    private void changeSelectTime(long time) {
        mSelectedTime = time;
        setTvDate(time);
        queryAndShowSleepReportAtTime(time);
    }

    private void queryAndShowSleepReportAtTime(long time) {
        mSleepRecordView.setTime(time);
        mActionLoadingDialog = new ActionLoadingDialog();
        mActionLoadingDialog.show(getFragmentManager());
        Call<SleepRecord> call = AppManager.getHttpService().getSleepDiaryDetail((int) (time / 1000L));
        addCall(call);
        call
                .enqueue(new BaseResponseCallback<SleepRecord>() {
                    @Override
                    protected void onSuccess(SleepRecord response) {
                        updateSleepRecordView(response);
                    }

                    @Override
                    protected void onFailure(int code, @NonNull String message) {
                        updateSleepRecordView(null);
                    }

                    @Override
                    protected void onFinish() {
                        super.onFinish();
                        if (mActionLoadingDialog != null) {
                            mActionLoadingDialog.dismiss();
                        }
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
