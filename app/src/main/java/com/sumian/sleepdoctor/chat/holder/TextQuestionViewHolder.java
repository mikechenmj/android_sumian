package com.sumian.sleepdoctor.chat.holder;

import android.annotation.SuppressLint;
import android.support.text.emoji.widget.EmojiAppCompatTextView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.chat.base.BaseChatViewHolder;
import com.sumian.sleepdoctor.chat.widget.CustomPopWindow;
import com.sumian.sleepdoctor.chat.widget.MsgSendErrorView;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sm
 * on 2018/1/30.
 * desc:
 */

public class TextQuestionViewHolder extends BaseChatViewHolder<AVIMTextMessage> {

    private static final String TAG = TextQuestionViewHolder.class.getSimpleName();

    @BindView(R.id.tv_time_line)
    TextView mTvTimeLine;

    @BindView(R.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R.id.tv_label)
    TextView mTvLabel;

    @BindView(R.id.iv_icon)
    CircleImageView mIvIcon;

    @BindView(R.id.tv_content)
    EmojiAppCompatTextView mTvContent;

    @BindView(R.id.msg_send_error_view)
    MsgSendErrorView mMsgSendErrorView;

    public TextQuestionViewHolder(ViewGroup parent, boolean isLeft, int leftLayoutId, int rightLayoutId) {
        super(parent, isLeft, leftLayoutId, rightLayoutId);
    }

    @Override
    public void initView(AVIMTextMessage avimTextMessage) {
        super.initView(avimTextMessage);
        updateUserProfile(avimTextMessage.getFrom(), mGroupId, mTvLabel, mTvNickname, mIvIcon);
        updateImageText(avimTextMessage, mTvContent);
    }

    @OnClick({R.id.iv_icon})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_icon:
                showOtherUserProfile(v);
                break;
            default:
                break;
        }
    }

    @Override
    protected boolean onItemLongClick(View v) {
        if (mRole == 0) {
            return true;
        }

        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(v.getContext()).inflate(R.layout.lay_pop_question, null, false);

        CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(v.getContext())
                .setView(rootView)//显示的布局，还可以通过设置一个View
                //     .size(600,400) //设置显示的大小，不设置就默认包裹内容
                .setFocusable(true)//是否获取焦点，默认为ture
                .setOutsideTouchable(true)//是否PopupWindow 以外触摸dissmiss
                .create()//创建PopupWindow
                .showAsDropDown(mTvContent, 0, (int) (-2.1 * mTvContent.getHeight()), Gravity.TOP | Gravity.CENTER);//显示PopupWindow

        rootView.setOnClickListener(v1 -> {
            popWindow.dismiss();
            if (mOnReplayListener != null) {
                mOnReplayListener.onReplyMsg(mItem);
            }
        });

        return super.onItemLongClick(v);
    }
}
