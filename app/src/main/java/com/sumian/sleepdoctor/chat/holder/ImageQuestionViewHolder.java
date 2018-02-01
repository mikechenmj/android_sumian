package com.sumian.sleepdoctor.chat.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;

/**
 * Created by sm
 * on 2018/1/30.
 * desc:
 */

public class ImageQuestionViewHolder extends BaseViewHolder<AVIMImageMessage> {

    private boolean mIsLeft;

    public ImageQuestionViewHolder(ViewGroup parent, boolean isLeft) {
        super(LayoutInflater.from(parent.getContext()).inflate(isLeft ? R.layout.lay_item_left_image_question_chat : R.layout.lay_item_left_image_question_chat, parent, false));
        this.mIsLeft = isLeft;
    }

    @Override
    public void initView(AVIMImageMessage avimImageMessage) {
        super.initView(avimImageMessage);
    }

    public void bindGroupId(int groupId) {

    }
}
