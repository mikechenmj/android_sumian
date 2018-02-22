package com.sumian.sleepdoctor.tab.fragment;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.app.delegate.HomeDelegate;
import com.sumian.sleepdoctor.base.BaseFragment;
import com.sumian.sleepdoctor.chat.engine.ChatEngine;
import com.sumian.sleepdoctor.chat.widget.SumianRefreshLayout;
import com.sumian.sleepdoctor.pager.activity.ScanQrCodeActivity;
import com.sumian.sleepdoctor.tab.adapter.GroupAdapter;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;
import com.sumian.sleepdoctor.tab.bean.GroupItem;
import com.sumian.sleepdoctor.tab.contract.GroupContract;
import com.sumian.sleepdoctor.tab.presenter.GroupPresenter;
import com.sumian.sleepdoctor.widget.GroupErrorView;
import com.sumian.sleepdoctor.widget.GroupRequestScanQrCodeView;
import com.sumian.sleepdoctor.widget.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

public class GroupFragment extends BaseFragment<GroupPresenter> implements HomeDelegate, GroupRequestScanQrCodeView.OnGrantedCallback,
        GroupContract.View, TitleBar.OnMoreListener, SwipeRefreshLayout.OnRefreshListener, ChatEngine.OnMsgCallback {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.refresh)
    SumianRefreshLayout mRefresh;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.group_error_view)
    GroupErrorView mGroupErrorView;

    @BindView(R.id.request_scan_qr_code_view)
    GroupRequestScanQrCodeView mRequestScanQrCodeView;

    private GroupAdapter mGroupAdapter;
    private boolean mIsRefresh;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_group;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        setStatusBarColor();

        mTitleBar.addOnMoreListener(this);
        mRefresh.setOnRefreshListener(this);
        mRecycler.setLayoutManager(new LinearLayoutManager(root.getContext()));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setAdapter(mGroupAdapter = new GroupAdapter(getContext()));
        mRequestScanQrCodeView.setFragment(this).setOnGrantedCallback(this);
        AppManager.getChatEngine().setOnMsgCallback(this);
    }

    @Override
    protected void initData() {
        super.initData();
        AppManager.getChatEngine().loginImServer();
        mPresenter.getGroups();
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        GroupPresenter.init(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRequestScanQrCodeView.onRequestPermissionsResultDelegate(requestCode, permissions, grantResults);
    }

    @Override
    protected void onRelease() {
        mPresenter.release();
        AppManager.getChatEngine().removeOnMsgCallback(this);
        super.onRelease();
    }

    @Override
    public void bindPresenter(GroupContract.Presenter presenter) {
        this.mPresenter = (GroupPresenter) presenter;
    }

    @Override
    public void onBegin() {
        mRefresh.setRefreshing(true);
        mGroupErrorView.showRequest();
    }

    @Override
    public void onFinish() {
        mRefresh.setRefreshing(false);
    }

    @Override
    public void onFailure(String error) {
        showToast(error);
    }

    @Override
    public void onGrantedSuccess() {
        ScanQrCodeActivity.show(getContext(), ScanQrCodeActivity.class);
        // commitReplace(ScanQrCodeActivity.class);
    }

    @Override
    public void onNoHaveAnyGroups() {//空白页
        mRequestScanQrCodeView.show();
    }

    @Override
    public void onGetGroupsSuccess(List<GroupDetail<UserProfile, UserProfile>> groups) {
        runOnUiThread(() -> {
            if (mIsRefresh) {
                mGroupAdapter.clear();
                mIsRefresh = false;
            }

            List<GroupItem> groupItems = new ArrayList<>();
            GroupItem groupItem;

            for (GroupDetail<UserProfile, UserProfile> group : groups) {
                groupItem = new GroupItem();
                groupItem.groupDetail = group;
                groupItems.add(groupItem);
            }

            mGroupErrorView.hideError();
            mRequestScanQrCodeView.hide();
            mGroupAdapter.addAll(groupItems);
        });
    }

    @Override
    public void noNoHaveMoreGroups(String noHaveMoreMsg) {
        showToast(noHaveMoreMsg);
    }

    @Override
    public void onShowErrorGroupView() {
        if (mGroupErrorView != null)
            mGroupErrorView.showError();
    }

    @Override
    public void onLoadMore(View v) {
        mRequestScanQrCodeView.requestCodeQRCodePermissions();
    }

    @Override
    public void onRefresh() {
        mIsRefresh = true;
        initData();
    }

    @Override
    public void onMsgCallback(AVIMTypedMessage msg) {
        int position = mGroupAdapter.updateMsg(msg);
        mRecycler.scrollToPosition(position);
    }
}
