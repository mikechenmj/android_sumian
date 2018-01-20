package com.sumian.sleepdoctor.main.tab.group.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.sumian.common.base.BaseRecyclerAdapter;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.main.tab.group.bean.GroupDetail;

/**
 * Created by jzz
 * on 2018/1/20.
 * desc:
 */

public class GroupAdapter extends BaseRecyclerAdapter<GroupDetail<UserProfile, UserProfile>> {

    public GroupAdapter(Context context) {
        super(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return null;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, GroupDetail<UserProfile, UserProfile> item, int position) {

    }
}
