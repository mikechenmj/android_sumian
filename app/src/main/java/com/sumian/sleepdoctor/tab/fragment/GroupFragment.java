package com.sumian.sleepdoctor.tab.fragment;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.app.delegate.HomeDelegate;
import com.sumian.sleepdoctor.base.BaseFragment;
import com.sumian.sleepdoctor.chat.engine.ChatEngine;
import com.sumian.sleepdoctor.improve.widget.SumianRefreshLayout;
import com.sumian.sleepdoctor.improve.doctor.activity.ScanDoctorQrCodeActivity;
import com.sumian.sleepdoctor.tab.adapter.GroupAdapter;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;
import com.sumian.sleepdoctor.tab.bean.GroupItem;
import com.sumian.sleepdoctor.tab.contract.GroupContract;
import com.sumian.sleepdoctor.tab.presenter.GroupPresenter;
import com.sumian.sleepdoctor.widget.GroupErrorView;
import com.sumian.sleepdoctor.widget.RequestScanQrCodeView;
import com.sumian.sleepdoctor.widget.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

public class GroupFragment extends BaseFragment<GroupPresenter> implements HomeDelegate, RequestScanQrCodeView.OnGrantedCallback,
        GroupContract.View, TitleBar.OnMoreListener, SwipeRefreshLayout.OnRefreshListener, ChatEngine.OnMsgCallback, ChatEngine.OnUpdateUnReadMsgCountCallback {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.refresh)
    SumianRefreshLayout mRefresh;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.group_error_view)
    GroupErrorView mGroupErrorView;

    @BindView(R.id.request_scan_qr_code_view)
    RequestScanQrCodeView mRequestScanQrCodeView;

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
        AppManager.getChatEngine().addOnMsgCallback(this);
        AppManager.getChatEngine().addOnUnReadMsgCountCallback(this);
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
        AppManager.getChatEngine().removeOnUnReadMsgCountCallback(this);
        super.onRelease();
    }

    @Override
    public void setPresenter(GroupContract.Presenter presenter) {
        this.mPresenter = (GroupPresenter) presenter;
    }

    @Override
    public void onBegin() {
        mRefresh.showRefreshAnim();
        mGroupErrorView.showRequest();
    }

    @Override
    public void onFinish() {
        mRefresh.hideRefreshAnim();
    }

    @Override
    public void onFailure(String error) {
        showToast(error);
    }

    @Override
    public void onGrantedSuccess() {
        ScanDoctorQrCodeActivity.show(getContext(), ScanDoctorQrCodeActivity.class);
        // commitReplace(ScanDoctorQrCodeActivity.class);
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
            AVIMConversation avimConversation;

            for (GroupDetail<UserProfile, UserProfile> group : groups) {
                groupItem = new GroupItem();
                groupItem.groupDetail = group;
                avimConversation = AppManager.getChatEngine().getAVIMConversation(group.conversation_id);
                avimConversation.queryMessages(2, new AVIMMessagesQueryCallback() {
                    @Override
                    public void done(List<AVIMMessage> list, AVIMException e) {
                        if (list == null || list.isEmpty()) {
                            mGroupAdapter.updateLastMsg(null);
                            mGroupAdapter.updateSecondMsg(null);
                        } else {
                            if (list.size() == 2) {
                                mGroupAdapter.updateLastMsg((AVIMTypedMessage) list.get(1));
                                mGroupAdapter.updateSecondMsg((AVIMTypedMessage) list.get(0));
                            } else {
                                mGroupAdapter.updateLastMsg((AVIMTypedMessage) list.get(0));
                                mGroupAdapter.updateSecondMsg(null);
                            }
                        }
                    }
                });
                //第一次初始化
                groupItem.unReadMsgCount = avimConversation.getUnreadMessagesCount();
                groupItem.isMsgMentioned = avimConversation.unreadMessagesMentioned();

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
    public void onReceiverMsgCallback(AVIMTypedMessage msg) {
        int position = mGroupAdapter.updateReceiveMsg(msg);
        mRecycler.scrollToPosition(position);
    }

    @Override
    public void onUpdateUnReadMsgCount(AVIMClient client, AVIMConversation conversation, int unReadMsgCount) {
        mGroupAdapter.updateItemForUnReadMsgCount(conversation, unReadMsgCount);
    }
}
