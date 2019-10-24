package com.sumian.sd.buz.anxiousandfaith

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.TimeUtilV2
import com.sumian.common.widget.SumianFlexboxLayout
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.anxiousandfaith.bean.MoodDiaryData
import com.sumian.sd.buz.anxiousandfaith.bean.MoodDiaryData.Companion.EXTRA_KEY_MOOD_DIARY
import com.sumian.sd.buz.anxiousandfaith.constant.MoodDiaryType
import com.sumian.sd.buz.anxiousandfaith.databinding.ActivityMoodDiaryDetailData
import com.sumian.sd.buz.anxiousandfaith.event.MoodDiaryChangeEvent
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.EventBusUtil
import com.sumian.sd.databinding.ActivityMoodDiaryDetailBinding
import kotlinx.android.synthetic.main.activity_mood_diary_detail.*
import org.greenrobot.eventbus.Subscribe

class MoodDiaryDetailActivity : WhileTitleNavBgActivity() {

    private var mMoodDiaryData: MoodDiaryData? = null
    private var mViewDataBinding: ActivityMoodDiaryDetailBinding? = null

    companion object {

        private const val ACTIVITY_REQUEST_CODE_COGNITION_BIAS = 0

        fun launch(moodDiaryData: MoodDiaryData? = null) {
            val intent = Intent(ActivityUtils.getTopActivity(), MoodDiaryDetailActivity::class.java)
            intent.putExtra(EXTRA_KEY_MOOD_DIARY, moodDiaryData)
            ActivityUtils.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_mood_diary_detail
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        mMoodDiaryData = bundle.getParcelable(EXTRA_KEY_MOOD_DIARY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBusUtil.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusUtil.unregister(this)
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(getString(R.string.mood_diary_detail_title_text))
        refreshDataBinding()
    }

    @Subscribe
    fun onAnxietyChangeEvent(event: MoodDiaryChangeEvent) {
        mMoodDiaryData = event.moodDiary
        refreshDataBinding()
    }

    private fun refreshDataBinding() {
        if (mViewDataBinding == null) {
            mViewDataBinding = DataBindingUtil.bind(activity_mood_diary_detail)
        }
        var moodLabelAdapter = object : BaseAdapter() {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                var view = SumianFlexboxLayout.getSimpleLabelTextView(this@MoodDiaryDetailActivity)
                SumianFlexboxLayout.updateLabelUi(view, false)
                var cognitionBias = mMoodDiaryData?.emotions ?: emptyList()
                view.text = cognitionBias[position]
                return view
            }

            override fun getItem(position: Int): Any {
                var cognitionBias = mMoodDiaryData?.emotions ?: emptyList()
                return cognitionBias[position]
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun getCount(): Int {
                var cognitionBias = mMoodDiaryData?.emotions ?: emptyList()
                return cognitionBias.size
            }
        }

        var cognitionBiasAdapter = object : BaseAdapter() {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                var view = SumianFlexboxLayout.getSimpleLabelTextView(this@MoodDiaryDetailActivity)
                SumianFlexboxLayout.updateLabelUi(view, false)
                var cognitionBias = mMoodDiaryData?.cognitionBias ?: emptyList()
                view.text = cognitionBias[position]
                return view
            }

            override fun getItem(position: Int): Any {
                var cognitionBias = mMoodDiaryData?.cognitionBias ?: emptyList()
                return cognitionBias[position]
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun getCount(): Int {
                var cognitionBias = mMoodDiaryData?.cognitionBias ?: emptyList()
                return cognitionBias.size
            }
        }

        var onCognitiveBiasClickListener = View.OnClickListener {
            MoodCognitionBiasActivity.startForResult(this, ACTIVITY_REQUEST_CODE_COGNITION_BIAS)
        }

        var binding = mViewDataBinding
        binding?.data = ActivityMoodDiaryDetailData(this, mMoodDiaryData,
                moodLabelAdapter, cognitionBiasAdapter, onCognitiveBiasClickListener).apply { editMode = false }
        if (mMoodDiaryData?.isPositiveMoodType() == false) {
            binding?.vsActivityMoodDiaryNoPositiveDetail?.viewStub?.inflate()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ACTIVITY_REQUEST_CODE_COGNITION_BIAS -> {
                if (resultCode == Activity.RESULT_OK) {
                    var bindingData = mViewDataBinding?.data
                    var cognitionBias = data?.getStringArrayExtra(MoodCognitionBiasActivity.EXTRA_COGNITION_BIAS_CHECKED_LABELS)?.toList()
                            ?: mMoodDiaryData?.cognitionBias ?: emptyList()
                    bindingData?.cognitionBias = cognitionBias
                    bindingData?.cognitionBiasAdapter?.notifyDataSetChanged()
                }
            }
        }
    }

    fun onSaveMoodDiarySuccess(response: MoodDiaryData?) {
        EventBusUtil.postStickyEvent(MoodDiaryChangeEvent(response!!))
    }

    fun onSaveMoodDiaryFail(errMessage: String) {
        ToastUtils.showShort(errMessage)
    }

    fun onChangeToEditMode() {
        et_mood_reason_content.post {
            et_mood_reason_content.requestFocus()
        }
    }
}