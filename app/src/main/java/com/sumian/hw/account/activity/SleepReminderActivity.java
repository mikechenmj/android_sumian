package com.sumian.hw.account.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.common.helper.ToastHelper;
import com.sumian.hw.account.contract.SleepReminderContract;
import com.sumian.hw.account.presenter.SleepReminderPresenter;
import com.sumian.hw.base.BaseActivity;
import com.sumian.hw.network.response.Reminder;
import com.sumian.hw.widget.TitleBar;
import com.sumian.hw.widget.ToggleButton;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;

import java.util.List;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * Created by jzz
 * on 2017/10/27.
 * <p>
 * desc:睡眠提醒模块
 */

public class SleepReminderActivity extends BaseActivity implements TitleBar.OnBackListener, SleepReminderContract.View, ToggleButton.OnToggleChanged, NumberPickerView.OnValueChangeListener {

    private static final String TAG = SleepReminderActivity.class.getSimpleName();

    TitleBar mTitleBar;
    ToggleButton mTbReminder;
    TextView mTvReminderNote;
    LinearLayout mLayTimerContainer;
    NumberPickerView mPickerOne;
    NumberPickerView mPickerTwo;

    private SleepReminderContract.Presenter mPresenter;
    private boolean mIsInit;

    public static void show(Context context) {
        context.startActivity(new Intent(context, SleepReminderActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_sleepy_reminder;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleBar = findViewById(R.id.title_bar);
        mTbReminder = findViewById(R.id.tb_reminder);
        mTvReminderNote = findViewById(R.id.tv_reminder_note);
        mLayTimerContainer = findViewById(R.id.lay_timer_container);
        mPickerOne = findViewById(R.id.picker_one);
        mPickerTwo = findViewById(R.id.picker_two);

        SleepReminderPresenter.init(this);
        this.mTitleBar.addOnBackListener(this);
        this.mTbReminder.setOnToggleChanged(this);
        this.mPickerOne.setOnValueChangedListener(this);
        this.mPickerTwo.setOnValueChangedListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mIsInit = true;
        mPresenter.syncReminder();
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    protected void onRelease() {
        mIsInit = false;
        super.onRelease();
    }

    @Override
    public void setPresenter(SleepReminderContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onFailure(String error) {
        runUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onSyncReminderSuccess(List<Reminder> reminders) {
        runUiThread(() -> {
            if (reminders == null || reminders.isEmpty()) {
                mTbReminder.setToggleOff();
                mLayTimerContainer.setVisibility(View.GONE);
                return;
            }
            Reminder reminder = reminders.get(0);
            updateReminder(reminder);
        });
    }

    @Override
    public void onSyncReminderFailed(String error) {
        runUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onModifyReminderSuccess(Reminder reminder) {
        runUiThread(() -> updateReminder(reminder));
    }

    @Override
    public void onModifyReminderFailed(String error) {
        runUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onAddReminderSuccess() {
        runUiThread(() -> ToastHelper.show(R.string.reminder_add_success_hint));
    }

    @Override
    public void onAddReminderFailed(String error) {
        runUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onToggle(boolean on) {
        AppManager.getOpenAnalytics().onClickEvent(this, "enable_sleep_reminder", on);
        mLayTimerContainer.setVisibility(on ? View.VISIBLE : View.GONE);
        modifyReminder(on);
    }

    @Override
    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
        modifyReminder(mTbReminder.isToggleOn());
    }

    private void updateReminder(Reminder reminder) {
        runUiThread(() -> {
            int enable = reminder.getEnable();
            if (enable == 1) {
                mTbReminder.setToggleOn();
            } else {
                mTbReminder.setToggleOff();
            }
            mLayTimerContainer.setVisibility(enable == 1 ? View.VISIBLE : View.GONE);
            if (!mIsInit) {
                ToastHelper.show(R.string.reminder_add_success_hint);
            }
            if (enable == 0) {
                return;
            }
            int hour = reminder.getReminderHour();
            mPickerOne.setMinValue(0);
            mPickerOne.setMaxValue(23);
            mPickerOne.setValue(hour);
            int min = reminder.getReminderMin();
            mPickerTwo.setMinValue(0);
            mPickerTwo.setMaxValue(59);
            mPickerTwo.setValue(min);
        });
    }

    private void modifyReminder(boolean isOn) {
        mIsInit = false;
        SleepReminderContract.Presenter presenter = this.mPresenter;
        presenter.modifyReminder(presenter.formatUnixTime(mPickerOne.getContentByCurrValue(), mPickerTwo.getContentByCurrValue()), isOn ? 1 : 0);
    }
}
