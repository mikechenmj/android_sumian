package com.sumian.sddoctor.me.mywallet

import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.common.mvp.IPresenter
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.base.SddBaseViewModelActivity
import com.sumian.sddoctor.me.mywallet.bean.SettlingRecord
import com.sumian.sddoctor.network.bean.PaginationResponseV2
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.util.MoneyUtil
import com.sumian.sddoctor.widget.EmptyViewCreator
import kotlinx.android.synthetic.main.layout_recycler_view_padding_top_10.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/25 16:36
 * desc   :
 * version: 1.0
 */
class SettlingRecordListActivity : SddBaseActivity() {
    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_recycler_view_padding_top_10
    }

    private val mAdapter = SettlingRecordAdapter()

    private val mPage = 1

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.settling_amount)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = mAdapter
        mAdapter.setOnLoadMoreListener({ queryData(mPage) }, recycler_view)
        mAdapter.setOnItemClickListener { adapter, view, position -> SettlingRecordDetailActivity.launch(mAdapter.getItem(position)!!.id) }
        mAdapter.emptyView = EmptyViewCreator.createImageTextEmptyView(this, R.drawable.ic_no_withdraw_record, R.string.no_settling_data)
    }

    override fun initData() {
        super.initData()
        queryData(mPage)
    }

    private fun queryData(page: Int) {
        val call = AppManager.getHttpService().getPendingIncomeList(page)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<PaginationResponseV2<SettlingRecord>>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: PaginationResponseV2<SettlingRecord>?) {
                LogUtils.d(response)
                if (response == null) {
                    return
                }
                mAdapter.addData(response.data)
                if (response.meta.pagination.isLastPage()) {
                    mAdapter.setEnableLoadMore(false)
                }
            }

            override fun onFinish() {
                super.onFinish()
                mAdapter.loadMoreComplete()
            }
        })
    }

    class SettlingRecordAdapter : BaseQuickAdapter<SettlingRecord, BaseViewHolder>(R.layout.list_item_withdraw_record) {
        override fun convert(helper: BaseViewHolder, item: SettlingRecord) {
            helper.setText(R.id.tv_amount, "${MoneyUtil.fenToYuanString(item.amount)}元")
            val text =
                    if (item.status == SettlingRecord.STATUS_SETTLING)
                        mContext.getString(R.string.predict_in_account, TimeUtilV2.formatYYYYMMDDHHMM(item.creditedAt))
                    else mContext.getString(R.string.money_is_re_checking_click_see_detail)
            helper.setText(R.id.tv_time, text)
            helper.setText(R.id.tv_status, item.getStatusText(mContext))
            helper.setTextColor(R.id.tv_status, ColorCompatUtil.getColor(mContext, item.getStatusTextColorRes()))
        }
    }
}