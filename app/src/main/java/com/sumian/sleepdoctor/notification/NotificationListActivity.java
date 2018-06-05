package com.sumian.sleepdoctor.notification;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.main.MainActivity;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.ErrorResponse;
import com.sumian.sleepdoctor.network.response.PaginationResponse;
import com.sumian.sleepdoctor.notification.bean.Notification;
import com.sumian.sleepdoctor.widget.TitleBar;

import java.util.List;

import butterknife.BindView;

public class NotificationListActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    public static final int PER_PAGE = 15;
    @BindView(R.id.title_bar)
    TitleBar titleBar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private NotificationListAdapter mAdapter;
    private int mPage = 1;

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
    protected void initData() {
        super.initData();
        queryNotification();
    }

    private void queryNotification() {
        AppManager.getHttpService().getNotificationList(mPage, PER_PAGE)
                .enqueue(new BaseResponseCallback<PaginationResponse<List<Notification>>>() {
                    @Override
                    protected void onSuccess(PaginationResponse<List<Notification>> response) {
                        LogUtils.d(response);
                        List<Notification> data = response.data;
                        mAdapter.addData(data);
                        mPage++;
                        if (data.size() < PER_PAGE) {
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

    private void readNotification(String notificationId) {
        AppManager.getHttpService().readNotification(notificationId)
                .enqueue(new BaseResponseCallback<Object>() {
                    @Override
                    protected void onSuccess(Object response) {
                        LogUtils.d(response);
                    }

                    @Override
                    protected void onFailure(ErrorResponse errorResponse) {
                        LogUtils.d(errorResponse);
                    }
                });
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
        readNotification(notification.getId());
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
}
