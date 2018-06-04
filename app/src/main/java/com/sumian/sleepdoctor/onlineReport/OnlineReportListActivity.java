package com.sumian.sleepdoctor.onlineReport;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.improve.widget.error.EmptyErrorView;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.ErrorResponse;
import com.sumian.sleepdoctor.network.response.PaginationResponse;
import com.sumian.sleepdoctor.widget.TitleBar;

import java.util.List;

import butterknife.BindView;

public class OnlineReportListActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.RequestLoadMoreListener {
    private static final int PER_PAGE = 15;
    @BindView(R.id.title_bar)
    TitleBar titleBar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private OnlineReportListAdapter mAdapter;
    private int mPage = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_online_report_list;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        titleBar.addOnBackListener(v -> finish());
        mAdapter = new OnlineReportListAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnLoadMoreListener(this, recyclerView);
        mAdapter.setEmptyView(getEmptyView());
    }

    @Override
    protected void initData() {
        super.initData();
        loadOnlineReports();
    }

    private void loadOnlineReports() {
        AppManager.getHttpService().queryReports(mPage, PER_PAGE)
                .enqueue(new BaseResponseCallback<PaginationResponse<List<OnlineReport>>>() {
                    @Override
                    protected void onSuccess(PaginationResponse<List<OnlineReport>> response) {
                        LogUtils.d(response);
                        List<OnlineReport> reportList = response.data;
                        mAdapter.addData(reportList);
                        mPage++;
                        if (reportList.size() < PER_PAGE) {
                            mAdapter.setEnableLoadMore(false);
                        }
                    }

                    @Override
                    protected void onFailure(ErrorResponse errorResponse) {
                        LogUtils.d(errorResponse);
                    }

                    @Override
                    protected void onFinish() {
                        super.onFinish();
                        mAdapter.loadMoreComplete();
                    }
                });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        OnlineReport item = (OnlineReport) adapter.getItem(position);
        if (item == null) {
            return;
        }
        OnlineReportDetailActivity.launch(this, item.getTitle(), item.getReport_url());
    }

    @Override
    public void onLoadMoreRequested() {
        loadOnlineReports();
    }


    public View getEmptyView() {
        return EmptyErrorView.create(this,
                R.mipmap.ic_empty_state_report,
                R.string.online_report_list_empty_title,
                R.string.online_report_list_empty_desc);
    }
}
