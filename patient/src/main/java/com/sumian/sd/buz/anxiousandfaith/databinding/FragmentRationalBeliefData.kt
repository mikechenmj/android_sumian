package com.sumian.sd.buz.anxiousandfaith.databinding

import android.text.TextUtils
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.anxiousandfaith.RationalBeliefFragment
import com.sumian.sd.buz.anxiousandfaith.bean.MoodDiaryData
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import retrofit2.Call

data class FragmentRationalBeliefData(
        val rationalBeliefFragment: RationalBeliefFragment,
        var savedMoodDiaryData: MoodDiaryData?
) : BaseObservable() {

    companion object {
        private const val NORMAL_CONTENT_MAX_COUNT = 200
    }

    @get:Bindable
    var refuteUnreasonableBelief: String = savedMoodDiaryData?.idea ?: ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            savedMoodDiaryData?.idea = value
            val length = value.length
            refuteUnreasonableBeliefHintColor = rationalBeliefFragment.resources.getColor(R.color.t2_color)
            refuteUnreasonableBeliefCount = "$length/$NORMAL_CONTENT_MAX_COUNT"
            refuteUnreasonableBeliefOutOfMax = length > NORMAL_CONTENT_MAX_COUNT
            notifyPropertyChanged(BR.refuteUnreasonableBelief)
        }

    @get:Bindable
    var refuteUnreasonableBeliefHintColor = rationalBeliefFragment.resources.getColor(R.color.t2_color)
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.refuteUnreasonableBeliefHintColor)
        }

    @get:Bindable
    var refuteUnreasonableBeliefCount: String = "${refuteUnreasonableBelief.length}/$NORMAL_CONTENT_MAX_COUNT"
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.refuteUnreasonableBeliefCount)
        }

    @get:Bindable
    var refuteUnreasonableBeliefOutOfMax: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.refuteUnreasonableBeliefOutOfMax)
        }

    @get:Bindable
    var reasonableBelief: String = savedMoodDiaryData?.rationalBelief ?: ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            savedMoodDiaryData?.rationalBelief = value
            val length = value.length
            reasonableBeliefHintColor = rationalBeliefFragment.resources.getColor(R.color.t2_color)
            reasonableBeliefCount = "$length/$NORMAL_CONTENT_MAX_COUNT"
            reasonableBeliefOutOfMax = length > NORMAL_CONTENT_MAX_COUNT
            notifyPropertyChanged(BR.reasonableBelief)
        }

    @get:Bindable
    var reasonableBeliefHintColor = rationalBeliefFragment.resources.getColor(R.color.t2_color)
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.reasonableBeliefHintColor)
        }

    @get:Bindable
    var reasonableBeliefCount: String = "${reasonableBelief.length}/$NORMAL_CONTENT_MAX_COUNT"
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.reasonableBeliefCount)
        }

    @get:Bindable
    var reasonableBeliefOutOfMax: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.reasonableBeliefOutOfMax)
        }

    @get:Bindable
    var reasonableBeliefResult: String = savedMoodDiaryData?.rationalBeliefResult ?: ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            savedMoodDiaryData?.rationalBeliefResult = value
            val length = value.length
            reasonableBeliefResultHintColor = rationalBeliefFragment.resources.getColor(R.color.t2_color)
            reasonableBeliefResultCount = "$length/$NORMAL_CONTENT_MAX_COUNT"
            reasonableBeliefResultOutOfMax = length > NORMAL_CONTENT_MAX_COUNT
            notifyPropertyChanged(BR.reasonableBeliefResult)
        }

    @get:Bindable
    var reasonableBeliefResultHintColor = rationalBeliefFragment.resources.getColor(R.color.t2_color)
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.reasonableBeliefResultHintColor)
        }

    @get:Bindable
    var reasonableBeliefResultCount: String = "${reasonableBeliefResult.length}/$NORMAL_CONTENT_MAX_COUNT"
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.reasonableBeliefResultCount)
        }

    @get:Bindable
    var reasonableBeliefResultOutOfMax: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.reasonableBeliefResultOutOfMax)
        }

    @get:Bindable
    var saveButtonClickable: Boolean = true
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.saveButtonClickable)
        }

    fun saveMoodChallenge() {
        if (refuteUnreasonableBeliefOutOfMax || reasonableBeliefOutOfMax || reasonableBeliefResultOutOfMax) {
            rationalBeliefFragment.onSaveMoodDiaryFail(rationalBeliefFragment.getString(R.string.input_is_too_long))
            return
        }

        var refuteUnreasonableBeliefIsEmpty = TextUtils.isEmpty(refuteUnreasonableBelief)
        var reasonableBeliefIsEmpty = TextUtils.isEmpty(reasonableBelief)
        var reasonableBeliefResultIsEmpty = TextUtils.isEmpty(reasonableBeliefResult)
        if (refuteUnreasonableBeliefIsEmpty) {
            refuteUnreasonableBeliefHintColor = rationalBeliefFragment.resources.getColor(R.color.t4_color)
        }
        if (reasonableBeliefIsEmpty) {
            reasonableBeliefHintColor = rationalBeliefFragment.resources.getColor(R.color.t4_color)
        }
        if (reasonableBeliefResultIsEmpty) {
            reasonableBeliefResultHintColor = rationalBeliefFragment.resources.getColor(R.color.t4_color)
        }

        if (refuteUnreasonableBeliefIsEmpty || reasonableBeliefIsEmpty || reasonableBeliefResultIsEmpty) {
            rationalBeliefFragment.onSaveMoodDiaryFail(rationalBeliefFragment.getString(R.string.please_finish_question_first))
            return
        }

        saveButtonClickable = false
        updateMoodDiaryData()
    }

    private fun updateMoodDiaryData() {
        var moodDiaryData = savedMoodDiaryData!!
        val call = AppManager.getSdHttpService().updateFaiths(
                moodDiaryData.id,
                moodDiaryData.emotionType,
                moodDiaryData.emotions,
                moodDiaryData.scene,
                moodDiaryData.irrationalBelief,
                moodDiaryData.cognitionBias,
                moodDiaryData.irrationalBeliefResult,
                moodDiaryData.idea,
                moodDiaryData.rationalBelief,
                moodDiaryData.rationalBeliefResult)
        call.enqueue(object : BaseSdResponseCallback<MoodDiaryData>() {
            override fun onSuccess(response: MoodDiaryData?) {
                savedMoodDiaryData = response ?: savedMoodDiaryData
                rationalBeliefFragment.onSaveMoodDiarySuccess(response)
                saveButtonClickable = true
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                rationalBeliefFragment.onSaveMoodDiaryFail(rationalBeliefFragment.getString(R.string.operation_fail))
                saveButtonClickable = true
            }
        })
    }
}
