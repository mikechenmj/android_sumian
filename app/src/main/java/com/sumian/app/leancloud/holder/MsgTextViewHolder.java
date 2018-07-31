package com.sumian.app.leancloud.holder;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.app.R;
import com.sumian.app.leancloud.LeanCloudHelper;
import com.sumian.app.leancloud.holder.base.BaseViewHolder;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2018/1/4.
 * desc:
 */

@SuppressWarnings("WeakerAccess")
public class MsgTextViewHolder extends BaseViewHolder<AVIMTextMessage> implements View.OnClickListener {

    private static final String TAG = MsgTextViewHolder.class.getSimpleName();

    TextView mTvTimeLine;
    CircleImageView mIvIcon;
    TextView mTvContent;
    ImageView mIvMsgFailed;
    ProgressBar mLoading;

    public MsgTextViewHolder(ViewGroup parent, boolean isLeft) {
        super(parent, isLeft ? R.layout.hw_lay_item_left_text_chat : R.layout.hw_lay_item_right_text_chat);
        this.mIsLeft = isLeft;

        mTvTimeLine = itemView.findViewById(R.id.tv_time_line);
        mIvIcon = itemView.findViewById(R.id.iv_icon);
        mTvContent = itemView.findViewById(R.id.tv_content);
        mIvMsgFailed = itemView.findViewById(R.id.iv_msg_failed);
        mLoading = itemView.findViewById(R.id.loading);
        itemView.findViewById(R.id.iv_msg_failed).setOnClickListener(this);
    }

    @Override
    public void initView(int serviceType, AVIMTextMessage msg) {
        this.mCacheMsg = msg;
        mIvMsgFailed.setOnClickListener(this);
        showTime(mTvTimeLine, msg);
        formatServiceType(mIvIcon, serviceType);
        showText(mTvContent, msg);
        showSendState(mIvMsgFailed, mLoading, msg);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (mCacheMsg == null) {
            return;
        }
        String tempContent = mCacheMsg.getText();
        if (TextUtils.isEmpty(tempContent)) {
            return;
        }
        LeanCloudHelper.sendTextMsg(LeanCloudHelper.SERVICE_TYPE_ONLINE_CUSTOMER, tempContent);
    }
}
