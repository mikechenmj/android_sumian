package com.sumian.sd.record;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sumian.sd.R;
import com.sumian.sd.doctor.bean.DoctorService;
import com.sumian.sd.widget.doctor.DoctorServiceItemView;

import java.util.List;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/5 9:36
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ServiceListAdapter extends BaseQuickAdapter<DoctorService, BaseViewHolder> {

    private OnServiceItemClickListener mOnServiceItemClickListener;

    ServiceListAdapter(@Nullable List<DoctorService> data) {
        super(R.layout.item_doctor_service, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DoctorService item) {
        DoctorServiceItemView doctorServiceItemView = helper.getView(R.id.doctor_service_item_view);
        doctorServiceItemView.setTitle(item.getName());
        doctorServiceItemView.setDesc(item.getNot_buy_description());
        doctorServiceItemView.setOnClickListener(v -> {
            if (mOnServiceItemClickListener != null) {
                mOnServiceItemClickListener.onServiceItemClick(item.getType());
            }
        });
    }

    public void setOnServiceItemClickListener(OnServiceItemClickListener onServiceItemClickListener) {
        mOnServiceItemClickListener = onServiceItemClickListener;
    }

    public interface OnServiceItemClickListener {
        void onServiceItemClick(int serviceType);
    }
}
