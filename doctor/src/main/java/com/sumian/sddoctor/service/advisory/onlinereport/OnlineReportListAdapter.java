package com.sumian.sddoctor.service.advisory.onlinereport;

import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sumian.sddoctor.R;
import com.sumian.sddoctor.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/4 9:27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class OnlineReportListAdapter extends BaseQuickAdapter<OnlineReport, BaseViewHolder> {
    private boolean mIsPickMode = false;
    private ArrayList<OnlineReport> mSelectedReports = new ArrayList<>();

    private int mMaxSelectCount = 9;

    OnlineReportListAdapter(@Nullable List<OnlineReport> data) {
        super(R.layout.item_online_report, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OnlineReport item) {
        long createAtInMillis = item.getCreateAtInMillis();
        String time = TimeUtil.formatDate("yyyy/MM/dd HH:mm:ss", createAtInMillis);
        helper.setText(R.id.tv_title, item.getTitle());
        helper.setText(R.id.tv_time, time);
        ImageView ivSelect = helper.getView(R.id.iv_selected);
        ivSelect.setVisibility(mIsPickMode ? View.VISIBLE : View.GONE);
        ivSelect.setSelected(mSelectedReports.contains(item));
    }

    public boolean isPickMode() {
        return mIsPickMode;
    }

    public void setPickMode(boolean pickMode) {
        mIsPickMode = pickMode;
    }

    public ArrayList<OnlineReport> getSelectedReports() {
        return mSelectedReports;
    }

    public void setSelectedReports(ArrayList<OnlineReport> selectedReports) {
        if (selectedReports == null) {
            mSelectedReports = new ArrayList<OnlineReport>();
        } else {
            mSelectedReports = selectedReports;
        }
    }

    public void setMaxSelectCount(int maxSelectCount) {
        mMaxSelectCount = maxSelectCount;
    }

    public void addOrRemoveSelectedItem(int position) {
        OnlineReport report = getItem(position);
        if (mSelectedReports.contains(report)) {
            mSelectedReports.remove(report);
        } else {
            if (mSelectedReports.size() == mMaxSelectCount) {
                ToastUtils.showShort(R.string.you_can_select_9_reports_at_most_hint);
                return;
            }
            mSelectedReports.add(report);
        }
        notifyItemChanged(position);
    }
}
