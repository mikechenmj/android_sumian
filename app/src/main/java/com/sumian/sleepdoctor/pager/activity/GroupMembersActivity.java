package com.sumian.sleepdoctor.pager.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.pager.adapter.MemberAdapter;
import com.sumian.sleepdoctor.widget.TitleBar;

import java.util.List;

import butterknife.BindView;

/**
 * Created by sm
 * on 2018/2/2.
 * desc:
 */

public class GroupMembersActivity extends BaseActivity implements TitleBar.OnBackListener {

    public static final String ARGS_MEMBERS = "group_members";

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private List<UserProfile> mMembers;

    private MemberAdapter mMemberAdapter;


    @Override
    protected boolean initBundle(Bundle bundle) {
        this.mMembers = bundle.getParcelableArrayList(ARGS_MEMBERS);
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_group_members;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecycler.setLayoutManager(new LinearLayoutManager(root.getContext()));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setAdapter(mMemberAdapter = new MemberAdapter(this));
        mTitleBar.addOnBackListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public void onBack(View v) {
        finish();
    }


}
