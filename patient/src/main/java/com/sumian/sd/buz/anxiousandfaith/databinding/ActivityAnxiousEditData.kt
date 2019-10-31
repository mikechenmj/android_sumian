package com.sumian.sd.buz.anxiousandfaith.databinding

import android.text.TextUtils
import android.util.Log
import androidx.databinding.*
import androidx.databinding.library.baseAdapters.BR
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.anxiousandfaith.AnxietyEditActivity
import com.sumian.sd.buz.anxiousandfaith.bean.AnxietyAnswer
import com.sumian.sd.buz.anxiousandfaith.bean.AnxietyData
import com.sumian.sd.buz.anxiousandfaith.event.AnxietyChangeEvent
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.EventBusUtil
import com.sumian.sd.widget.divider.SettingDividerView
import kotlin.collections.ArrayList

@BindingMethods(value = [
    BindingMethod(
            type = SettingDividerView::class,
            attribute = "type_content",
            method = "setContent")])

class ActivityAnxiousEditData(
        var anxietyEditActivity: AnxietyEditActivity,
        var anxietyData: AnxietyData?) : BaseObservable() {

    companion object {
        private const val TIME_ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L

        private const val DETAIL_TEXT_MAX_COUNT = 200
        private const val SOLUTION_TEXT_MAX_COUNT = 50
        private const val HARD_TEXT_MAX_COUNT = 50
        private const val HOW_TO_SOLVE_MAX_COUNT = 50

        @InverseMethod("getIsCheckedFromId")
        @JvmStatic
        fun getIdFromIsChecked(positiveId: Int, negativeId: Int, isChecked: Boolean): Int {
            return if (isChecked) positiveId else negativeId
        }

        @JvmStatic
        fun getIsCheckedFromId(positiveId: Int, negativeId: Int, id: Int): Boolean {
            return id == positiveId
        }

        @InverseMethod("getAnswerValueFromId")
        @JvmStatic
        fun getIdFromIsAnswerValue(positiveId: Int, negativeId: Int, value: String): Int {
            return when (value) {
                AnxietyData.ANSWER_CHECKED_YES -> positiveId
                AnxietyData.ANSWER_CHECKED_NO -> negativeId
                else -> 0
            }
        }

        @JvmStatic
        fun getAnswerValueFromId(positiveId: Int, negativeId: Int, id: Int): String {
            return when (id) {
                positiveId -> AnxietyData.ANSWER_CHECKED_YES
                negativeId -> AnxietyData.ANSWER_CHECKED_NO
                else -> AnxietyData.ANSWER_INVALID_VALUE
            }
        }
    }

    @get:Bindable
    var detailText: String = anxietyData?.anxiety ?: ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            anxietyHintColor = anxietyEditActivity.resources.getColor(R.color.t2_color)
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
    var detailPlanSelectAnswer: String =
            if (anxietyData != null) {
                anxietyData!!.getAnswer(AnxietyData.ANSWER_HAS_DETAILED_PLAN_ID)
            } else {
                AnxietyData.ANSWER_INVALID_VALUE
            }
        set(value) {
            if (value == field) {
                return
            }
            field = value
            if (value == AnxietyData.ANSWER_CHECKED_YES) {
                hardSelectAnswer = AnxietyData.ANSWER_INVALID_VALUE
                howToResolveCheckedIndex = AnxietyData.ANSWER_INVALID_VALUE
                howToResolveCheckedId = 0
            } else if (value == AnxietyData.ANSWER_CHECKED_NO) {
                hardSelectAnswer = anxietyData?.getAnswer(AnxietyData.ANSWER_IS_HARD_PROBLEM_ID)
                        ?: hardSelectAnswer
                howToResolveCheckedIndex = anxietyData?.getAnswer(AnxietyData.ANSWER_HOW_TO_SOLVE_ID)
                        ?: howToResolveCheckedIndex
                howToResolveCheckedId = refreshHowToResolveCheckedId()
            }
            hardCheckBoxColor = anxietyEditActivity.resources.getColor(R.color.b3_color)
            hasPlanCheckBoxColor = anxietyEditActivity.resources.getColor(R.color.b3_color)
            solutionHintColor = anxietyEditActivity.resources.getColor(R.color.t2_color)
            notifyPropertyChanged(BR.detailPlanSelectAnswer)
        }

    @get:Bindable
    var solutionText: String =
            if (anxietyData != null && anxietyData!!.hasDetailedPlanChecked())
                anxietyData!!.solution
            else {
                ""
            }
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            solutionHintColor = anxietyEditActivity.resources.getColor(R.color.t2_color)
            solutionTextCount = "$length/$SOLUTION_TEXT_MAX_COUNT"
            solutionTextCountOutOfMax = length > SOLUTION_TEXT_MAX_COUNT
            notifyPropertyChanged(BR.solutionText)
        }

    @get:Bindable
    var solutionTextCount: String = "${solutionText.length}/$SOLUTION_TEXT_MAX_COUNT"
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.solutionTextCount)
        }

    @get:Bindable
    var solutionTextCountOutOfMax: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.solutionTextCountOutOfMax)
        }

    @get:Bindable
    var hardSelectAnswer: String =
            if (anxietyData != null) {
                anxietyData!!.getAnswer(AnxietyData.ANSWER_IS_HARD_PROBLEM_ID)
            } else {
                AnxietyData.ANSWER_INVALID_VALUE
            }
        set(value) {
            if (value == field) {
                return
            }
            field = value
            if (value == AnxietyData.ANSWER_CHECKED_NO) {
                howToResolveCheckedIndex = AnxietyData.ANSWER_INVALID_VALUE
                howToResolveCheckedId = 0
            } else if (value == AnxietyData.ANSWER_CHECKED_YES) {
                howToResolveCheckedIndex = anxietyData?.getAnswer(AnxietyData.ANSWER_HOW_TO_SOLVE_ID)
                        ?: howToResolveCheckedIndex
                howToResolveCheckedId = refreshHowToResolveCheckedId()
            }
            howToSolveCheckBoxColor = anxietyEditActivity.resources.getColor(R.color.b3_color)
            hardCheckBoxColor = anxietyEditActivity.resources.getColor(R.color.b3_color)
            solutionHintColor = anxietyEditActivity.resources.getColor(R.color.t2_color)
            notifyPropertyChanged(BR.hardSelectAnswer)
        }

    @get:Bindable
    var hardText: String =
            if (anxietyData != null && !anxietyData!!.hardChecked() && !anxietyData!!.hasDetailedPlanChecked())
                anxietyData!!.solution
            else {
                ""
            }
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            solutionHintColor = anxietyEditActivity.resources.getColor(R.color.t2_color)
            hardTextCount = "$length/$HARD_TEXT_MAX_COUNT"
            hardTextCountOutOfMax = length > HARD_TEXT_MAX_COUNT
            notifyPropertyChanged(BR.hardText)
        }

    @get:Bindable
    var hardTextCount: String = "${hardText.length}/$HARD_TEXT_MAX_COUNT"
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.hardTextCount)
        }

    @get:Bindable
    var hardTextCountOutOfMax: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.hardTextCountOutOfMax)
        }

    private var howToResolveCheckedIndex: String = anxietyData?.getAnswer(AnxietyData.ANSWER_HOW_TO_SOLVE_ID)
            ?: AnxietyData.ANSWER_INVALID_VALUE

    @get:Bindable
    var howToResolveCheckedId: Int = refreshHowToResolveCheckedId()
        set(value) {
            if (value == field) {
                return
            }
            field = value
            howToResolveCheckedIndex = when (value) {
                R.id.rb_anxiety_ask_how_to_resolve_one -> {
                    AnxietyData.ANSWER_HOW_TO_SOLVE_ONE_INDEX
                }
                R.id.rb_anxiety_ask_how_to_resolve_two -> {
                    AnxietyData.ANSWER_HOW_TO_SOLVE_TWO_INDEX
                }
                R.id.rb_anxiety_ask_how_to_resolve_three -> {
                    AnxietyData.ANSWER_HOW_TO_SOLVE_THREE_INDEX
                }
                else -> {
                    AnxietyData.ANSWER_INVALID_VALUE
                }
            }
            howToSolveCheckBoxColor = anxietyEditActivity.resources.getColor(R.color.b3_color)
            solutionHintColor = anxietyEditActivity.resources.getColor(R.color.t2_color)
            notifyPropertyChanged(BR.howToResolveCheckedId)
        }

    @get:Bindable
    var askHowToResolveOneText: String =
            if (anxietyData != null && anxietyData!!.getHowToResolveCheckedId() == AnxietyData.ANSWER_HOW_TO_SOLVE_ONE_INDEX) {
                anxietyData!!.solution
            } else {
                ""
            }
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            solutionHintColor = anxietyEditActivity.resources.getColor(R.color.t2_color)
            askHowToResolveOneTextCount = "$length/$HOW_TO_SOLVE_MAX_COUNT"
            askHowToResolveOneTextCountOutOfMax = length > HOW_TO_SOLVE_MAX_COUNT
            notifyPropertyChanged(BR.askHowToResolveOneText)
        }

    @get:Bindable
    var askHowToResolveOneTextCount: String = "${askHowToResolveOneText.length}/$HOW_TO_SOLVE_MAX_COUNT"
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.askHowToResolveOneTextCount)
        }

    @get:Bindable
    var askHowToResolveOneTextCountOutOfMax: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.askHowToResolveOneTextCountOutOfMax)
        }

    @get:Bindable
    var askHowToResolveTwoText: String =
            if (anxietyData != null && anxietyData!!.getHowToResolveCheckedId() == AnxietyData.ANSWER_HOW_TO_SOLVE_TWO_INDEX) {
                anxietyData!!.solution
            } else {
                ""
            }
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            solutionHintColor = anxietyEditActivity.resources.getColor(R.color.t2_color)
            askHowToResolveTwoTextCount = "$length/$HOW_TO_SOLVE_MAX_COUNT"
            askHowToResolveTwoTextCountOutOfMax = length > HOW_TO_SOLVE_MAX_COUNT
            notifyPropertyChanged(BR.askHowToResolveTwoText)
        }

    @get:Bindable
    var askHowToResolveTwoTextCount: String = "${askHowToResolveTwoText.length}/$HOW_TO_SOLVE_MAX_COUNT"
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.askHowToResolveTwoTextCount)
        }

    @get:Bindable
    var askHowToResolveTwoTextCountOutOfMax: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.askHowToResolveTwoTextCountOutOfMax)
        }

    @get:Bindable
    var askHowToResolveThreeText: String =
            if (anxietyData != null && anxietyData!!.getHowToResolveCheckedId() == AnxietyData.ANSWER_HOW_TO_SOLVE_THREE_INDEX) {
                anxietyData!!.solution
            } else {
                ""
            }
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            solutionHintColor = anxietyEditActivity.resources.getColor(R.color.t2_color)
            askHowToResolveThreeTextCount = "$length/$HOW_TO_SOLVE_MAX_COUNT"
            askHowToResolveThreeTextCountOutOfMax = length > HOW_TO_SOLVE_MAX_COUNT
            notifyPropertyChanged(BR.askHowToResolveThreeText)
        }

    @get:Bindable
    var askHowToResolveThreeTextCount: String = "${askHowToResolveThreeText.length}/$HOW_TO_SOLVE_MAX_COUNT"
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.askHowToResolveThreeTextCount)
        }

    @get:Bindable
    var askHowToResolveThreeTextCountOutOfMax: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.askHowToResolveThreeTextCountOutOfMax)
        }

    var remindTimeInMillis: Long =
            if (anxietyData != null && anxietyData!!.remindAt > 0) {
                anxietyData!!.getRemindAtInMillis()
            } else {
                System.currentTimeMillis() + TIME_ONE_DAY_IN_MILLIS
            }
        set(value) {
            if (value == field) {
                return
            }
            field = value
            anxietyData?.setRemindAtInSecond(value)
        }

    @get:Bindable
    var remindSettingTypeContent: String = TimeUtilV2.formatYYYYMMDDHHMM(remindTimeInMillis)
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.remindSettingTypeContent)
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

    @get:Bindable
    var anxietyHintColor = anxietyEditActivity.resources.getColor(R.color.t2_color)
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.anxietyHintColor)
        }

    @get:Bindable
    var solutionHintColor = anxietyEditActivity.resources.getColor(R.color.t2_color)
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.solutionHintColor)
        }

    @get:Bindable
    var hasPlanCheckBoxColor = anxietyEditActivity.resources.getColor(R.color.b3_color)
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.hasPlanCheckBoxColor)
        }

    @get:Bindable
    var hardCheckBoxColor = anxietyEditActivity.resources.getColor(R.color.b3_color)
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.hardCheckBoxColor)
        }

    @get:Bindable
    var howToSolveCheckBoxColor = anxietyEditActivity.resources.getColor(R.color.b3_color)
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.howToSolveCheckBoxColor)
        }

    private fun refreshHowToResolveCheckedId(): Int {
        return when (anxietyData?.getAnswer(AnxietyData.ANSWER_HOW_TO_SOLVE_ID)) {
            AnxietyData.ANSWER_HOW_TO_SOLVE_ONE_INDEX -> {
                R.id.rb_anxiety_ask_how_to_resolve_one
            }
            AnxietyData.ANSWER_HOW_TO_SOLVE_TWO_INDEX -> {
                R.id.rb_anxiety_ask_how_to_resolve_two
            }
            AnxietyData.ANSWER_HOW_TO_SOLVE_THREE_INDEX -> {
                R.id.rb_anxiety_ask_how_to_resolve_three
            }
            else -> {
                0
            }
        }
    }

    fun saveAnxiety() {
        if (detailTextCountOutOfMax || solutionTextCountOutOfMax
                || hardTextCountOutOfMax || askHowToResolveOneTextCountOutOfMax
                || askHowToResolveTwoTextCountOutOfMax || askHowToResolveThreeTextCountOutOfMax) {
            anxietyEditActivity.onSaveAnxietyFail(anxietyEditActivity.getString(R.string.input_is_too_long))
            return
        }
        var checkedIndex = howToResolveCheckedIndex

        var data: AnxietyData =
                if (anxietyData != null) {
                    anxietyData!!
                } else {
                    AnxietyData(AnxietyData.ANXIETY_INVALID_ID, AnxietyData.ANXIETY_INVALID_ID)
                }

        var solution =
                if (detailPlanSelectAnswer == AnxietyData.ANSWER_CHECKED_YES) {
                    solutionText
                } else if (hardSelectAnswer == AnxietyData.ANSWER_CHECKED_NO) {
                    if (hardText.isEmpty()) {
                        anxietyEditActivity.getString(R.string.anxiety_record_hint)
                    } else {
                        hardText
                    }
                } else {
                    when (checkedIndex) {
                        AnxietyData.ANSWER_HOW_TO_SOLVE_ONE_INDEX -> {
                            askHowToResolveOneText
                        }
                        AnxietyData.ANSWER_HOW_TO_SOLVE_TWO_INDEX -> {
                            askHowToResolveTwoText
                        }
                        AnxietyData.ANSWER_HOW_TO_SOLVE_THREE_INDEX -> {
                            if (askHowToResolveThreeText.isEmpty()) {
                                anxietyEditActivity.getString(R.string.anxiety_ask_how_to_resolve_three_edit_hint)
                            } else {
                                askHowToResolveThreeText
                            }
                        }
                        else -> {
                            ""
                        }
                    }
                }

        var answers = ArrayList<AnxietyAnswer>(3)
        answers.add(AnxietyAnswer(AnxietyData.ANSWER_HAS_DETAILED_PLAN_ID, detailPlanSelectAnswer))
        if (hardSelectAnswer != AnxietyData.ANSWER_INVALID_VALUE) {
            answers.add(AnxietyAnswer(AnxietyData.ANSWER_IS_HARD_PROBLEM_ID, hardSelectAnswer))
        }
        if (checkedIndex != AnxietyData.ANSWER_INVALID_VALUE) {
            answers.add(AnxietyAnswer(AnxietyData.ANSWER_HOW_TO_SOLVE_ID, checkedIndex))
        }

        data.apply {
            anxiety = detailText
            this.solution = solution
            this.answers = answers
            if (id == AnxietyData.ANXIETY_INVALID_ID) {
                setRemindAtInSecond(remindTimeInMillis)
            }
        }

        var isUpdate = data.id != AnxietyData.ANXIETY_INVALID_ID
        Log.i("MCJ", "add: $data")

        var anxietyIsEmpty = TextUtils.isEmpty(data.anxiety)
        if (anxietyIsEmpty) {
            anxietyHintColor = anxietyEditActivity.resources.getColor(R.color.t4_color)
        }
        var solutionIsEmpty = TextUtils.isEmpty(data.solution)
        if (solutionIsEmpty) {
            solutionHintColor = anxietyEditActivity.resources.getColor(R.color.t4_color)
        }

        if (detailPlanSelectAnswer == AnxietyData.ANSWER_INVALID_VALUE) {
            hasPlanCheckBoxColor = anxietyEditActivity.resources.getColor(R.color.t4_color)
        } else if (detailPlanSelectAnswer == AnxietyData.ANSWER_CHECKED_NO
                && hardSelectAnswer == AnxietyData.ANSWER_INVALID_VALUE) {
            hardCheckBoxColor = anxietyEditActivity.resources.getColor(R.color.t4_color)
        } else if (hardSelectAnswer == AnxietyData.ANSWER_CHECKED_YES
                && howToResolveCheckedIndex == AnxietyData.ANSWER_INVALID_VALUE) {
            howToSolveCheckBoxColor = anxietyEditActivity.resources.getColor(R.color.t4_color)
        }

        if (anxietyIsEmpty || solutionIsEmpty) {
            anxietyEditActivity.onSaveAnxietyFail(anxietyEditActivity.getString(R.string.please_finish_question_first))
            return
        }

        if (data.getRemindAtInMillis() <= System.currentTimeMillis() && !isUpdate) {
            anxietyEditActivity.onSaveAnxietyFail(anxietyEditActivity.getString(R.string.anxiety_remind_time_too_old_tip))
        }

        val call = if (!isUpdate) {
            AppManager.getSdHttpService().addAnxietyBody(data)
        } else {
            AppManager.getSdHttpService().updateAnxietyBody(data.id, data)
        }
        anxietyEditActivity.addCall(call)
        saveButtonEnable = false
        call.enqueue(object : BaseSdResponseCallback<AnxietyData>() {
            override fun onSuccess(response: AnxietyData?) {
                anxietyData = response ?: anxietyData
                Log.i("MCJ", "response: $response")
                EventBusUtil.postStickyEvent(AnxietyChangeEvent(response!!))
                anxietyEditActivity.onSaveAnxietySuccess(response)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                anxietyEditActivity.onSaveAnxietyFail(errorResponse.message)
                saveButtonEnable = true
            }
        })
    }
}