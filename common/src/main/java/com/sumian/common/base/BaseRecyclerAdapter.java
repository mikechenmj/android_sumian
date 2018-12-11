package com.sumian.common.base;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

;

/**
 * Created by haibin
 * on 17/2/27.
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter {

    protected LayoutInflater mInflater;
    protected List<T> mItems;
    protected RequestManager mLoader;
    private OnItemClickListener onItemClickListener;
    private OnClickListener onClickListener;

    protected BaseRecyclerAdapter(Context context) {
        this.mItems = new ArrayList<>();
        mLoader = Glide.with(context);
        mInflater = LayoutInflater.from(context);
        onClickListener = new OnClickListener() {
            @Override
            public void onClick(int position, long itemId) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(position, itemId);
            }
        };

    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder holder = onCreateDefaultViewHolder(parent, viewType);
        if (holder != null) {
            holder.itemView.setTag(holder);
            holder.itemView.setOnClickListener(onClickListener);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        onBindViewHolder(holder, mItems.get(position), position);
    }

    protected abstract RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type);

    protected abstract void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @NonNull T item, int position);

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void addAll(List<T> items) {
        if (items != null && items.size() > 0) {
            mItems.addAll(items);
            notifyItemRangeInserted(mItems.size(), items.size());
        }
    }

    public final void addItem(T item) {
        if (item != null) {
            this.mItems.add(item);
            notifyItemChanged(mItems.size());
        }
    }

    public void addItemPosition(int position, T item) {
        if (item != null) {
            this.mItems.add(position, item);
            notifyItemInserted(position);
        }
    }

    public void updateItem(int position) {
        if (getItemCount() > position) {
            notifyItemChanged(position);
        }
    }

    public final T getItem(int position) {
        if (position < 0 || position >= mItems.size())
            return null;
        return mItems.get(position);
    }

    public List<T> getItems() {
        return mItems;
    }

    public final void resetItem(List<T> items) {
        if (items != null) {
            clear();
            addAll(items);
        }
    }

    public void removeItem(int position) {
        if (mItems != null) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public final void clear() {
        this.mItems.clear();
        notifyDataSetChanged();
    }

    public static abstract class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
            onClick(holder.getAdapterPosition(), holder.getItemId());
        }

        public abstract void onClick(int position, long itemId);
    }


    public interface OnItemClickListener {
        void onItemClick(int position, long itemId);
    }
}
