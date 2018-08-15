package com.sumian.hw.improve.report.note;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.common.helper.ToastHelper;
import com.sumian.hw.improve.report.dailyreport.DailyReport;
import com.sumian.hw.improve.widget.note.WakeUpMoodView;
import com.sumian.hw.tab.report.contract.SleepNoteContract;
import com.sumian.hw.tab.report.presenter.SleepNotePresenter;
import com.sumian.hw.widget.BaseDialogFragment;
import com.sumian.hw.widget.FlowLayout;
import com.sumian.hw.widget.adapter.OnTextWatcherAdapter;
import com.sumian.sd.R;
import com.sumian.sd.app.App;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by jzz
 * on 2017/11/27.
 * <p>
 * desc:
 */

public class NoteDialog extends BaseDialogFragment implements View.OnClickListener, SleepNoteContract.View, WakeUpMoodView.OnSelectCallback {

    LinearLayout mLaySleepState;
    WakeUpMoodView mWakeUpMoodView;
    FlowLayout mLayFlow;
    EditText mEtSleepRemark;
    TextView mTvInputCount;
    Button mBtSubmit;

    private SleepNoteContract.Presenter mPresenter;

    private List<String> mBedTimeStates = new ArrayList<>(0);

    private OnWriteNoteCallback mOnWriteNoteCallback;

    private SleepNote mSleepNote;
    private int mMoodState = -1;

    public static NoteDialog newInstance(SleepNote sleepNote) {
        NoteDialog noteDialog = new NoteDialog();
        Bundle args = new Bundle();
        args.putSerializable("sleepNote", sleepNote);
        noteDialog.setArguments(args);
        return noteDialog;
    }

    public void setOnWriteNoteCallback(OnWriteNoteCallback onWriteNoteCallback) {
        mOnWriteNoteCallback = onWriteNoteCallback;
    }

    @Override
    protected void initBundle(Bundle arguments) {
        super.initBundle(arguments);
        if (arguments != null) {
            this.mSleepNote = (SleepNote) arguments.getSerializable("sleepNote");
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.hw_lay_dialog_sleep_note;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mLaySleepState = rootView.findViewById(R.id.lay_sleep_state);
        mWakeUpMoodView = rootView.findViewById(R.id.wake_up_mood_view);
        mLayFlow = rootView.findViewById(R.id.lay_flow);
        mEtSleepRemark = rootView.findViewById(R.id.et_sleep_remark);
        mTvInputCount = rootView.findViewById(R.id.tv_input_count);
        mBtSubmit = rootView.findViewById(R.id.bt_submit);
        rootView.findViewById(R.id.tv_ignore).setOnClickListener(this);
        rootView.findViewById(R.id.bt_submit).setOnClickListener(this);

        mWakeUpMoodView.setOnSelectCallback(this);
        mEtSleepRemark.addTextChangedListener(new OnTextWatcherAdapter() {

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                @ColorRes int ColorId = !TextUtils.isEmpty(s) && s.length() <= 200 ? R.color.translucent_general_color : R.color.warn_color;
                mTvInputCount.setTextColor(getResources().getColor(ColorId));
                mTvInputCount.setVisibility(TextUtils.isEmpty(s) || s.length() <= 0 ? View.GONE : View.VISIBLE);
                if (!TextUtils.isEmpty(s)) {
                    mTvInputCount.setText(s.length() + "/" + "200");
                }
            }
        });
        SleepNotePresenter.init(this);
    }

    @Override
    protected void initData() {
        super.initData();
        if (mSleepNote != null) {
            mMoodState = mSleepNote.wakeUpMood;
            mWakeUpMoodView.setWakeUpMood(mMoodState);
            String remark = mSleepNote.remark;
            mEtSleepRemark.setText(remark);
            mTvInputCount.setVisibility(TextUtils.isEmpty(remark) || remark.length() <= 0 ? View.GONE : View.VISIBLE);
            if (!TextUtils.isEmpty(remark)) {
                mTvInputCount.setText(String.format(Locale.getDefault(), "%d/200", remark.length()));
            }

            @ColorRes int ColorId = !TextUtils.isEmpty(remark) && remark.length() <= 200 ? R.color.translucent_general_color : R.color.warn_color;
            mTvInputCount.setTextColor(getResources().getColor(ColorId));

        }
        mPresenter.syncSleepNoteOptions();
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_ignore) {
            dismiss();
        } else if (i == R.id.bt_submit) {
            String remark = mEtSleepRemark.getText().toString().trim();
            if (mMoodState == -1 && mBedTimeStates.size() <= 0 && TextUtils.isEmpty(remark)) {
                ToastHelper.show(R.string.note_not_null);
                return;
            }
            if (remark.length() > 200) {
                ToastHelper.show(R.string.sleep_note_error_three);
                return;
            }
            mPresenter.uploadDiary(mSleepNote.sleepId, mMoodState, mBedTimeStates, remark);
        }
    }


    @Override
    public void setPresenter(SleepNoteContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onFailure(String error) {
        ToastHelper.show(error);
    }

    @Override
    public void onSyncSleepNoteOptionsSuccess(List<String> bedtimeState) {
        for (int i = 0, len = bedtimeState.size(); i < len; i++) {
            String option = bedtimeState.get(i);
            View view = LayoutInflater.from(App.Companion.getAppContext()).inflate(R.layout.hw_lay_options, mLayFlow, false);
            TextView tvOption = view.findViewById(R.id.tv_option);
            tvOption.setTag(i);
            tvOption.setOnClickListener(v -> {
                int position = (int) v.getTag();
                View child = mLayFlow.getChildAt(position);
                TextView textView = child.findViewById(R.id.tv_option);
                String text;
                if (textView.isActivated()) {
                    textView.setActivated(false);
                    text = textView.getText().toString().trim();
                    if (TextUtils.isEmpty(text)) {
                        return;
                    }
                    mBedTimeStates.remove(text);
                } else {
                    textView.setActivated(true);
                    text = textView.getText().toString().trim();
                    if (TextUtils.isEmpty(text)) {
                        return;
                    }
                    mBedTimeStates.add(text);
                }
            });

            if (isSelectOption(option)) {
                tvOption.setActivated(true);
                mBedTimeStates.add(option);
            } else {
                tvOption.setActivated(false);
                mBedTimeStates.remove(option);
            }
            tvOption.setText(option);
            mLayFlow.addView(view);
        }
    }

    private boolean isSelectOption(String option) {
        if (mSleepNote != null) {
            if (mSleepNote.bedtimeState != null) {
                for (String bedTimeState : mSleepNote.bedtimeState) {
                    if (option.equals(bedTimeState)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onSyncSleepNoteOptionsFailed(String error) {
        ToastHelper.show(error);
    }

    @Override
    public void onUploadDiarySuccess(DailyReport dailyReport) {
        if (mOnWriteNoteCallback != null) {
            mOnWriteNoteCallback.onWrite(dailyReport);
        }
        dismiss();
        ToastHelper.show(R.string.upload_sleep_note);
    }

    @Override
    public void onUploadDiaryFailed(String error) {
        ToastHelper.show(error);
    }

    @Override
    public void selectWakeUpMood(int moodState) {
        this.mMoodState = moodState;
    }

    public interface OnWriteNoteCallback {

        void onWrite(DailyReport dailyReport);
    }
}
