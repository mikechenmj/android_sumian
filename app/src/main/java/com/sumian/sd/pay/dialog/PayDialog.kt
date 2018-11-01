package com.sumian.sd.pay.dialog

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.sumian.sd.R
import com.sumian.sd.pay.contract.PayContract
import java.lang.ref.WeakReference

/**
 * Created by sm
 * on 2018/1/28.
 * desc:
 */

class PayDialog(context: Context, private val listener: Listener) : QMUIDialog(context, R.style.QMUI_Dialog), View.OnClickListener {

    private val mIvPayStatus: ImageView  by lazy {
        findViewById<ImageView>(R.id.iv_pay_status)
    }

    private val mTvPayDesc: TextView  by lazy {
        findViewById<TextView>(R.id.tv_pay_desc)

    }
    private val mBtJoin: Button by lazy {
        val btJoin = findViewById<Button>(R.id.bt_join)
        btJoin.setOnClickListener(this@PayDialog)
        btJoin
    }

    private var mPresenterWeakReference: WeakReference<PayContract.Presenter>? = null
    private var mPayStatus: Int = 0

    fun bindPresenter(presenter: PayContract.Presenter) {
        this.mPresenterWeakReference = WeakReference(presenter)
    }

    fun setPayStatus(payStatus: Int): PayDialog {
        this.mPayStatus = payStatus
        when (payStatus) {
            PAY_SUCCESS -> {
                mIvPayStatus.setImageResource(R.mipmap.ic_msg_icon_success)
                mTvPayDesc.setText(R.string.pay_success)
                mBtJoin.setText(R.string.complete)
                setCancelable(false)
            }
            PAY_FAILED -> {
                mIvPayStatus.setImageResource(R.mipmap.ic_msg_icon_fail)
                mTvPayDesc.setText(R.string.pay_failed)
                mBtJoin.setText(R.string.re_pay)
                setCancelable(true)
            }
            PAY_INVALID -> {
                mIvPayStatus.setImageResource(R.mipmap.ic_msg_icon_abnormal)
                mTvPayDesc.setText(R.string.pay_invalid)
                mBtJoin.setText(R.string.back)
                setCancelable(true)
            }
            PAY_CANCELED -> {
                mIvPayStatus.setImageResource(R.mipmap.ic_msg_icon_fail)
                mTvPayDesc.setText(R.string.pay_cancel)
                mBtJoin.setText(R.string.repay)
                setCancelable(true)
            }
            else -> {
            }
        }
        return this
    }

    fun bindContentView(@LayoutRes id: Int): PayDialog {
        setContentView(id)
        return this
    }

    override fun onClick(v: View) {
        val presenterWeakReference = this.mPresenterWeakReference
        val presenter = presenterWeakReference!!.get() ?: return
        when (v.id) {
            R.id.bt_join -> when (mPayStatus) {
                PAY_SUCCESS -> presenter.checkPayOrder()
                PAY_FAILED -> //presenter.doPay(Objects.requireNonNull(getOwnerActivity()));
                    cancel()
                PAY_INVALID -> cancel()
                PAY_CANCELED -> //                    if (listener != null) {
                    //                        listener.onRepayClick();
                    //                    }
                    cancel()
            }
            else -> {
            }
        }
    }

    interface Listener {
        fun onRepayClick()
    }

    companion object {

        const val PAY_INVALID = 0
        const val PAY_SUCCESS = 1
        const val PAY_FAILED = 2
        const val PAY_CANCELED = 3
    }

}
