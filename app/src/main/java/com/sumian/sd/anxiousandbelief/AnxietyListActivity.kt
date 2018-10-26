package com.sumian.sd.anxiousandbelief

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.common.base.BaseBackActivity
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.common.utils.TimeUtilV2
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.R
import com.sumian.sd.anxiousandbelief.bean.AnxietyData
import com.sumian.sd.anxiousandbelief.event.AnxietyChangeEvent
import com.sumian.sd.anxiousandbelief.widget.EditAnxietyBottomSheetDialog
import com.sumian.sd.app.AppManager
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.refresh_recycler_view.*
import org.greenrobot.eventbus.Subscribe

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/26 15:56
 * desc   :
 * version: 1.0
 */
class AnxietyListActivity : BaseBackActivity(), BaseQuickAdapter.RequestLoadMoreListener {

    private val mAdapter = AnxietyAdapter()
    private var mPage = 1

    override fun getChildContentId(): Int {
        return R.layout.refresh_recycler_view
    }

    override fun onStart() {
        super.onStart()
        EventBusUtil.register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBusUtil.unregister(this)
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.anxiety_record)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = mAdapter
        mAdapter.setOnLoadMoreListener(this, recycler_view)
        refresh_layout.setOnRefreshListener {
            mPage = 1
            getAnxieties()
        }
        getAnxieties()
    }

    override fun onLoadMoreRequested() {
        getAnxieties()
    }

    private fun getAnxieties() {
        val call = AppManager.getSdHttpService().getAnxieties(mPage)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<PaginationResponseV2<AnxietyData>>() {
            override fun onSuccess(response: PaginationResponseV2<AnxietyData>?) {
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

    inner class AnxietyAdapter : BaseQuickAdapter<AnxietyData, BaseViewHolder>(R.layout.view_anxious_belief_item) {
        override fun convert(helper: BaseViewHolder, item: AnxietyData) {
            helper.getView<TextView>(R.id.tv_title).text = item.anxiety
            helper.getView<TextView>(R.id.tv_message).text = item.solution
            helper.getView<TextView>(R.id.tv_time).text = TimeUtilV2.formatTimeYYYYMMDD_HHMM(item.getUpdateAtInMillis())
            helper.getView<View>(R.id.iv_more).setOnClickListener {
                EditAnxietyBottomSheetDialog(mContext, object : EditAnxietyBottomSheetDialog.OnItemClickListener {
                    override fun onEditClick() {
                        AnxietyActivity.launch(item)
                    }

                    override fun onDeleteClick() {
                        deleteAnxiety(item.id)
                    }
                }).show()
            }
        }
    }

    private fun deleteAnxiety(id: Int) {
        showDeleteDialog(View.OnClickListener {
            val call = AppManager.getSdHttpService().deleteAnxiety(id)
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
    fun onAnxietyChangeEvent(event: AnxietyChangeEvent) {
        EventBusUtil.removeStickyEvent(event)
        val anxiety = event.anxiety
        val position = getItemPosition(anxietyId = anxiety.id)
        mAdapter.data[position] = anxiety
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