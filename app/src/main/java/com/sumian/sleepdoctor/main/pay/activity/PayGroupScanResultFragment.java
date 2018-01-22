package com.sumian.sleepdoctor.main.pay.activity;

import android.view.View;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseFragment;

import net.qiujuer.genius.ui.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sm
 * on 2018/1/22.
 * desc:
 */

public class PayGroupScanResultFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.iv_group_icon)
    CircleImageView mIvGroupIcon;
    @BindView(R.id.tv_desc)
    TextView mTvDesc;
    @BindView(R.id.tv_doctor_name)
    TextView mTvDoctorName;

    @BindView(R.id.tv_group_desc)
    TextView mTvGroupDesc;

    @BindView(R.id.tv_money)
    TextView mTvMoney;

    @BindView(R.id.bt_join)
    Button mBtJoin;
    @BindView(R.id.bt_re_scan)
    Button mBtReScan;


    public static PayGroupScanResultFragment newInstance() {
        return new PayGroupScanResultFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_join_one;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @OnClick({R.id.bt_join, R.id.bt_re_scan})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_join:
                break;
            case R.id.bt_re_scan:
                break;
            default:
                break;
        }
    }
}
