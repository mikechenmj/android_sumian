package com.sumian.sddoctor.me.myservice

import android.content.Context
import android.content.Intent
import android.text.Html
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.image.ImageLoader
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.me.myservice.bean.DoctorService
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.widget.divider.SettingDividerView
import kotlinx.android.synthetic.main.activity_my_service_detail.*
import kotlinx.android.synthetic.main.layout_service_item.view.*
import retrofit2.Call

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/9/28 18:02
 * desc   :
 * version: 1.0
 */
class MyServiceDetailActivity : SddBaseActivity() {

    companion object {
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"

        fun launch(context: Context, id: Int, name: String) {
            val intent = Intent(context, MyServiceDetailActivity::class.java)
            intent.putExtra(KEY_ID, id)
            intent.putExtra(KEY_NAME, name)
            ActivityUtils.startActivity(intent)
        }
    }

    private var mGetServiceDetailCall: Call<DoctorService>? = null

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_my_service_detail
    }

//    override fun getTitleBarTitle(): String? {
//        return intent.getStringExtra(KEY_NAME)
//    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(intent.getStringExtra(KEY_NAME))
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    private fun loadData(): Boolean {
        val id = intent.getIntExtra(KEY_ID, -1)
        if (id == -1) {
            LogUtils.d("id not set")
            return true
        }
        mGetServiceDetailCall = AppManager.getHttpService().getServiceDetail(id)
        mGetServiceDetailCall?.enqueue(object : BaseSdResponseCallback<DoctorService>() {

            override fun onSuccess(response: DoctorService?) {
                if (response == null) {
                    return
                }
                ImageLoader.loadImage(response.icon, service_item.iv_service)
                service_item.tv_title.text = response.name
                service_item.tv_desc.text = response.introduction
                tv_package_label.text = getString(R.string.service_type_number, response.servicePackages.size)
                tv_service_desc.text = Html.fromHtml(response.description)
                ll_package_list.removeAllViews()
                for (servicePackage in response.servicePackages) {
                    val p = servicePackage.packages
                    val settingDividerView = SettingDividerView(this@MyServiceDetailActivity)
                    settingDividerView.labelText = servicePackage.name
                    if (p.enable == 1) {
                        settingDividerView.setContentText(resources.getString(R.string.string_yuan, p.getPriceString()))
                    } else {
                        settingDividerView.setContentText(resources.getString(R.string.not_open))
                    }
                    settingDividerView.setOnClickListener { OpenServiceActivity.launch(this@MyServiceDetailActivity, servicePackage) }
                    ll_package_list.addView(settingDividerView)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
        return false
    }

    override fun onDestroy() {
        mGetServiceDetailCall?.cancel()
        super.onDestroy()
    }
}