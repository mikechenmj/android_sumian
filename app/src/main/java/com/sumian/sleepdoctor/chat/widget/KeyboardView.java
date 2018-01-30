package com.sumian.sleepdoctor.chat.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.text.emoji.widget.EmojiAppCompatEditText;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
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

    @BindView(R.id.bt_ask)
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
    @BindView(R.id.iv_voice_input)
    ImageView mIvVoiceInput;
    @BindView(R.id.iv_garbage)
    ImageView mIvGarbage;

    @BindView(R.id.tv_answer_label)
    TextView mTvAnswerLabel;

    private onKeyboardActionListener mOnKeyboardActionListener;

    private InputMethodManager inputMethodManager;

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

    }

    public KeyboardView setOnKeyboardActionListener(onKeyboardActionListener onKeyboardActionListener) {
        mOnKeyboardActionListener = onKeyboardActionListener;
        return this;
    }

    public EditText getEtInputView() {
        return mEtInput;
    }

    public String getContent() {
        String content = mEtInput.getText().toString().trim();
        mEtInput.setText(null);
        return content;
    }

    public boolean isQuestion() {
        return mBtAsk.getTag() != null;
    }

    @OnClick({R.id.bt_ask, R.id.et_input, R.id.iv_voice, R.id.iv_image, R.id.bt_send, R.id.iv_garbage, R.id.keyboardView})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_ask:

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

        Log.e(TAG, "onFocusChange: --------->" + hasFocus + "   " + active);

    }

    public interface onKeyboardActionListener {

        void sendText(String content);

        void sendPic();

        void sendVoice(String path,int duration);

    }
}
