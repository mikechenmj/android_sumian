package com.sumian.sd.buz.anxiousandfaith

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.FragmentContainer
import com.sumian.sd.R
import com.sumian.sd.buz.anxiousandfaith.bean.MoodDiaryData
import com.sumian.sd.buz.anxiousandfaith.bean.MoodDiaryData.Companion.EXTRA_KEY_MOOD_DIARY
import com.sumian.sd.buz.stat.StatConstants

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/26 17:38
 * desc   :
 * version: 1.0
 */
@SuppressLint("SetTextI18n")
class MoodDiaryEditActivity : WhileTitleNavBgActivity(), FragmentContainer {
    private var mProgress = 0
    private var mEvent = ""
    private var mThought = ""
    private var mEmotion = -1
    private var mId = -1

    private var mPageIndex = MOOD_SELECT_DIARY_FRAGMENT_INDEX

    override fun getLayoutId(): Int {
        return R.layout.activity_mood_diary
    }

    override fun switchNextFragment(data: Bundle?) {
        switchToFragment(mPageIndex + 1, data)
    }

    override fun switchToFragment(index: Int, data: Bundle?) {
        var fragment: Fragment? = null
        when (index) {
            MOOD_SELECT_DIARY_FRAGMENT_INDEX -> {
                fragment = MoodSelectFragment()
            }
        }
        if (fragment == null) {
            return
        }
        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mood_diary_layout_container, fragment)
        if (mPageIndex != MOOD_SELECT_DIARY_FRAGMENT_INDEX) {
            transaction.addToBackStack(fragment::class.java.simpleName)
        }
        transaction.commit()
        mPageIndex = index
    }

    companion object {

        const val MOOD_SELECT_DIARY_FRAGMENT_INDEX = 0

        fun launch(moodDiaryData: MoodDiaryData? = null) {
            val intent = Intent(ActivityUtils.getTopActivity(), MoodDiaryEditActivity::class.java)
            intent.putExtra(EXTRA_KEY_MOOD_DIARY, moodDiaryData)
            ActivityUtils.startActivity(intent)
        }
    }

    override fun getPageName(): String {
        return StatConstants.page_add_mood_diary
    }

//    override fun initBundle(bundle: Bundle) {
//        super.initBundle(bundle)
//        val faithData = bundle.getParcelable<MoodDiaryData>(EXTRA_KEY_MOOD_DIARY)
//        if (faithData != null) {
//            mId = faithData.id
//            mEvent = faithData.scene
//            mThought = faithData.idea
//            mEmotion = faithData.emotion_type
//        }
//    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.mood_diary)
        switchToFragment(mPageIndex, null)
    }

//    private fun preStep() {
//        if (mProgress > 0) {
//            mProgress--
//            updateUIByProgress(mProgress)
//        }
//    }
//
//    override fun onBackPressed() {
//        if (mProgress > 0) {
//            preStep()
//        } else {
//            super.onBackPressed()
//        }
//    }
}