package com.sumian.sd.buz.anxiousandfaith.databinding

import android.view.View
import android.widget.BaseAdapter
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.sumian.common.widget.SumianFlexboxLayout
import com.sumian.sd.buz.anxiousandfaith.constant.MoodDiaryType

data class FragmentSelectMoodData(
        val moodLabelAdapter: BaseAdapter,
        val labelListener: SumianFlexboxLayout.OnItemClickListener,
        val nextListener: View.OnClickListener) : BaseObservable() {

    @get:Bindable
    var moodDiaryType: MoodDiaryType? = null
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR.moodDiaryType)
        }
}