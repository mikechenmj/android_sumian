package com.sumian.sd.buz.anxiousandfaith.databinding

import android.util.Log
import android.view.View
import android.widget.BaseAdapter
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.sumian.common.widget.SumianFlexboxLayout

data class ActivityMoodDiaryDetailData(
        val moodDiaryTextId: Int,
        val moodDiaryEmotionResId: Int,
        val updateTimeFormatted: String,
        val adapter: BaseAdapter,
        val listener: SumianFlexboxLayout.OnItemClickListener) : BaseObservable() {

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
    var moodReasonContent: String = ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.moodReasonContent)
        }

    @get:Bindable
    var beliefContent: String = ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.beliefContent)
        }

    @get:Bindable
    var unreasonableResultContent: String = ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.unreasonableResultContent)
        }

    @get:Bindable
    var refuteUnreasonableContent: String = ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.refuteUnreasonableContent)
        }

    @get:Bindable
    var reasonableBeliefContent: String = ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.reasonableBeliefContent)
        }

    @get:Bindable
    var reasonableBeliefResultContent: String = ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.reasonableBeliefResultContent)
        }

    fun onItemClickWhenEditMode(parent: SumianFlexboxLayout, view: View, position: Int, id: Long) {
        if (editMode) {
            listener.onItemClick(parent, view, position, id)
        }
    }

}