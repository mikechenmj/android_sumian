package com.sumian.sleepdoctor.chat.holder;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;

/**
 * Created by sm
 * on 2018/1/30.
 * desc:
 */

public class TextReplyViewHolder extends BaseViewHolder<AVIMTextMessage> {
    private boolean mIsLeft;

    public TextReplyViewHolder(ViewGroup parent, boolean isLeft) {
        super(LayoutInflater.from(parent.getContext()).inflate(isLeft ? R.layout.lay_keybord_container : 1, parent, false));
        this.mIsLeft = isLeft;
    }

}
