package com.sumian.sd.account.achievement

import android.content.Intent
import android.view.Gravity
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.R
import com.sumian.sd.account.achievement.adapter.MyMedalAdapter
import com.sumian.sd.account.achievement.bean.AchievementData
import com.sumian.sd.account.achievement.bean.AchievementMeta
import com.sumian.sd.account.achievement.bean.LastAchievementData
import com.sumian.sd.account.achievement.bean.ShareAchievement
import com.sumian.sd.account.achievement.contract.LastAchievementContract
import com.sumian.sd.account.achievement.contract.MyAchievementContract
import com.sumian.sd.account.achievement.presenter.LastAchievementPresenter
import com.sumian.sd.account.achievement.presenter.MyAchievementPresenter
import kotlinx.android.synthetic.main.activity_main_my_achievement.*

/**
 * Created by jzz
 *
 * on 2019/1/22
 *
 * desc: 我的勋章成就
 */
class MyAchievementActivity : BasePresenterActivity<MyAchievementContract.Presenter>(), BaseRecyclerAdapter.OnItemClickListener, MyAchievementContract.View, LastAchievementContract.View {

    companion object {
        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, MyAchievementActivity::class.java))
            }
        }
    }

    private val adapter by lazy {
        val myMedalAdapter = MyMedalAdapter(this)
        myMedalAdapter.setOnItemClickListener(this)
        myMedalAdapter
    }

    private lateinit var mAchievementMeta: AchievementMeta
    private lateinit var mAchievementData: AchievementData

    override fun getLayoutId(): Int {
        return R.layout.activity_main_my_achievement
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mPresenter = MyAchievementPresenter.create(this)
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.my_metal)
        recycler.layoutManager = GridLayoutManager(this, 3)
        recycler.itemAnimator = null
        recycler.adapter = adapter
    }

    override fun onItemClick(position: Int, itemId: Long) {
        val achievement = adapter.getItem(position)
        if (!achievement.isHave()) {
            ToastHelper.show(this, getString(R.string.none_get_achievement_toast), Gravity.CENTER)
            return
        }
        val shareAchievement = ShareAchievement(achievement = achievement,
                createdAt = mAchievementData.createdAt,
                id = mAchievementData.id,
                name = mAchievementData.name,
                type = mAchievementData.type,
                updatedAt = mAchievementData.updatedAt,
                avatar = mAchievementData.meta.avatar,
                qrCode = mAchievementData.meta.qrCode)
        MyAchievementShareActivity.show(shareAchievement)
    }

    override fun initData() {
        super.initData()
        mPresenter?.getMyAchievement()
    }

    override fun onResume() {
        super.onResume()
        LastAchievementPresenter.init(this).getLastAchievement()
    }

    override fun showLoading() {
        //super.showLoading()
    }

    override fun dismissLoading() {
        //super.dismissLoading()
    }

    override fun onGetMyAchievementListSuccess(achievementDataList: List<AchievementData>) {
        // TODO
    }

    override fun onGetMyAchievementListForTypeFailed(error: String) {
        ToastHelper.show(this, error, Gravity.CENTER)
    }

    override fun onGetMetaCallback(achievementMeta: AchievementMeta) {
        this.mAchievementMeta = achievementMeta
    }

    override fun onGetMyAchievementListForTypeSuccess(achievementData: AchievementData) {
        tv_share_medal_title.text = achievementData.name
        adapter.resetItem(achievementData.achievements)
        this.mAchievementData = achievementData
    }

    override fun onGetMyAchievementListTypeFailed(error: String) {
        onGetMyAchievementListForTypeFailed(error)
    }

    override fun onGetAchievementListForTypeSuccess(lastAchievementData: LastAchievementData) {
        MyAchievementShareActivity.showFromLastAchievement(lastAchievementData)
    }
}