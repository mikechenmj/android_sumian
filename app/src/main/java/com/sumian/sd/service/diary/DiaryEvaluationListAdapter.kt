package com.sumian.sd.service.diary

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.sd.R
import com.sumian.sd.service.diary.bean.DiaryEvaluationData
import com.sumian.sd.service.util.ServiceTimeUtil
import com.umeng.socialize.utils.DeviceConfig.context
import kotlinx.android.synthetic.main.item_diary_evaluation.view.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/9/14 9:19
 *     desc   :
 *     version: 1.0
 * </pre>
 */


class DiaryEvaluateVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val context: Context by lazy {
        itemView.context
    }

    fun setData(data: DiaryEvaluationData?) {
        data?.apply {
            itemView.tv_description.text = getItemDesc(data)
            itemView.tv_time.text = ServiceTimeUtil.formatTimeYYYYMMDDHHMM(getUpdateAtInMillis())
            itemView.tv_status.text = getStatusString(status)
        }
    }

    fun getItemDescAccordingToStatus(data: DiaryEvaluationData): String {
        return when (data.status) {
            DiaryEvaluationData.STATUS_0_WAITING_RESPONSE -> getItemDesc(data)
            DiaryEvaluationData.STATUS_1_FINISHED -> getItemDesc(data)
            DiaryEvaluationData.STATUS_2_CLOSED -> getItemDesc(data)
            DiaryEvaluationData.STATUS_3_CANCELED -> data.description
            DiaryEvaluationData.STATUS_4_UNUSED -> data.description
            else -> data.description
        }
    }

    private fun getItemDesc(data: DiaryEvaluationData): String {
        return if (data.diaryEndAt != 0) {
            "评估时间：${getYYYYMMDD(data.getDiaryStartAtInMillis())}-${getYYYYMMDD(data.getDiaryEndAtInMillis())}\n备注详情：$data.remark"
        } else {
            data.description
        }
    }

    private fun getYYYYMMDD(unixTime: Long): String? {
        return ServiceTimeUtil.formatTimeYYYYMMDD(unixTime)
    }

    private fun getStatusString(status: Int): String {
        return when (status) {
            DiaryEvaluationData.STATUS_0_WAITING_RESPONSE -> context.getString(R.string.waiting_for_reply)
            DiaryEvaluationData.STATUS_1_FINISHED -> context.getString(R.string.finished)
            DiaryEvaluationData.STATUS_2_CLOSED -> context.getString(R.string.closed)
            DiaryEvaluationData.STATUS_3_CANCELED -> context.getString(R.string.bind_canceled)
            DiaryEvaluationData.STATUS_4_UNUSED -> ""
            else -> ""
        }
    }
}

class DiaryEvaluationListAdapter() : BaseQuickAdapter<DiaryEvaluationData, BaseViewHolder>(R.layout.item_diary_evaluation) {

    override fun convert(helper: BaseViewHolder, data: DiaryEvaluationData) {
        helper
                .setText(R.id.tv_description, getItemDesc(data))
                .setText(R.id.tv_time, ServiceTimeUtil.formatTimeYYYYMMDDHHMM(data.getUpdateAtInMillis()))
                .setText(R.id.tv_status, getStatusString(data.status))
    }

    fun getItemDescAccordingToStatus(data: DiaryEvaluationData): String {
        return when (data.status) {
            DiaryEvaluationData.STATUS_0_WAITING_RESPONSE -> getItemDesc(data)
            DiaryEvaluationData.STATUS_1_FINISHED -> getItemDesc(data)
            DiaryEvaluationData.STATUS_2_CLOSED -> getItemDesc(data)
            DiaryEvaluationData.STATUS_3_CANCELED -> data.description
            DiaryEvaluationData.STATUS_4_UNUSED -> data.description
            else -> data.description
        }
    }

    private fun getItemDesc(data: DiaryEvaluationData): String {
        return if (data.diaryEndAt != 0) {
            "评估时间：${getYYYYMMDD(data.getDiaryStartAtInMillis())}-${getYYYYMMDD(data.getDiaryEndAtInMillis())}\n备注详情：$data.remark"
        } else {
            data.description
        }
    }

    private fun getYYYYMMDD(unixTime: Long): String? {
        return ServiceTimeUtil.formatTimeYYYYMMDD(unixTime)
    }

    private fun getStatusString(status: Int): String {
        return when (status) {
            DiaryEvaluationData.STATUS_0_WAITING_RESPONSE -> context.getString(R.string.waiting_for_reply)
            DiaryEvaluationData.STATUS_1_FINISHED -> context.getString(R.string.finished)
            DiaryEvaluationData.STATUS_2_CLOSED -> context.getString(R.string.closed)
            DiaryEvaluationData.STATUS_3_CANCELED -> context.getString(R.string.bind_canceled)
            DiaryEvaluationData.STATUS_4_UNUSED -> ""
            else -> ""
        }
    }
    //    private var mData: MutableList<DiaryEvaluationData>? = null
//    var mOnItemClickListener: OnItemClickListener<DiaryEvaluationData>? = null
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryEvaluateVH {
//        val inflate = LayoutInflater.from(parent.context).inflate(R.layout.item_diary_evaluation, null, false)
//        return DiaryEvaluateVH(inflate)
//    }
//
//    override fun getItemCount(): Int {
//        return mData?.size ?: 0
//    }
//
//    override fun onBindViewHolder(holder: DiaryEvaluateVH, position: Int) {
//        val data = mData?.get(position)
//        holder.setData(data)
//        holder.itemView.setOnClickListener { mOnItemClickListener?.onItemClick(data!!) }
//    }
//
//    fun setData(data: List<DiaryEvaluationData>?) {
//        mData = data?.toMutableList()
//        notifyDataSetChanged()
//    }
//
//    fun addData(data: List<DiaryEvaluationData>?) {
//        if (data == null || data.isEmpty()) {
//            return
//        }
//        if (mData == null) {
//            mData = ArrayList<DiaryEvaluationData>()
//        }
//        val oldSize = mData!!.size
//        mData!!.addAll(data)
////        notifyItemRangeChanged(oldSize, data.size)
//        notifyDataSetChanged()
//    }
}

interface OnItemClickListener<T> {
    fun onItemClick(data: T)
}
