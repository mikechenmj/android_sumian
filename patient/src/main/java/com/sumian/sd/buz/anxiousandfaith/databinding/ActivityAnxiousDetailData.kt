package com.sumian.sd.buz.anxiousandfaith.databinding

import android.view.View

data class ActivityAnxiousDetailData(
        val hasDetailedPlanChecked: Boolean,
        val hasHardChecked: Boolean,
        val detailContent: String,
        val resolvePlanContent: String,
        val notHardProblemContent: String,
        val howToResolveTitle: String,
        val howToResolveContent: String,
        val solutionProblemTime: String,
        val updateTimeFormatted: String,
        val onEditListener: View.OnClickListener)