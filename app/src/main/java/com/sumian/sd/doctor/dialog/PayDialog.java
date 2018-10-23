package com.sumian.sd.doctor.dialog;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.sumian.sd.R;
import com.sumian.sd.doctor.contract.PayContract;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sm
 * on 2018/1/28.
 * desc:
 */

public class PayDialog extends QMUIDialog implements View.OnClickListener {

    public static final int PAY_INVALID = 0;
    public static final int PAY_SUCCESS = 1;
    public static final int PAY_FAILED = 2;
    public static final int PAY_CANCELED = 3;

    @BindView(R.id.iv_pay_status)
    ImageView mIvPayStatus;
    @BindView(R.id.tv_pay_desc)
    TextView mTvPayDesc;
    @BindView(R.id.bt_join)
    Button mBtJoin;

    private WeakReference<PayContract.Presenter> mPresenterWeakReference;
    private int mPayStatus;
    private Listener mListener;

    public PayDialog(Context context, Listener listener) {
        super(context, R.style.QMUI_Dialog);
        mListener = listener;
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
                mBtJoin.setText(R.string.complete);
                setCancelable(false);
                break;
            case PAY_FAILED:
                mIvPayStatus.setImageResource(R.mipmap.ic_msg_icon_fail);
                mTvPayDesc.setText(R.string.pay_failed);
                mBtJoin.setText(R.string.re_pay);
                setCancelable(true);
                break;
            case PAY_INVALID:
                mIvPayStatus.setImageResource(R.mipmap.ic_msg_icon_abnormal);
                mTvPayDesc.setText(R.string.pay_invalid);
                mBtJoin.setText(R.string.back);
                setCancelable(true);
                break;
            case PAY_CANCELED:
                mIvPayStatus.setImageResource(R.mipmap.ic_msg_icon_fail);
                mTvPayDesc.setText(R.string.pay_cancel);
                mBtJoin.setText(R.string.repay);
                setCancelable(true);
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
                    //presenter.doPay(Objects.requireNonNull(getOwnerActivity()));
                    cancel();
                } else if (mPayStatus == PAY_INVALID) {
                    cancel();
                } else if (mPayStatus == PAY_CANCELED) {
//                    if (mListener != null) {
//                        mListener.onRepayClick();
//                    }
                    cancel();
                }
                break;
            default:
                break;
        }
    }

    public interface Listener {
        void onRepayClick();
    }

}
