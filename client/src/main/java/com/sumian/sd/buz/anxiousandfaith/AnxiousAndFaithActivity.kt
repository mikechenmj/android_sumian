package com.sumian.sd.buz.anxiousandfaith

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.common.statistic.StatUtil
import com.sumian.common.widget.dialog.SumianArticleDialog
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.anxiousandfaith.bean.AnxietyData
import com.sumian.sd.buz.anxiousandfaith.bean.AnxietyFaithItemViewData
import com.sumian.sd.buz.anxiousandfaith.bean.FaithData
import com.sumian.sd.buz.anxiousandfaith.widget.AnxiousFaithItemView
import com.sumian.sd.buz.anxiousandfaith.widget.EditAnxietyBottomSheetDialog
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.buz.stat.StatConstants.click_anxiety_and_faith_page_question_mark
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.activity_anxious_and_faith.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/26 9:26
 * desc   :
 * version: 1.0
 */
class AnxiousAndFaithActivity : BaseActivity() {
    private var mHasAnxiety = false
    private var mHasBelief = false

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_anxious_and_faith
    }

    companion object {
        fun getLaunchIntent(): Intent {
            return Intent(ActivityUtils.getTopActivity(), AnxiousAndFaithActivity::class.java)
        }
    }

    override fun getPageName(): String {
        return StatConstants.page_anxiety_and_faith
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.anxious_and_belief)
        tv_add_anxiety.setOnClickListener { AnxietyActivity.launch() }
        tv_add_belief.setOnClickListener { FaithActivity.launch() }
        vg_question.setOnClickListener {
            showExplainDialog()
            StatUtil.event(StatConstants.click_anxiety_and_faith_page_question_mark)
        }
        vg_anxiety_label.setOnClickListener { if (mHasAnxiety) ActivityUtils.startActivity(AnxietyListActivity::class.java) }
        vg_faith_label.setOnClickListener { if (mHasBelief) ActivityUtils.startActivity(FaithListActivity::class.java) }
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
        getFaith()
    }

    private fun getAnxious() {
        val call = AppManager.getSdHttpService().getAnxieties(1, 2)
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
            val itemView = AnxiousFaithItemView(this@AnxiousAndFaithActivity)
            itemView.setData(AnxietyFaithItemViewData.create(data), object : EditAnxietyBottomSheetDialog.OnItemClickListener {
                override fun onEditClick() {
                    AnxietyActivity.launch(data)
                }

                override fun onDeleteClick() {
                    deleteAnxiety(data.id)
                }
            })
            itemView.tag = data.id
            vg_anxious_record.addView(itemView)
        }
    }

    private fun getFaith() {
        val call = AppManager.getSdHttpService().getFaiths(1, 2)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<PaginationResponseV2<FaithData>>() {
            override fun onSuccess(response: PaginationResponseV2<FaithData>?) {
                if (response == null) {
                    return
                }
                val list = response.data
                updateFaithList(list)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
    }

    private fun updateFaithList(list: ArrayList<FaithData>) {
        val hasData = list.size > 0
        mHasBelief = hasData
        tv_faith_no_record.visibility = if (hasData) View.GONE else View.VISIBLE
        iv_faith_arrow.visibility = if (!hasData) View.GONE else View.VISIBLE
        vg_faith_record.removeAllViews()
        if (list.size == 0) {

        }
        for (data in list) {
            val itemView = AnxiousFaithItemView(this@AnxiousAndFaithActivity)
            itemView.setData(AnxietyFaithItemViewData.create(data), object : EditAnxietyBottomSheetDialog.OnItemClickListener {
                override fun onEditClick() {
                    FaithActivity.launch(data)
                }

                override fun onDeleteClick() {
                    deleteFaith(data.id)
                }
            })
            itemView.tag = data.id
            vg_faith_record.addView(itemView)
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

    private fun deleteFaith(id: Int) {
        showDeleteDialog(View.OnClickListener {
            val call = AppManager.getSdHttpService().deleteFaiths(id)
            addCall(call)
            call.enqueue(object : BaseSdResponseCallback<Any>() {
                override fun onSuccess(response: Any?) {
                    getFaith()
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
            val itemView = AnxiousFaithItemView(this@AnxiousAndFaithActivity)
            if (data is AnxietyData) {
                itemView.setData(AnxietyFaithItemViewData.create(data), listener)
                itemView.tag = data.id
            } else if (data is FaithData) {
                itemView.setData(AnxietyFaithItemViewData.create(data), listener)
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