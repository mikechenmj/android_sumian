package com.sumian.sd.buz.diary.fillsleepdiary.fragment

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.widget.CommonDividerItemDecoration
import com.sumian.common.widget.SimpleViewHolder
import com.sumian.common.widget.picker.WheelPickerBottomSheet
import com.sumian.sd.R
import com.sumian.sd.buz.diary.sleeprecord.bean.SleepPill
import com.sumian.sd.widget.dialog.SumianAlertDialogV2
import kotlinx.android.synthetic.main.fragment_sleep_pills.*
import kotlinx.android.synthetic.main.list_item_pills.view.*
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/17 15:28
 * desc   :
 * version: 1.0
 */
class SleepPillsFragment : BaseFillSleepDiaryFragment() {
    private val mAdapter = PillAdapter()
    private val mPillNameOptions by lazy { mFillDiaryViewModel.getMedicines() }
    private val mPillAmountOptions by lazy { resources.getStringArray(R.array.pill_amount_options) }
    private val mPillTimeOptions by lazy { resources.getStringArray(R.array.pill_time_options) }

    companion object {
        fun newInstance(progress: Int): SleepPillsFragment {
            val bundle = Bundle()
            bundle.putInt(KEY_PROGRESS, progress)
            val fragment = SleepPillsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getContentViewLayout(): Int {
        return R.layout.fragment_sleep_pills
    }

    override fun initWidget() {
        super.initWidget()
        recycler_view.layoutManager = LinearLayoutManager(activity!!)
        recycler_view.adapter = mAdapter
        recycler_view.addItemDecoration(CommonDividerItemDecoration(mActivity))
        mAdapter.mListener = object : PillAdapter.Listener {
            override fun onDeletePill(pill: SleepPill) {
                removePill(pill)
            }
        }
        tv_continue_add.setOnClickListener { showAddPillBottomSheet() }
        tv_add_pills.setOnClickListener { showAddPillBottomSheet() }
        mFillDiaryViewModel.mPillsLiveData.observe(this, Observer {
            vg_add.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            tv_continue_add.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
            recycler_view.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
        })
        v_history_pills_hint_bg.visibility = if (mFillDiaryViewModel.mHasHistoryPills) View.VISIBLE else View.GONE
    }

    private fun showAddPillBottomSheet() {
        if (mAdapter.mData.size == 10) {
            ToastUtils.showShort(getString(R.string.add_pills_too_many_hint))
            return
        }
        WheelPickerBottomSheet(activity!!,
                getString(R.string.take_pills_record),
                createPickerData(),
                object : WheelPickerBottomSheet.Listener {
                    override fun onConfirmClick(values: List<Int>): Boolean {
                        val pill = SleepPill(
                                mPillNameOptions[values[0]]!!,
                                mPillAmountOptions[values[1]],
                                mPillTimeOptions[values[2]]
                        )
                        addPill(pill)
                        return true
                    }
                }
        )
                .show()
    }

    private fun removePill(pill: SleepPill) {
        mAdapter.removeData(pill)
        mFillDiaryViewModel.mPillsLiveData.value = mAdapter.mData
    }

    private fun addPill(pill: SleepPill) {
        val list = mAdapter.mData
        var repeatIndex = -1
        for ((index, data) in list.withIndex()) {
            if (data.time == pill.time && data.name == pill.name) {
                repeatIndex = index
                break
            }
        }
        if (repeatIndex >= 0) {
            val pillString = "【${pill.name}, ${pill.amount}, ${pill.time}】"
            val message = Html.fromHtml("<font color=#6595F4>$pillString</font>，是否覆盖？")
            SumianAlertDialogV2(activity!!)
                    .setTitleText(getString(R.string.you_have_add_this_pill))
                    .setMessageText(message)
                    .setLeftBtnOnClickListener(R.string.cancel, null)
                    .whitenLeft()
                    .setRightBtnOnClickListener(R.string.cover, object : View.OnClickListener {
                        override fun onClick(v: View?) {
                            mAdapter.mData[repeatIndex] = pill
                            mAdapter.notifyDataSetChanged()
                            mFillDiaryViewModel.mPillsLiveData.value = mAdapter.mData
                        }
                    })
                    .show()
        } else {
            mAdapter.addData(pill)
            mFillDiaryViewModel.mPillsLiveData.value = mAdapter.mData
        }
    }

    private fun createPickerData(): ArrayList<Pair<Array<String?>, Int>> {
        val list = ArrayList<Pair<Array<String?>, Int>>()
        list.add(Pair(mPillNameOptions, 0))
        list.add(Pair(mPillAmountOptions, 0))
        list.add(Pair(mPillTimeOptions, 0))
        return list
    }

    override fun initData() {
        super.initData()
        mAdapter.setData(mFillDiaryViewModel.mPillsLiveData.value)
    }

    class PillAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var mData: MutableList<SleepPill> = ArrayList<SleepPill>()
        var mListener: Listener? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return SimpleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_pills, parent, false))
        }

        override fun getItemCount(): Int {
            return mData.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val sleepPill = mData[position]
            holder.itemView.tv_pill_time.text = sleepPill.time
            holder.itemView.tv_pill_name.text = sleepPill.name
            holder.itemView.tv_pill_amount.text = sleepPill.amount
            holder.itemView.iv_delete.setOnClickListener { mListener?.onDeletePill(sleepPill) }
        }

        interface Listener {
            fun onDeletePill(pill: SleepPill)
        }

        fun setData(data: List<SleepPill>?) {
            mData.clear()
            if (data != null) {
                mData.addAll(data)
            }
            notifyDataSetChanged()
        }

        fun addData(data: SleepPill) {
            if (!mData.contains(data)) {
                mData.add(data)
            }
            notifyDataSetChanged()
        }

        fun removeData(data: SleepPill) {
            mData.remove(data)
            notifyDataSetChanged()
        }
    }
}