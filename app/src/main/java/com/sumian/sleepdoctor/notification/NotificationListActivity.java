package com.sumian.sleepdoctor.notification;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.event.NotificationReadEvent;
import com.sumian.sleepdoctor.improve.widget.error.EmptyErrorView;
import com.sumian.sleepdoctor.main.MainActivity;
import com.sumian.sleepdoctor.notification.bean.Notification;
import com.sumian.sleepdoctor.widget.TitleBar;

import org.greenrobot.eventbus.EventBus;

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
        titleBar.setMenuOnClickListener(v -> markAllAsRead());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new NotificationListAdapter(this, null);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnLoadMoreListener(this, recyclerView);
        mAdapter.setEmptyView(getEmptyView());
        queryNotificationSetting();
    }

    private View getEmptyView() {
        return EmptyErrorView.create(this,
                R.mipmap.ic_empty_state_alarm,
                R.string.notification_list_empty_title,
                R.string.notification_list_empty_desc);
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
        markAsRead(notification, position);
        launchActivityByNotificationType(notification);
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
        titleBar.setMenuVisibility(mAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
        mAdapter.addData(notificationList);
        mAdapter.loadMoreComplete();
        mAdapter.setEnableLoadMore(hasMore);
    }

    @Override
    public void onReadSuccess() {
        EventBus.getDefault().postSticky(new NotificationReadEvent());
    }

    @Override
    public void onLoadMoreRequested() {
        mPresenter.loadMore();
    }
}
