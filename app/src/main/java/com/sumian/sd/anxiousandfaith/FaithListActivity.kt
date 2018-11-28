package com.sumian.sd.anxiousandfaith

import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.mvp.IPresenter
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.R
import com.sumian.sd.anxiousandfaith.bean.AnxietyFaithItemViewData
import com.sumian.sd.anxiousandfaith.bean.FaithData
import com.sumian.sd.anxiousandfaith.event.FaithChangeEvent
import com.sumian.sd.anxiousandfaith.widget.AnxiousFaithItemView
import com.sumian.sd.anxiousandfaith.widget.EditAnxietyBottomSheetDialog
import com.sumian.sd.app.AppManager
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.activity_anxiety_faith_list.*
import org.greenrobot.eventbus.Subscribe

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/26 15:56
 * desc   :
 * version: 1.0
 */
class FaithListActivity : BasePresenterActivity<IPresenter>() {

    private val mAdapter = FaithAdapter()
    private var mPage = 1

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_anxiety_faith_list
    }

    override fun onStart() {
        super.onStart()
        EventBusUtil.register(this)
        refreshData()
    }

    override fun onStop() {
        super.onStop()
        EventBusUtil.unregister(this)
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.faith_record)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = mAdapter
        mAdapter.setOnLoadMoreListener({ loadData() }, recycler_view)
        refresh_layout.setOnRefreshListener {
            refreshData()
        }
        bt_add_record.setText(R.string.add_belief)
        bt_add_record.setOnClickListener { FaithActivity.launch() }
    }

    private fun refreshData() {
        mPage = 1
        loadData()
    }

    private fun loadData() {
        val call = AppManager.getSdHttpService().getFaiths(mPage)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<PaginationResponseV2<FaithData>>() {
            override fun onSuccess(response: PaginationResponseV2<FaithData>?) {
                if (response == null) {
                    return
                }
                if (mPage == 1) {
                    mAdapter.setNewData(response.data)
                } else {
                    mAdapter.addData(response.data)
                }
                mPage++
                mAdapter.setEnableLoadMore(!response.meta.pagination.isLastPage())
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                mAdapter.loadMoreComplete()
                refresh_layout.hideRefreshAnim()
            }
        })
    }

    inner class FaithAdapter : BaseQuickAdapter<FaithData, BaseViewHolder>(R.layout.list_item_anxiety_faith) {
        override fun convert(helper: BaseViewHolder, item: FaithData) {
            val itemView = helper.getView<AnxiousFaithItemView>(R.id.anxiety_faith_view)
            itemView.setTextMaxLines(true)
            itemView.setData(AnxietyFaithItemViewData.create(item), object : EditAnxietyBottomSheetDialog.OnItemClickListener {
                override fun onEditClick() {
                    FaithActivity.launch(item)
                }

                override fun onDeleteClick() {
                    deleteFaith(item.id)
                }
            })
        }
    }

    private fun deleteFaith(id: Int) {
        showDeleteDialog(View.OnClickListener {
            val call = AppManager.getSdHttpService().deleteFaiths(id)
            addCall(call)
            call.enqueue(object : BaseSdResponseCallback<Any>() {
                override fun onSuccess(response: Any?) {
                    val itemPosition = getItemPosition(id)
                    mAdapter.remove(itemPosition)
                    if (mAdapter.data.size == 0) {
                        finish()
                    }
                }

                override fun onFailure(errorResponse: ErrorResponse) {
                    ToastUtils.showShort(errorResponse.message)
                }
            })
        })
    }

    private fun showDeleteDialog(onConfirmClickListener: View.OnClickListener) {
        SumianDialog(this)
                .setTitleText(R.string.delete_record)
                .setMessageText(R.string.delete_record_confirm_hint)
                .whitenLeft()
                .setLeftBtn(R.string.cancel, null)
                .setRightBtn(R.string.confirm, onConfirmClickListener)
                .show()
    }

    @Subscribe(sticky = true)
    fun onFaithChangeEvent(event: FaithChangeEvent) {
        EventBusUtil.removeStickyEvent(event)
        val faith = event.faith
        val position = getItemPosition(anxietyId = faith.id)
        mAdapter.data[position] = faith
        mAdapter.notifyItemChanged(position)
    }

    private fun getItemPosition(anxietyId: Int): Int {
        val list = mAdapter.data
        for ((index, data) in list.withIndex()) {
            if (data.id == anxietyId) {
                return index
            }
        }
        return -1
    }

}