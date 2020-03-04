package com.sumian.sd.common.pay.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.common.image.ImageLoader
import com.sumian.common.statistic.StatUtil
import com.sumian.sd.R
import com.sumian.sd.buz.doctor.bean.DoctorService
import com.sumian.sd.buz.doctor.bean.DoctorServicePackage
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.pay.bean.PayCouponCode
import com.sumian.sd.common.pay.bean.PayOrder
import com.sumian.sd.common.pay.dialog.PayDialog
import com.sumian.sd.common.pay.presenter.PayPresenter
import com.sumian.sd.common.pay.widget.PayCalculateItemView
import com.sumian.sd.common.pay.widget.PayItemGroupView
import com.sumian.sd.widget.TitleBar
import kotlinx.android.synthetic.main.activity_main_shopping_car.*

/**
 * @author sm
 * on 2018/1/22.
 * desc:
 */

class PaymentActivity : BaseViewModelActivity<PayPresenter>(), View.OnClickListener, PayItemGroupView.OnSelectPayWayListener, TitleBar.OnBackClickListener, PayCalculateItemView.OnMoneyChangeCallback {

    companion object {

        private val TAG = PaymentActivity::class.java.simpleName

        private const val WECHAT_PAY_TYPE = "wx"
        private const val ALIPAY_PAY_TYPE = "alipay"

        private const val ARGS_DOCTOR_SERVICE = "com.sumian.app.extra.doctor.service"
        private const val ARGS_DOCTOR_SERVICE_PACKAGE_ID = "com.sumian.app.extra.doctor.service.packageId"

        const val EXTRA_ERROR_REASON = "error_reason"

        @JvmStatic
        fun startForResult(activity: Activity, doctorService: DoctorService, packageId: Int, requestCode: Int) {
            val extras = Bundle()
            extras.putParcelable(ARGS_DOCTOR_SERVICE, doctorService)
            val intent = Intent(activity, PaymentActivity::class.java).apply {
                putExtras(extras)
                putExtra(ARGS_DOCTOR_SERVICE_PACKAGE_ID, packageId)
            }
            activity.startActivityForResult(intent, requestCode)
        }

        @JvmStatic
        fun startForResult(fragment: Fragment, doctorService: DoctorService, packageId: Int, requestCode: Int) {
            val extras = Bundle()
            extras.putParcelable(ARGS_DOCTOR_SERVICE, doctorService)
            val intent = Intent(fragment.activity, PaymentActivity::class.java).apply {
                putExtras(extras)
                putExtra(ARGS_DOCTOR_SERVICE_PACKAGE_ID, packageId)
            }
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    private var mPayChannel = WECHAT_PAY_TYPE

//    private val mActionLoadingDialog: ActionLoadingDialogV2  by lazy {
//        ActionLoadingDialogV2(this)
//    }

    private val mPayDialog: PayDialog by lazy {
        val payDialog = PayDialog(this, object : PayDialog.Listener {
            override fun onRepayClick() {
                return pay()
            }
        }).bindContentView(R.layout.dialog_pay)
        payDialog.ownerActivity = this@PaymentActivity
        payDialog.bindPresenter(mViewModel!!)
        return@lazy payDialog
    }
    private var mDoctorService: DoctorService? = null
    private var mServicePackage: DoctorServicePackage? = null
    private var mPackage: DoctorServicePackage.ServicePackage? = null
    private var mErrorReason: String = ""

    override fun initBundle(bundle: Bundle) {
        bundle.let {
            this.mDoctorService = bundle.getParcelable(ARGS_DOCTOR_SERVICE)
            val packageId = bundle.getInt(ARGS_DOCTOR_SERVICE_PACKAGE_ID)
            for (servicePackage in mDoctorService!!.service_packages) {
                if (servicePackage.id == packageId) {
                    this.mServicePackage = servicePackage
                    this.mPackage = mServicePackage!!.packages[0]
                    break
                }
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_shopping_car
    }

    override fun initWidget() {
        super.initWidget()
        PayPresenter.init(this)
        nested_scroll_view.setOnClickListener {
            pay_calculate_item_view.closeKeyBoard()
        }
        title_bar.setOnBackClickListener(this)
        bt_pay.setOnClickListener(this)
        pay_group_view.setOnSelectPayWayListener(this)
        pay_calculate_item_view.setOnMoneyChangeCallback(this)
        StatUtil.event(StatConstants.page_pay)
    }

    override fun getPageName(): String {
        return StatConstants.page_pay
    }

    override fun initData() {
        super.initData()
        mDoctorService?.let {
            ImageLoader.loadImage(mDoctorService!!.icon, lay_group_icon)
            tv_name.text = mDoctorService?.name
            tv_desc.text = mServicePackage?.name
            pay_calculate_item_view.defaultMoney = mPackage?.unit_price!!
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mViewModel?.onPayActivityResultDelegate(requestCode, resultCode, data)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bt_pay -> {
                pay()
                StatUtil.event(StatConstants.click_pay)
            }
            else -> {
            }
        }
    }

    override fun onSelectWechatPayWay() {
        this.mPayChannel = WECHAT_PAY_TYPE
    }

    override fun onSelectAlipayWay() {
        this.mPayChannel = ALIPAY_PAY_TYPE
    }

    override fun onBack(v: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        dismissLoading()
        cancelPayDialog()
        setResult(Activity.RESULT_CANCELED, Intent().apply { putExtra(EXTRA_ERROR_REASON, mErrorReason) })
        finish()
    }

    override fun onMoneyChange(money: Long) {
        bt_pay.isEnabled = money > 0.00f
        bt_pay.alpha = if (money > 0.00f) 1.00f else 0.50f
    }

    override fun onCheckCouponCode(couponCode: String) {
        Log.e(TAG, "onCheckCouponCode: -------->")
        checkCouponCode()
    }

    fun setPresenter(presenter: PayPresenter) {
        this.mViewModel = presenter
    }

    fun onFailure(error: String) {
        ToastUtils.showShort(error)
        dismissLoading()
    }

    fun onCreatePayOrderSuccess() {
        mViewModel?.doPay(this)
        ToastUtils.showShort(R.string.create_order_success)
    }

    fun onOrderPaySuccess(payMsg: String) {
        StatUtil.event(StatConstants.e_pay_success,
                mapOf(
                        "amount" to pay_calculate_item_view.currentMoney.toString(),
                        "quantity" to pay_calculate_item_view.currentBuyCount.toString(),
                        "channel" to if (mPayChannel == ALIPAY_PAY_TYPE) "支付宝" else "微信",
                        "productName" to mDoctorService!!.name
                ))
        if (!mPayDialog.isShowing) {
            mPayDialog.setPayStatus(PayDialog.PAY_SUCCESS).show()
        }
    }

    fun onOrderPayFailed(payMsg: String) {
        if (!mPayDialog.isShowing) {
            mPayDialog.setPayStatus(PayDialog.PAY_FAILED).show()
        }
        mErrorReason = payMsg
    }

    fun onOrderPayInvalid(payMsg: String) {
        if (!mPayDialog.isShowing) {
            mPayDialog.setPayStatus(PayDialog.PAY_INVALID).show()
        }
        mErrorReason = payMsg
    }

    fun onOrderPayCancel(payMsg: String) {
        if (!mPayDialog.isShowing) {
            mPayDialog.setPayStatus(PayDialog.PAY_CANCELED).show()
        }
        dismissLoading()
        mErrorReason = payMsg
    }

    fun onCheckOrderPayIsOk() {
        cancelPayDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    fun onCheckOrderPayIsInvalid(invalidError: String) {
        ToastUtils.showShort(invalidError)
        if (!mPayDialog.isShowing) {
            mPayDialog.setPayStatus(PayDialog.PAY_INVALID).show()
        } else {
            mPayDialog.setPayStatus(PayDialog.PAY_INVALID)
        }
    }

    fun onCheckOrderPayFinialIsInvalid(invalidError: String, errorReason: String?) {
        ToastUtils.showShort(invalidError)
        cancelPayDialog()
        if (errorReason != null) {
            mErrorReason = errorReason
        }
        setResult(Activity.RESULT_CANCELED, Intent().apply { putExtra(EXTRA_ERROR_REASON, mErrorReason) })
        finish()
    }

    fun onCheckCouponCodeSuccess(payCouponCode: PayCouponCode?) {
        pay_calculate_item_view.updateCouponCode(payCouponCode, null)
    }

    fun onCheckCouponCodeFailed(error: String, code: Int) {
        ToastUtils.showShort(error)
        pay_calculate_item_view.updateCouponCode(null, error)
    }

    override fun showLoading() {
        super.showLoading()
        bt_pay.isEnabled = false
    }

    override fun dismissLoading() {
        super.dismissLoading()
        bt_pay.isEnabled = true
    }

    private fun pay() {
        Log.e(TAG, "go pay: -------->")
        val payOrder = PayOrder(pay_calculate_item_view.getValidCode(), null, null, null, pay_calculate_item_view.currentMoney, mPayChannel, "cny", mDoctorService!!.name, mDoctorService!!.description, null, mPackage!!.id, pay_calculate_item_view.currentBuyCount)
        mViewModel?.createPayOrder(this, payOrder)
    }

    private fun checkCouponCode() {
        val payCouponCodeText = pay_calculate_item_view.getCouponCode()
        mViewModel?.checkCouponCode(payCouponCodeText!!, mPackage!!.id)
    }

    private fun cancelPayDialog() {
        if (mPayDialog.isShowing) {
            mPayDialog.cancel()
        }
    }
}
