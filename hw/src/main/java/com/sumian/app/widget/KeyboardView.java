package com.sumian.app.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sumian.app.R;
import com.sumian.app.leancloud.utils.LCIMPathUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2018/1/5.
 * desc:
 */

public class KeyboardView extends LinearLayout implements View.OnClickListener {

    @BindView(R.id.iv_keyboard_or_voice)
    ImageView mIvKeyboardOrVoice;
    @BindView(R.id.bt_voice)
    LCIMRecordButton mBtRecordVoice;
    @BindView(R.id.et_input)
    EditText mEtInput;
    @BindView(R.id.iv_keyboard_image)
    ImageView mIvKeyboardImage;
    @BindView(R.id.iv_keyboard_send)
    ImageView mIvKeyboardSend;

    private onKeyboardActionListener mOnKeyboardActionListener;

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
        ButterKnife.bind(LayoutInflater.from(context).inflate(R.layout.hw_lay_keybord_container, this, true));
        mBtRecordVoice.setSavePath(LCIMPathUtils.getRecordPathByCurrentTime(getContext()));

    }

    public KeyboardView setOnKeyboardActionListener(onKeyboardActionListener onKeyboardActionListener) {
        mOnKeyboardActionListener = onKeyboardActionListener;
        return this;
    }

    public KeyboardView setRecordEventListener(LCIMRecordButton.RecordEventListener recordEventListener) {
        mBtRecordVoice.setRecordEventListener(recordEventListener);
        return this;
    }

    public KeyboardView setCheckRecordPermission(LCIMRecordButton.OnCheckRecordPermission checkRecordPermission) {
        mBtRecordVoice.setOnCheckRecordPermission(checkRecordPermission);
        return this;
    }

    public EditText getEtInputView() {
        return mEtInput;
    }

    @OnClick({R.id.iv_keyboard_or_voice, R.id.iv_keyboard_image, R.id.iv_keyboard_send})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_keyboard_or_voice:
                Object tag = this.mIvKeyboardOrVoice.getTag();
                mEtInput.setVisibility(tag == null ? GONE : VISIBLE);
                mIvKeyboardOrVoice.setImageResource(tag == null ? R.mipmap.ic_advisory_icon_keyboard : R.mipmap.ic_advisory_icon_voice);
                mBtRecordVoice.setVisibility(tag == null ? VISIBLE : GONE);
                if (mOnKeyboardActionListener != null) {
                    mOnKeyboardActionListener.onSoftKeyboardCallback(tag == null);
                }
                mIvKeyboardOrVoice.setTag(tag == null ? true : null);
                break;
            case R.id.bt_voice://录音
                if (mOnKeyboardActionListener != null) {
                    // mOnKeyboardActionListener.onRecordVoiceCallback();
                }
                break;
            case R.id.iv_keyboard_image:
                if (mOnKeyboardActionListener != null) {
                    this.mOnKeyboardActionListener.sendPic();
                }
                break;
            case R.id.iv_keyboard_send:
                if (mOnKeyboardActionListener != null) {

                    String input = this.mEtInput.getText().toString().trim();

                    if (TextUtils.isEmpty(input)) {
                        return;
                    }

                    boolean isSendText = this.mOnKeyboardActionListener.sendText(input);
                    if (isSendText) {
                        mEtInput.setText("");
                    }
                }
                break;
        }
    }

    public interface onKeyboardActionListener {

        boolean sendText(String input);

        void sendPic();

        void onSoftKeyboardCallback(boolean closeAction);

    }
}
