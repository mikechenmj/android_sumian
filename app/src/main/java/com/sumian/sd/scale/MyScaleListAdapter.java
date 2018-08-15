package com.sumian.sd.scale;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sumian.sd.R;
import com.sumian.sd.scale.bean.Scale;
import com.sumian.sd.utils.TimeUtil;

import java.util.List;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/7 11:20
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class MyScaleListAdapter extends BaseQuickAdapter<Scale, BaseViewHolder> {
    MyScaleListAdapter(@Nullable List<Scale> data) {
        super(R.layout.item_scale_of_mine, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Scale item) {
        Scale.ScaleDetail scale = item.getScale();
        Scale.ResultBean result = item.getResult();
        helper.setText(R.id.tv_title, scale.getTitle());
        helper.setText(R.id.tv_content, TimeUtil.formatDate(mContext.getString(R.string.scale_fill_date), result.getCreateAtInMillis()));
        helper.setText(R.id.tv_score, result.getScore() + mContext.getResources().getString(R.string.score));
        String resultString = result.getResult();
        helper.setText(R.id.tv_result, !TextUtils.isEmpty(resultString) ? resultString : mContext.getString(R.string.already_finish));
    }
}
