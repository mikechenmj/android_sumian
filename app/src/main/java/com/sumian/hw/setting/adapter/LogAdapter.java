package com.sumian.hw.setting.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.hw.app.HwAppManager;
import com.sumian.hw.common.util.TimeUtil;
import com.sumian.sleepdoctor.app.AppManager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2017/10/17.
 * <p>
 * desc:
 */

public class LogAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = LogAdapter.class.getSimpleName();

    private static final int LEFT_TEXT_TYPE = 0x11;

    private static final int RIGHT_TEXT_TYPE = 0x21;

    private List<AVIMMessage> mItems;

    public LogAdapter() {
        this.mItems = new ArrayList<>();
    }

    private static String getShowTime(AVIMMessage msg) {
        long msgTime = msg.getTimestamp();
        return TimeUtil.formatMsgTime(msgTime);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case LEFT_TEXT_TYPE:
                LeftTextViewHolder leftTextViewHolder = new LeftTextViewHolder(inflater.inflate(R.layout.hw_lay_item_left_text_chat, parent, false));
                leftTextViewHolder.itemView.setTag(leftTextViewHolder);
                return leftTextViewHolder;
            case RIGHT_TEXT_TYPE:
                RightTextViewHolder rightTextViewHolder = new RightTextViewHolder(inflater.inflate(R.layout.hw_lay_item_right_text_chat, parent, false));
                rightTextViewHolder.itemView.setTag(rightTextViewHolder);
                return rightTextViewHolder;
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        int viewType = getItemViewType(position);

        List<AVIMMessage> messages = this.mItems;
        AVIMMessage message = messages.get(position);
        boolean showTime = (position % 6 == 0);

        switch (viewType) {
            case LEFT_TEXT_TYPE:
                LeftTextViewHolder leftTextViewHolder = (LeftTextViewHolder) holder;
                leftTextViewHolder.initView(showTime, message);
                break;
            case RIGHT_TEXT_TYPE:
                RightTextViewHolder rightTextViewHolder = (RightTextViewHolder) holder;
                rightTextViewHolder.initView(showTime, message);
                break;
            default:
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        AVIMMessage message = this.mItems.get(position);
        String from = message.getFrom();
        if (from.equals(AppManager.getAccountViewModel().getLeanCloudId())) {
            return RIGHT_TEXT_TYPE;
        } else {
            return LEFT_TEXT_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        List<AVIMMessage> items = this.mItems;
        return items == null ? 0 : items.size();
    }

    /**
     * 加载历史聊天记录
     *
     * @param items leancloud  msg
     */
    public void addHistories(List<AVIMMessage> items) {
        this.mItems.addAll(0, items);
        notifyItemRangeInserted(0, items.size());
    }

    /**
     * 加载聊天记录
     *
     * @param items leancloud  msg
     */
    public void addMessages(List<AVIMMessage> items) {
        if (items != null) {
            int position = mItems.size();
            mItems.addAll(items);
            notifyItemRangeInserted(position, items.size());
        }
    }

    public void addMsg(AVIMMessage item) {
        mItems.add(item);
        int position = mItems.size();
        notifyItemInserted(position);
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    static class LeftTextViewHolder extends ViewHolder {

        TextView mTvTimeLine;
        CircleImageView mIvIcon;
        TextView mTvContent;

        LeftTextViewHolder(View itemView) {
            super(itemView);
            mTvTimeLine = itemView.findViewById(R.id.tv_time_line);
            mIvIcon = itemView.findViewById(R.id.iv_icon);
            mTvContent = itemView.findViewById(R.id.tv_content);
        }

        void initView(boolean showTime, AVIMMessage msg) {
            if (msg instanceof AVIMTextMessage) {
                AVIMTextMessage textMessage = (AVIMTextMessage) msg;
                String text = textMessage.getText().trim();
                if (TextUtils.isEmpty(text) || "".equals(text)) {
                    return;
                }
                mTvContent.setText(text);

                if (showTime) {
                    String time = getShowTime(msg);
                    mTvTimeLine.setText(time);
                    mTvTimeLine.setVisibility(View.VISIBLE);
                }
            }

        }
    }

    static class RightTextViewHolder extends ViewHolder {

        TextView mTvTimeLine;
        TextView mTvContent;
        CircleImageView mIvIcon;

        RightTextViewHolder(View itemView) {
            super(itemView);
            mTvTimeLine = itemView.findViewById(R.id.tv_time_line);
            mTvContent = itemView.findViewById(R.id.tv_content);
            mIvIcon = itemView.findViewById(R.id.iv_icon);
        }

        void initView(boolean showTime, AVIMMessage msg) {
            if (msg instanceof AVIMTextMessage) {
                AVIMTextMessage textMessage = (AVIMTextMessage) msg;
                String text = textMessage.getText().trim();
                if (TextUtils.isEmpty(text) || "".equals(text)) {
                    return;
                }
                mTvContent.setText(text);

                if (showTime) {
                    String time = getShowTime(msg);
                    mTvTimeLine.setText(time);
                    mTvTimeLine.setVisibility(View.VISIBLE);
                }
            }
        }
    }

}
