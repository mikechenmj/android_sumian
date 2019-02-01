package com.sumian.sddoctor.service.plan.adapter

import android.view.View
import android.widget.ImageView
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.sddoctor.R
import com.sumian.sddoctor.service.plan.bean.Plan

/**
 * <pre>
 * Created by dq
 * on 2018/08/30
 *
 * desc:   随访计划 adapter  单选
</pre> *
 */
class PlanListAdapter(data: List<Plan>?) : BaseQuickAdapter<Plan, BaseViewHolder>(R.layout.lay_item_scale, data) {

    private var isPickMode = true

    private var mSelectedId = -1

    private var mMaxSelectCount = 1


    override fun convert(helper: BaseViewHolder, item: Plan) {
        helper.setText(R.id.tv_title, item.name)
        val ivSelect = helper.getView<ImageView>(R.id.iv_selected)
        ivSelect.visibility = if (isPickMode) View.VISIBLE else View.GONE
        ivSelect.isSelected = mSelectedId == item.id
    }

    fun setMaxSelectCount(maxSelectCount: Int) {
        mMaxSelectCount = maxSelectCount
    }

    fun addOrRemoveSelectedItem(position: Int) {
        val plan = getItem(position)!!
        if (plan.id == mSelectedId) {
            ToastUtils.showShort("请选择其它随访计划")
        } else {
            this.mSelectedId = plan.id
            notifyDataSetChanged()
        }
    }

    fun getSelectedPlanId(): Int {
        return mSelectedId
    }
}
