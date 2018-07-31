package com.sumian.hw.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sumian.sleepdoctor.R;
import com.sumian.hw.leancloud.utils.LCIMPathUtils;

/**
 * Created by jzz
 * on 2018/1/5.
 * desc:
 */

public class KeyboardView extends LinearLayout implements View.OnClickListener {

    ImageView mIvKeyboardOrVoice;
    LCIMRecordButton mBtRecordVoice;
    EditText mEtInput;
    ImageView mIvKeyboardImage;
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
        View inflate = LayoutInflater.from(context).inflate(R.layout.hw_lay_keybord_container, this, true);
        mIvKeyboardOrVoice = inflate.findViewById(R.id.iv_keyboard_or_voice);
        mBtRecordVoice = inflate.findViewById(R.id.bt_voice);
        mEtInput = inflate.findViewById(R.id.et_input);
        mIvKeyboardImage = inflate.findViewById(R.id.iv_keyboard_image);
        mIvKeyboardSend = inflate.findViewById(R.id.iv_keyboard_send);
        inflate.findViewById(R.id.iv_keyboard_or_voice).setOnClickListener(this);
        inflate.findViewById(R.id.iv_keyboard_image).setOnClickListener(this);
        inflate.findViewById(R.id.iv_keyboard_send).setOnClickListener(this);

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


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_keyboard_or_voice) {
            Object tag = this.mIvKeyboardOrVoice.getTag();
            mEtInput.setVisibility(tag == null ? GONE : VISIBLE);
            mIvKeyboardOrVoice.setImageResource(tag == null ? R.mipmap.ic_advisory_icon_keyboard : R.mipmap.ic_advisory_icon_voice);
            mBtRecordVoice.setVisibility(tag == null ? VISIBLE : GONE);
            if (mOnKeyboardActionListener != null) {
                mOnKeyboardActionListener.onSoftKeyboardCallback(tag == null);
            }
            mIvKeyboardOrVoice.setTag(tag == null ? true : null);
        } else if (i == R.id.bt_voice) {
            if (mOnKeyboardActionListener != null) {
                // mOnKeyboardActionListener.onRecordVoiceCallback();
            }
        } else if (i == R.id.iv_keyboard_image) {
            if (mOnKeyboardActionListener != null) {
                this.mOnKeyboardActionListener.sendPic();
            }
        } else if (i == R.id.iv_keyboard_send) {
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
        }
    }

    public interface onKeyboardActionListener {

        boolean sendText(String input);

        void sendPic();

        void onSoftKeyboardCallback(boolean closeAction);

    }
}
