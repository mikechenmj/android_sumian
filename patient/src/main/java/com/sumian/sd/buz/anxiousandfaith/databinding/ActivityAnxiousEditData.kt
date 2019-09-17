package com.sumian.sd.buz.anxiousandfaith.databinding

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.databinding.*
import androidx.databinding.library.baseAdapters.BR
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.anxiousandfaith.bean.AnxietyData
import com.sumian.sd.buz.anxiousandfaith.event.AnxietyChangeEvent
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.EventBusUtil
import com.sumian.sd.widget.divider.SettingDividerView
import com.sumian.sd.widget.sheet.SelectTimeHHmmBottomSheet
import java.util.*

@BindingMethods(value = [
    BindingMethod(
            type = SettingDividerView::class,
            attribute = "type_content",
            method = "setContent")])

class ActivityAnxiousEditData(var baseActivity: BaseActivity, var anxietyData: AnxietyData?) : BaseObservable() {

    companion object {
        private const val DETAIL_TEXT_MAX_COUNT = 200
        private const val SOLUTION_TEXT_MAX_COUNT = 50
        private const val HARD_TEXT_MAX_COUNT = 50
        @InverseMethod("getIsCheckedFromId")
        @JvmStatic
        fun getIdFromIsChecked(positiveId: Int, negativeId: Int, isChecked: Boolean): Int {
            return if (isChecked) positiveId else negativeId
        }

        @JvmStatic
        fun getIsCheckedFromId(positiveId: Int, negativeId: Int, id: Int): Boolean {
            return id == positiveId
        }
    }

    @get:Bindable
    var detailText: String = ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            detailTextCount = "$length/$DETAIL_TEXT_MAX_COUNT"
            detailTextCountOutOfMax = length > DETAIL_TEXT_MAX_COUNT
            notifyPropertyChanged(BR.detailText)
        }

    @get:Bindable
    var detailTextCount: String = "0/$DETAIL_TEXT_MAX_COUNT"
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
    var solutionChecked: Boolean = true
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.solutionChecked)
        }

    @get:Bindable
    var solutionText: String = ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            solutionTextCount = "$length/$SOLUTION_TEXT_MAX_COUNT"
            solutionTextCountOutOfMax = length > SOLUTION_TEXT_MAX_COUNT
            notifyPropertyChanged(BR.solutionText)
        }

    @get:Bindable
    var solutionTextCount: String = "0/$SOLUTION_TEXT_MAX_COUNT"
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
    var hardChecked: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.hardChecked)
        }


    @get:Bindable
    var hardText: String = ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            hardTextCount = "$length/$HARD_TEXT_MAX_COUNT"
            hardTextCountOutOfMax = length > HARD_TEXT_MAX_COUNT
            notifyPropertyChanged(BR.hardText)
        }

    @get:Bindable
    var hardTextCount: String = "0/$HARD_TEXT_MAX_COUNT"
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


    @get:Bindable
    var howToResolveCheckedId: Int = R.id.rb_anxiety_ask_how_to_resolve_one
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.howToResolveCheckedId)
        }

    @get:Bindable
    var askHowToResolveOneText: String = ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            askHowToResolveOneTextCount = "$length/100"
            askHowToResolveOneTextCountOutOfMax = length > 100
            notifyPropertyChanged(BR.askHowToResolveOneText)
        }

    @get:Bindable
    var askHowToResolveOneTextCount: String = "0/100"
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
    var askHowToResolveTwoText: String = ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            askHowToResolveTwoTextCount = "$length/100"
            askHowToResolveTwoTextCountOutOfMax = length > 100
            notifyPropertyChanged(BR.askHowToResolveTwoText)
        }

    @get:Bindable
    var askHowToResolveTwoTextCount: String = "0/100"
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
    var askHowToResolveThreeText: String = ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val length = value.length
            askHowToResolveThreeTextCount = "$length/100"
            askHowToResolveThreeTextCountOutOfMax = length > 100
            notifyPropertyChanged(BR.askHowToResolveThreeText)
        }

    @get:Bindable
    var askHowToResolveThreeTextCount: String = "0/100"
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

    @get:Bindable
    var remindSettingTypeContent: String = "请设置"
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

    var remindTimeInMillis: Long = 0

    fun showBottomSheet(context: Context) {
        val bottomSheet = SelectTimeHHmmBottomSheet(context, R.string.set_remind_time, SelectTimeHHmmBottomSheet.DEFAULT_DAY,
                SelectTimeHHmmBottomSheet.DEFAULT_HOUR, SelectTimeHHmmBottomSheet.DEFAULT_MINUTE,
                object : SelectTimeHHmmBottomSheet.OnTimePickedListener {
                    override fun onTimePicked(hour: Int, minute: Int) {}

                    override fun onTimePicked(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
                        super.onTimePicked(year, month, day, hour, minute)
                        var formatDateContent = context.getString(R.string.pattern_yyyy_MM_dd_hh_mm).format(year, month, day, hour, minute)
                        remindSettingTypeContent = formatDateContent
                        remindTimeInMillis = Calendar.getInstance().apply { set(year, month, day, hour, minute) }.timeInMillis
                    }
                })
        bottomSheet.setOnDismissListener {}
        bottomSheet.show()
    }

    fun saveAnxiety() {
        if (detailTextCountOutOfMax || solutionTextCountOutOfMax || hardTextCountOutOfMax) {
            ToastUtils.showShort(R.string.input_is_too_long)
            return
        }
        if (TextUtils.isEmpty(detailText) || (TextUtils.isEmpty(solutionText) && solutionChecked) || remindTimeInMillis < Calendar.getInstance().timeInMillis) {
            ToastUtils.showShort(R.string.please_finish_question_first)
            return
        }

        val call = if (anxietyData == null) {
            AppManager.getSdHttpService().addAnxiety(detailText, solutionText)
        } else {
            AppManager.getSdHttpService().updateAnxiety(anxietyData!!.id, detailText, solutionText)
        }
        baseActivity.addCall(call)
        saveButtonEnable = false
        call.enqueue(object : BaseSdResponseCallback<AnxietyData>() {
            override fun onSuccess(response: AnxietyData?) {
                EventBusUtil.postStickyEvent(AnxietyChangeEvent(response!!))
                baseActivity.finish()
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
                saveButtonEnable = true
            }
        })
    }
}