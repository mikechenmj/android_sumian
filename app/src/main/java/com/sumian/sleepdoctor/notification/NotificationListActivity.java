package com.sumian.sleepdoctor.notification;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.main.MainActivity;
import com.sumian.sleepdoctor.notification.bean.Notification;
import com.sumian.sleepdoctor.widget.TitleBar;

import java.util.List;

import butterknife.BindView;

public class NotificationListActivity extends BaseActivity<NotificationListContract.Presenter>
        implements BaseQuickAdapter.OnItemClickListener, NotificationListContract.View, BaseQuickAdapter.RequestLoadMoreListener {

    @BindView(R.id.title_bar)
    TitleBar titleBar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private NotificationListAdapter mAdapter;

    public static void launch(Context context) {
        NotificationListActivity.show(context, NotificationListActivity.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_notification_list;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        titleBar.addOnBackListener(v -> finish());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new NotificationListAdapter(this, null);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnLoadMoreListener(this, recyclerView);
        queryNotificationSetting();
    }

    private void queryNotificationSetting() {
        addHeadView();
    }

    private void addHeadView() {
        NotificationListHeadView headerView = new NotificationListHeadView(this);
        headerView.setOnClickListener(v -> goOpenNotification());
        mAdapter.addHeaderView(headerView);
    }

    private void goOpenNotification() {
    }

    @Override
    protected void initPresenter() {
        mPresenter = new NotificationListPresenter(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.loadMore();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Notification notification = mAdapter.getItem(position);
        if (notification == null) {
            return;
        }
        updateNotificationReadState(notification, position);
        launchActivityByNotificationType(notification);
    }

    private void updateNotificationReadState(Notification notification, int position) {
        // update local read state
        notification.setRead_at((int) (System.currentTimeMillis() / 1000L));
        mAdapter.setData(position, notification);
        // update server read state
        mPresenter.readNotification(notification.getId());
    }

    private void launchActivityByNotificationType(Notification notification) {
        String type = notification.getType();
        switch (type) {
            case Notification.TYPE_DIARY_EVALUATION:
                MainActivity.launchSleepRecordTab(this, notification.getData().getDateInMillis(), true);
                break;
            case Notification.TYPE_SCALE_DISTRIBUTION:

                break;
            case Notification.TYPE_ONLINE_REPORT:

                break;
        }
    }

    @Override
    public void onLoadMore(List<Notification> notificationList, boolean hasMore) {
        mAdapter.addData(notificationList);
        mAdapter.loadMoreComplete();
        mAdapter.setEnableLoadMore(hasMore);
    }

    @Override
    public void onReadSuccess() {
        ViewModelProviders.of(this)
                .get(NotificationViewModel.class)
                .updateUnreadCount();
    }

    @Override
    public void onLoadMoreRequested() {
        mPresenter.loadMore();
    }
}
