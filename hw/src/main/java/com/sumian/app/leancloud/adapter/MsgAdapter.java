package com.sumian.app.leancloud.adapter;

import android.support.annotation.NonNull;
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


    public MsgAdapter(int serviceType) {
        this.mAdapterDelegate = new AdapterDelegate(serviceType);
        this.mItems = new ArrayList<>(0);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return mAdapterDelegate.findViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        mAdapterDelegate.onBindViewHolder(getItemViewType(position), holder, mItems.get(position));
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
            int position = mItems.size();
            mItems.addAll(items);
            notifyItemRangeInserted(position, items.size());
        }
    }

    /**
     * add one msg
     *
     * @param item msg
     */
    public void addMsg(AVIMMessage item) {
        int position = mItems.size();
        mItems.add(item);
        notifyItemInserted(position);
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
