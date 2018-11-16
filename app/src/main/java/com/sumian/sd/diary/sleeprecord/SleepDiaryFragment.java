package com.sumian.sd.diary.sleeprecord;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.sumian.common.network.response.ErrorResponse;
import com.sumian.common.utils.TimeUtilV2;
import com.sumian.sd.R;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.base.SdBaseFragment;
import com.sumian.sd.diary.sleeprecord.bean.SleepRecord;
import com.sumian.sd.diary.sleeprecord.calendar.custom.CalendarPopup;
import com.sumian.sd.diary.sleeprecord.widget.SleepRecordView;
import com.sumian.sd.network.callback.BaseSdResponseCallback;
import com.sumian.sd.widget.dialog.SumianAlertDialogV2;
import com.sumian.sd.widget.refresh.SumianRefreshLayout;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;

@SuppressWarnings("ALL")
public class SleepDiaryFragment extends SdBaseFragment {

    private static final String KEY_SLEEP_RECORD_TIME = "key_sleep_record_time";
    public static final int REQUEST_CODE_FILL_SLEEP_RECORD = 1;

    View mToolbar;
    SleepRecordView mSleepRecordView;
    private long mSelectedTime = System.currentTimeMillis();
    private CalendarPopup mCalendarPopup;
    private long mInitTime = 0;
    private SumianRefreshLayout mRefreshLayout;

    public static SleepDiaryFragment newInstance(long time) {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_SLEEP_RECORD_TIME, time);
        SleepDiaryFragment sleepDiaryFragment = new SleepDiaryFragment();
        sleepDiaryFragment.setArguments(bundle);
        return sleepDiaryFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sleep_diary;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mToolbar = root.findViewById(R.id.rl_toolbar);
        mSleepRecordView = root.findViewById(R.id.sleep_record);
        mSleepRecordView.setOnClickRefillSleepRecordListener(v -> {
            if (isRefillable()) {
                launchFillSleepRecordActivity(mSelectedTime);
            } else {
                showRefillNotEnableDialog();
            }
        });
        mSleepRecordView.setOnClickFillSleepRecordBtnListener(v -> {
            if (isRefillable()) {
                launchFillSleepRecordActivity(mSelectedTime);
            }
        });
        mRefreshLayout = root.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryAndShowSleepReportAtTime(mSelectedTime);
            }
        });
    }

    private boolean isRefillable() {
        return TimeUtilV2.Companion.getDayDistance(System.currentTimeMillis(), mSelectedTime) < 3;
    }

    private void showRefillNotEnableDialog() {
        new SumianAlertDialogV2(getActivity())
                .setMessageText(R.string.only_last_3_days_can_refill_sleep_diary)
                .setTopIcon(R.mipmap.ic_msg_icon_abnormal)
                .setOnBtnClickListener(R.string.hao_de, null)
                .show();
    }

    public long getInitTime() {
        return getArguments().getLong(KEY_SLEEP_RECORD_TIME);
    }

    @Override
    protected void initData() {
        super.initData();
        changeSelectTime(getInitTime());
    }

    private void launchFillSleepRecordActivity(long time) {
        FillSleepRecordActivity.launchForResult(this, time, REQUEST_CODE_FILL_SLEEP_RECORD);
    }

    private void changeSelectTime(long time) {
        mSelectedTime = time;
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
                mRefreshLayout.hideRefreshAnim();
            }
        });
    }

    private void updateSleepRecordView(SleepRecord response) {
        mSleepRecordView.setSleepRecord(response);
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
