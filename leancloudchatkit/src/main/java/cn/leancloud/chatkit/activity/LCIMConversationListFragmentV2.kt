package cn.leancloud.chatkit.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.leancloud.chatkit.LCIMManager
import cn.leancloud.chatkit.R
import cn.leancloud.chatkit.adapter.LCIMCommonListAdapter
import cn.leancloud.chatkit.cache.LCIMConversationItemCache
import cn.leancloud.chatkit.event.LCIMConversationItemLongClickEvent
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent
import cn.leancloud.chatkit.event.LCIMOfflineMessageCountChangeEvent
import cn.leancloud.chatkit.viewholder.LCIMConversationItemHolderV2
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.lcim_layout_empty_view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by wli on 16/2/29.
 * 会话列表页
 */
class LCIMConversationListFragmentV2 : Fragment() {
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: LCIMCommonListAdapter<AVIMConversation>
    private lateinit var layoutManager: LinearLayoutManager
    private var mHost: Host? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.lcim_conversation_list_fragment_v2, container, false)
        refreshLayout = view.findViewById<View>(R.id.fragment_conversation_srl_pullrefresh) as SwipeRefreshLayout
        recyclerView = view.findViewById<View>(R.id.fragment_conversation_srl_view) as RecyclerView
        refreshLayout.isEnabled = false
        layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        itemAdapter = LCIMCommonListAdapter(LCIMConversationItemHolderV2::class.java)
        recyclerView.adapter = itemAdapter
        EventBus.getDefault().register(this)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Host) {
            mHost = context
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        updateConversationList()
    }

    override fun onResume() {
        super.onResume()
        updateConversationList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    /**
     * 收到对方消息时响应此事件
     *
     * @param event
     */
    @Subscribe
    fun onEvent(event: LCIMIMTypeMessageEvent) {
        updateConversationList()
    }

    /**
     * 删除会话列表中的某个 item
     *
     * @param event
     */
    @Subscribe
    fun onEvent(event: LCIMConversationItemLongClickEvent) {
        if (null != event.conversation) {
            val conversationId = event.conversation.conversationId
            LCIMConversationItemCache.getInstance().deleteConversation(conversationId)
            updateConversationList()
        }
    }

    /**
     * 刷新页面
     */
    private fun updateConversationList() {
        LCIMManager.getInstance().queryConversationList(100, object : AVIMConversationQueryCallback() {
            override fun done(list: List<AVIMConversation>?, e: AVIMException?) {
                if (e != null) {
                    ToastUtils.showShort(e.message)
                    return
                }
                showEmptyView(list == null || list.isEmpty())
                itemAdapter.dataList = list
                itemAdapter.notifyDataSetChanged()
            }
        })
    }

    fun showEmptyView(show: Boolean) {
        empty_view_root.visibility = if (show) View.VISIBLE else View.GONE
        val isDoctor = mHost?.isDoctor() ?: false
        iv_empty_view_icon.setImageResource(if (!isDoctor) R.drawable.lcim_emptystate_img_doctormessage else R.drawable.lcim_emptystate_img_patientmessage)
        tv_empty_view_title.text = getString(if (!isDoctor) R.string.lcim_no_doctor_message_yet else R.string.lcim_no_patient_message_yet)
        tv_empty_view_desc.text = getString(if (!isDoctor) R.string.lcim_no_doctor_message_hint else R.string.lcim_no_patient_message_hint)
    }

    /**
     * 离线消息数量发生变化是响应此事件
     * 避免登陆后先进入此页面，然后才收到离线消息数量的通知导致的页面不刷新的问题
     *
     * @param updateEvent
     */
    @Subscribe
    fun onEvent(updateEvent: LCIMOfflineMessageCountChangeEvent) {
        updateConversationList()
    }

    interface Host {
        fun isDoctor(): Boolean
    }
}
