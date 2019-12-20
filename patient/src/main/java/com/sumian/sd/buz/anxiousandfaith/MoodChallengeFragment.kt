package com.sumian.sd.buz.anxiousandfaith


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.sumian.sd.buz.anxiousandfaith.event.MoodDiaryChangeEvent
import com.sumian.sd.common.utils.EventBusUtil
import com.sumian.sd.databinding.FragmentMoodChallengeLayoutBinding

class MoodChallengeFragment : BaseFragment() {

    private var mBinding: FragmentMoodChallengeLayoutBinding? = null
    private var mMoodDiaryDataOwner: MoodDiaryData.MoodDiaryDataOwner? = null
    private var mCognitionBias: List<String> = emptyList()

    companion object {
        private const val ACTIVITY_REQUEST_CODE_COGNITION_BIAS = 0
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_mood_challenge_layout
    }

    override fun onResume() {
        super.onResume()
        if (activity != null) {
            (activity as BaseActivity).setTitle(R.string.mood_diary_challenge_unreasonable_belief_title)
        }
    }

    override fun initWidget() {
        if (view == null) {
            return
        }
        if (activity != null && activity is MoodDiaryData.MoodDiaryDataOwner) {
            mMoodDiaryDataOwner = activity as MoodDiaryData.MoodDiaryDataOwner
        }
        var binding: FragmentMoodChallengeLayoutBinding? = DataBindingUtil.bind<FragmentMoodChallengeLayoutBinding>(view!!)
        if (binding == null) {
            return
        }

        var cognitionBiasAdapter = object : BaseAdapter() {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                if (activity == null) {
                    return null
                }
                var view = SumianFlexboxLayout.getSimpleLabelTextView(activity!!)
                SumianFlexboxLayout.updateLabelUi(view, false)
                view.text = mCognitionBias[position]
                return view
            }

            override fun getItem(position: Int): Any {
                return mCognitionBias[position]
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun getCount(): Int {
                return mCognitionBias.size
            }
        }

        var onCognitiveBiasClickListener = View.OnClickListener {
            var act = activity
            if (act == null) {
                return@OnClickListener
            }
            MoodCognitionBiasListActivity.startForResult(this, ACTIVITY_REQUEST_CODE_COGNITION_BIAS)
        }

        var moodDiaryData = mMoodDiaryDataOwner?.getMoodDiaryData()
        mCognitionBias = moodDiaryData?.cognitionBias ?: mCognitionBias
        binding.data = FragmentMoodChallengeData(
                this, moodDiaryData, cognitionBiasAdapter, onCognitiveBiasClickListener
        ).apply { cognitionBias = mCognitionBias }
        mBinding = binding
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ACTIVITY_REQUEST_CODE_COGNITION_BIAS -> {
                if (resultCode == Activity.RESULT_OK) {
                    mCognitionBias = data?.getStringArrayExtra(MoodCognitionBiasListActivity.EXTRA_COGNITION_BIAS_CHECKED_LABELS)?.toList()
                            ?: mCognitionBias
                    var data = mBinding?.data
                    data?.moodLabelAdapter?.notifyDataSetChanged()
                    data?.cognitionBias = mCognitionBias
                }
            }
        }
    }

    fun onSaveMoodDiaryFail(errMessage: String) {
        ToastUtils.showShort(errMessage)
    }

    fun onSaveMoodDiarySuccess(response: MoodDiaryData?) {
        mMoodDiaryDataOwner?.setMoodDiaryData(response)
        EventBusUtil.postStickyEvent(MoodDiaryChangeEvent(response!!))
        if (response.isPositiveMoodType()) {
            activity?.finish()
        }
    }

    fun onReasonableBeliefPractice(response: MoodDiaryData?) {
        if (activity is FragmentContainer) {
            var fragmentContainer = activity as FragmentContainer
            fragmentContainer.switchNextFragment(null)
        }
    }
}