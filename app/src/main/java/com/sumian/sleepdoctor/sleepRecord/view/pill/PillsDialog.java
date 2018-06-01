package com.sumian.sleepdoctor.sleepRecord.view.pill;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.sleepRecord.bean.SleepPill;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/1 11:28
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class PillsDialog extends Dialog {

    public static final String KEY_PILLS = "pills";

    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.rv)
    RecyclerView recyclerView;
    private List<SleepPill> mSleepPills;

    private PillsDialog(@NonNull Context context, List<SleepPill> pills) {
        super(context);
        mSleepPills = pills;
        init(context);
    }

    private void init(Context context) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(context).inflate(R.layout.lay_pills_dialog, null);
        ButterKnife.bind(this, inflate);
        PillAdapter adapter = new PillAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter.setSleepPills(mSleepPills);
        setContentView(inflate);
    }

    @OnClick(R.id.iv_close)
    public void onViewClicked() {
        dismiss();
    }

    public static void show(Context context, List<SleepPill> pills) {
        PillsDialog pillsDialog = new PillsDialog(context, pills);
        pillsDialog.show();
    }

    public void setSleepPills(List<SleepPill> sleepPills) {
        mSleepPills = sleepPills;
    }
}
