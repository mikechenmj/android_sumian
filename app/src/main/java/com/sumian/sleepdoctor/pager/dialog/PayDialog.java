package com.sumian.sleepdoctor.pager.dialog;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.improve.doctor.contract.PayContract;

import net.qiujuer.genius.ui.widget.Button;

import java.lang.ref.WeakReference;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sm
 * on 2018/1/28.
 * desc:
 */

public class PayDialog extends QMUIDialog implements View.OnClickListener {

    public static final int PAY_SUCCESS = 0x01;
    public static final int PAY_FAILED = 0x02;
    public static final int PAY_INVALID = 0x0f;

    @BindView(R.id.iv_pay_status)
    ImageView mIvPayStatus;

    @BindView(R.id.tv_pay_desc)
    TextView mTvPayDesc;

    @BindView(R.id.bt_join)
    Button mBtJoin;

    private WeakReference<PayContract.Presenter> mPresenterWeakReference;

    private int mPayStatus;


    public PayDialog(Context context) {
        this(context, R.style.QMUI_Dialog);
    }

    public PayDialog(Context context, int styleRes) {
        super(context, styleRes);
    }

    public void bindPresenter(PayContract.Presenter presenter) {
        this.mPresenterWeakReference = new WeakReference<>(presenter);
    }

    public PayDialog setPayStatus(int payStatus) {
        this.mPayStatus = payStatus;

        switch (payStatus) {
            case PAY_SUCCESS:
                mIvPayStatus.setImageResource(R.mipmap.ic_msg_icon_success);
                mTvPayDesc.setText(R.string.pay_success);
                mBtJoin.setText(R.string.pay_success);
                break;
            case PAY_FAILED:
                mIvPayStatus.setImageResource(R.mipmap.ic_msg_icon_fail);
                mTvPayDesc.setText(R.string.pay_failed);
                mBtJoin.setText(R.string.re_pay);
                break;
            case PAY_INVALID:
                mIvPayStatus.setImageResource(R.mipmap.ic_msg_icon_abnormal);
                mTvPayDesc.setText(R.string.pay_invalid);
                mBtJoin.setText(R.string.back);
                break;
            default:
                break;
        }

        return this;
    }

    public PayDialog bindContentView(@LayoutRes int id) {
        setContentView(id);
        ButterKnife.bind(this);
        return this;
    }

    @OnClick({R.id.bt_join})
    @Override
    public void onClick(View v) {
        WeakReference<PayContract.Presenter> presenterWeakReference = this.mPresenterWeakReference;

        PayContract.Presenter presenter = presenterWeakReference.get();
        if (presenter == null) return;

        switch (v.getId()) {
            case R.id.bt_join:
                if (mPayStatus == PAY_SUCCESS) {
                    presenter.checkPayOrder();
                } else if (mPayStatus == PAY_FAILED) {
                    presenter.doPay(Objects.requireNonNull(getOwnerActivity()));
                } else if (mPayStatus == PAY_INVALID) {
                    cancel();
                }
                break;
            default:
                break;
        }
    }

}
