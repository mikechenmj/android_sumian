package com.sumian.sd.buz.anxiousandfaith

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.FragmentContainer
import com.sumian.sd.R
import com.sumian.sd.buz.anxiousandfaith.bean.MoodDiaryData
import com.sumian.sd.buz.stat.StatConstants

@SuppressLint("SetTextI18n")
class MoodDiaryEditActivity : TitleBaseActivity(), FragmentContainer, MoodDiaryData.MoodDiaryDataOwner {

    private var mPageIndex = -1
    private var mMoodDiaryData: MoodDiaryData? = null

    companion object {
        const val MOOD_SELECT_FRAGMENT_INDEX = 0
        const val MOOD_DETAIL_FRAGMENT_INDEX = 1
        const val MOOD_CHALLENGE_FRAGMENT_INDEX = 2
        const val MOOD_RATIONAL_BELIEF_FRAGMENT_INDEX = 3
        const val EXTRA_KEY_PAGE_INDEX = "extra_key_page_index"

        fun launch(moodDiaryData: MoodDiaryData? = null, pageIndex: Int = MOOD_SELECT_FRAGMENT_INDEX) {
            val intent = Intent(ActivityUtils.getTopActivity(), MoodDiaryEditActivity::class.java)
            intent.putExtra(MoodDiaryData.EXTRA_KEY_MOOD_DIARY, moodDiaryData)
            intent.putExtra(EXTRA_KEY_PAGE_INDEX, pageIndex)
            ActivityUtils.startActivity(intent)
        }
    }

    override fun getMoodDiaryData(): MoodDiaryData? {
        return mMoodDiaryData
    }

    override fun setMoodDiaryData(moodDiaryData: MoodDiaryData?) {
        mMoodDiaryData = moodDiaryData
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_mood_diary
    }

    override fun switchNextFragment(data: Bundle?) {
        switchToFragment(mPageIndex + 1, data)
    }

    override fun switchToFragment(index: Int, data: Bundle?, animated: Boolean) {
        var fragment: Fragment? = null
        when (index) {
            MOOD_SELECT_FRAGMENT_INDEX -> {
                fragment = MoodSelectFragment()
            }
            MOOD_DETAIL_FRAGMENT_INDEX -> {
                fragment = MoodDetailFragment()
            }
            MOOD_CHALLENGE_FRAGMENT_INDEX -> {
                fragment = MoodChallengeFragment()
            }
            MOOD_RATIONAL_BELIEF_FRAGMENT_INDEX -> {
                fragment = RationalBeliefFragment()
            }
        }
        if (fragment == null) {
            return
        }
        fragment.arguments = data
        var transaction = supportFragmentManager.beginTransaction()
        if (animated) {
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                    android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
        transaction.replace(R.id.mood_diary_layout_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
        mPageIndex = index
    }

    override fun getPageName(): String {
        return StatConstants.page_add_mood_diary
    }

    override fun onBackPressed() {
        if (mPageIndex <= MOOD_DETAIL_FRAGMENT_INDEX) {
            finish()
        } else {
            super.onBackPressed()
            mPageIndex -= 1
        }

    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.mood_diary)
        mMoodDiaryData = intent.getParcelableExtra(MoodDiaryData.EXTRA_KEY_MOOD_DIARY)
        mPageIndex = intent.getIntExtra(EXTRA_KEY_PAGE_INDEX, MOOD_SELECT_FRAGMENT_INDEX)
        switchToFragment(mPageIndex, null, false)
    }
}