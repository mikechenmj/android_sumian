package com.sumian.sd.buz.anxiousandfaith

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.utils.TimeUtilV2
import com.sumian.common.widget.SumianFlexboxLayout
import com.sumian.sd.R
import com.sumian.sd.buz.anxiousandfaith.bean.AnxietyMoodDiaryItemViewData
import com.sumian.sd.buz.anxiousandfaith.bean.MoodDiaryData
import com.sumian.sd.buz.anxiousandfaith.bean.MoodDiaryData.Companion.EXTRA_KEY_MOOD_DIARY
import com.sumian.sd.buz.anxiousandfaith.databinding.ActivityMoodDiaryDetailData
import com.sumian.sd.buz.anxiousandfaith.event.MoodDiaryChangeEvent
import com.sumian.sd.common.utils.EventBusUtil
import com.sumian.sd.databinding.ActivityMoodDiaryDetailBinding
import kotlinx.android.synthetic.main.activity_mood_diary_detail.*
import org.greenrobot.eventbus.Subscribe

class MoodDiaryDetailActivity : WhileTitleNavBgActivity() {

    private val mLabelData = arrayOf(
            SumianFlexboxLayout.SimpleLabelBean("非黑即白", false),
            SumianFlexboxLayout.SimpleLabelBean("贴标签", false),
            SumianFlexboxLayout.SimpleLabelBean("不公平的比较", true)
    )

    private var mMoodDiaryData: MoodDiaryData? = null
    private var mViewDataBinding: ActivityMoodDiaryDetailBinding? = null
    private var mFlexLabelAdapter = object : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view = SumianFlexboxLayout.getSimpleLabelTextView(this@MoodDiaryDetailActivity)
            SumianFlexboxLayout.updateLabelUi(view as TextView, mLabelData[position].isChecked)
            view.text = (getItem(position) as SumianFlexboxLayout.SimpleLabelBean).label
            return view
        }

        override fun getItem(position: Int): Any {
            return mLabelData[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return mLabelData.size
        }
    }

    private var mFlexLabelItemClickListener = object : SumianFlexboxLayout.OnItemClickListener {
        override fun onItemClick(parent: SumianFlexboxLayout, view: View, position: Int, id: Long) {
            var isChecked = !mLabelData[position].isChecked
            mLabelData[position].isChecked = isChecked
            SumianFlexboxLayout.updateLabelUi(view as TextView, isChecked)
        }
    }

    companion object {
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
        mTitleBar.setMenuTextDpSize(15)
        mTitleBar.setMenuText(getString(
                if (mViewDataBinding?.data?.editMode == true)
                    R.string.anxiety_mood_diary_detail_save_menu_text
                else
                    R.string.anxiety_mood_diary_detail_edit_menu_text))
        mTitleBar.setOnMenuClickListener {
            mViewDataBinding?.data?.editMode = !mViewDataBinding!!.data!!.editMode
            mTitleBar.setMenuText(getString(
                    if (mViewDataBinding?.data?.editMode == true)
                        R.string.anxiety_mood_diary_detail_save_menu_text
                    else
                        R.string.anxiety_mood_diary_detail_edit_menu_text))
            if (mViewDataBinding?.data?.editMode == true) {
                tv_mood_reason_content.post {
                    tv_mood_reason_content.requestFocus()
                }
            }
        }
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
        var binding = mViewDataBinding
        binding?.data = ActivityMoodDiaryDetailData(
                AnxietyMoodDiaryItemViewData.getEmotionTextRes(mMoodDiaryData?.emotion_type ?: 0),
                AnxietyMoodDiaryItemViewData.getEmotionImageRes(mMoodDiaryData?.emotion_type ?: 0),
                TimeUtilV2.formatYYYYMMDDHHMMss(mMoodDiaryData?.getUpdateAtInMillis() ?: 0),
                mFlexLabelAdapter, mFlexLabelItemClickListener)
                .apply {
                    editMode = false
                    moodReasonContent = mMoodDiaryData?.idea ?: ""
                    beliefContent = "如果我一直睡不着，明天会累到无法工作"    //mMoodDiaryData?.beliefContent
                    unreasonableResultContent = "一晚上没睡着"    //mMoodDiaryData?.unreasonableResultContent
                    refuteUnreasonableContent = "以前也试过睡不好，但是并不影响工作。我只是目前没有睡着，并不代表我会一直都睡不着"  //mMoodDiaryData?.refuteUnreasonableContent
                    reasonableBeliefContent = "虽然我现在睡不着，但可能待会儿就有睡意了"    //mMoodDiaryData?.reasonableBeliefContent
                    reasonableBeliefResultContent = "我这么想之后，感觉轻松一点了"   //mMoodDiaryData?.reasonableBeliefResultContent
                }

        if (mMoodDiaryData?.emotion_type ?: 0 < 3) {
            binding?.vsActivityMoodDiaryNoPositiveDetail?.viewStub?.inflate()
        }
    }

    private fun updateUiFromIsChecked(view: View, isChecked: Boolean) {
        var backgroundRes: Int
        var textColor: Int
        if (isChecked) {
            backgroundRes = R.drawable.label_text_selected_background
            textColor = Color.WHITE
        } else {
            backgroundRes = R.drawable.label_text_un_selected_background
            textColor = resources.getColor(R.color.t2_color)
        }
        view.setBackgroundResource(backgroundRes)
        (view as TextView).setTextColor(textColor)
    }
}