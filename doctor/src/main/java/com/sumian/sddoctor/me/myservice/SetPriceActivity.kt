package com.sumian.sddoctor.me.myservice

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.JsonUtil
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.me.myservice.bean.Packages
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.util.BooleanUtil
import com.sumian.sddoctor.util.PriceUtil
import kotlinx.android.synthetic.main.activity_set_price.*
import retrofit2.Call

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/9/29 11:39
 * desc   :
 * version: 1.0
 */
class SetPriceActivity : SddBaseActivity() {
    private var updateServicePackageCall: Call<Packages>? = null

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_set_price
    }

    companion object {
        private const val KEY_PRICE = "price"
        private const val KEY_ID = "id"
        private const val MAX_PRICE = 1000 * 1000
        fun launch(activity: Activity, id: Int, price: Int, requestCode: Int) {
            val intent = Intent(activity, SetPriceActivity::class.java)
            intent.putExtra(KEY_PRICE, price)
            intent.putExtra(KEY_ID, id)
            ActivityUtils.startActivityForResult(activity, intent, requestCode)
        }
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.modify_price)
        et_price.hint = PriceUtil.formatPrice(intent.getIntExtra(KEY_PRICE, 0))

        btn_complete.setOnClickListener {
            val priceStr = et_price.text.toString()
            if (TextUtils.isEmpty(priceStr)) {
                showError(getString(R.string.price_must_be_set))
                return@setOnClickListener
            }
            val price = (priceStr.toFloat() * 100).toInt()
            if (price > MAX_PRICE) {
                showError(getString(R.string.max_price_hint))
                return@setOnClickListener
            } else if (price <= 0) {
                showError(getString(R.string.min_price_hint))
                return@setOnClickListener
            }
            updatePackage(price)
        }
    }

    private fun showError(msg: String) {
        tv_warn.text = msg
        tv_warn.visibility = View.VISIBLE
    }

    private fun updatePackage(price: Int) {
        showLoading()
        val id = intent.getIntExtra(KEY_ID, 0)
        updateServicePackageCall = AppManager.getHttpService().updateServicePackage(id, price, BooleanUtil.getIntFromBoolean(true))
        updateServicePackageCall?.enqueue(object : BaseSdResponseCallback<Packages>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                showError(errorResponse.message)
            }

            override fun onSuccess(response: Packages?) {
                val json = JsonUtil.toJson(response)
                val intent = Intent()
                intent.putExtra("package", json)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

            override fun onFinish() {
                super.onFinish()
                dismissLoading()
            }
        })
    }
}