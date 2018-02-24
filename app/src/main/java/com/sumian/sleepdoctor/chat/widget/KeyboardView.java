package com.sumian.sleepdoctor.chat.widget;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.text.emoji.widget.EmojiAppCompatEditText;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumian.common.helper.ToastHelper;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.chat.AudioRecorder;
import com.sumian.sleepdoctor.chat.utils.FilePathUtil;
import com.sumian.sleepdoctor.chat.utils.UiUtil;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by jzz
 * on 2018/1/5.
 * desc:
 */

public class KeyboardView extends LinearLayout implements View.OnClickListener, View.OnKeyListener,
        View.OnFocusChangeListener, EasyPermissions.PermissionCallbacks, View.OnTouchListener {

    private static final String TAG = KeyboardView.class.getSimpleName();

    private static final int RECORD_PERM = 0x01;

    @BindView(R.id.et_input)
    EmojiAppCompatEditText mEtInput;

    @BindView(R.id.bt_question)
    ImageView mBtAsk;
    @BindView(R.id.iv_voice)
    ImageView mIvVoice;
    @BindView(R.id.iv_image)
    ImageView mIvImage;
    @BindView(R.id.bt_send)
    Button mBtSend;

    @BindView(R.id.tv_voice_container)
    RelativeLayout mTvVoiceContainer;

    @BindView(R.id.tv_voice_label)
    TextView mTvVoiceLabel;

    @BindView(R.id.iv_voice_anim)
    ImageView mLayVoice;

    @BindView(R.id.iv_voice_input)
    ImageView mIvVoiceInput;

    @BindView(R.id.iv_garbage)
    ImageView mIvGarbage;

    @BindView(R.id.tv_answer_label)
    TextView mTvAnswerLabel;

    private onKeyboardActionListener mOnKeyboardActionListener;

    private Animation mAnimation;
    private float mDownX;

    private AudioRecorder mAudioRecorder;

    private String mAudioFilePath;
    private CountDownTimer mCountDownTimer;

    private int mDuration;

    private boolean mIsDelete;
    private boolean mIsSend;

    private WeakReference<Activity> mActivityWeakReference;

    public KeyboardView(Context context) {
        this(context, null);
    }

    public KeyboardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        ButterKnife.bind(inflate(context, R.layout.lay_keybord_container, this));

        mEtInput.setOnFocusChangeListener(this);
        mEtInput.setOnKeyListener(this);
        mIvVoiceInput.setOnTouchListener(this);

        this.mAudioRecorder = AudioRecorder.init();
    }

    public KeyboardView setActivity(Activity activity) {
        this.mActivityWeakReference = new WeakReference<>(activity);
        return this;
    }

    public void setOnKeyboardActionListener(onKeyboardActionListener onKeyboardActionListener) {
        mOnKeyboardActionListener = onKeyboardActionListener;
    }

    public String getContent() {
        String content = mEtInput.getText().toString().trim();
        mEtInput.setText(null);
        return content;
    }

    public void showQuestionAction() {
        mBtAsk.setVisibility(VISIBLE);
    }

    public void hideQuestionAction() {
        mBtAsk.setVisibility(GONE);
    }

    public boolean isQuestion() {
        return mBtAsk.getTag() != null;
    }

    @OnClick({R.id.bt_question, R.id.iv_voice, R.id.iv_image, R.id.bt_send, R.id.iv_garbage, R.id.keyboardView})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_question:

                if (mBtAsk.getTag() == null) {
                    mBtAsk.setImageResource(R.mipmap.inputbox_icon_label_selected);
                    mBtAsk.setTag(true);
                } else {
                    mBtAsk.setImageResource(R.mipmap.inputbox_icon_label_unselected);
                    mBtAsk.setTag(null);
                }

                mEtInput.requestFocus();
                mTvVoiceContainer.setVisibility(GONE);
                mIvVoice.setTag(null);

                UiUtil.showSoftKeyboard(mEtInput);

                break;
            case R.id.et_input:
                mTvVoiceContainer.setVisibility(GONE);
                mIvVoice.setTag(null);
                UiUtil.showSoftKeyboard(mEtInput);
                break;
            case R.id.iv_voice:
                checkRecordPermission();
                break;
            case R.id.iv_image:

                mEtInput.clearFocus();

                UiUtil.closeKeyboard(mEtInput);

                mTvVoiceContainer.setVisibility(GONE);
                mIvVoice.setTag(null);

                if (mOnKeyboardActionListener != null) {
                    this.mOnKeyboardActionListener.sendPic();
                }
                break;
            case R.id.bt_send:

                mTvVoiceContainer.setVisibility(GONE);
                mIvVoice.setTag(null);

                String input = this.mEtInput.getText().toString().trim();

                if (TextUtils.isEmpty(input)) {
                    return;
                }

                mEtInput.setText("");

                if (mOnKeyboardActionListener != null) {
                    this.mOnKeyboardActionListener.sendText(input);
                }

                break;
            case R.id.iv_garbage:
                break;
            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // boolean active = inputMethodManager.isActive(v);
        // inputMethodManager.isWatchingCursor()
        mTvVoiceContainer.setVisibility(GONE);
        //  Log.e(TAG, "onFocusChange: --------->" + hasFocus + "   " + active);
    }

    public void setAnswerLabel(String replyText) {
        mTvAnswerLabel.setText(replyText);
        mTvAnswerLabel.setVisibility(VISIBLE);
        mTvAnswerLabel.setTag(true);
    }

    public void clearReplayMsgLabel() {
        if (mOnKeyboardActionListener != null) {
            mOnKeyboardActionListener.clearReplyMsg();
        }

        mTvAnswerLabel.setText(null);
        mTvAnswerLabel.setVisibility(GONE);
    }

    public EditText getInputView() {
        return mEtInput;
    }

    public void onRequestPermissionsResultDelegate(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.e(TAG, "onPermissionsGranted: ----------->");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        ToastHelper.show("未授予录音权限,请正确授予录音权限");
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(mActivityWeakReference.get(), perms)) {
            new AppSettingsDialog.Builder(mActivityWeakReference.get()).build().show();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (TextUtils.isEmpty(mEtInput.getText().toString().trim())) {
                clearReplayMsgLabel();
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:

                mIsDelete = false;
                mIsSend = false;
                mDuration = 0;

                if (mAnimation == null) {
                    mAnimation = AnimationUtils.loadAnimation(v.getContext(), R.anim.record_scale_anim);
                }

                mLayVoice.startAnimation(mAnimation);

                mAudioFilePath = FilePathUtil.makeFilePath(v.getContext(), AudioRecorder.AUDIO_DIR_PATH, System.currentTimeMillis() + AudioRecorder.AUDIO_SUFFIX_WAV);

                mDownX = event.getX();

                mTvVoiceLabel.setText(R.string.turn_right_cancel_record);
                mTvVoiceLabel.setTextColor(getResources().getColor(R.color.t2_color));

                if (mAudioRecorder.getState() != AudioRecorder.State.RECORDING) {
                    this.mCountDownTimer = new CountDownTimer(60 * 1000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            int time = (int) (millisUntilFinished / 1000L);

                            Log.e(TAG, "onTick: --------->" + time);

                            mDuration = 60 - time;

                            mTvVoiceLabel.setText(String.format(Locale.getDefault(), "%d%s", time, "s后停止录音"));
                            if (time <= 10) {
                                mTvVoiceLabel.setTextColor(getResources().getColor(R.color.t2_color));
                            }

                            if (mAudioRecorder.getState() != AudioRecorder.State.RECORDING) {
                                mAudioRecorder.reset();
                                mAudioRecorder.setOutputFile(mAudioFilePath);
                                mAudioRecorder.prepare();
                                mAudioRecorder.start();
                            }

                        }

                        @Override
                        public void onFinish() {
                            mLayVoice.clearAnimation();
                            if (mAudioRecorder.getState() != AudioRecorder.State.STOPPED) {
                                mAudioRecorder.finishRecord();
                            }

                            mIsSend = true;

                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();

                            retriever.setDataSource(mAudioFilePath);
                            String durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            double duration = (double) Long.parseLong(durationString) / 1000.0D;

                            if (duration < 1) {
                                ToastHelper.show(getContext(), "你说话太快了,我似乎没有听清楚", Gravity.CENTER);
                                mAudioRecorder.deleteMixRecorderFile(mAudioFilePath);
                                return;
                            }

                            if (mOnKeyboardActionListener != null) {
                                mOnKeyboardActionListener.sendVoice(mAudioFilePath, (int) duration);
                            }
                        }
                    }.start();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                if (moveX - mDownX > 40) {//向右滑动
                    mTvVoiceLabel.setText(R.string.up_cancel_record);
                    mTvVoiceLabel.setTextColor(getResources().getColor(R.color.t4_color));
                    mIvGarbage.setImageResource(R.mipmap.inputbox_btn_delete_pre);
                    mIsDelete = true;
                } else {
                    mIsDelete = false;
                    mTvVoiceLabel.setText(String.format(Locale.getDefault(), "%d%s", (60 - mDuration), "s后停止录音"));
                    mTvVoiceLabel.setTextColor(getResources().getColor(R.color.t2_color));
                    mIvGarbage.setImageResource(R.mipmap.inputbox_btn_delete_default);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mLayVoice.clearAnimation();
                mTvVoiceLabel.setText(R.string.start_record);
                mTvVoiceLabel.setTextColor(getResources().getColor(R.color.t2_color));
                mIvGarbage.setImageResource(R.mipmap.inputbox_btn_delete_default);

                mCountDownTimer.cancel();
                mAudioRecorder.finishRecord();
                if (mIsDelete) {
                    mAudioRecorder.deleteListRecord();
                } else {
                    if (!mIsSend)
                        mCountDownTimer.onFinish();
                    mCountDownTimer = null;
                }

                break;
            default:
                break;
        }
        return true;
    }

    private void prepareSendVoice() {
        mEtInput.clearFocus();
        UiUtil.closeKeyboard(mEtInput);
        if (mIvVoice.getTag() == null) {//show
            mTvVoiceContainer.setVisibility(VISIBLE);
            mIvVoice.setTag(true);
        } else {//hide
            mTvVoiceContainer.setVisibility(GONE);
            mIvVoice.setTag(null);
        }
    }

    @AfterPermissionGranted(RECORD_PERM)
    private void checkRecordPermission() {
        Activity activity = this.mActivityWeakReference.get();
        String[] perms = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(activity, perms)) {
            prepareSendVoice();
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(activity, activity.getResources().getString(R.string.str_request_record_message), RECORD_PERM, perms);
        }
    }

    public interface onKeyboardActionListener {

        void sendText(String content);

        void sendPic();

        void sendVoice(String path, int duration);

        void clearReplyMsg();
    }
}
