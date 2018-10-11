package com.sumian.sd.diary.sleeprecord;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumian.sd.R;
import com.sumian.sd.diary.sleeprecord.bean.SleepPill;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/1 11:36
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class PillVH extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_pill)
    TextView tvPill;
    @BindView(R.id.tv_time)
    TextView tvTime;

    private PillVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public static PillVH create(ViewGroup parent) {
        Context context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_pill, parent, false);
        return new PillVH(inflate);
    }

    public void setData(SleepPill pill) {
        tvPill.setText(String.format(Locale.getDefault(), "%s（%s）", pill.getName(), pill.getAmount()));
        tvTime.setText(pill.getTime());
    }
}
