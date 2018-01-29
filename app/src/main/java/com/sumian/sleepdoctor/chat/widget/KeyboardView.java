package com.sumian.sleepdoctor.chat.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2018/1/5.
 * desc:
 */

public class KeyboardView extends LinearLayout implements View.OnClickListener {

    @BindView(R.id.et_input)
    EditText mEtInput;

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
        ButterKnife.bind(inflate(context, R.layout.lay_keybord_container, this));
        // mBtRecordVoice.setSavePath(LCIMPathUtils.getRecordPathByCurrentTime(getContext()));

    }

    public KeyboardView setOnKeyboardActionListener(onKeyboardActionListener onKeyboardActionListener) {
        mOnKeyboardActionListener = onKeyboardActionListener;
        return this;
    }

    public EditText getEtInputView() {
        return mEtInput;
    }

    @OnClick({R.id.bt_ask, R.id.iv_voice, R.id.iv_image, R.id.bt_send, R.id.iv_garbage, R.id.keyboardView})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_ask:
                break;
            case R.id.iv_voice:
                if (mOnKeyboardActionListener != null) {
                    // mOnKeyboardActionListener.onRecordVoiceCallback();
                }
                break;
            case R.id.iv_image:
                if (mOnKeyboardActionListener != null) {
                    this.mOnKeyboardActionListener.sendPic();
                }
                break;
            case R.id.bt_send:

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
            case R.id.iv_garbage:
                break;
            default:
                break;
        }
    }

    public interface onKeyboardActionListener {

        boolean sendText(String input);

        void sendPic();

        void onSoftKeyboardCallback(boolean closeAction);

    }
}
