package com.sumian.sddoctor.service.advisory.onlinereport;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sumian.common.network.response.ErrorResponse;
import com.sumian.sddoctor.R;
import com.sumian.sddoctor.app.AppManager;
import com.sumian.sddoctor.constants.StatConstants;
import com.sumian.sddoctor.network.bean.PaginationResponse;
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback;
import com.sumian.sddoctor.widget.EmptyErrorView;
import com.sumian.sddoctor.widget.TitleBar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/4 10:25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class OnlineReportListActivity extends SdBaseActivity implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.RequestLoadMoreListener {
    public static final String KEY_LAUNCH_TYPE = "KEY_LAUNCH_TYPE";
    public static final String KEY_LAUNCH_DATA = "KEY_LAUNCH_DATA";
    public static final String KEY_RESULT_DATA = "data";
    public static final String LAUNCH_TYPE_SHOW_ALL = "LAUNCH_TYPE_SHOW_ALL";
    public static final String LAUNCH_TYPE_SHOW_INPUT_DATA = "LAUNCH_TYPE_SHOW_INPUT_DATA";
    public static final String LAUNCH_TYPE_PICK = "LAUNCH_TYPE_PICK";
    private static final int PER_PAGE = 15;

    private OnlineReportListAdapter mAdapter;
    private int mPage = 1;
    private ArrayList<OnlineReport> mLaunchOnlineReports;
    private boolean mIsPickMode;
    private boolean mIsShowListMode;

    public static void launchForShowAll(ActivityLauncher launcher) {
        Intent intent = new Intent(launcher.getActivity(), OnlineReportListActivity.class);
        intent.putExtra(KEY_LAUNCH_TYPE, LAUNCH_TYPE_SHOW_ALL);
        launcher.startActivity(intent);
    }

    public static void launchForShowList(Context context, ArrayList<OnlineReport> data) {
        Intent intent = new Intent(context, OnlineReportListActivity.class);
        intent.putExtra(KEY_LAUNCH_TYPE, LAUNCH_TYPE_SHOW_INPUT_DATA);
        intent.putExtra(KEY_LAUNCH_DATA, data);
        context.startActivity(intent);
    }

    /**
     * Get data in onActivityResult:
     * ArrayList<OnlineReport> reports = data.getParcelableArrayListExtra("data");
     */
    public static void launchForPick(ActivityLauncher launcher, int requestCode, ArrayList<OnlineReport> data) {
        Intent intent = new Intent(launcher.getActivity(), OnlineReportListActivity.class);
        intent.putExtra(KEY_LAUNCH_TYPE, LAUNCH_TYPE_PICK);
        intent.putExtra(KEY_LAUNCH_DATA, data);
        launcher.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_online_report_list;
    }

    @Override
    public String getPageName() {
        return StatConstants.page_service_patient_report_list;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        if (bundle != null) {
            String launchType = bundle.getString(KEY_LAUNCH_TYPE);
            mIsShowListMode = LAUNCH_TYPE_SHOW_INPUT_DATA.equals(launchType);
            mIsPickMode = LAUNCH_TYPE_PICK.equals(launchType);
            mLaunchOnlineReports = bundle.getParcelableArrayList(KEY_LAUNCH_DATA);
        }
        return super.initBundle(bundle);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        TitleBar titleBar = findViewById(R.id.title_bar);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        titleBar.setOnBackClickListener(v -> finish());
        titleBar.setMenuVisibility(mIsPickMode ? View.VISIBLE : View.GONE);
        titleBar.setOnMenuClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(KEY_RESULT_DATA, mAdapter.getSelectedReports());
            setResult(RESULT_OK, intent);
            finish();
        });
        mAdapter = new OnlineReportListAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnLoadMoreListener(this, recyclerView);
        mAdapter.setEmptyView(getEmptyView());
        mAdapter.setSelectedReports(mLaunchOnlineReports);
    }

    @Override
    protected void initData() {
        super.initData();
        mAdapter.setEnableLoadMore(!mIsShowListMode);
        if (mIsShowListMode) {
            mAdapter.addData(mLaunchOnlineReports);
        } else {
            loadOnlineReports();
        }
        mIsPickMode = false;
        mAdapter.setPickMode(false);
    }

    private void loadOnlineReports() {
        Call<PaginationResponse<OnlineReport>> call = AppManager.getHttpService().getReports(mPage, PER_PAGE);
        addCall(call);
        call
                .enqueue(new BaseSdResponseCallback<PaginationResponse<OnlineReport>>() {
                    @Override
                    protected void onFailure(@NonNull ErrorResponse errorResponse) {
                        LogUtils.d(errorResponse.getMessage());
                    }

                    @Override
                    protected void onSuccess(PaginationResponse<OnlineReport> response) {
                        LogUtils.d(response);
                        List<OnlineReport> reportList = response.data;
                        mAdapter.addData(reportList);
                        mPage++;
                        if (reportList.size() < PER_PAGE) {
                            mAdapter.setEnableLoadMore(false);
                        }
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
        if (mIsPickMode) {
            mAdapter.addOrRemoveSelectedItem(position);
        } else {
            OnlineReportDetailActivity.launch(this, item);
        }
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
