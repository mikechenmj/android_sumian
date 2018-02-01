package com.sumian.sleepdoctor.chat.holder;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;

/**
 * Created by sm
 * on 2018/1/30.
 * desc:
 */

public class VoiceQuestionViewHolder extends BaseViewHolder<AVIMAudioMessage> {
    private boolean mIsLeft;

    public VoiceQuestionViewHolder(ViewGroup parent, boolean isLeft) {
        super(LayoutInflater.from(parent.getContext()).inflate(isLeft ? R.layout.lay_item_left_voice_question_chat : R.layout.lay_item_right_voice_question_chat, parent, false));
        this.mIsLeft = isLeft;
    }

    @Override
    public void initView(AVIMAudioMessage avimAudioMessage) {
        super.initView(avimAudioMessage);
    }

    public void bindGroupId(int groupId) {

    }
}
