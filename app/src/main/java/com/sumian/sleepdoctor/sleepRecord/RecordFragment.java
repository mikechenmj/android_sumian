package com.sumian.sleepdoctor.sleepRecord;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.sumian.common.utils.SettingsUtil;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.ActivityLauncher;
import com.sumian.sleepdoctor.constants.SpKeys;
import com.sumian.sleepdoctor.h5.H5Uri;
import com.sumian.sleepdoctor.h5.SimpleWebActivity;
import com.sumian.sleepdoctor.improve.doctor.activity.DoctorServiceWebActivity;
import com.sumian.sleepdoctor.improve.doctor.base.BasePagerFragment;
import com.sumian.sleepdoctor.improve.doctor.bean.DoctorService;
import com.sumian.sleepdoctor.improve.widget.DoctorServiceItemView;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.ErrorResponse;
import com.sumian.sleepdoctor.notification.NotificationListActivity;
import com.sumian.sleepdoctor.notification.NotificationViewModel;
import com.sumian.sleepdoctor.scale.ScaleListActivity;
import com.sumian.sleepdoctor.sleepRecord.bean.DoctorServiceList;
import com.sumian.sleepdoctor.sleepRecord.bean.SleepRecord;
import com.sumian.sleepdoctor.sleepRecord.bean.SleepRecordSummary;
import com.sumian.sleepdoctor.sleepRecord.view.SleepRecordView;
import com.sumian.sleepdoctor.sleepRecord.view.calendar.calendarView.CalendarView;
import com.sumian.sleepdoctor.sleepRecord.view.calendar.custom.SleepCalendarViewWrapper;
import com.sumian.sleepdoctor.utils.NotificationUtil;
import com.sumian.sleepdoctor.utils.TimeUtil;
import com.sumian.sleepdoctor.widget.dialog.ActionLoadingDialog;
import com.sumian.sleepdoctor.widget.dialog.SumianAlertDialog;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class RecordFragment extends BasePagerFragment implements CalendarView.OnDateClickListener, ActivityLauncher {
    public static final int DATE_ARROW_CLICK_COLD_TIME = 300;
    public static final int REQUEST_CODE_FILL_SLEEP_RECORD = 1;
    public static final int REQUEST_CODE_OPEN_NOTIFICATION = 2;
    private static final String KEY_SLEEP_RECORD_TIME = "key_sleep_record_time";
    private static final String KEY_SCROLL_TO_BOTTOM = "key_scroll_to_bottom";
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
    @BindView(R.id.scroll_view)
    ScrollView mScrollView;
    @BindView(R.id.iv_notification)
    ImageView mIvNotification;
    @BindView(R.id.dsiv_sleep_scale)
    DoctorServiceItemView mSleepScale;

    private long mPopupDismissTime;
    private SleepCalendarViewWrapper mCalendarViewWrapper;
    private long mSelectedTime = System.currentTimeMillis();
    private ActionLoadingDialog mActionLoadingDialog;
    private boolean mNeedScrollToBottom;
    private long mInitTime;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_record;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mSleepRecordView.setOnClickRefillSleepRecordListener(v -> launchFillSleepRecordActivity(mSelectedTime));
        mSleepRecordView.setOnClickFillSleepRecordBtnListener(v -> launchFillSleepRecordActivity(mSelectedTime));
        showOpenNotificationDialogIfNeeded();
    }

    private void setTvDate(long timeInMillis) {
        mTvDate.setText(TimeUtil.formatDate("yyyy/MM/dd", timeInMillis));
    }

    @SuppressWarnings("unused")
    private void showOpenNotificationDialogIfNeeded() {
        long previousShowTime = SPUtils.getInstance().getLong(SpKeys.SLEEP_RECORD_PREVIOUS_SHOW_NOTIFICATION_TIME, 0);
        boolean alreadyShowed = previousShowTime > 0;
        if (NotificationUtil.Companion.areNotificationsEnabled(getActivity()) || alreadyShowed) {
            return;
        }
        SumianAlertDialog.create()
                .setCloseIconVisible(true)
                .setTopIconResource(R.mipmap.ic_notification_alert)
                .setTitle(R.string.open_notification)
                .setMessage(R.string.open_notification_and_receive_doctor_response)
                .setRightBtn(R.string.open_notification, v -> SettingsUtil.launchSettingActivityForResult(this, REQUEST_CODE_OPEN_NOTIFICATION))
                .show(getFragmentManager());
        SPUtils.getInstance().put(SpKeys.SLEEP_RECORD_PREVIOUS_SHOW_NOTIFICATION_TIME, System.currentTimeMillis());
    }

    public static RecordFragment newInstance(long scrollToTime, boolean needScrollToBottom) {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_SLEEP_RECORD_TIME, scrollToTime);
        bundle.putBoolean(KEY_SCROLL_TO_BOTTOM, needScrollToBottom);
        RecordFragment recordFragment = new RecordFragment();
        recordFragment.setArguments(bundle);
        return recordFragment;
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
        AppManager.getHttpService().getServiceList().enqueue(new BaseResponseCallback<DoctorServiceList>() {
            @Override
            protected void onSuccess(DoctorServiceList response) {
                showServiceList(response);
            }

            @Override
            protected void onFailure(@NonNull ErrorResponse errorResponse) {

            }
        });
    }

    private void showServiceList(DoctorServiceList response) {
        List<DoctorService> serviceList = response.getServiceList();
        boolean hasSleepReportService = false;
        mServiceContainer.removeAllViewsInLayout();
        for (DoctorService doctorService : serviceList) {
            hasSleepReportService = doctorService.getLast_count() > 0;
            if (doctorService.getType() == DoctorService.SERVICE_TYPE_SLEEP_REPORT && !hasSleepReportService) {
                DoctorServiceItemView doctorServiceItemView = new DoctorServiceItemView(getContext());
                doctorServiceItemView.setTitle(doctorService.getName());
                doctorServiceItemView.setDesc(doctorService.getNot_buy_description());
                doctorServiceItemView.loadImage(doctorService.getIcon());
                doctorServiceItemView.setOnClickListener(v -> launchDoctorServicePage(doctorService));
                mServiceContainer.addView(doctorServiceItemView);
            }
        }
        mSleepRecordView.setForceShowDoctorAdvice(hasSleepReportService);
    }

    @Override
    protected void initData() {
        super.initData();
        changeSelectTime(mInitTime);
        queryServices();
        ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(NotificationViewModel.class)
                .getUnreadCount()
                .observe(this, unreadCount -> mIvNotification.setActivated(unreadCount != null && unreadCount > 0));
    }

    private void launchFillSleepRecordActivity(long time) {
        FillSleepRecordActivity.launchForResult(this, time, REQUEST_CODE_FILL_SLEEP_RECORD);
    }

    @Override
    public void onResume() {
        super.onResume();
        queryServices();
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
                    protected void onFailure(@NonNull ErrorResponse errorResponse) {

                    }
                });
    }

    @OnClick({
            R.id.tv_date,
            R.id.iv_date_arrow,
            R.id.iv_notification,
            R.id.dsiv_sleep_scale,
            R.id.iv_weekly_report,
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_date:
            case R.id.iv_date_arrow:
                showDatePopup(!mIvDateArrow.isActivated());
                break;
            case R.id.iv_notification:
                NotificationListActivity.launch(getActivity());
                break;
            case R.id.iv_weekly_report:
                int selectTimeInSecond = (int) (mSelectedTime / 1000);
                String urlContentPart = H5Uri.SLEEP_RECORD_WEEKLY_REPORT.replace("{date}", String.valueOf(selectTimeInSecond));
                String title = getString(R.string.record_weekly_report);
                SimpleWebActivity.launch(getActivity(), title, urlContentPart);
                break;
            case R.id.dsiv_sleep_scale:
                ScaleListActivity.launch(getContext(), ScaleListActivity.TYPE_ALL);
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
        AppManager.getHttpService().getSleepDiaryDetail((int) (time / 1000L))
                .enqueue(new BaseResponseCallback<SleepRecord>() {
                    @Override
                    protected void onSuccess(SleepRecord response) {
                        updateSleepRecordView(response);
                    }

                    @Override
                    protected void onFailure(@NonNull ErrorResponse errorResponse) {
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

    private void launchDoctorServicePage(DoctorService doctorService) {
        DoctorServiceWebActivity.show(getContext(), doctorService, true);
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

    @Override
    public void selectTab(int position) {
        queryServices();
    }
}
