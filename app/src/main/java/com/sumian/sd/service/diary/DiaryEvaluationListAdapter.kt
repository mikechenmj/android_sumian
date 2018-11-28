package com.sumian.sd.service.diary

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.R
import com.sumian.sd.app.App
import com.sumian.sd.service.diary.bean.DiaryEvaluationData

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/9/14 9:19
 *     desc   :
 *     version: 1.0
 * </pre>
 */

class DiaryEvaluationListAdapter() : BaseQuickAdapter<DiaryEvaluationData, BaseViewHolder>(R.layout.item_diary_evaluation) {

    override fun convert(helper: BaseViewHolder, data: DiaryEvaluationData) {
        helper
                .setText(R.id.tv_description, getItemDesc(data))
                .setText(R.id.tv_time, TimeUtilV2.formatYYYYMMDDHHMM(data.getUpdateAtInMillis()))
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
            "评估时间：${getYYYYMMDD(data.getDiaryStartAtInMillis())}-${getYYYYMMDD(data.getDiaryEndAtInMillis())}\n${data.description}"
        } else {
            data.description
        }
    }

    private fun getYYYYMMDD(unixTime: Long): String? {
        return TimeUtilV2.formatYYYYMMDD(unixTime)
    }

    private fun getStatusString(status: Int): String {
        return when (status) {
            DiaryEvaluationData.STATUS_0_WAITING_RESPONSE -> App.getAppContext().getString(R.string.waiting_for_reply)
            DiaryEvaluationData.STATUS_1_FINISHED -> App.getAppContext().getString(R.string.finished)
            DiaryEvaluationData.STATUS_2_CLOSED -> App.getAppContext().getString(R.string.closed)
            DiaryEvaluationData.STATUS_3_CANCELED -> App.getAppContext().getString(R.string.finished)
            DiaryEvaluationData.STATUS_4_UNUSED -> ""
            else -> ""
        }
    }
}

interface OnItemClickListener<T> {
    fun onItemClick(data: T)
}
