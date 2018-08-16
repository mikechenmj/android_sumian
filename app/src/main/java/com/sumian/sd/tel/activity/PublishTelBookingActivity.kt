package com.sumian.sd.tel.activity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseBackPresenterActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.R
import com.sumian.sd.tel.bean.TelBooking
import com.sumian.sd.tel.contract.PublishTelBookingContract
import com.sumian.sd.tel.presenter.PublishTelBookingPresenter
import kotlinx.android.synthetic.main.activity_main_publish_tel_booking.*

/**
 * Created by sm
 *
 * on 2018/8/13
 *
 * desc:电话预约服务
 *
 */
class PublishTelBookingActivity : BaseBackPresenterActivity<PublishTelBookingContract.Presenter>(), View.OnClickListener, PublishTelBookingContract.View {

    companion object {

        private const val EXTRA_SERVICE_PACKAGE = "com.sumian.sd.extra.service.package"

        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, PublishTelBookingActivity::class.java))
            }
        }

        @JvmStatic
        fun show(servicePackage: TelBooking.ServicePackage) {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, PublishTelBookingActivity::class.java).apply {
                    putExtra(EXTRA_SERVICE_PACKAGE, servicePackage)
                })
            }
        }

    }

    private var mServicePackage: TelBooking.ServicePackage? = null

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.mServicePackage = bundle.getParcelable(EXTRA_SERVICE_PACKAGE)
    }

    override fun getChildContentId(): Int {
        return R.layout.activity_main_publish_tel_booking
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mPresenter = PublishTelBookingPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.tel_ask_detail)
        sdv_make_date.setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
        if (mServicePackage == null) {
            this.mPresenter?.getLatestTelBookingOrder()
        } else {
            //sdv_duration.setContent(mServicePackage.get)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sdv_make_date -> {

            }
            R.id.bt_submit -> {

            }
        }
    }

    override fun onGetLatestTelBookingOrderSuccess(latestTelBooking: TelBooking) {
        sdv_duration.setContent(latestTelBooking.p_package.servicePackage.formatServiceLengthType())
    }

    override fun onGetLatestTelBookingOrderFailed(error: String) {
        onPublishTelBookingOrderFailed(error)
    }

    override fun onPublishTelBookingOrderSuccess(telBooking: TelBooking) {

    }

    override fun onPublishTelBookingOrderFailed(error: String) {
        ToastHelper.show(this, error, Gravity.CENTER)
    }


}