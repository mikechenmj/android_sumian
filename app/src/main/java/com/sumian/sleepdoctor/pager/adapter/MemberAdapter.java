package com.sumian.sleepdoctor.pager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.sumian.common.base.BaseRecyclerAdapter;
import com.sumian.sleepdoctor.account.bean.UserProfile;

/**
 * Created by sm
 * on 2018/2/14.
 * desc:
 */

public class MemberAdapter extends BaseRecyclerAdapter<UserProfile> {


    public MemberAdapter(Context context) {
        super(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return null;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, UserProfile item, int position) {

    }

}
