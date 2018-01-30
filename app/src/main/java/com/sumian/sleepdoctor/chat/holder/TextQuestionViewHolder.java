package com.sumian.sleepdoctor.chat.holder;

import android.view.View;

import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;

/**
 * Created by sm
 * on 2018/1/30.
 * desc:
 */

public class TextQuestionViewHolder extends BaseViewHolder<AVIMTextMessage> {

    private boolean mIsLeft;

    public TextQuestionViewHolder(View itemView, boolean isLeft) {
        super(itemView);
        this.mIsLeft = isLeft;
    }
}
