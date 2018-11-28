package com.sumian.sd.diary.sleeprecord.pill;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.sumian.sd.R;
import com.sumian.sd.diary.sleeprecord.bean.SleepPill;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/1 11:28
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class PillsDialog extends Dialog implements View.OnClickListener {

    public static final String KEY_PILLS = "pills";

    private List<SleepPill> mSleepPills;

    private PillsDialog(@NonNull Context context, List<SleepPill> pills) {
        super(context, R.style.SumianDialog);
        mSleepPills = pills;
        init(context);
    }

    public static void show(Context context, List<SleepPill> pills) {
        PillsDialog pillsDialog = new PillsDialog(context, pills);
        pillsDialog.show();
    }

    private void init(Context context) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(context).inflate(R.layout.lay_pills_dialog, null);
        //ImageView ivClose = inflate.findViewById(R.id.iv_close);
        RecyclerView recyclerView = inflate.findViewById(R.id.rv);
        inflate.findViewById(R.id.iv_close).setOnClickListener(this);

        PillAdapter adapter = new PillAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter.setSleepPills(mSleepPills);
        setContentView(inflate);
    }

    public void setSleepPills(List<SleepPill> sleepPills) {
        mSleepPills = sleepPills;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
