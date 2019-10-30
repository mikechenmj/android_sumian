package com.sumian.sd.buz.anxiousandfaith

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.sumian.common.base.BaseActivity
import com.sumian.common.base.BaseFragment
import com.sumian.common.base.FragmentContainer
import com.sumian.common.helper.ToastHelper
import com.sumian.common.widget.SumianFlexboxLayout
import com.sumian.sd.R
import com.sumian.sd.buz.anxiousandfaith.bean.MoodDiaryData
import com.sumian.sd.buz.anxiousandfaith.constant.MoodDiaryType
import com.sumian.sd.buz.anxiousandfaith.databinding.FragmentSelectMoodData
import com.sumian.sd.databinding.FragmentMoodSelectLayoutBinding

class MoodSelectFragment : BaseFragment() {

    private val mAngryLabelData = arrayOf(
            SumianFlexboxLayout.SimpleLabelBean("尴尬", false),
            SumianFlexboxLayout.SimpleLabelBean("警觉", false),
            SumianFlexboxLayout.SimpleLabelBean("愤怒", false),
            SumianFlexboxLayout.SimpleLabelBean("怨恨", false)
    )

    private val mSadLabelData = arrayOf(
            SumianFlexboxLayout.SimpleLabelBean("价值感低", false),
            SumianFlexboxLayout.SimpleLabelBean("悲伤", false),
            SumianFlexboxLayout.SimpleLabelBean("担忧", false),
            SumianFlexboxLayout.SimpleLabelBean("害怕", false),
            SumianFlexboxLayout.SimpleLabelBean("恐惧", false),
            SumianFlexboxLayout.SimpleLabelBean("绝望", false)
    )

    private val mDullLabelData = arrayOf(
            SumianFlexboxLayout.SimpleLabelBean("平静", false),
            SumianFlexboxLayout.SimpleLabelBean("轻松", false),
            SumianFlexboxLayout.SimpleLabelBean("幸福", false)
    )

    private val mHappenLabelData = arrayOf(
            SumianFlexboxLayout.SimpleLabelBean("自信", false),
            SumianFlexboxLayout.SimpleLabelBean("充满希望", false),
            SumianFlexboxLayout.SimpleLabelBean("喜悦", false),
            SumianFlexboxLayout.SimpleLabelBean("高兴", false)
    )

    private val mExcitedLabelData = arrayOf(
            SumianFlexboxLayout.SimpleLabelBean("积极", false),
            SumianFlexboxLayout.SimpleLabelBean("惊喜", false),
            SumianFlexboxLayout.SimpleLabelBean("狂喜", false),
            SumianFlexboxLayout.SimpleLabelBean("激动", false)
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
            data[position].isChecked = isChecked
            SumianFlexboxLayout.updateLabelUi(view as TextView, isChecked)
        }
    }

    private val mNextClickListener = View.OnClickListener {
        var moodDiaryType = mBinding?.data?.moodDiaryType
        if (moodDiaryType == null) {
            ToastHelper.show(getString(R.string.mood_un_selected_tip))
            return@OnClickListener
        }

        var data = getLabelData()
        var checkedLabels = ArrayList<String>()
        for (label in data) {
            if (label.isChecked) {
                checkedLabels.add(label.label)
            }
        }
        if (checkedLabels.size < 1) {
            ToastHelper.show(getString(R.string.mood_un_selected_tip))
            return@OnClickListener
        }

        if (activity is FragmentContainer) {
            var fragmentContainer = activity as FragmentContainer

            fragmentContainer.switchNextFragment(Bundle().apply {
                putSerializable(MoodDiaryData.EXTRA_MOOD_DIARY_TYPE, moodDiaryType)
                putStringArrayList(MoodDiaryData.EXTRA_MOOD_DIARY_LABEL, checkedLabels)
            })
        }
    }

    private var mBinding: FragmentMoodSelectLayoutBinding? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_mood_select_layout
    }

    override fun onResume() {
        super.onResume()
        if (activity != null) {
            (activity as BaseActivity).setTitle(R.string.mood_diary)
        }
    }

    override fun initWidget() {
        if (view == null) {
            return
        }
        var binding: FragmentMoodSelectLayoutBinding? = DataBindingUtil.bind<FragmentMoodSelectLayoutBinding>(view!!)
        if (binding == null) {
            return
        }
        binding.data = FragmentSelectMoodData(this, mFlexLabelAdapter, mFlexLabelItemClickListener, mNextClickListener)
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
            MoodDiaryType.UNHAPPY -> {
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

    fun onMoodDiaryTypeChange(oldType: MoodDiaryType?) {
        var data = getLabelData()
        for (label in data) {
            label.isChecked = false
        }
    }
}