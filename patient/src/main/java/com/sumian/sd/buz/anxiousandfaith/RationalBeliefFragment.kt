package com.sumian.sd.buz.anxiousandfaith


import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.base.BaseFragment
import com.sumian.common.base.FragmentContainer
import com.sumian.common.widget.SumianFlexboxLayout
import com.sumian.sd.R
import com.sumian.sd.buz.anxiousandfaith.bean.MoodDiaryData
import com.sumian.sd.buz.anxiousandfaith.databinding.FragmentMoodChallengeData
import com.sumian.sd.buz.anxiousandfaith.databinding.FragmentRationalBeliefData
import com.sumian.sd.buz.anxiousandfaith.event.MoodDiaryChangeEvent
import com.sumian.sd.common.utils.EventBusUtil
import com.sumian.sd.databinding.FragmentMoodChallengeLayoutBinding
import com.sumian.sd.databinding.FragmentRationalBeliefLayoutBinding

class RationalBeliefFragment : BaseFragment() {

    private var mBinding: FragmentRationalBeliefLayoutBinding? = null
    private var mMoodDiaryDataOwner: MoodDiaryData.MoodDiaryDataOwner? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_rational_belief_layout
    }

    override fun onResume() {
        super.onResume()
        if (activity != null) {
            (activity as BaseActivity).setTitle(R.string.mood_diary_reasonable_belief_practice_title)
        }
    }

    override fun initWidget() {
        if (view == null) {
            return
        }
        var binding: FragmentRationalBeliefLayoutBinding? = DataBindingUtil.bind<FragmentRationalBeliefLayoutBinding>(view!!)
        if (binding == null) {
            return
        }

        if (activity != null && activity is MoodDiaryData.MoodDiaryDataOwner) {
            mMoodDiaryDataOwner = activity as MoodDiaryData.MoodDiaryDataOwner
        }

        binding.data = FragmentRationalBeliefData(this, mMoodDiaryDataOwner?.getMoodDiaryData())
        mBinding = binding
    }

    fun onSaveMoodDiaryFail(errMessage: String) {
        ToastUtils.showShort(errMessage)
    }

    fun onSaveMoodDiarySuccess(response: MoodDiaryData?) {
        mMoodDiaryDataOwner?.setMoodDiaryData(response)
        EventBusUtil.postStickyEvent(MoodDiaryChangeEvent(response!!))
        MoodDiaryDetailActivity.launch(mMoodDiaryDataOwner?.getMoodDiaryData())
        activity?.finish()
    }
}