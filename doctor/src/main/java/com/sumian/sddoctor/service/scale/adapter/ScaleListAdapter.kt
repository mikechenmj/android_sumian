package com.sumian.sddoctor.service.scale.adapter

import android.view.View
import android.widget.ImageView
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.sddoctor.R
import com.sumian.sddoctor.service.scale.bean.Scale
import java.util.*

/**
 * <pre>
 * Created by dq
 * on 2018/08/30
 *
 * desc:   量表计划 adapter
</pre> *
 */
class ScaleListAdapter(data: List<Scale>?) : BaseQuickAdapter<Scale, BaseViewHolder>(R.layout.lay_item_scale, data) {

    var isPickMode = false
    var mSelectedPlans: ArrayList<Scale>? = ArrayList()
        set(selectedSacles) = if (selectedSacles == null) {
            field = ArrayList()
        } else {
            field = selectedSacles
        }

    private var mMaxSelectCount = 9

    val selectedScaleIds: ArrayList<Int>
        get() {
            if (mSelectedPlans == null || mSelectedPlans!!.isEmpty()) {
                return ArrayList(0)
            } else {
                val tmpScaleIds = ArrayList<Int>(mSelectedPlans!!.size)
                for (selectedReport in mSelectedPlans!!) {
                    tmpScaleIds.add(selectedReport.id)
                }
                return tmpScaleIds
            }
        }

    override fun convert(helper: BaseViewHolder, item: Scale) {
        helper.setText(R.id.tv_title, item.title)
        val ivSelect = helper.getView<ImageView>(R.id.iv_selected)
        ivSelect.visibility = if (isPickMode) View.VISIBLE else View.GONE
        ivSelect.isSelected = mSelectedPlans!!.contains(item)
    }

    fun setMaxSelectCount(maxSelectCount: Int) {
        mMaxSelectCount = maxSelectCount
    }

    fun addOrRemoveSelectedItem(position: Int) {
        val scale = getItem(position)!!
        if (mSelectedPlans!!.contains(scale)) {
            mSelectedPlans!!.remove(scale)
        } else {
            if (mSelectedPlans!!.size == mMaxSelectCount) {
                ToastUtils.showShort("最多只能选择9份量表")
                return
            }
            mSelectedPlans!!.add(scale)
        }
        notifyItemChanged(position)
    }
}
