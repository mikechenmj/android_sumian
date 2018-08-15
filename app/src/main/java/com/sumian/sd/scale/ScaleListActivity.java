package com.sumian.sd.scale;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sumian.sd.R;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.base.SdBaseActivity;
import com.sumian.sd.event.EventBusUtil;
import com.sumian.sd.event.ScaleFinishFillingEvent;
import com.sumian.sd.network.callback.BaseResponseCallback;
import com.sumian.sd.network.response.PaginationResponse;
import com.sumian.sd.scale.bean.Scale;
import com.sumian.sd.widget.TitleBar;
import com.sumian.sd.widget.error.EmptyErrorView;

import org.greenrobot.eventbus.Subscribe;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;

public class ScaleListActivity extends SdBaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, BaseQuickAdapter.OnItemClickListener {

    public static final String KEY_TYPE = "type";
    public static final String TYPE_ALL = "all";
    public static final String TYPE_NOT_FILLED = "not_filled";
    public static final String TYPE_FILLED = "filled";
    private static final int PER_PAGE = 15;
    @BindView(R.id.title_bar)
    TitleBar titleBar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private BaseQuickAdapter<Scale, BaseViewHolder> mAdapter;
    private int mPage = 1;
    private String mType;

    public static void launch(Context context, @Type String type) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TYPE, type);
        show(context, ScaleListActivity.class, bundle);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mType = bundle.getString(KEY_TYPE);
        return super.initBundle(bundle);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        titleBar.setOnBackClickListener(v -> finish());
        boolean isTypeAll = TYPE_ALL.equals(mType);
        titleBar.setTitle(isTypeAll ? R.string.scale_sleep_scale : R.string.scale_self_scale);
        mAdapter = isTypeAll ? new ScaleListAdapter(null) : new MyScaleListAdapter(null);
        mAdapter.setOnLoadMoreListener(this, recyclerView);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setEmptyView(EmptyErrorView.createNormalEmptyView(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scale_list;
    }

    @Override
    protected void initData() {
        super.initData();
        loadMoreData();
    }

    private void loadMoreData() {
        Call<PaginationResponse<Scale>> call = AppManager.getHttpService().getScaleList(mPage, PER_PAGE, mType);
        addCall(call);
        call
                .enqueue(new BaseResponseCallback<PaginationResponse<Scale>>() {
                    @Override
                    protected void onSuccess(PaginationResponse<Scale> response) {
                        List<Scale> data = response.data;
                        mAdapter.addData(data);
                        mPage++;
                        if (data.size() < PER_PAGE) {
                            mAdapter.setEnableLoadMore(false);
                        }
                    }

                    @Override
                    protected void onFailure(int code, @NonNull String message) {

                    }

                    @Override
                    protected void onFinish() {
                        super.onFinish();
                        mAdapter.loadMoreComplete();
                    }
                });
    }

    @Override
    public void onLoadMoreRequested() {
        loadMoreData();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Scale item = mAdapter.getItem(position);
        Scale.ScaleDetail scale = item.getScale();
        ScaleDetailActivity.launch(this, scale.getTitle(), item.getId());
    }

    @Override
    protected boolean openEventBus() {
        return true;
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true)
    public void onScaleFinishFillingEvent(ScaleFinishFillingEvent event) {
        // refresh data
        EventBusUtil.removeStickyEvent(event);
        mPage = 1;
        mAdapter.setNewData(null);
        loadMoreData();
    }

    @StringDef({
            TYPE_ALL,
            TYPE_NOT_FILLED,
            TYPE_FILLED,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface Type {
    }
}
