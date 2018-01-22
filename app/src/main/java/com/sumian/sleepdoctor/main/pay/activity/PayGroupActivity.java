package com.sumian.sleepdoctor.main.pay.activity;

import android.view.View;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.widget.TitleBar;
import com.sumian.sleepdoctor.widget.pay.PayCalculateItemView;
import com.sumian.sleepdoctor.widget.pay.PayItemGroupView;

import net.qiujuer.genius.ui.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sm
 * on 2018/1/22.
 * desc:
 */

public class PayGroupActivity extends BaseActivity implements View.OnClickListener, PayItemGroupView.OnSelectPayWayListener, TitleBar.OnBackListener {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.iv_group_icon)
    CircleImageView mIvGroupIcon;

    @BindView(R.id.tv_desc)
    TextView mTvDesc;

    @BindView(R.id.pay_calculate_item_view)
    PayCalculateItemView mPayCalculateItemView;

    @BindView(R.id.tv_group_money)
    TextView mTvGroupMoney;

    @BindView(R.id.pay_group_view)
    PayItemGroupView mPayGroupView;

    @BindView(R.id.bt_pay)
    Button mBtPay;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_join_two;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleBar.addOnBackListener(this);
        mPayGroupView.setOnSelectPayWayListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @OnClick({R.id.bt_pay})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_pay:

                break;
            default:
                break;
        }
    }

    @Override
    public void onSelectWechatPayWay() {

    }

    @Override
    public void onSelectAlipayWay() {

    }

    @Override
    public void onBack(View v) {

    }

}
