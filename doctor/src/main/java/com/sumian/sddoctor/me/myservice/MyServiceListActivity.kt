package com.sumian.sddoctor.me.myservice

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.common.image.ImageLoader
import com.sumian.common.mvp.IPresenter
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.base.SddBaseViewModelActivity
import com.sumian.sddoctor.me.myservice.bean.DoctorService
import com.sumian.sddoctor.network.bean.PaginationResponse
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.widget.SddDividerItemDecoration
import kotlinx.android.synthetic.main.activity_my_service_list.*
import retrofit2.Call

/**
 *  @author : Zhan Xuzhao
 *  e-mail : xuzhao.z@sumian.com
 *  time   : 2018/9/28 17:04
 *  desc   :
 *  version: 1.0
 */
class MyServiceListActivity : SddBaseActivity() {
    private val mMyServiceListAdapter = MyServiceListAdapter()

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_my_service_list
    }

//    override fun getTitleBarTitle(): String? {
//        return getString(R.string.my_service)
//    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.my_service)
        recycler_view.adapter = mMyServiceListAdapter
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.addItemDecoration(SddDividerItemDecoration(this))
        mMyServiceListAdapter.setOnItemClickListener { adapter, view, position ->
            run {
                val service = (adapter as MyServiceListAdapter).getItem(position)
                MyServiceDetailActivity.launch(this@MyServiceListActivity, service!!.id, service.name)
            }
        }
    }

    private var mGetMyServiceListCall: Call<PaginationResponse<DoctorService>>? = null

    override fun onStart() {
        super.onStart()
        mGetMyServiceListCall = AppManager.getHttpService().getMyServiceList()
        mGetMyServiceListCall?.enqueue(object : BaseSdResponseCallback<PaginationResponse<DoctorService>>() {
            override fun onSuccess(response: PaginationResponse<DoctorService>?) {
                mMyServiceListAdapter.setNewData(response?.data)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
    }

    override fun onDestroy() {
        mGetMyServiceListCall?.cancel()
        super.onDestroy()
    }
}

class MyServiceListAdapter : BaseQuickAdapter<DoctorService, BaseViewHolder>(R.layout.item_my_service) {
    override fun convert(helper: BaseViewHolder, item: DoctorService) {
        ImageLoader.loadImage(item.icon, helper.getView(R.id.iv_service))
        helper.setText(R.id.tv_title, item.name)
        helper.setText(R.id.tv_desc, item.introduction)
        helper.getView<View>(R.id.v_cover).visibility = if (item.is_opened) View.GONE else View.VISIBLE
        helper.getView<View>(R.id.fl_not_open).visibility = if (item.is_opened) View.GONE else View.VISIBLE
    }
}