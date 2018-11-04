package com.sumian.sd.pay.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.sumian.common.image.ImageLoader
import com.sumian.sd.R
import com.sumian.sd.base.SdBaseActivity
import com.sumian.sd.doctor.bean.DoctorService
import com.sumian.sd.doctor.bean.DoctorServicePackage
import com.sumian.sd.pay.bean.PayOrder
import com.sumian.sd.pay.contract.PayContract
import com.sumian.sd.pay.dialog.PayDialog
import com.sumian.sd.pay.pay.PayCalculateItemView
import com.sumian.sd.pay.pay.PayItemGroupView
import com.sumian.sd.pay.presenter.PayPresenter
import com.sumian.sd.widget.TitleBar
import com.sumian.sd.widget.dialog.ActionLoadingDialog
import kotlinx.android.synthetic.main.activity_main_shopping_car.*

/**
 * @author sm
 * on 2018/1/22.
 * desc:
 */

class PaymentActivity : SdBaseActivity<PayContract.Presenter>(), View.OnClickListener, PayItemGroupView.OnSelectPayWayListener, TitleBar.OnBackClickListener, PayCalculateItemView.OnMoneyChangeCallback, PayContract.View {

    companion object {

        private const val WECHAT_PAY_TYPE = "wx"
        private const val ALIPAY_PAY_TYPE = "alipay"

        private const val ARGS_DOCTOR_SERVICE = "com.sumian.app.extra.doctor.service"
        private const val ARGS_DOCTOR_SERVICE_PACKAGE_ID = "com.sumian.app.extra.doctor.service.packageId"

        fun startForResult(activity: Activity, doctorService: DoctorService, packageId: Int, requestCode: Int) {
            val extras = Bundle()
            extras.putParcelable(ARGS_DOCTOR_SERVICE, doctorService)
            val intent = Intent(activity, PaymentActivity::class.java).apply {
                putExtras(extras)
                putExtra(ARGS_DOCTOR_SERVICE_PACKAGE_ID, packageId)
            }
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private var mPayChannel = WECHAT_PAY_TYPE

    private val mActionLoadingDialog: ActionLoadingDialog  by lazy {
        ActionLoadingDialog()
    }

    private val mPayDialog: PayDialog  by lazy {
        val payDialog = PayDialog(this, object : PayDialog.Listener {
            override fun onRepayClick() {
                return pay()
            }
        }).bindContentView(R.layout.dialog_pay)
        payDialog.ownerActivity = this@PaymentActivity
        payDialog.bindPresenter(mPresenter)
        return@lazy payDialog
    }

    private var mDoctorService: DoctorService? = null

    private var mServicePackage: DoctorServicePackage? = null

    private var mPackage: DoctorServicePackage.ServicePackage? = null

    override fun initBundle(bundle: Bundle?): Boolean {
        if (bundle != null) {
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
        return super.initBundle(bundle)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_shopping_car
    }

    override fun initWidget(root: View) {
        super.initWidget(root)
        title_bar.setOnBackClickListener(this)
        bt_pay.setOnClickListener(this)
        pay_group_view.setOnSelectPayWayListener(this)
        pay_calculate_item_view.setOnMoneyChangeCallback(this)
    }

    override fun initData() {
        super.initData()
        ImageLoader.loadImage(mDoctorService!!.icon, lay_group_icon)
        tv_name.text = mDoctorService!!.name
        tv_desc.text = mServicePackage!!.name

        pay_calculate_item_view.defaultMoney = mPackage!!.unit_price
    }

    override fun initPresenter() {
        super.initPresenter()
        PayPresenter.init(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        mPresenter.onPayActivityResultDelegate(requestCode, resultCode, data)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bt_pay -> pay()
            else -> {
            }
        }
    }

    private fun pay() {
        val payOrder = PayOrder(pay_calculate_item_view.currentMoney, mPayChannel, "cny", mDoctorService!!.name, mDoctorService!!.description, mPackage!!.id, pay_calculate_item_view.currentBuyCount)
        mPresenter.createPayOrder(this, payOrder)
    }

    override fun onSelectWechatPayWay() {
        this.mPayChannel = WECHAT_PAY_TYPE
    }

    override fun onSelectAlipayWay() {
        this.mPayChannel = ALIPAY_PAY_TYPE
    }

    override fun onBack(v: View) {
        onBack()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onBack()
    }

    private fun onBack() {
        mActionLoadingDialog.dismissAllowingStateLoss()
        cancelDialog()
        finish()
    }

    override fun onMoneyChange(money: Double) {
        bt_pay.isEnabled = money > 0.00f
        bt_pay.alpha = if (money > 0.00f) 1.00f else 0.50f
    }

    override fun setPresenter(presenter: PayContract.Presenter) {
        this.mPresenter = presenter
    }

    override fun onFailure(error: String) {
        showCenterToast(error)
        mActionLoadingDialog.dismissAllowingStateLoss()
    }

    override fun onBegin() {
        mActionLoadingDialog.show(supportFragmentManager)
    }

    override fun onFinish() {
        mActionLoadingDialog.dismissAllowingStateLoss()
    }

    override fun onCreatePayOrderSuccess() {
        mPresenter.doPay(this)
        showCenterToast(R.string.create_order_success)
    }

    override fun onOrderPaySuccess(payMsg: String) {
        if (!mPayDialog.isShowing) {
            mPayDialog.setPayStatus(PayDialog.PAY_SUCCESS).show()
        }
    }

    override fun onOrderPayFailed(payMsg: String) {
        //showCenterToast(payMsg);
        if (!mPayDialog.isShowing) {
            mPayDialog.setPayStatus(PayDialog.PAY_FAILED).show()
        }
    }

    override fun onOrderPayInvalid(payMsg: String) {
        //showCenterToast(payMsg);
        if (!mPayDialog.isShowing) {
            mPayDialog.setPayStatus(PayDialog.PAY_INVALID).show()
        }
    }

    override fun onOrderPayCancel(payMsg: String) {
        if (!mPayDialog.isShowing) {
            mPayDialog.setPayStatus(PayDialog.PAY_CANCELED).show()
        }
        mActionLoadingDialog.dismissAllowingStateLoss()
    }

    override fun onCheckOrderPayIsOk() {
        cancelDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun cancelDialog() {
        if (mPayDialog.isShowing) {
            mPayDialog.cancel()
        }
    }

    override fun onCheckOrderPayIsInvalid(invalidError: String) {
        showCenterToast(invalidError)
        if (!mPayDialog.isShowing) {
            mPayDialog.setPayStatus(PayDialog.PAY_INVALID).show()
        } else {
            mPayDialog.setPayStatus(PayDialog.PAY_INVALID)
        }
    }

    override fun onCheckOrderPayFinialIsInvalid(invalidError: String) {
        showCenterToast(invalidError)
        cancelDialog()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
