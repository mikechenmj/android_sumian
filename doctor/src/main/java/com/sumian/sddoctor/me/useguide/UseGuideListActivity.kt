package com.sumian.sddoctor.me.useguide

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.widget.CommonDividerItemDecoration
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity
import kotlinx.android.synthetic.main.activity_my_wallet.*
import kotlinx.android.synthetic.main.list_item_use_guide.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/13 15:32
 * desc   :
 * version: 1.0
 */
class UseGuideListActivity : SddBaseActivity() {
    private val mList by lazy {
        resources.getStringArray(R.array.use_guide_labels)
    }

    override fun getLayoutId(): Int {
        return R.layout.recycler_view_padding_top_10
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.use_guide)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = ListAdapter(mList)
        recycler_view.addItemDecoration(CommonDividerItemDecoration(this))
    }

    class VH(viewGroup: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_use_guide, viewGroup, false))

    class ListAdapter(data: Array<String>) : RecyclerView.Adapter<VH>() {
        private var mData = data
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(parent)
        }

        override fun getItemCount(): Int {
            return mData.size
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.itemView.tv_label.text = mData[position]
            holder.itemView.setOnClickListener { UseGuideActivity.launch(position) }
        }
    }

    companion object {
        fun launch() {
            ActivityUtils.startActivity(ActivityUtils.getTopActivity(), UseGuideListActivity::class.java)
        }
    }
}