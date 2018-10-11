package com.sumian.sd.diary.sleeprecord.pill;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.sumian.sd.diary.sleeprecord.PillVH;
import com.sumian.sd.diary.sleeprecord.bean.SleepPill;

import java.util.List;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/1 11:44
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class PillAdapter extends RecyclerView.Adapter<PillVH> {
    private List<SleepPill> mSleepPills;

    @NonNull
    @Override
    public PillVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return PillVH.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull PillVH holder, int position) {
        holder.setData(mSleepPills.get(position));
    }

    @Override
    public int getItemCount() {
        return mSleepPills == null ? 0 : mSleepPills.size();
    }

    public void setSleepPills(List<SleepPill> sleepPills) {
        mSleepPills = sleepPills;
        notifyDataSetChanged();
    }
}
