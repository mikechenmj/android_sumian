package com.sumian.sleepdoctor.chat.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.sumian.sleepdoctor.chat.base.BaseChatViewHolder;
import com.sumian.sleepdoctor.chat.holder.delegate.AdapterDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzz
 * on 2017/10/17.
 * <p>
 * desc:
 */

public class MsgAdapter extends RecyclerView.Adapter<BaseChatViewHolder> {

    private static final String TAG = MsgAdapter.class.getSimpleName();

    private List<AVIMTypedMessage> mItems;
    private AdapterDelegate mAdapterDelegate;

    public MsgAdapter(AdapterDelegate.OnReplyCallback onReplyCallback) {
        this.mItems = new ArrayList<>(0);
        this.mAdapterDelegate = new AdapterDelegate();
        this.mAdapterDelegate.setOnReplyCallback(onReplyCallback);
    }

    public MsgAdapter bindGroupId(int groupId) {
        this.mAdapterDelegate.bindGroupId(groupId);
        return this;
    }

    public MsgAdapter bindRole(int role){
        this.mAdapterDelegate.bindGroupRole(role);
        return this;
    }

    @Override
    public BaseChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mAdapterDelegate.findViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BaseChatViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        AVIMTypedMessage msg = this.mItems.get(position);
        mAdapterDelegate.onBindViewHolder(viewType, holder, msg);
    }

    @Override
    public int getItemViewType(int position) {
        AVIMTypedMessage msg = this.mItems.get(position);
        return mAdapterDelegate.getItemViewType(msg);
    }

    @Override
    public int getItemCount() {
        return this.mItems.size();
    }

    /**
     * 加载历史聊天记录
     *
     * @param items leancloud  msg
     */
    public void addHistories(List<AVIMTypedMessage> items) {
        this.mItems.addAll(0, items);
        notifyItemRangeInserted(0, items.size());
    }

    /**
     * 加载聊天记录
     *
     * @param items leancloud  msg
     */
    public void addMessages(List<AVIMTypedMessage> items) {
        if (items != null) {
            mItems.addAll(items);
            notifyItemRangeInserted(mItems.size(), items.size());
        }
    }

    /**
     * add one msg
     *
     * @param item msg
     */
    public void addMsg(AVIMTypedMessage item) {
        mItems.add(item);
        notifyItemInserted(mItems.size());
    }

    /**
     * 更新position 位置的 msg
     *
     * @param msg msg
     */
    public void updateMsg(AVIMTypedMessage msg) {
        int tempPosition = -1;
        for (int i = 0, len = mItems.size(); i < len; i++) {
            AVIMMessage tempMsg = mItems.get(i);
            String messageId = tempMsg.getMessageId();
            if (!TextUtils.isEmpty(messageId) && messageId.equals(msg.getMessageId())) {
                tempPosition = i;
                break;
            }
        }
        if (tempPosition == -1) {
            return;
        }
        this.mItems.get(tempPosition).setMessageStatus(msg.getMessageStatus());
        notifyItemChanged(tempPosition);
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

}
