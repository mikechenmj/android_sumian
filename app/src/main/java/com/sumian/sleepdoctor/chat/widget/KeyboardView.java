package com.sumian.sleepdoctor.chat.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.text.emoji.widget.EmojiAppCompatEditText;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.chat.AudioRecorder;
import com.sumian.sleepdoctor.chat.utils.FilePathUtil;
import com.sumian.sleepdoctor.chat.utils.UiUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2018/1/5.
 * desc:
 */

public class KeyboardView extends LinearLayout implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String TAG = KeyboardView.class.getSimpleName();

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

    private InputMethodManager inputMethodManager;
    private Animation mAnimation;
    private float mDownX;

    private AudioRecorder mAudioRecorder;

    private String mAudioFilePath;
    private CountDownTimer mCountDownTimer;

    private int mDuration;

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
        // mBtRecordVoice.setSavePath(LCIMPathUtils.getRecordPathByCurrentTime(getContext()));
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        mEtInput.setOnFocusChangeListener(this);

        this.mAudioRecorder = AudioRecorder.init();

        mIvVoiceInput.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:

                    mAnimation = AnimationUtils.loadAnimation(v.getContext(), R.anim.record_scale_anim);
                    mLayVoice.startAnimation(mAnimation);

                    mAudioFilePath = FilePathUtil.makeFilePath(v.getContext(), AudioRecorder.AUDIO_DIR_PATH, System.currentTimeMillis() + AudioRecorder.AUDIO_SUFFIX_WAV);

                    mDownX = event.getX();

                    mTvVoiceLabel.setText("向右滑动,取消发送");
                    mTvVoiceLabel.setTextColor(getResources().getColor(R.color.t2_color));

                    if (mAudioRecorder.getState() != AudioRecorder.State.RECORDING) {
                        this.mCountDownTimer = new CountDownTimer(60 * 1000, 1000) {

                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTick(long millisUntilFinished) {
                                int time = (int) (millisUntilFinished / 1000L);

                                mDuration = 60 - time;

                                mTvVoiceLabel.setText(time + "s后停止录音");
                                if (time <= 10) {
                                    mTvVoiceLabel.setTextColor(getResources().getColor(R.color.t2_color));
                                }

                                if (mAudioRecorder.getState() != AudioRecorder.State.RECORDING) {
                                    mAudioRecorder.setOutputFile(mAudioFilePath);
                                    mAudioRecorder.prepare();
                                    mAudioRecorder.start();
                                }

                            }

                            @Override
                            public void onFinish() {
                                mDuration = 0;
                                mLayVoice.clearAnimation();
                                mAudioRecorder.finishRecord();
                                if (mOnKeyboardActionListener != null) {
                                    mOnKeyboardActionListener.sendVoice(mAudioFilePath, mDuration);
                                }
                            }
                        }.start();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:

                    float moveX = event.getX();
                    if (moveX - mDownX > 10) {//向右滑动
                        mTvVoiceLabel.setText("松开手指,取消发送");
                        mTvVoiceLabel.setTextColor(getResources().getColor(R.color.t4_color));
                        mIvGarbage.setImageResource(R.mipmap.inputbox_btn_delete_pre);

                        mAudioRecorder.deleteListRecord();
                        mAudioRecorder.finishRecord();

                        mCountDownTimer.cancel();
                        mDuration = 0;
                        mLayVoice.clearAnimation();
                    }

                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    mLayVoice.clearAnimation();
                    mTvVoiceLabel.setText("按住说话");
                    mTvVoiceLabel.setTextColor(getResources().getColor(R.color.t2_color));
                    mIvGarbage.setImageResource(R.mipmap.inputbox_btn_delete_default);

                    mCountDownTimer.onFinish();
                    mCountDownTimer.cancel();
                    mCountDownTimer = null;
                    break;
                default:
                    break;
            }
            return true;
        });
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

    @OnClick({R.id.bt_question, R.id.iv_voice, R.id.iv_image, R.id.bt_send,
            R.id.iv_garbage, R.id.keyboardView})
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

                boolean active = inputMethodManager.isActive();
                Log.e(TAG, "onClick: --------->" + active);
                mEtInput.requestFocus();
                mTvVoiceContainer.setVisibility(GONE);
                mIvVoice.setTag(null);

                UiUtil.showSoftKeyboard(mEtInput);

                break;
            case R.id.et_input:
                Log.e(TAG, "onClick: --------->");
                mTvVoiceContainer.setVisibility(GONE);
                mIvVoice.setTag(null);
                UiUtil.showSoftKeyboard(mEtInput);
                break;
            case R.id.iv_voice:

                mEtInput.clearFocus();
                UiUtil.closeKeyboard(mEtInput);

                if (mIvVoice.getTag() == null) {//show
                    mTvVoiceContainer.setVisibility(VISIBLE);
                    mIvVoice.setTag(true);
                } else {//hide
                    mTvVoiceContainer.setVisibility(GONE);
                    mIvVoice.setTag(null);
                }

                if (mOnKeyboardActionListener != null) {
                    mOnKeyboardActionListener.CheckRecordPermission();
                }

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

        boolean active = inputMethodManager.isActive(v);
        // inputMethodManager.isWatchingCursor()

        mTvVoiceContainer.setVisibility(GONE);

        Log.e(TAG, "onFocusChange: --------->" + hasFocus + "   " + active);

    }

    public void setAnswerLabel(String replyText) {
        mTvAnswerLabel.setText(replyText);
        mTvAnswerLabel.setVisibility(VISIBLE);
        mTvAnswerLabel.setTag(true);
    }

    public void clearReplayMsgLabel() {
        mTvAnswerLabel.setText(null);
        mTvAnswerLabel.setVisibility(GONE);
    }

    public interface onKeyboardActionListener {

        void sendText(String content);

        void sendPic();

        void sendVoice(String path, int duration);

        void CheckRecordPermission();

    }
}
