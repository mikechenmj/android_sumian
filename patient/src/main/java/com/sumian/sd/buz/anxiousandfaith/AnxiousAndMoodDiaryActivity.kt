package com.sumian.sd.buz.anxiousandfaith

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.common.statistic.StatUtil
import com.sumian.common.widget.dialog.SumianArticleDialog
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.anxiousandfaith.bean.AnxietyData
import com.sumian.sd.buz.anxiousandfaith.bean.MoodDiaryData
import com.sumian.sd.buz.anxiousandfaith.widget.AnxiousMoodDiaryItemView
import com.sumian.sd.buz.anxiousandfaith.widget.EditAnxietyBottomSheetDialog
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.activity_anxious_and_mood_diary.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/26 9:26
 * desc   :
 * version: 1.0
 */
class AnxiousAndMoodDiaryActivity : WhileTitleNavBgActivity() {
    private var mHasAnxiety = false
    private var mHasMoodDiary = false

    override fun getLayoutId(): Int {
        return R.layout.activity_anxious_and_mood_diary
    }

    companion object {

        private const val PER_PAGE_PREVIEW_COUNT = 3

        fun getLaunchIntent(): Intent {
            return Intent(ActivityUtils.getTopActivity(), AnxiousAndMoodDiaryActivity::class.java)
        }
    }

