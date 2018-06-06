package com.sumian.sleepdoctor.notification;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.notification.bean.Notification;
import com.sumian.sleepdoctor.utils.TimeUtil;

import java.util.List;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/5 14:09
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class NotificationListAdapter extends BaseQuickAdapter<Notification, BaseViewHolder> {

    private final Context mContext;

    NotificationListAdapter(Context context, @Nullable List<Notification> data) {
        super(R.layout.item_notification, data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, Notification item) {
        Notification.DataBean data = item.getData();
        boolean isUnRead = item.getReadAtInMillis() == 0;
        helper.getView(R.id.v_red_dot).setVisibility(isUnRead ? View.VISIBLE : View.GONE);
        helper.setTextColor(R.id.tv_title, mContext.getResources().getColor(isUnRead ? R.color.t3_color : R.color.t1_color));
        helper.setText(R.id.tv_title, data.getTitle());
        helper.setText(R.id.tv_content, data.getContent());
        long time = item.getCreateAtInMillis();
        helper.setText(R.id.tv_time, TimeUtil.formatDate("yyyy/MM/dd hh:mm", time));
    }
}
