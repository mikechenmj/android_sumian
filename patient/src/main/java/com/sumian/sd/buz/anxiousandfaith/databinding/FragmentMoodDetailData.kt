package com.sumian.sd.buz.anxiousandfaith.databinding

import android.text.TextUtils
import android.util.Log
import android.widget.BaseAdapter
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.anxiousandfaith.MoodDetailFragment
import com.sumian.sd.buz.anxiousandfaith.bean.MoodDiaryData
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import retrofit2.Call

data class FragmentMoodDetailData(
        val moodDetailFragment: MoodDetailFragment,
        val moodDiaryTextId: Int,
        val moodDiaryEmotionResId: Int,
        val moodLabelAdapter: BaseAdapter,
        val moodDiaryPositive: Boolean) : BaseObservable() {

    companion object {
        private const val DETAIL_TEXT_MAX_COUNT = 200
    }

    var savedMoodDiaryData: MoodDiaryData? = null

    @get:Bindable
    var detailText: String = ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            detailHintColor = moodDetailFragment.resources.getColor(R.color.t2_color)
            savedMoodDiaryData?.scene = detailText
            detailTextCount = "$length/$DETAIL_TEXT_MAX_COUNT"
            detailTextCountOutOfMax = length > DETAIL_TEXT_MAX_COUNT
            notifyPropertyChanged(BR.detailText)
        }

    @get:Bindable
    var detailTextCount: String = "${detailText.length}/$DETAIL_TEXT_MAX_COUNT"
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.detailTextCount)
        }

    @get:Bindable
    var detailTextCountOutOfMax: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.detailTextCountOutOfMax)
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

    @get:Bindable
    var detailHintColor = moodDetailFragment.resources.getColor(R.color.t2_color)
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.detailHintColor)
        }


    fun saveMoodDiary() {
        saveMoodDiaryWithCallback {}
    }

    private fun saveMoodDiaryWithCallback(onSaveSuccess: (response: MoodDiaryData?) -> Unit) {
        if (detailTextCountOutOfMax) {
            moodDetailFragment.onSaveMoodDiaryFail(moodDetailFragment.getString(R.string.input_is_too_long))
            return
        }

        if (TextUtils.isEmpty(detailText)) {
            moodDetailFragment.onSaveMoodDiaryFail(moodDetailFragment.getString(R.string.please_finish_question_first))
            detailHintColor = moodDetailFragment.resources.getColor(R.color.t4_color)
            return
        }

        saveButtonClickable = false
        if (savedMoodDiaryData != null) {
            updateMoodDiaryData(onSaveSuccess)
        } else {
            addMoodDiaryData(onSaveSuccess)
        }
    }

    private fun addMoodDiaryData(onSaveSuccess: (response: MoodDiaryData?) -> Unit) {
        val call = AppManager.getSdHttpService().addFaiths(moodDetailFragment.getMoodDiaryType(),
                moodDetailFragment.getMoodDiaryLabel(), detailText)
        moodDetailFragment.addCall(call)
        call.enqueue(object : BaseSdResponseCallback<MoodDiaryData>() {
            override fun onSuccess(response: MoodDiaryData?) {
                onSaveSuccess(response)
                savedMoodDiaryData = response
                moodDetailFragment.onSaveMoodDiarySuccess(moodDetailFragment.getString(R.string.mood_detail_save_success_text), response)
                saveButtonClickable = true
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                moodDetailFragment.onSaveMoodDiaryFail(moodDetailFragment.getString(R.string.operation_fail))
                saveButtonClickable = true
            }
        })
    }

    private fun updateMoodDiaryData(onSaveSuccess: (response: MoodDiaryData?) -> Unit) {
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
                onSaveSuccess(response)
                savedMoodDiaryData = response
                moodDetailFragment.onSaveMoodDiarySuccess(moodDetailFragment.getString(R.string.mood_detail_save_success_text), response)
                saveButtonClickable = true
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                moodDetailFragment.onSaveMoodDiaryFail(moodDetailFragment.getString(R.string.operation_fail))
                saveButtonClickable = true
            }
        })
    }

    fun challengeUnreasonableBelief() {
        saveMoodDiaryWithCallback { response -> moodDetailFragment.onChallengeUnreasonableBelief(response) }
    }
}