    override fun getPageName(): String {
        return StatConstants.page_anxiety_and_faith
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.anxious_and_belief_title)
        tv_add_anxiety.setOnClickListener { AnxietyEditActivity.launch() }
        tv_mood_diary.setOnClickListener { MoodDiaryEditActivity.launch() }
        vg_question.setOnClickListener {
            showExplainDialog()
            StatUtil.event(StatConstants.click_anxiety_and_faith_page_question_mark)
        }
        vg_anxiety_label.setOnClickListener { if (mHasAnxiety) ActivityUtils.startActivity(AnxietyListActivity::class.java) }
        vg_mood_diary_label.setOnClickListener { if (mHasMoodDiary) ActivityUtils.startActivity(MoodDiaryListActivity::class.java) }
    }

    private fun showExplainDialog() {
        SumianArticleDialog(this)
                .setTitleText(R.string.anxiety_and_belief)
                .setMessageText(R.string.anxiety_and_belief_explain)
                .show()
    }

    override fun onStart() {
        super.onStart()
        getAnxious()
        getMoodDiary()
    }

    private fun getAnxious() {
        val call = AppManager.getSdHttpService().getAnxieties(1, PER_PAGE_PREVIEW_COUNT)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<PaginationResponseV2<AnxietyData>>() {
            override fun onSuccess(response: PaginationResponseV2<AnxietyData>?) {
                if (response == null) {
                    return
                }
                val list = response.data
                updateAnxietyList(list)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
    }

    private fun updateAnxietyList(list: ArrayList<AnxietyData>) {
        val hasData = list.size > 0
        mHasAnxiety = hasData
        tv_anxiety_no_record.visibility = if (hasData) View.GONE else View.VISIBLE
        iv_anxiety_arrow.visibility = if (!hasData) View.GONE else View.VISIBLE
        vg_anxious_record.removeAllViews()
        for (data in list) {
            val itemView = AnxiousMoodDiaryItemView(this@AnxiousAndMoodDiaryActivity)
            itemView.setData(data.anxiety, data.getUpdateAtInMillis(), null, object : EditAnxietyBottomSheetDialog.OnItemClickListener {
                override fun onEditClick() {
                    AnxietyEditActivity.launch(data)
                }

                override fun onDeleteClick() {
                    deleteAnxiety(data.id)
                }
            })
            itemView.setOnClickListener { AnxietyDetailActivity.launch(data) }
            itemView.tag = data.id
            vg_anxious_record.addView(itemView)
        }
    }

    private fun getMoodDiary() {
        val call = AppManager.getSdHttpService().getFaiths(1, PER_PAGE_PREVIEW_COUNT)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<PaginationResponseV2<MoodDiaryData>>() {
            override fun onSuccess(response: PaginationResponseV2<MoodDiaryData>?) {
                if (response == null) {
                    return
                }
                val list = response.data
                updateMoodDiaryList(list)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
    }

    private fun updateMoodDiaryList(list: ArrayList<MoodDiaryData>) {
        val hasData = list.size > 0
        mHasMoodDiary = hasData
        tv_mood_diary_no_record.visibility = if (hasData) View.GONE else View.VISIBLE
        iv_mood_diary_arrow.visibility = if (!hasData) View.GONE else View.VISIBLE
        vg_mood_diary_record.removeAllViews()
        if (list.size == 0) {

        }
        for (data in list) {
            val itemView = AnxiousMoodDiaryItemView(this@AnxiousAndMoodDiaryActivity)
            itemView.setData(data.scene, data.getUpdateAtInMillis(), data.getEmotionImageRes(), object : EditAnxietyBottomSheetDialog.OnItemClickListener {
                override fun onEditClick() {
                    MoodDiaryDetailActivity.launch(data)
                }

                override fun onDeleteClick() {
                    deleteMoodDiary(data.id)
                }
            })

            var isNegativeNoFillAll = !data.isFillAll() && !data.isPositiveMoodType()
            if (isNegativeNoFillAll) {
                itemView.showUnHandlerTip(getString(R.string.faith_un_handle_tip_text))
            }
            itemView.setOnClickListener {
                if (isNegativeNoFillAll) {
                    MoodDiaryEditActivity.launch(data, MoodDiaryEditActivity.MOOD_DETAIL_FRAGMENT_INDEX)
                } else {
                    MoodDiaryDetailActivity.launch(data)
                }
            }
            itemView.tag = data.id
            vg_mood_diary_record.addView(itemView)
        }
    }

    private fun deleteAnxiety(id: Int) {
        showDeleteDialog(View.OnClickListener {
            val call = AppManager.getSdHttpService().deleteAnxiety(id)
            addCall(call)
            call.enqueue(object : BaseSdResponseCallback<Any>() {
                override fun onSuccess(response: Any?) {
                    getAnxious()
                }

                override fun onFailure(errorResponse: ErrorResponse) {
                    ToastUtils.showShort(errorResponse.message)
                }
            })
        })
    }

    private fun deleteMoodDiary(id: Int) {
        showDeleteDialog(View.OnClickListener {
            val call = AppManager.getSdHttpService().deleteFaiths(id)
            addCall(call)
            call.enqueue(object : BaseSdResponseCallback<Any>() {
                override fun onSuccess(response: Any?) {
                    getMoodDiary()
                }

                override fun onFailure(errorResponse: ErrorResponse) {
                    ToastUtils.showShort(errorResponse.message)
                }
            })
        })
    }

    private fun addVgChild(vg: ViewGroup, list: List<Any>, listener: EditAnxietyBottomSheetDialog.OnItemClickListener) {
        vg.removeAllViews()
        for (data in list) {
            val itemView = AnxiousMoodDiaryItemView(this@AnxiousAndMoodDiaryActivity)
            if (data is AnxietyData) {
                itemView.setData(data.anxiety, data.getUpdateAtInMillis(), null, listener)
                itemView.tag = data.id
            } else if (data is MoodDiaryData) {
                itemView.setData(data.scene, data.getUpdateAtInMillis(), data.getEmotionImageRes(), listener)
                itemView.tag = data.id
            }
            vg.addView(itemView)
        }
    }

    private fun removeVgChild(vg: ViewGroup, id: Int) {
        val childCount = vg.childCount
        for (i in 0 until childCount) {
            val childAt = vg.getChildAt(i)
            if (childAt.tag == id) {
                vg.removeViewAt(i)
                break
            }
        }
    }

    private fun showDeleteDialog(onConfirmClickListener: View.OnClickListener) {
        SumianDialog(this)
                .setTitleText(R.string.delete_record)
                .setMessageText(R.string.delete_record_confirm_hint)
                .whitenLeft()
                .setLeftBtn(R.string.cancel, null)
                .setRightBtn(R.string.confirm, onConfirmClickListener)
                .show()
    }
}