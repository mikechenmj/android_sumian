package com.sumian.sd.buz.anxiousandfaith

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseSectionMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.anxiousandfaith.bean.AnxietyMoodDiaryItemViewData
import com.sumian.sd.buz.anxiousandfaith.bean.MoodDiaryData
import com.sumian.sd.buz.anxiousandfaith.bean.FaithSectionMultiEntity
import com.sumian.sd.buz.anxiousandfaith.event.MoodDiaryChangeEvent
import com.sumian.sd.buz.anxiousandfaith.widget.AnxiousMoodDiaryItemView
import com.sumian.sd.buz.anxiousandfaith.widget.EditAnxietyBottomSheetDialog
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.EventBusUtil
import kotlinx.android.synthetic.main.activity_anxiety_mood_diary_list.*
import org.greenrobot.eventbus.Subscribe

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/26 15:56
 * desc   :
 * version: 1.0
 */
class MoodDiaryListActivity : WhileTitleNavBgActivity() {

    private val mAdapter = FaithAdapter()
    private var mPage = 1
    private var mHasAWeekAgoHead = false

    companion object {
        private const val TIME_MILLI_A_WEEK = 7 * 24 * 60 * 60 * 1000L
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_anxiety_mood_diary_list
    }

    override fun getPageName(): String {
        return StatConstants.page_mood_diary_list
    }

    override fun onStart() {
        super.onStart()
        EventBusUtil.register(this)
        refreshData()
    }

    override fun onStop() {
        super.onStop()
        EventBusUtil.unregister(this)
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.mood_diary)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = mAdapter
        mAdapter.setOnLoadMoreListener({ loadData() }, recycler_view)
        refresh_layout.setOnRefreshListener {
            refreshData()
        }
        bt_add_record.setText(R.string.add_belief)
        bt_add_record.setOnClickListener { MoodDiaryEditActivity.launch() }
    }

    private fun refreshData() {
        mPage = 1
        loadData()
        mHasAWeekAgoHead = false
    }

    private fun loadData() {
        val call = AppManager.getSdHttpService().getFaiths(mPage)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<PaginationResponseV2<MoodDiaryData>>() {
            override fun onSuccess(response: PaginationResponseV2<MoodDiaryData>?) {
                if (response == null) {
                    return
                }
                var data = response.data
                if (data.size < 1) {
                    return
                }
                var currentTime = System.currentTimeMillis()
                var dataWithSection = mutableListOf<FaithSectionMultiEntity>()
                var isFirstPage = mPage == 1
                if (isFirstPage) {
                    if (currentTime - data[0].getUpdateAtInMillis() < TIME_MILLI_A_WEEK)
                        dataWithSection.add(FaithSectionMultiEntity(true, "本周"))
                }
                data.forEach {
                    var isAWeekAgo = currentTime - it.getUpdateAtInMillis() > TIME_MILLI_A_WEEK
                    if (isAWeekAgo && !mHasAWeekAgoHead) {
                        mHasAWeekAgoHead = true
                        dataWithSection.add(FaithSectionMultiEntity(true, "一周前"))
                    }
                    dataWithSection.add(FaithSectionMultiEntity(it))
                }
                if (isFirstPage) {
                    mAdapter.setNewData(dataWithSection)
                } else {
                    mAdapter.addData(dataWithSection)
                }
                mPage++
                mAdapter.setEnableLoadMore(!response.meta.pagination.isLastPage())
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                mAdapter.loadMoreComplete()
                refresh_layout.hideRefreshAnim()
            }
        })
    }

    inner class FaithAdapter : BaseSectionMultiItemQuickAdapter<FaithSectionMultiEntity, BaseViewHolder> {
        constructor() : super(R.layout.list_section_header_anxiety_mood_diary, null) {
            addItemType(0, R.layout.list_item_anxiety_mood_diary)
        }

        override fun convert(helper: BaseViewHolder, item: FaithSectionMultiEntity) {
            val itemView = helper.getView<AnxiousMoodDiaryItemView>(R.id.anxiety_mood_diary_view)
            itemView.setTextMaxLines(true)
            itemView.setData(AnxietyMoodDiaryItemViewData.create(item.t), object : EditAnxietyBottomSheetDialog.OnItemClickListener {
                override fun onEditClick() {
                    MoodDiaryEditActivity.launch(item.t)
                }

                override fun onDeleteClick() {
                    deleteFaith(item.t.id)
                }
            })
        }

        override fun convertHead(helper: BaseViewHolder, item: FaithSectionMultiEntity) {
            helper.setText(R.id.section_header, item.header)
        }
    }

    private fun deleteFaith(id: Int) {
        showDeleteDialog(View.OnClickListener {
            val call = AppManager.getSdHttpService().deleteFaiths(id)
            addCall(call)
            call.enqueue(object : BaseSdResponseCallback<Any>() {
                override fun onSuccess(response: Any?) {
                    val itemPosition = getItemPosition(id)
                    mAdapter.remove(itemPosition)
                    if (mAdapter.data.size == 0) {
                        finish()
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

    @Subscribe(sticky = true)
    fun onMoodDiaryChangeEvent(event: MoodDiaryChangeEvent) {
        EventBusUtil.removeStickyEvent(event)
        val faith = event.moodDiary
        val position = getItemPosition(id = faith.id)
        mAdapter.data[position] = FaithSectionMultiEntity(faith)
        mAdapter.notifyItemChanged(position)
    }

    private fun getItemPosition(id: Int): Int {
        val list = mAdapter.data
        for ((index, data) in list.withIndex()) {
            if (data.t.id == id) {
                return index
            }
        }
        return -1
    }

}