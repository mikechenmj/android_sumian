package com.sumian.sleepdoctor.sleepRecord;

import android.app.Activity;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseFragment;
import com.sumian.sleepdoctor.improve.widget.DoctorServiceItemView;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.ErrorResponse;
import com.sumian.sleepdoctor.sleepRecord.bean.SleepRecord;
import com.sumian.sleepdoctor.sleepRecord.bean.SleepRecordSummary;
import com.sumian.sleepdoctor.sleepRecord.view.SleepRecordView;
import com.sumian.sleepdoctor.utils.TimeUtil;
import com.sumian.sleepdoctor.widget.calendar.calendarView.CalendarViewAdapter;
import com.sumian.sleepdoctor.widget.calendar.calendarView.CalendarViewData;
import com.sumian.sleepdoctor.widget.calendar.calendarView.DayType;
import com.sumian.sleepdoctor.widget.calendar.calendarViewWrapper.CalendarViewWrapper;
import com.sumian.sleepdoctor.widget.dialog.ActionLoadingDialog;
import com.sumian.sleepdoctor.widget.dialog.SumianAlertDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class RecordFragment extends BaseFragment implements CalendarViewAdapter.OnDateClickListener {
    public static final int DATE_ARROW_CLICK_COLD_TIME = 300;
    public static final int REQUEST_CODE_FILL_SLEEP_RECORD = 1;

    @BindView(R.id.dsiv)
    DoctorServiceItemView mDoctorServiceItemView;
    @BindView(R.id.rl_toolbar)
    View mToolbar;
    @BindView(R.id.iv_date_arrow)
    ImageView mIvDateArrow;
    private PopupWindow mPopupWindow;
    @BindView(R.id.tv_date)
    TextView mTvDate;
    @BindView(R.id.sleep_record)
    SleepRecordView mSleepRecordView;

    private long mPopupDismissTime;
    private CalendarViewWrapper mCalendarViewWrapper;
    private long mSelectedTime = System.currentTimeMillis();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_record;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        initDoctorServiceItemView();
        setTvDate(System.currentTimeMillis());
        mSleepRecordView.setOnClickRefillSleepRecordListener(v -> {
            launchFillSleepRecordActivity(mSelectedTime);
        });
        mSleepRecordView.setOnClickFillSleepRecordBtnListener(v -> {
            launchFillSleepRecordActivity(mSelectedTime);
        });
    }

    private void initDoctorServiceItemView() {
        mDoctorServiceItemView.setTitle("远程睡眠管理服务");
        mDoctorServiceItemView.setDesc("连续7天监测你的睡眠日记");
        mDoctorServiceItemView.setPrice(50f);
        mDoctorServiceItemView.loadImage(R.mipmap.ic_doctor_service_item_view_sleep_diary);
        mDoctorServiceItemView.setOnClickListener(v -> {
        });
    }

    private void setTvDate(long timeInMillis) {
        mTvDate.setText(TimeUtil.formatDate("yyyy/MM/dd", timeInMillis));
    }

    @SuppressWarnings("unused")
    private void showOpenNotificationDialog() {
        SumianAlertDialog.create()
                .setCloseIconVisible(true)
                .setTopIconResource(R.mipmap.ic_notification_alert)
                .setTitle(R.string.open_notification)
                .setMessage(R.string.open_notification_and_receive_doctor_response)
                .setRightBtn(R.string.open_notification, v -> openNotification())
                .show(getFragmentManager());
    }

    private void openNotification() {

    }

    @Override
    protected void initData() {
        super.initData();
        queryAndShowSleepReportAtTime(System.currentTimeMillis());
    }

    @OnClick({R.id.tv_date, R.id.iv_date_arrow, R.id.iv_notification})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_date:
            case R.id.iv_date_arrow:
                turnDateArrow(!mIvDateArrow.isActivated());
                break;
            case R.id.iv_notification:

                break;
        }
    }

    private void launchFillSleepRecordActivity(long time) {
        FillSleepRecordActivity.launchForResult(this, time, REQUEST_CODE_FILL_SLEEP_RECORD);
    }

    private void turnDateArrow(boolean activated) {
        if (System.currentTimeMillis() - mPopupDismissTime < DATE_ARROW_CLICK_COLD_TIME) {
            return;
        }
        mIvDateArrow.setActivated(activated);
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mCalendarViewWrapper = new CalendarViewWrapper(getContext());
            mCalendarViewWrapper.setOnDateClickListener(this);
            mCalendarViewWrapper.setData(getCalendarViewDataList());
            mCalendarViewWrapper.scrollToTime(System.currentTimeMillis() - DateUtils.DAY_IN_MILLIS * 60, false);
            mCalendarViewWrapper.setOnBgClickListener(v -> {
                mPopupWindow.dismiss();
            });
            mPopupWindow.setContentView(mCalendarViewWrapper);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(null);
            mPopupWindow.setAnimationStyle(0);
            mPopupWindow.setOnDismissListener(() -> {
                mPopupDismissTime = System.currentTimeMillis();
                mIvDateArrow.setActivated(false);
            });
        }
        if (activated) {
            mPopupWindow.showAsDropDown(mToolbar);
            querySleepReportSummaryList();
        } else {
            mPopupWindow.dismiss();
        }
    }

    private void querySleepReportSummaryList() {
        AppManager.getHttpService().querySleepDiarySummaryList((int) (System.currentTimeMillis() / 1000), 1, 10, 0)
                .enqueue(new BaseResponseCallback<Map<String, List<SleepRecordSummary>>>() {
                    @Override
                    protected void onSuccess(Map<String, List<SleepRecordSummary>> response) {
                        LogUtils.d(response);
                        sleepDataResponseToCalendarViewData(response);
                    }

                    @Override
                    protected void onFailure(ErrorResponse errorResponse) {

                    }

                });

    }

    private List<CalendarViewData> sleepDataResponseToCalendarViewData(Map<String, List<SleepRecordSummary>> response) {
        if (response == null) {
            return null;
        }
        List<CalendarViewData> list = new ArrayList<>();
        for (Map.Entry<String, List<SleepRecordSummary>> entry : response.entrySet()) {
            String key = entry.getKey();
            long monthTime = Integer.valueOf(key);
            CalendarViewData calendarViewData = new CalendarViewData();
            calendarViewData.monthTime = monthTime;

            LongSparseArray<DayType> map = new LongSparseArray<>();
//            for (SleepRecordSummary sleepData : entry.getValue()) {
//                long dateInMillis = sleepData.getDateInMillis();
//                getDayType(sleepData);
//            }
//            map.put(monthTime + DateUtils.DAY_IN_MILLIS * 10, DayType.EMPHASIZE_2);
            calendarViewData.dayDayTypeMap = map;
            list.add(calendarViewData);
        }
        return list;
    }

    @Override
    public void onDateClick(long time) {
        mPopupWindow.dismiss();
        mSelectedTime = time;
        setTvDate(time);
        queryAndShowSleepReportAtTime(time);
    }

    private void queryAndShowSleepReportAtTime(long time) {
        mSleepRecordView.setTime(time);
        AppManager.getHttpService().querySleepDiaryDetail((int) (time / 1000L))
                .enqueue(new BaseResponseCallback<SleepRecord>() {
                    @Override
                    protected void onSuccess(SleepRecord response) {
                        mSleepRecordView.setSleepRecord(response);
                    }

                    @Override
                    protected void onFailure(ErrorResponse errorResponse) {
                        mSleepRecordView.setSleepRecord(null);
                    }
                });
    }

    private List<CalendarViewData> getCalendarViewDataList() {
        List<CalendarViewData> list = new ArrayList<>();
        long l = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            CalendarViewData calendarViewData = new CalendarViewData();
            long monthTime = l - DateUtils.DAY_IN_MILLIS * 31 * i;
            calendarViewData.monthTime = monthTime;
            LongSparseArray<DayType> map = new LongSparseArray<>();
//            map.put(monthTime + DateUtils.DAY_IN_MILLIS * 10, DayType.EMPHASIZE_2);
            calendarViewData.dayDayTypeMap = map;
            list.add(calendarViewData);
        }
        return list;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FILL_SLEEP_RECORD) {
            if (resultCode == Activity.RESULT_OK) {
                SleepRecord sleepRecord = FillSleepRecordActivity.resolveResultData(data);
                mSleepRecordView.setSleepRecord(sleepRecord);
                assert sleepRecord != null;
                ToastUtils.showShort(sleepRecord.toString());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
