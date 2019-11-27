package com.sumian.sd.buz.anxiousandfaith.databinding

import android.view.View
import android.widget.BaseAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sumian.common.widget.SumianFlexboxLayout
import com.sumian.sd.buz.anxiousandfaith.MoodCognitionBiasListActivity

data class ActivityMoodCognitionBiasListData(
        val cognitionBiasAdapter: MoodCognitionBiasListActivity.MoodCognitionBiasListAdapter,
        val onConfirmClickListener : View.OnClickListener)