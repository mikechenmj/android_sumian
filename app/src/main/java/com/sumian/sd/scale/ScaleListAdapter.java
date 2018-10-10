package com.sumian.sd.scale;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sumian.sd.R;
import com.sumian.sd.scale.bean.Scale;
import com.sumian.sd.utils.ResourceUtilKt;
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
public class ScaleListAdapter extends BaseQuickAdapter<Scale, BaseViewHolder> {
    ScaleListAdapter(@Nullable List<Scale> data) {
        super(R.layout.item_scale, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Scale item) {
        Scale.ScaleDetail scale = item.getScale();
        Scale.DoctorBean doctor = item.getDoctor();
        Scale.ResultBean result = item.getResult();
        TextView tvDoctor = helper.getView(R.id.tv_doctor);
        helper.setText(R.id.tv_title, scale.getTitle());
        String contentText;
        if (result != null) {
            contentText = TimeUtil.formatDate("填写日期：yyyy.MM.dd", result.getCreateAtInMillis());
        } else {
            contentText = ResourceUtilKt.getString(R.string.scale_scale_not_finish_yet_hint);
        }
        helper.setText(R.id.tv_content, contentText);
        tvDoctor.setVisibility(doctor != null ? View.VISIBLE : View.GONE);
        if (doctor != null) {
            tvDoctor.setText(doctor.getName());
        }
    }
}
