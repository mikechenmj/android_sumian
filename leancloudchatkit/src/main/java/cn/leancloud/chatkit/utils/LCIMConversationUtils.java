package cn.leancloud.chatkit.utils;

import android.text.TextUtils;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.LCChatProfilesCallBack;
import cn.leancloud.chatkit.LCIMManager;
import cn.leancloud.chatkit.cache.LCIMProfileCache;

/**
 * Created by wli on 16/3/2.
 * 和 Conversation 相关的 Util 类
 */
public class LCIMConversationUtils {

    /**
     * 获取会话名称
     * 优先级：
     * 1、AVIMConersation name 属性
     * 2、单聊：对方用户名
     * 群聊：成员用户名合并
     *
     * @param conversation
     * @param callback
     */
    public static void getConversationName(final AVIMConversation conversation, final AVCallback<String> callback) {
        if (null == callback) {
            return;
        }
        if (null == conversation) {
            callback.internalDone(null, new AVException(new Throwable("conversation can not be null!")));
            return;
        }
        if (conversation.isTemporary()) {
            callback.internalDone(conversation.getName(), null);
        } else if (conversation.isTransient()) {
            callback.internalDone(conversation.getName(), null);
        } else if (isCbtiTreatConversation(conversation)) {
            getUserByConversationType(getConversationType(conversation), new AVCallback<LCChatKitUser>() {
                @Override
                protected void internalDone0(LCChatKitUser user, AVException e) {
                    if (user != null) {
                        callback.internalDone(user.getName(), null);
                    } else {
                        callback.internalDone(null, e);
                    }
                }
            });
        } else if (2 == conversation.getMembers().size()) {
            String peerId = getConversationPeerId(conversation);
            getUser(peerId, new AVCallback<LCChatKitUser>() {
                @Override
                protected void internalDone0(LCChatKitUser lcChatKitUser, AVException e) {
                    callback.internalDone(lcChatKitUser == null ? "" : lcChatKitUser.getName(), null);
                }
            });
        } else {
            if (!TextUtils.isEmpty(conversation.getName())) {
                callback.internalDone(conversation.getName(), null);
            } else {
                LCIMProfileCache.getInstance().getCachedUsers(conversation.getMembers(), new AVCallback<List<LCChatKitUser>>() {
                    @Override
                    protected void internalDone0(List<LCChatKitUser> lcimUserProfiles, AVException e) {
                        List<String> nameList = new ArrayList<String>();
                        if (null != lcimUserProfiles) {
                            for (LCChatKitUser userProfile : lcimUserProfiles) {
                                nameList.add(userProfile.getName());
                            }
                        }
                        callback.internalDone(TextUtils.join(",", nameList), e);
                    }
                });
            }
        }
    }

    /**
     * 获取单聊会话的 icon
     * 单聊：对方用户的头像
     * 群聊：返回 null
     *
     * @param conversation
     * @param callback
     */
    public static void getConversationPeerIcon(final AVIMConversation conversation, final AVCallback<String> callback) {
        if (conversation == null) {
            callback.internalDone(null, new AVException(new Throwable("cannot find icon!")));
        } else if (isCbtiTreatConversation(conversation)) {
            getUserByConversationType(getConversationType(conversation), new AVCallback<LCChatKitUser>() {
                @Override
                protected void internalDone0(LCChatKitUser user, AVException e) {
                    if (user != null) {
                        callback.internalDone(user.getAvatarUrl(), null);
                    } else {
                        callback.internalDone(null, e);
                    }
                }
            });
        } else if (!conversation.isTransient() && !conversation.getMembers().isEmpty()) {
            String peerId = getConversationPeerId(conversation);
            if (1 == conversation.getMembers().size()) {
                peerId = conversation.getMembers().get(0);
            }
            getUser(peerId, new AVCallback<LCChatKitUser>() {
                @Override
                protected void internalDone0(LCChatKitUser lcChatKitUser, AVException e) {
                    callback.internalDone(lcChatKitUser == null ? "" : lcChatKitUser.getAvatarUrl(), null);
                }
            });
        } else {
            callback.internalDone("", null);
        }
    }

    /**
     * 获取 “对方” 的用户 id，只对单聊有效，群聊返回空字符串
     *
     * @param conversation
     * @return
     */
    private static String getConversationPeerId(AVIMConversation conversation) {
        if (null != conversation && 2 == conversation.getMembers().size()) {
            String currentUserId = LCIMManager.getInstance().getUserId();
            String firstMemeberId = conversation.getMembers().get(0);
            return conversation.getMembers().get(firstMemeberId.equals(currentUserId) ? 1 : 0);
        }
        return "";
    }

    public static void getUser(final String userId, final AVCallback<LCChatKitUser> callback) {
        LCIMManager.getInstance().getProfileProvider().fetchProfiles(Collections.singletonList(userId), new LCChatProfilesCallBack() {
            @Override
            public void done(List<LCChatKitUser> userList, Exception exception) {
                LCChatKitUser user = null;
                if (userList != null) {
                    for (LCChatKitUser item : userList) {
                        if (item.getUserId().equals(userId)) {
                            user = item;
                            break;
                        }
                    }
                }
                callback.internalDone(user, null);
            }
        });
    }

    public static void getUserByConversationType(int conversationType, final AVCallback<LCChatKitUser> callback) {
        LCIMManager.getInstance().getProfileProvider().fetchProfileByConversationType(conversationType, new AVCallback<LCChatKitUser>() {
            @Override
            protected void internalDone0(LCChatKitUser lcChatKitUser, AVException e) {
                callback.internalDone(lcChatKitUser, e);
            }
        });
    }

    public static boolean isCbtiTreatConversation(AVIMConversation conversation) {
        return getConversationType(conversation) == 1;
    }

    public static int getConversationType(AVIMConversation conversation) {
        return (int) conversation.getAttribute("conversation_type");
    }

    public static boolean isMe(AVIMTypedMessage msg) {
        String selfId = LCIMManager.getInstance().getUserId();
        return msg.getFrom() != null && msg.getFrom().equals(selfId);
    }

    public static boolean isMe(String userId) {
        String selfId = LCIMManager.getInstance().getUserId();
        return userId != null && userId.equals(selfId);
    }
}