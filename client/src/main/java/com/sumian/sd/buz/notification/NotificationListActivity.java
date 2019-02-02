package com.sumian.sd.buz.notification;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sumian.common.notification.NotificationUtil;
import com.sumian.common.utils.SettingsUtil;
import com.sumian.common.widget.CommonEmptyView;
import com.sumian.sd.R;
import com.sumian.sd.base.SdBaseActivity;
import com.sumian.sd.buz.notification.bean.Notification;
import com.sumian.sd.widget.TitleBar;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class NotificationListActivity extends SdBaseActivity<NotificationListContract.Presenter>
        implements BaseQuickAdapter.OnItemClickListener, NotificationListContract.View, BaseQuickAdapter.RequestLoadMoreListener {

    public static final int REQUEST_CODE_OPEN_NOTIFICATION = 1;
    private TitleBar titleBar;
    private NotificationListAdapter mAdapter;
    private NotificationListHeadView mHeaderView;

    public static void launch(Context context) {
        ActivityUtils.startActivity(getLaunchIntent(context));
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
        titleBar = findViewById(R.id.title_bar);
        titleBar.setOnBackClickListener(v -> finish());
        titleBar.setOnMenuClickListener(v -> markAllAsRead());
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
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
        return NotificationUtil.INSTANCE.areNotificationsEnabled(this);
    }

    private void removeHeadViewInNeeded() {
        boolean enabled = isNotificationEnabled();
        if (enabled && mHeaderView != null) {
            mAdapter.removeHeaderView(mHeaderView);
            mHeaderView = null;
        }
    }

    private View getEmptyView() {
        return new CommonEmptyView(this, null)
                .setImage(R.mipmap.ic_empty_state_alarm)
                .setTitle(R.string.notification_list_empty_title)
                .setMessage(R.string.notification_list_empty_desc);

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
        mPresenter.readNotification(notification.getId(), notification.getDataId());
        notification.setReadAt((int) (System.currentTimeMillis() / 1000L));
        mAdapter.setData(position, notification);
    }

    private void markAllAsRead() {
        mPresenter.readNotification("0", 0);
        List<Notification> data = mAdapter.getData();
        long currentTimeMillis = System.currentTimeMillis();
        for (Notification notification : data) {
            notification.setReadAt((int) (currentTimeMillis / 1000L));
        }
        mAdapter.notifyDataSetChanged();
    }

    private void launchActivityIfNeed(Notification notification) {
        String type = notification.getType();
        if (Notification.Companion.getTYPE_FOLLOW_UP_LIFE_NOTICE().equals(type)
                || Notification.Companion.getTYPE_FOLLOW_UP_REFERRAL_NOTICE().equals(type)) {
            return;
        }
        String scheme = notification.getData().getScheme();
        Intent intent = SchemeResolver.INSTANCE.schemeResolver(this, scheme);
        if (intent == null) {
            LogUtils.d("Unresolved scheme", scheme);
            return;
        }
        startActivity(intent);
    }

    @Override
    public void onLoadMore(List<Notification> notificationList, boolean hasMore) {
        titleBar.setMenuVisibility(mAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
        if (notificationList == null) {
            notificationList = new ArrayList<>();
        }
        mAdapter.addData(notificationList);
        mAdapter.loadMoreComplete();
        mAdapter.setEnableLoadMore(hasMore);
    }

    @Override
    public void onReadSuccess() {
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
