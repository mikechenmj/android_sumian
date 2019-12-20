package com.sumian.sd.buz.anxiousandfaith.databinding

import android.view.View
import android.widget.BaseAdapter
import com.sumian.common.widget.SumianFlexboxLayout

data class ActivityMoodCognitionBiasData(
        val cognitionBiasAdapter: BaseAdapter,
        val cognitionBiasItemListener: SumianFlexboxLayout.OnItemClickListener,
        val onConfirmClickListener : View.OnClickListener)