package com.sumian.sd.relaxation

import android.support.v7.widget.GridLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.image.ImageLoader
import com.sumian.common.mvp.IPresenter
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.relaxation.bean.RelaxationData
import com.sumian.sd.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_relaxation_list.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/27 10:00
 * desc   :
 * version: 1.0
 */
class RelaxationListActivity : BasePresenterActivity<IPresenter>() {
    private val mAdapter = ItemAdapter()

    override fun getLayoutId(): Int {
        return R.layout.activity_relaxation_list
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun onStart() {
        super.onStart()
        StatusBarUtil.setStatusBarTextColorDark(this, false)
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.relax_exercise)
        rv_relaxation.layoutManager = GridLayoutManager(this, 2)
        rv_relaxation.adapter = mAdapter
        mAdapter.setOnItemClickListener { adapter, view, position ->
            run {
                if (position == mAdapter.itemCount - 1) {
                    return@run
                } else {
                    RelaxationDetailActivity.start(mAdapter.getItem(position)!!.id)
                }
            }
        }
        rv_relaxation.isNestedScrollingEnabled = false
    }

    override fun initData() {
        super.initData()
        val call = AppManager.getSdHttpService().getRelaxations()
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<MutableList<RelaxationData>>() {
            override fun onSuccess(response: MutableList<RelaxationData>?) {
                response?.let {
                    response.add(RelaxationData("", "", 0, "", "", ""))
                    mAdapter.setNewData(response)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
    }

    class ItemAdapter : BaseQuickAdapter<RelaxationData, BaseViewHolder>(R.layout.list_item_relaxation) {
        override fun convert(helper: BaseViewHolder, item: RelaxationData) {
            if (helper.adapterPosition == itemCount - 1) {
                helper.setText(R.id.tv_relaxation, "")
                ImageLoader.loadImage(R.drawable.relax_bg_coming_soon, helper.getView(R.id.iv_relaxation))
            } else {
                helper.setText(R.id.tv_relaxation, item.name)
                ImageLoader.loadImage(item.cover, helper.getView(R.id.iv_relaxation))
            }
        }
    }

}