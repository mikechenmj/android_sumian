package com.sumian.sd.buz.anxiousandfaith

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sumian.common.base.BaseFragment
import com.sumian.common.base.FragmentContainer
import com.sumian.common.helper.ToastHelper
import com.sumian.common.widget.SumianFlexboxLayout
import com.sumian.sd.R
import com.sumian.sd.buz.anxiousandfaith.constant.MoodDiaryType
import com.sumian.sd.buz.anxiousandfaith.databinding.FragmentSelectMoodData
import com.sumian.sd.databinding.FragmentMoodSelectLayoutBinding

class MoodSelectFragment : BaseFragment() {

    private val mAngryLabelData = arrayOf(
            SumianFlexboxLayout.SimpleLabelBean("生气", false),
            SumianFlexboxLayout.SimpleLabelBean("好生气", false),
            SumianFlexboxLayout.SimpleLabelBean("好生气", false),
            SumianFlexboxLayout.SimpleLabelBean("好生气", false),
            SumianFlexboxLayout.SimpleLabelBean("生气", false),
            SumianFlexboxLayout.SimpleLabelBean("生气", false),
            SumianFlexboxLayout.SimpleLabelBean("好生气", false),
            SumianFlexboxLayout.SimpleLabelBean("生气", false)
    )

    private val mSadLabelData = arrayOf(
            SumianFlexboxLayout.SimpleLabelBean("难过", false),
            SumianFlexboxLayout.SimpleLabelBean("好难过", false),
            SumianFlexboxLayout.SimpleLabelBean("好难过", false),
            SumianFlexboxLayout.SimpleLabelBean("好难过", false),
            SumianFlexboxLayout.SimpleLabelBean("难过", false),
            SumianFlexboxLayout.SimpleLabelBean("难过", false),
            SumianFlexboxLayout.SimpleLabelBean("好难过", false),
            SumianFlexboxLayout.SimpleLabelBean("难过", false)
    )

    private val mDullLabelData = arrayOf(
            SumianFlexboxLayout.SimpleLabelBean("平静", false),
            SumianFlexboxLayout.SimpleLabelBean("好平静", false),
            SumianFlexboxLayout.SimpleLabelBean("好平静", false),
            SumianFlexboxLayout.SimpleLabelBean("好平静", false),
            SumianFlexboxLayout.SimpleLabelBean("平静", false),
            SumianFlexboxLayout.SimpleLabelBean("平静", false),
            SumianFlexboxLayout.SimpleLabelBean("好平静", false),
            SumianFlexboxLayout.SimpleLabelBean("平静", false)
    )

    private val mHappenLabelData = arrayOf(
            SumianFlexboxLayout.SimpleLabelBean("开心", false),
            SumianFlexboxLayout.SimpleLabelBean("好开心", false),
            SumianFlexboxLayout.SimpleLabelBean("好开心", false),
            SumianFlexboxLayout.SimpleLabelBean("好开心", false),
            SumianFlexboxLayout.SimpleLabelBean("开心", false),
            SumianFlexboxLayout.SimpleLabelBean("开心", false),
            SumianFlexboxLayout.SimpleLabelBean("好开心", false),
            SumianFlexboxLayout.SimpleLabelBean("开心", false)
    )

    private val mExcitedLabelData = arrayOf(
            SumianFlexboxLayout.SimpleLabelBean("兴奋", false),
            SumianFlexboxLayout.SimpleLabelBean("好兴奋", false),
            SumianFlexboxLayout.SimpleLabelBean("好兴奋", false),
            SumianFlexboxLayout.SimpleLabelBean("好兴奋", false),
            SumianFlexboxLayout.SimpleLabelBean("兴奋", false),
            SumianFlexboxLayout.SimpleLabelBean("兴奋", false),
            SumianFlexboxLayout.SimpleLabelBean("好兴奋", false),
            SumianFlexboxLayout.SimpleLabelBean("兴奋", false)
    )

    private val mFlexLabelAdapter = object : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var context = activity
            if (context == null) {
                return null
            }
            var view = SumianFlexboxLayout.getSimpleLabelTextView(context)
            SumianFlexboxLayout.updateLabelUi(view, getLabelData()[position].isChecked)
            view.text = (getItem(position) as SumianFlexboxLayout.SimpleLabelBean).label
            return view
        }

        override fun getItem(position: Int): Any {
            return getLabelData()[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return getLabelData().size
        }
    }

    private val mFlexLabelItemClickListener = object : SumianFlexboxLayout.OnItemClickListener {
        override fun onItemClick(parent: SumianFlexboxLayout, view: View, position: Int, id: Long) {
            var isChecked = !getLabelData()[position].isChecked
            var data = getLabelData()
            for (i in data.indices) {
                data[i].isChecked = false
            }
            data[position].isChecked = isChecked
            for (i in 0 until parent.childCount) {
                SumianFlexboxLayout.updateLabelUi(parent.getChildAt(i) as TextView, false)
            }
            SumianFlexboxLayout.updateLabelUi(view as TextView, isChecked)
        }
    }

    private val mNextClickListener = View.OnClickListener {
        var moodDiaryType = mBinding?.data?.moodDiaryType
        var data = getLabelData()
        if (moodDiaryType == null) {
            ToastHelper.show(getString(R.string.mood_un_selected_tip))
            return@OnClickListener
        }

        var checkedLabel :SumianFlexboxLayout.SimpleLabelBean? = null
        for (label in data) {
            if (label.isChecked) {
                checkedLabel = label
            }
        }
        if (checkedLabel == null) {
            ToastHelper.show(getString(R.string.mood_un_selected_tip))
            return@OnClickListener
        }

        if (activity is FragmentContainer) {
           var fragmentContainer =  activity as FragmentContainer
            fragmentContainer.switchNextFragment(null)
        }
    }

    private var mBinding: FragmentMoodSelectLayoutBinding? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_mood_select_layout
    }

    override fun initWidget() {
        if (view == null) {
            return
        }
        var binding: FragmentMoodSelectLayoutBinding? = DataBindingUtil.bind<FragmentMoodSelectLayoutBinding>(view!!)
        if (binding == null) {
            return
        }
        binding.data = FragmentSelectMoodData(mFlexLabelAdapter, mFlexLabelItemClickListener, mNextClickListener)
        mBinding = binding
    }

    private fun getLabelData(): Array<SumianFlexboxLayout.SimpleLabelBean> {
        var moodDiaryType = mBinding?.data?.moodDiaryType
        if (moodDiaryType == null) {
            return emptyArray()
        }
        return when (moodDiaryType) {
            MoodDiaryType.ANGRY -> {
                mAngryLabelData
            }
            MoodDiaryType.SAD -> {
                mSadLabelData
            }
            MoodDiaryType.DULL -> {
                mDullLabelData
            }
            MoodDiaryType.HAPPEN -> {
                mHappenLabelData
            }
            MoodDiaryType.EXCITED -> {
                mExcitedLabelData
            }
            else -> emptyArray()
        }
    }
}