package com.sumian.chat.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.sumian.app.leancloud.holder.base.BaseViewHolder;
import com.sumian.app.leancloud.holder.delegate.AdapterDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzz
 * on 2017/10/17.
 * <p>
 * desc:
 */

public class MsgAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = MsgAdapter.class.getSimpleName();

    private List<AVIMMessage> mItems;
    private AdapterDelegate mAdapterDelegate;

    private int mServiceType;

    public MsgAdapter() {
        this.mAdapterDelegate = new AdapterDelegate();
        this.mItems = new ArrayList<>(0);
    }

    public MsgAdapter setServiceType(int serviceType) {
        mServiceType = serviceType;
        return this;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mAdapterDelegate.findViewHolder(parent, viewType,mServiceType);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        AVIMMessage msg = this.mItems.get(position);
        mAdapterDelegate.onBindViewHolder(mServiceType, viewType, holder, msg);
    }

    @Override
    public int getItemViewType(int position) {
        AVIMMessage msg = this.mItems.get(position);
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
            mItems.addAll(items);
            notifyItemRangeInserted(mItems.size(), items.size());
        }
    }

    /**
     * add one msg
     *
     * @param item msg
     */
    public void addMsg(AVIMMessage item) {
        mItems.add(item);
        notifyItemInserted(mItems.size());
    }

    /**
     * 更新position 位置的 msg
     *
     * @param msg msg
     */
    public void updateMsg(AVIMMessage msg) {
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
