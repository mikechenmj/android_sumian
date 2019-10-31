package com.sumian.sd.buz.anxiousandfaith


import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.base.BaseFragment
import com.sumian.common.base.FragmentContainer
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.widget.SumianFlexboxLayout
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.anxiousandfaith.bean.MoodDiaryData
import com.sumian.sd.buz.anxiousandfaith.constant.MoodDiaryType
import com.sumian.sd.buz.anxiousandfaith.databinding.FragmentMoodDetailData
import com.sumian.sd.buz.anxiousandfaith.event.MoodDiaryChangeEvent
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.EventBusUtil
import com.sumian.sd.databinding.FragmentMoodDetailLayoutBinding
import com.sumian.sd.databinding.FragmentMoodSelectLayoutBinding
import kotlinx.android.synthetic.main.activity_faith.*
import java.lang.IllegalArgumentException

class MoodDetailFragment : BaseFragment() {

    private var mBinding: FragmentMoodDetailLayoutBinding? = null
    private var mMoodDiaryDataOwner: MoodDiaryData.MoodDiaryDataOwner? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_mood_detail_layout
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
        if (activity != null && activity is MoodDiaryData.MoodDiaryDataOwner) {
            mMoodDiaryDataOwner = activity as MoodDiaryData.MoodDiaryDataOwner
        }
        var binding: FragmentMoodDetailLayoutBinding? = DataBindingUtil.bind<FragmentMoodDetailLayoutBinding>(view!!)
        if (binding == null) {
            return
        }
        var type = getMoodDiaryType()
        var typeLabels = getMoodDiaryLabel()
        var moodDiaryPositive = MoodDiaryData.isPositiveMoodType(type)
        var moodLabelAdapter = object : BaseAdapter() {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                if (activity == null) {
                    return null
                }
                var view = SumianFlexboxLayout.getSimpleLabelTextView(activity!!)
                SumianFlexboxLayout.updateLabelUi(view, false)
                view.text = typeLabels[position]
                return view
            }

            override fun getItem(position: Int): Any {
                return typeLabels[position]
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun getCount(): Int {
                return typeLabels.size
            }
        }
        binding.data = FragmentMoodDetailData(this,
                MoodDiaryData.getEmotionTextRes(type), MoodDiaryData.getEmotionImageRes(type),
                moodLabelAdapter, moodDiaryPositive)
                .apply {
                    var data = mMoodDiaryDataOwner?.getMoodDiaryData()
                    detailText = data?.scene ?: detailText
                    savedMoodDiaryData = data?.apply {
                        emotionType = type
                        emotions = typeLabels
                    }
                }
        mBinding = binding
    }

    fun onSaveMoodDiaryFail(errMessage: String) {
        ToastUtils.showShort(errMessage)
    }

    fun onSaveMoodDiarySuccess(response: MoodDiaryData?) {
        mMoodDiaryDataOwner?.setMoodDiaryData(response)
        EventBusUtil.postStickyEvent(MoodDiaryChangeEvent(response!!))
        if (response.isPositiveMoodType()) {
            MoodDiaryDetailActivity.launch(mMoodDiaryDataOwner?.getMoodDiaryData())
            activity?.finish()
        }
    }

    fun onChallengeUnreasonableBelief(response: MoodDiaryData?) {
        if (activity is FragmentContainer) {
            var fragmentContainer = activity as FragmentContainer

            fragmentContainer.switchNextFragment(null)
        }
    }

    fun getMoodDiaryType(): Int {
        if (arguments?.get(MoodDiaryData.EXTRA_MOOD_DIARY_TYPE) != null) {
            return (arguments?.get(MoodDiaryData.EXTRA_MOOD_DIARY_TYPE) as MoodDiaryType).value
        }
        return mMoodDiaryDataOwner?.getMoodDiaryData()?.emotionType ?: -1
    }

    fun getMoodDiaryLabel(): List<String> {
        if (arguments?.get(MoodDiaryData.EXTRA_MOOD_DIARY_LABEL) != null) {
            return arguments?.get(MoodDiaryData.EXTRA_MOOD_DIARY_LABEL) as List<String>
        }
        return mMoodDiaryDataOwner?.getMoodDiaryData()?.emotions ?: emptyList()
    }
}