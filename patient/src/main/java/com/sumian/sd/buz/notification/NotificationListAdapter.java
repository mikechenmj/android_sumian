package com.sumian.sd.buz.notification;

import android.content.Context;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sumian.common.utils.TimeUtilV2;
import com.sumian.sd.R;
import com.sumian.sd.buz.notification.bean.Notification;
import com.sumian.sd.buz.notification.bean.NotificationData;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * <pre>
 *     @author : Zhan Xuzhao
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
        NotificationData data = item.getData();
        boolean isUnRead = item.getReadAt() == 0;
        helper.getView(R.id.v_red_dot).setVisibility(isUnRead ? View.VISIBLE : View.GONE);
        helper.setTextColor(R.id.tv_title, mContext.getResources().getColor(isUnRead ? R.color.t3_color : R.color.t1_color));
        helper.setText(R.id.tv_title, data.getTitle());
        helper.setText(R.id.tv_content, data.getContent());
        long time = item.getCreatedAt() * 1000L;
        helper.setText(R.id.tv_time, TimeUtilV2.Companion.formatYYYYMMDDHHMM(time));
    }
}
