package com.sumian.sleepdoctor.sleepRecord;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseFragment;
import com.sumian.sleepdoctor.improve.doctor.activity.DoctorServiceWebActivity;
import com.sumian.sleepdoctor.improve.doctor.bean.DoctorService;
import com.sumian.sleepdoctor.improve.widget.DoctorServiceItemView;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.ErrorResponse;
import com.sumian.sleepdoctor.sleepRecord.bean.DoctorServiceList;
import com.sumian.sleepdoctor.sleepRecord.bean.SleepRecord;
import com.sumian.sleepdoctor.sleepRecord.bean.SleepRecordSummary;
import com.sumian.sleepdoctor.sleepRecord.view.SleepRecordView;
import com.sumian.sleepdoctor.sleepRecord.view.calendar.calendarView.CalendarView;
import com.sumian.sleepdoctor.sleepRecord.view.calendar.custom.SleepCalendarViewWrapper;
import com.sumian.sleepdoctor.utils.TimeUtil;
import com.sumian.sleepdoctor.widget.dialog.ActionLoadingDialog;
import com.sumian.sleepdoctor.widget.dialog.SumianAlertDialog;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class RecordFragment extends BaseFragment implements CalendarView.OnDateClickListener {
    public static final int DATE_ARROW_CLICK_COLD_TIME = 300;
    public static final int REQUEST_CODE_FILL_SLEEP_RECORD = 1;
    public static final int PAGE_SIZE = 12;

    @BindView(R.id.rl_toolbar)
    View mToolbar;
    @BindView(R.id.iv_date_arrow)
    ImageView mIvDateArrow;
    private PopupWindow mPopupWindow;
    @BindView(R.id.tv_date)
    TextView mTvDate;
    @BindView(R.id.sleep_record)
    SleepRecordView mSleepRecordView;
    @BindView(R.id.ll_service_container)
    LinearLayout mServiceContainer;

    private long mPopupDismissTime;
    private SleepCalendarViewWrapper mCalendarViewWrapper;
    private long mSelectedTime = System.currentTimeMillis();
    private ActionLoadingDialog mActionLoadingDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_record;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        setTvDate(System.currentTimeMillis());
        mSleepRecordView.setOnClickRefillSleepRecordListener(v -> launchFillSleepRecordActivity(mSelectedTime));
        mSleepRecordView.setOnClickFillSleepRecordBtnListener(v -> launchFillSleepRecordActivity(mSelectedTime));
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
        queryServices();
    }

    private void queryServices() {
        AppManager.getHttpService().getServiceList().enqueue(new BaseResponseCallback<DoctorServiceList>() {
            @Override
            protected void onSuccess(DoctorServiceList response) {
                List<DoctorService> serviceList = response.getServiceList();
                for (DoctorService doctorService : serviceList) {
                    if (doctorService.getLast_count() == 0) {
                        DoctorServiceItemView doctorServiceItemView = new DoctorServiceItemView(getContext());
                        doctorServiceItemView.setTitle(doctorService.getName());
                        doctorServiceItemView.setDesc(doctorService.getNot_buy_description());
                        doctorServiceItemView.loadImage(doctorService.getIcon());
                        doctorServiceItemView.setOnClickListener(v -> launchDoctorServicePage(doctorService));
                        mServiceContainer.addView(doctorServiceItemView);
                    }
                }
            }

            @Override
            protected void onFailure(ErrorResponse errorResponse) {

            }
        });
//        AppManager.getHttpService().getBindDoctorInfo()
//                .enqueue(new BaseResponseCallback<Doctor>() {
//                    @Override
//                    protected void onSuccess(Doctor response) {
//
//                    }
//
//                    @Override
//                    protected void onFailure(ErrorResponse errorResponse) {
//
//                    }
//                });
//        AppManager.getHttpService().getNotificationList(1, 15)
//                .enqueue(new BaseResponseCallback<Object>() {
//                    @Override
//                    protected void onSuccess(Object response) {
//
//                    }
//
//                    @Override
//                    protected void onFailure(ErrorResponse errorResponse) {
//
//                    }
//                });
    }

    @OnClick({R.id.tv_date, R.id.iv_date_arrow, R.id.iv_notification})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_date:
            case R.id.iv_date_arrow:
                showDatePopup(!mIvDateArrow.isActivated());
                break;
            case R.id.iv_notification:

                break;
        }
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
            mPopupWindow.showAsDropDown(mToolbar);
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
        AppManager.getHttpService().getSleepDiarySummaryList((int) (time / 1000), 1, PAGE_SIZE, 0)
                .enqueue(new BaseResponseCallback<Map<String, List<SleepRecordSummary>>>() {
                    @Override
                    protected void onSuccess(Map<String, List<SleepRecordSummary>> response) {
                        mCalendarViewWrapper.addSleepRecordSummaries(response);
                    }

                    @Override
                    protected void onFailure(ErrorResponse errorResponse) {

                    }
                });
    }

    @Override
    public void onDateClick(long time) {
        if (time > TimeUtil.getStartTimeOfTheDay(System.currentTimeMillis())) {
            return;
        }
        mPopupWindow.dismiss();
        mSelectedTime = time;
        setTvDate(time);
        queryAndShowSleepReportAtTime(time);
    }

    private void queryAndShowSleepReportAtTime(long time) {
        mSleepRecordView.setTime(time);
        mActionLoadingDialog = new ActionLoadingDialog();
        mActionLoadingDialog.show(getFragmentManager());
        AppManager.getHttpService().getSleepDiaryDetail((int) (time / 1000L))
                .enqueue(new BaseResponseCallback<SleepRecord>() {
                    @Override
                    protected void onSuccess(SleepRecord response) {
                        mSleepRecordView.setSleepRecord(response);
                    }

                    @Override
                    protected void onFailure(ErrorResponse errorResponse) {
                        mSleepRecordView.setSleepRecord(null);
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

    private void launchDoctorServicePage(DoctorService doctorService) {
        DoctorServiceWebActivity.show(getContext(), doctorService);
    }
}
