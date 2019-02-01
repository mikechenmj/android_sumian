package com.sumian.sddoctor.me.myservice

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.CompoundButton.OnCheckedChangeListener
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.mvp.IPresenter
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.JsonUtil
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.base.SddBaseViewModelActivity
import com.sumian.sddoctor.me.authentication.AuthenticationHelper
import com.sumian.sddoctor.me.myservice.bean.Packages
import com.sumian.sddoctor.me.myservice.bean.ServicePackage
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.util.BooleanUtil
import kotlinx.android.synthetic.main.activity_open_service.*
import retrofit2.Call

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/9/29 10:23
 * desc   :
 * version: 1.0
 */
class OpenServiceActivity : SddBaseActivity() {
    private val mServicePackage by lazy {
        JsonUtil.fromJson(intent.getStringExtra(KEY_SERVICE_PACKAGE), ServicePackage::class.java)!!
    }
    private var updateServicePackageCall: Call<Packages>? = null

    companion object {
        private const val REQUEST_CODE_SET_PRICE = 1
        private const val KEY_SERVICE_PACKAGE = "ServicePackage"
        fun launch(context: Context, servicePackage: ServicePackage) {
            val intent = Intent(context, OpenServiceActivity::class.java)
            intent.putExtra(KEY_SERVICE_PACKAGE, JsonUtil.toJson(servicePackage))
            ActivityUtils.startActivity(intent)
        }
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_open_service
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(mServicePackage.name)
        updateUIByPackage(mServicePackage.packages)
        sw_open_service.setOnCheckedChangeListener(mOnCheckChangeListener)
        ll_price.setOnClickListener { SetPriceActivity.launch(this, mServicePackage.packages.id, mServicePackage.packages.unit_price, REQUEST_CODE_SET_PRICE) }
    }

    private val mOnCheckChangeListener = OnCheckedChangeListener { buttonView, isChecked ->
        if (AuthenticationHelper.checkAuthenticationStatusWithToast(this, R.string.after_authentication_you_can_open_service)) {
            updatePackage(mServicePackage.packages.id, mServicePackage.packages.unit_price, isChecked, true)
        } else {
            setCheckWithoutCallback(false)
        }
    }

    private fun updatePackage(id: Int, price: Int, open: Boolean, rollbackSwitchOnFail: Boolean) {
        showLoading()
        updateServicePackageCall = AppManager.getHttpService().updateServicePackage(id, price, BooleanUtil.getIntFromBoolean(open))
        updateServicePackageCall?.enqueue(object : BaseSdResponseCallback<Packages>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
                if (rollbackSwitchOnFail) {
                    setCheckWithoutCallback(!sw_open_service.isChecked)
                }
            }

            override fun onSuccess(response: Packages?) {
                LogUtils.d(response)
                mServicePackage.packages = response!!
                updateUIByPackage(mServicePackage.packages)
            }

            override fun onFinish() {
                super.onFinish()
                dismissLoading()
            }
        })
    }

    override fun onDestroy() {
        updateServicePackageCall?.cancel()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (REQUEST_CODE_SET_PRICE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                val packageJson = data!!.getStringExtra("package")
                val packages = JsonUtil.fromJson(packageJson, Packages::class.java)
                mServicePackage.packages = packages!!
                updateUIByPackage(mServicePackage.packages)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun updateUIByPackage(packages: Packages) {
        setCheckWithoutCallback(packages.isEnable())
        ll_price.visibility = if (packages.isEnable()) View.VISIBLE else View.GONE
        tv_price.text = packages.getPriceString()
    }

    private fun setCheckWithoutCallback(isChecked: Boolean) {
        sw_open_service.setOnCheckedChangeListener(null)
        sw_open_service.isChecked = isChecked
        sw_open_service.setOnCheckedChangeListener(mOnCheckChangeListener)
    }
}