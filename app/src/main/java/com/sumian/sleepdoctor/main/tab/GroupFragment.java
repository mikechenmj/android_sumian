package com.sumian.sleepdoctor.main.tab;

import android.view.View;
import android.widget.ImageView;

import com.sumian.common.qr.QrCodeActivity;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseFragment;


import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

public class GroupFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.ib_scan)
    ImageView mIbScan;

    public static GroupFragment newInstance() {
        return new GroupFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_group;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        //mIbScan.setChangeAlphaWhenPress(true);
        //mIbScan.setChangeAlphaWhenDisable(true);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @OnClick(R.id.ib_scan)
    @Override
    public void onClick(View v) {
        QrCodeActivity.show(v.getContext());
    }
}
