package com.sumian.sd.anxiousandbelief

import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseBackActivity
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.common.utils.TimeUtilV2
import com.sumian.common.widget.dialog.SumianArticleDialog
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.R
import com.sumian.sd.anxiousandbelief.bean.AnxietyData
import com.sumian.sd.anxiousandbelief.widget.AnxiousBeliefItemView
import com.sumian.sd.anxiousandbelief.widget.EditAnxietyBottomSheetDialog
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.activity_anxious_and_belief.*
import kotlinx.android.synthetic.main.view_anxious_belief_item.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/26 9:26
 * desc   :
 * version: 1.0
 */
class AnxiousAndBeliefActivity : BaseBackActivity() {
    private var mHasAnxiety = false
    private var mHasBelief = false

    override fun getChildContentId(): Int {
        return R.layout.activity_anxious_and_belief
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.anxious_and_belief)
        tv_add_anxiety.setOnClickListener { AnxietyActivity.launch() }
        tv_add_belief.setOnClickListener { }
        vg_question.setOnClickListener { showExplainDialog() }
        tv_anxious_record.setOnClickListener { if (mHasAnxiety) ActivityUtils.startActivity(AnxietyListActivity::class.java) }
        tv_belief_record.setOnClickListener { }
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
                mHasAnxiety = list.size > 0
                vg_anxious_record.removeAllViews()
                for (data in list) {
                    val itemView = AnxiousBeliefItemView(this@AnxiousAndBeliefActivity)
                    itemView.tv_title.text = data.anxiety
                    itemView.tv_message.text = data.solution
                    itemView.tv_time.text = TimeUtilV2.formatTimeYYYYMMDD_HHMM(data.getUpdateAtInMillis())
                    itemView.iv_more.setOnClickListener {
                        EditAnxietyBottomSheetDialog(this@AnxiousAndBeliefActivity, object : EditAnxietyBottomSheetDialog.OnItemClickListener {
                            override fun onEditClick() {
                                AnxietyActivity.launch(data)
                            }

                            override fun onDeleteClick() {
                                deleteAnxiety(data.id)
                            }
                        }).show()
                        itemView.tag = data.id
                    }
                    vg_anxious_record.addView(itemView)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
    }

    private fun getBelief() {

    }

    private fun deleteAnxiety(id: Int) {
        showDeleteDialog(View.OnClickListener {
            val call = AppManager.getSdHttpService().deleteAnxiety(id)
            addCall(call)
            call.enqueue(object : BaseSdResponseCallback<Any>() {
                override fun onSuccess(response: Any?) {
                    val childCount = vg_anxious_record.childCount
                    for (i in 0 until childCount) {
                        val childAt = vg_anxious_record.getChildAt(i)
                        if (childAt.tag == id) {
                            vg_anxious_record.removeViewAt(i)
                            break
                        }
                    }
                }

                override fun onFailure(errorResponse: ErrorResponse) {
                    ToastUtils.showShort(errorResponse.message)
                }
            })
        })
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