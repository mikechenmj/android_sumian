package com.sumian.sd.buz.anxiousandfaith.databinding

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.BaseAdapter
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.alibaba.fastjson.util.TypeUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.anxiousandfaith.MoodDiaryDetailActivity
import com.sumian.sd.buz.anxiousandfaith.bean.MoodDiaryData
import com.sumian.sd.common.network.callback.BaseSdResponseCallback

data class ActivityMoodDiaryDetailData(
        val moodDiaryDetailActivity: MoodDiaryDetailActivity,
        val moodDiaryData: MoodDiaryData?,
        val moodLabelAdapter: BaseAdapter,
        val cognitionBiasAdapter: BaseAdapter,
        val onCognitiveBiasClickListener: View.OnClickListener,
        val moodDiaryTextId: Int = moodDiaryData?.getEmotionTextRes() ?: 0,
        val moodDiaryEmotionResId: Int = moodDiaryData?.getEmotionImageRes() ?: 0,
        val updateTimeFormatted: String = TimeUtilV2.formatYYYYMMDDHHMMss(moodDiaryData?.getUpdateAtInMillis()
                ?: 0)
) : BaseObservable() {

    companion object {
        const val NORMAL_CONTENT_MAX_COUNT = 200
    }

    @get:Bindable
    var editMode: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.editMode)
        }

    @get:Bindable
    var moodReasonContent: String = moodDiaryData?.scene ?: ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            moodReasonContentCount = "$length/$NORMAL_CONTENT_MAX_COUNT"
            moodReasonContentCountOutOfMax = length > NORMAL_CONTENT_MAX_COUNT
            moodDiaryData?.scene = value
            notifyPropertyChanged(BR.moodReasonContent)
        }

    @get:Bindable
    var moodReasonContentCount: String = "${moodReasonContent.length}/$NORMAL_CONTENT_MAX_COUNT"
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.moodReasonContentCount)
        }

    @get:Bindable
    var moodReasonContentCountOutOfMax: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.moodReasonContentCountOutOfMax)
        }

    @get:Bindable
    var beliefContent: String = moodDiaryData?.irrationalBelief ?: ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            beliefContentCount = "$length/$NORMAL_CONTENT_MAX_COUNT"
            beliefContentCountOutOfMax = length > NORMAL_CONTENT_MAX_COUNT
            moodDiaryData?.irrationalBelief = value
            notifyPropertyChanged(BR.beliefContent)
        }

    @get:Bindable
    var beliefContentCount: String = "${beliefContent.length}/$NORMAL_CONTENT_MAX_COUNT"
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.beliefContentCount)
        }

    @get:Bindable
    var beliefContentCountOutOfMax: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.beliefContentCountOutOfMax)
        }

    @get:Bindable
    var cognitionBias: List<String> = moodDiaryData?.cognitionBias ?: emptyList()
        set(value) {
            if (value == field) {
                return
            }
            field = value
            moodDiaryData?.cognitionBias = value
            notifyPropertyChanged(BR.cognitionBias)
        }

    @get:Bindable
    var unreasonableResultContent: String = moodDiaryData?.irrationalBeliefResult ?: ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            unreasonableResultContentCount = "$length/$NORMAL_CONTENT_MAX_COUNT"
            unreasonableResultContentCountOutOfMax = length > NORMAL_CONTENT_MAX_COUNT
            moodDiaryData?.irrationalBeliefResult = value
            notifyPropertyChanged(BR.unreasonableResultContent)
        }

    @get:Bindable
    var unreasonableResultContentCount: String = "${unreasonableResultContent.length}/$NORMAL_CONTENT_MAX_COUNT"
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.unreasonableResultContentCount)
        }

    @get:Bindable
    var unreasonableResultContentCountOutOfMax: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.unreasonableResultContentCountOutOfMax)
        }

    @get:Bindable
    var refuteUnreasonableContent: String = moodDiaryData?.idea ?: ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            refuteUnreasonableContentCount = "$length/$NORMAL_CONTENT_MAX_COUNT"
            refuteUnreasonableContentCountOutOfMax = length > NORMAL_CONTENT_MAX_COUNT
            moodDiaryData?.idea = value
            notifyPropertyChanged(BR.refuteUnreasonableContent)
        }

    @get:Bindable
    var refuteUnreasonableContentCount: String = "${refuteUnreasonableContent.length}/$NORMAL_CONTENT_MAX_COUNT"
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.refuteUnreasonableContentCount)
        }

    @get:Bindable
    var refuteUnreasonableContentCountOutOfMax: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.refuteUnreasonableContentCountOutOfMax)
        }

    @get:Bindable
    var reasonableBeliefContent: String = moodDiaryData?.rationalBelief ?: ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            reasonableBeliefContentCount = "$length/$NORMAL_CONTENT_MAX_COUNT"
            reasonableBeliefContentCountOutOfMax = length > NORMAL_CONTENT_MAX_COUNT
            moodDiaryData?.rationalBelief = value
            notifyPropertyChanged(BR.reasonableBeliefContent)
        }

    @get:Bindable
    var reasonableBeliefContentCount: String = "${reasonableBeliefContent.length}/$NORMAL_CONTENT_MAX_COUNT"
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.reasonableBeliefContentCount)
        }

    @get:Bindable
    var reasonableBeliefContentCountOutOfMax: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.reasonableBeliefContentCountOutOfMax)
        }

    @get:Bindable
    var reasonableBeliefResultContent: String = moodDiaryData?.rationalBeliefResult ?: ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            reasonableBeliefResultContentCount = "$length/$NORMAL_CONTENT_MAX_COUNT"
            reasonableBeliefResultContentCountOutOfMax = length > NORMAL_CONTENT_MAX_COUNT
            moodDiaryData?.rationalBeliefResult = value
            notifyPropertyChanged(BR.reasonableBeliefResultContent)
        }

    @get:Bindable
    var reasonableBeliefResultContentCount: String = "${reasonableBeliefResultContent.length}/$NORMAL_CONTENT_MAX_COUNT"
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.reasonableBeliefResultContentCount)
        }

    @get:Bindable
    var reasonableBeliefResultContentCountOutOfMax: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.reasonableBeliefResultContentCountOutOfMax)
        }

    @get:Bindable
    var saveButtonEnable: Boolean = true
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.saveButtonEnable)
        }

    var filledChallenge: Boolean = !TextUtils.isEmpty(moodDiaryData?.irrationalBelief)
            && !TextUtils.isEmpty(moodDiaryData?.irrationalBeliefResult)
            && moodDiaryData?.cognitionBias?.isNotEmpty() ?: false

    var filledReasonableBelief: Boolean = filledChallenge && !TextUtils.isEmpty(moodDiaryData?.idea)
            && !TextUtils.isEmpty(moodDiaryData?.rationalBelief) && !TextUtils.isEmpty(moodDiaryData?.rationalBeliefResult)


    fun saveMoodDiary() {
        if (moodReasonContentCountOutOfMax || beliefContentCountOutOfMax
                || unreasonableResultContentCountOutOfMax || refuteUnreasonableContentCountOutOfMax
                || reasonableBeliefContentCountOutOfMax || reasonableBeliefResultContentCountOutOfMax) {
            moodDiaryDetailActivity.onSaveMoodDiaryFail(moodDiaryDetailActivity.getString(R.string.input_is_too_long))
            return
        }
        if (TextUtils.isEmpty(moodReasonContent)) {
            moodDiaryDetailActivity.onSaveMoodDiaryFail(moodDiaryDetailActivity.getString(R.string.please_finish_question_first))
            return
        }

        var data = moodDiaryData
        if (data == null) {
            moodDiaryDetailActivity.onSaveMoodDiaryFail(moodDiaryDetailActivity.getString(R.string.empty_network_error_msg))
            return
        }
        val call = AppManager.getSdHttpService().updateFaiths(
                data.id, data.emotionType, data.emotions, data.scene, data.irrationalBelief, data.cognitionBias,
                data.irrationalBeliefResult, data.idea, data.rationalBelief, data.rationalBeliefResult)
        moodDiaryDetailActivity.addCall(call)
        call.enqueue(object : BaseSdResponseCallback<MoodDiaryData>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                moodDiaryDetailActivity.onSaveMoodDiaryFail(moodDiaryDetailActivity.getString(R.string.empty_network_error_desc))
            }

            override fun onSuccess(response: MoodDiaryData?) {
                moodDiaryDetailActivity.onSaveMoodDiarySuccess(response)

            }

            override fun onFinish() {
                saveButtonEnable = true
            }
        })
        saveButtonEnable = false
    }

    fun changeToEditMode() {
        editMode = true
        moodDiaryDetailActivity.onChangeToEditMode()
    }

}