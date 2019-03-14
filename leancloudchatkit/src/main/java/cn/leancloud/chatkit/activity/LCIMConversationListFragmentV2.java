package cn.leancloud.chatkit.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.blankj.utilcode.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cn.leancloud.chatkit.LCIMManager;
import cn.leancloud.chatkit.R;
import cn.leancloud.chatkit.adapter.LCIMCommonListAdapter;
import cn.leancloud.chatkit.cache.LCIMConversationItemCache;
import cn.leancloud.chatkit.event.LCIMConversationItemLongClickEvent;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.chatkit.event.LCIMOfflineMessageCountChangeEvent;
import cn.leancloud.chatkit.viewholder.LCIMConversationItemHolderV2;

/**
 * Created by wli on 16/2/29.
 * 会话列表页
 */
public class LCIMConversationListFragmentV2 extends Fragment {
    protected SwipeRefreshLayout refreshLayout;
    protected RecyclerView recyclerView;

    protected LCIMCommonListAdapter<AVIMConversation> itemAdapter;
    protected LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lcim_conversation_list_fragment_v2, container, false);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_conversation_srl_pullrefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_conversation_srl_view);
        refreshLayout.setEnabled(false);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        itemAdapter = new LCIMCommonListAdapter<AVIMConversation>(LCIMConversationItemHolderV2.class);
        recyclerView.setAdapter(itemAdapter);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateConversationList();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateConversationList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 收到对方消息时响应此事件
     *
     * @param event
     */
    @Subscribe
    public void onEvent(LCIMIMTypeMessageEvent event) {
        updateConversationList();
    }

    /**
     * 删除会话列表中的某个 item
     *
     * @param event
     */
    @Subscribe
    public void onEvent(LCIMConversationItemLongClickEvent event) {
        if (null != event.conversation) {
            String conversationId = event.conversation.getConversationId();
            LCIMConversationItemCache.getInstance().deleteConversation(conversationId);
            updateConversationList();
        }
    }

    /**
     * 刷新页面
     */
    private void updateConversationList() {
//        LCIMManager.getInstance().open(new AVIMClientCallback() {
//            @Override
//            public void done(AVIMClient avimClient, AVIMException e) {
//                AVIMConversationsQuery query = avimClient.getConversationsQuery();
//                query.limit(100);
//                query.findInBackground(new AVIMConversationQueryCallback() {
//                    @Override
//                    public void done(List<AVIMConversation> list, AVIMException e) {
//                        for (AVIMConversation conversation : list) {
//                            System.out.println("conversation unread count" + conversation.getUnreadMessagesCount());
//                        }
//                        itemAdapter.setDataList(list);
//                        itemAdapter.notifyDataSetChanged();
//                    }
//                });
//            }
//        });
        LCIMManager.getInstance().queryConversationList(10, new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> list, AVIMException e) {
                if (e != null) {
                    ToastUtils.showShort(e.getMessage());
                    return;
                }
                for (AVIMConversation conversation : list) {
                    System.out.println("conversation unread count" + conversation.getUnreadMessagesCount());
                }
                itemAdapter.setDataList(list);
                itemAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 离线消息数量发生变化是响应此事件
     * 避免登陆后先进入此页面，然后才收到离线消息数量的通知导致的页面不刷新的问题
     *
     * @param updateEvent
     */
    @Subscribe
    public void onEvent(LCIMOfflineMessageCountChangeEvent updateEvent) {
        updateConversationList();
    }
}
