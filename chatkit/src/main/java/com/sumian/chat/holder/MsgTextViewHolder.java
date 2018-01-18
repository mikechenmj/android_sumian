package com.sumian.chat.holder;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
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
public class MsgTextViewHolder extends BaseViewHolder<AVIMMessage> implements View.OnClickListener {

    @BindView(R.id.tv_time_line)
    TextView mTvTimeLine;

    @BindView(R.id.iv_icon)
    CircleImageView mIvIcon;
    @BindView(R.id.tv_content)
    TextView mTvContent;

    @BindView(R.id.iv_msg_failed)
    ImageView mIvMsgFailed;
    @BindView(R.id.loading)
    ProgressBar mLoading;

    public MsgTextViewHolder(ViewGroup parent, boolean isLeft) {
        super(parent, isLeft ? R.layout.lay_item_left_text_chat : R.layout.lay_item_right_text_chat);
    }

    @Override
    public void initView(boolean isLeft, int serviceType, AVIMMessage msg) {
        super.mIsLeft = isLeft;
        super.mCacheMsg = msg;
        mIvMsgFailed.setOnClickListener(this);
        showTime(mTvTimeLine, msg);
        formatServiceType(mIvIcon, serviceType);
        showText(mTvContent, msg);
        showSendState(mIvMsgFailed, mLoading, msg);
    }

    @OnClick({R.id.iv_msg_failed})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_msg_failed://发送失败,再次发送
                if (mCacheMsg == null) return;
                String tempContent;
                if (mCacheMsg instanceof AVIMTextMessage) {
                    tempContent = ((AVIMTextMessage) mCacheMsg).getText();
                } else {
                    tempContent = mCacheMsg.getContent();
                }

                if (TextUtils.isEmpty(tempContent)) return;
                LeanCloudHelper.sendTextMsg(LeanCloudHelper.SERVICE_TYPE_ONLINE_CUSTOMER, tempContent);
                break;
            default:
                break;
        }
    }
}
