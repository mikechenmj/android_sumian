package com.sumian.sd.buz.anxiousandfaith.databinding

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.BaseAdapter
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.anxiousandfaith.MoodChallengeFragment
import com.sumian.sd.buz.anxiousandfaith.bean.MoodDiaryData
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import retrofit2.Call

data class FragmentMoodChallengeData(
        val moodChallengeFragment: MoodChallengeFragment,
        var savedMoodDiaryData: MoodDiaryData?,
        val moodLabelAdapter: BaseAdapter?,
        val onCognitiveBiasClickListener: View.OnClickListener?
) : BaseObservable() {

    companion object {
        private const val CHALLENGE_BELIEF_CONTENT_MAX_COUNT = 200
    }

    private var mCall: Call<MoodDiaryData>? = null

    @get:Bindable
    var cognitionBias: List<String> = emptyList()
        set(value) {
            if (value == field) {
                return
            }
            field = value
            cognitionBiasTitleColor = moodChallengeFragment.resources.getColor(R.color.t1_color)
            notifyPropertyChanged(BR.cognitionBias)
        }

    @get:Bindable
    var cognitionBiasTitleColor = moodChallengeFragment.resources.getColor(R.color.t1_color)
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.cognitionBiasTitleColor)
        }

    @get:Bindable
    var challengeBeliefContent: String = savedMoodDiaryData?.irrationalBelief ?: ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            challengeBeliefHintColor = moodChallengeFragment.resources.getColor(R.color.t2_color)
            challengeBeliefContentCount = "$length/$CHALLENGE_BELIEF_CONTENT_MAX_COUNT"
            challengeBeliefContentOutOfMax = length > CHALLENGE_BELIEF_CONTENT_MAX_COUNT
            notifyPropertyChanged(BR.challengeBeliefContent)
        }

    @get:Bindable
    var challengeBeliefHintColor = moodChallengeFragment.resources.getColor(R.color.t2_color)
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.challengeBeliefHintColor)
        }

    @get:Bindable
    var challengeBeliefContentCount: String = "${challengeBeliefContent.length}/$CHALLENGE_BELIEF_CONTENT_MAX_COUNT"
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.challengeBeliefContentCount)
        }

    @get:Bindable
    var challengeBeliefContentOutOfMax: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.challengeBeliefContentOutOfMax)
        }

    @get:Bindable
    var challengeResultContent: String = savedMoodDiaryData?.irrationalBeliefResult ?: ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            challengeBeliefResultHintColor = moodChallengeFragment.resources.getColor(R.color.t2_color)
            challengeResultContentCount = "$length/$CHALLENGE_BELIEF_CONTENT_MAX_COUNT"
            challengeResultContentOutOfMax = length > CHALLENGE_BELIEF_CONTENT_MAX_COUNT
            notifyPropertyChanged(BR.challengeResultContent)
        }

    @get:Bindable
    var challengeBeliefResultHintColor = moodChallengeFragment.resources.getColor(R.color.t2_color)
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.challengeBeliefResultHintColor)
        }

    @get:Bindable
    var challengeResultContentCount: String = "${challengeResultContent.length}/$CHALLENGE_BELIEF_CONTENT_MAX_COUNT"
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.challengeResultContentCount)
        }

    @get:Bindable
    var challengeResultContentOutOfMax: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.challengeResultContentOutOfMax)
        }

    fun saveMoodChallenge() {
        if (challengeResultContentOutOfMax || challengeBeliefContentOutOfMax) {
            moodChallengeFragment.onSaveMoodDiaryFail(moodChallengeFragment.getString(R.string.input_is_too_long))
            return
        }

        var cognitionBiasIsEmpty = cognitionBias.isEmpty()
        if (cognitionBiasIsEmpty) {
            cognitionBiasTitleColor = moodChallengeFragment.resources.getColor(R.color.t4_color)
        }
        var challengeBeliefIsEmpty = TextUtils.isEmpty(challengeBeliefContent)
        if (challengeBeliefIsEmpty) {
            challengeBeliefHintColor = moodChallengeFragment.resources.getColor(R.color.t4_color)
        }
        var challengeResultIsEmpty = TextUtils.isEmpty(challengeResultContent)
        if (challengeResultIsEmpty) {
            challengeBeliefResultHintColor = moodChallengeFragment.resources.getColor(R.color.t4_color)
        }

        if (challengeBeliefIsEmpty || challengeResultIsEmpty || cognitionBiasIsEmpty) {
            moodChallengeFragment.onSaveMoodDiaryFail(moodChallengeFragment.getString(R.string.please_finish_question_first))
            return
        }

        if (mCall?.isExecuted == true) {
            mCall?.cancel()
            mCall = null
        }

        updateMoodDiaryData { response -> moodChallengeFragment.onReasonableBeliefPractice(response) }
    }

    private fun updateMoodDiaryData(onSaveSuccess: (response: MoodDiaryData?) -> Unit) {
        var moodDiaryData = savedMoodDiaryData!!
        val call = AppManager.getSdHttpService().updateFaiths(
                moodDiaryData.id,
                moodDiaryData.emotionType,
                moodDiaryData.emotions,
                moodDiaryData.scene,
                challengeBeliefContent,
                cognitionBias,
                challengeResultContent,
                moodDiaryData.idea,
                moodDiaryData.rationalBelief,
                moodDiaryData.rationalBeliefResult)
        call.enqueue(object : BaseSdResponseCallback<MoodDiaryData>() {
            override fun onSuccess(response: MoodDiaryData?) {
                onSaveSuccess(response)
                savedMoodDiaryData = response ?: savedMoodDiaryData
                moodChallengeFragment.onSaveMoodDiarySuccess(response)
                mCall = null
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                moodChallengeFragment.onSaveMoodDiaryFail(moodChallengeFragment.getString(R.string.operation_fail))
                mCall = null
            }
        })
    }
}
