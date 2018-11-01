package com.sumian.sd.onlinereport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sumian.common.network.response.ErrorResponse;
import com.sumian.sd.R;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.base.SdBaseActivity;
import com.sumian.sd.network.callback.BaseSdResponseCallback;
import com.sumian.sd.network.response.PaginationResponse;
import com.sumian.sd.widget.TitleBar;
import com.sumian.sd.widget.error.EmptyErrorView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

    public static void launchForShowAll(Fragment launcher) {
        Intent intent = new Intent(ActivityUtils.getTopActivity(), OnlineReportListActivity.class);
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
    public static void launchForPick(Activity launcher, int requestCode, ArrayList<OnlineReport> data) {
        Intent intent = new Intent(ActivityUtils.getTopActivity(), OnlineReportListActivity.class);
        intent.putExtra(KEY_LAUNCH_TYPE, LAUNCH_TYPE_PICK);
        intent.putExtra(KEY_LAUNCH_DATA, data);
        launcher.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_online_report_list;
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
        titleBar.setOnBackClickListener(v -> finish());
        titleBar.setMenuVisibility(mIsPickMode ? View.VISIBLE : View.GONE);
        titleBar.setOnMenuClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(KEY_RESULT_DATA, mAdapter.getSelectedReports());
            setResult(RESULT_OK, intent);
            finish();
        });
        mAdapter = new OnlineReportListAdapter(null);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

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
        mAdapter.setPickMode(mIsPickMode);
    }

    private void loadOnlineReports() {
        Call<PaginationResponse<OnlineReport>> call = AppManager.getSdHttpService().getReports(mPage, PER_PAGE);
        addCall(call);
        call.enqueue(new BaseSdResponseCallback<PaginationResponse<OnlineReport>>() {
            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
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

    private String getUrlToken() {
        return "token=" + AppManager.getAccountViewModel().getTokenString();
    }

    protected String appendToken(String url) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean hasQuestionMark = url != null && url.contains("?");
        stringBuilder.append(url)
                .append(hasQuestionMark ? "&" : "?")
                .append(getUrlToken());
        return stringBuilder.toString();
    }
}
