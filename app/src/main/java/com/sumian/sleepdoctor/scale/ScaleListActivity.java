package com.sumian.sleepdoctor.scale;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.event.EventBusUtil;
import com.sumian.sleepdoctor.event.ScaleFinishFillingEvent;
import com.sumian.sleepdoctor.improve.widget.error.EmptyErrorView;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.ErrorResponse;
import com.sumian.sleepdoctor.network.response.PaginationResponse;
import com.sumian.sleepdoctor.scale.bean.Scale;
import com.sumian.sleepdoctor.widget.TitleBar;

import org.greenrobot.eventbus.Subscribe;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import butterknife.BindView;

public class ScaleListActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, BaseQuickAdapter.OnItemClickListener {

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
        AppManager.getHttpService()
                .getScaleList(mPage, PER_PAGE, mType)
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
                    protected void onFailure(ErrorResponse errorResponse) {

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
