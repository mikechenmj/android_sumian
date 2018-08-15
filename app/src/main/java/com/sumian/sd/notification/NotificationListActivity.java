package com.sumian.sd.notification;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sumian.common.utils.SettingsUtil;
import com.sumian.sd.R;
import com.sumian.sd.base.SdBaseActivity;
import com.sumian.sd.event.EventBusUtil;
import com.sumian.sd.event.NotificationReadEvent;
import com.sumian.sd.notification.bean.Notification;
import com.sumian.sd.notification.push.SchemeResolveUtil;
import com.sumian.sd.utils.NotificationUtil;
import com.sumian.sd.widget.TitleBar;
import com.sumian.sd.widget.error.EmptyErrorView;

import java.util.List;

import butterknife.BindView;

public class NotificationListActivity extends SdBaseActivity<NotificationListContract.Presenter>
        implements BaseQuickAdapter.OnItemClickListener, NotificationListContract.View, BaseQuickAdapter.RequestLoadMoreListener {

    public static final int REQUEST_CODE_OPEN_NOTIFICATION = 1;
    @BindView(R.id.title_bar)
    TitleBar titleBar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private NotificationListAdapter mAdapter;
    private NotificationListHeadView mHeaderView;

    public static void launch(Context context) {
        show(context, getLaunchIntent(context));
    }

    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, NotificationListActivity.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_notification_list;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        titleBar.setOnBackClickListener(v -> finish());
        titleBar.setOnMenuClickListener(v -> markAllAsRead());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new NotificationListAdapter(this, null);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnLoadMoreListener(this, recyclerView);
        mAdapter.setEmptyView(getEmptyView());
        initHeadView();
    }

    private void initHeadView() {
        boolean enabled = isNotificationEnabled();
        if (!enabled) {
            mHeaderView = new NotificationListHeadView(this);
            mHeaderView.setOnClickListener(v -> SettingsUtil.launchSettingActivityForResult(this, REQUEST_CODE_OPEN_NOTIFICATION));
            mAdapter.addHeaderView(mHeaderView);
        }
    }

    private boolean isNotificationEnabled() {
        return NotificationUtil.Companion.areNotificationsEnabled(this);
    }

    private void removeHeadViewInNeeded() {
        boolean enabled = isNotificationEnabled();
        if (enabled && mHeaderView != null) {
            mAdapter.removeHeaderView(mHeaderView);
            mHeaderView = null;
        }
    }

    private View getEmptyView() {
        return EmptyErrorView.create(this,
                R.mipmap.ic_empty_state_alarm,
                R.string.notification_list_empty_title,
                R.string.notification_list_empty_desc);
    }


    @Override
    protected void initPresenter() {
        mPresenter = new NotificationListPresenter(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.loadData(true);
    }

    /**
     * 重复启动（点击notification）会进入该分支，需要刷新数据
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mAdapter.setNewData(null);
        mPresenter.loadData(true);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Notification notification = mAdapter.getItem(position);
        if (notification == null) {
            return;
        }
        markAsRead(notification, position);
        launchActivityIfNeed(notification);
    }

    private void markAsRead(Notification notification, int position) {
        mPresenter.readNotification(notification.getId());
        notification.setRead_at((int) (System.currentTimeMillis() / 1000L));
        mAdapter.setData(position, notification);
    }

    private void markAllAsRead() {
        mPresenter.readNotification("0");
        List<Notification> data = mAdapter.getData();
        long currentTimeMillis = System.currentTimeMillis();
        for (Notification notification : data) {
            notification.setRead_at((int) (currentTimeMillis / 1000L));
        }
        mAdapter.notifyDataSetChanged();
    }

    private void launchActivityIfNeed(Notification notification) {
        String type = notification.getType();
        if (Notification.TYPE_FOLLOW_UP_LIFE_NOTICE.equals(type)
                || Notification.TYPE_FOLLOW_UP_REFERRAL_NOTICE.equals(type)) {
            return;
        }
        String scheme = notification.getData().getScheme();
        Intent intent = SchemeResolveUtil.Companion.schemeResolver(this, scheme);
        if (intent == null) {
            LogUtils.d("Unresolved scheme", scheme);
            return;
        }
        startActivity(intent);
    }

    @Override
    public void onLoadMore(List<Notification> notificationList, boolean hasMore) {
        titleBar.setMenuVisibility(mAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
        mAdapter.addData(notificationList);
        mAdapter.loadMoreComplete();
        mAdapter.setEnableLoadMore(hasMore);
    }

    @Override
    public void onReadSuccess() {
        EventBusUtil.postStickyEvent(new NotificationReadEvent());
    }

    @Override
    public void onLoadMoreRequested() {
        mPresenter.loadData(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_OPEN_NOTIFICATION) {
            removeHeadViewInNeeded();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
