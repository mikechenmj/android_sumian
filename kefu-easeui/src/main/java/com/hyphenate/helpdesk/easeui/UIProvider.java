package com.hyphenate.helpdesk.easeui;


import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.easeui.widget.ChatEaseTitleBar;
import com.hyphenate.helpdesk.easeui.widget.MessageList;
import com.hyphenate.helpdesk.emojicon.Emojicon;
import com.hyphenate.util.EasyUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

@SuppressWarnings("ALL")
public class UIProvider {
    private static final String TAG = UIProvider.class.getSimpleName();

    public static final int LIGHT_THEME = 0x01;

    public static final int NIGHT_THEME = 0x02;

    /**
     * the global EaseUI instance
     */
    private static UIProvider instance = null;

    /**
     * 用户属性提供者
     */
    private UserProfileProvider userProvider;

    private SettingsProvider settingsProvider;
    private OnLoginCallback mOnLoginCallback;

    /**
     * application context
     */
    private Context appContext = null;

    /**
     * the notifier
     */
    private Notifier notifier = null;

    private boolean showProgress = true;

    private UnreadMessageChangeListener mUnreadMessageChangeListener;

    private int mCacheMsgSize;

    private int mThemeMode = LIGHT_THEME;

    private volatile boolean mIsLogin;

    /**
     * 用来记录注册了eventlistener的foreground Activity
     */
    private List<Activity> activityList = Collections.synchronizedList(new ArrayList<Activity>());

    public boolean isLogin() {
        return mIsLogin;
    }

    public void setLogin(boolean login) {
        mIsLogin = login;
    }

    public void pushActivity(Activity activity) {
        if (!activityList.contains(activity)) {
            activityList.add(0, activity);
        }
    }

    public void popActivity(Activity activity) {
        activityList.remove(activity);
    }


    private UIProvider() {
    }

    /**
     * 获取EaseUI单实例对象
     *
     * @return
     */
    public synchronized static UIProvider getInstance() {
        if (instance == null) {
            instance = new UIProvider();
        }
        return instance;
    }

    public void setShowProgress(boolean isShowProgress) {
        this.showProgress = isShowProgress;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setUnreadMessageChangeListener(UnreadMessageChangeListener unreadMessageChangeListener) {
        mUnreadMessageChangeListener = unreadMessageChangeListener;
    }

    public void clickLogin(TextView tvNetWorkErrorTips) {

    }

    public interface UnreadMessageChangeListener {
        void onMessageCountChange(int messageCount);
    }

    public int isHaveMsgSize() {
        return mCacheMsgSize;
    }

    public void clearCacheMsg() {
        mCacheMsgSize = 0;
        if (mUnreadMessageChangeListener != null) {
            mUnreadMessageChangeListener.onMessageCountChange(mCacheMsgSize);
        }
    }

    /**
     * @param context
     */
    public synchronized void init(final Context context) {
        appContext = context;
        initNotifier();

        if (settingsProvider == null) {
            settingsProvider = new DefaultSettingsProvider();
        }

        ChatClient.getInstance().chatManager().addMessageListener(new ChatManager.MessageListener() {
            @Override
            public void onMessage(List<Message> msgs) {
                mCacheMsgSize = msgs.size();
                if (!EasyUtils.isAppRunningForeground(context)) {
                    UIProvider.getInstance().getNotifier().onNewMesg(msgs);
                }
                if (mUnreadMessageChangeListener != null) {
                    mUnreadMessageChangeListener.onMessageCountChange(msgs.size());
                }
            }

            @Override
            public void onCmdMessage(List<Message> msgs) {

            }

            @Override
            public void onMessageStatusUpdate() {

            }

            @Override
            public void onMessageSent() {

            }
        });
    }

    void initNotifier() {
        notifier = createNotifier();
        notifier.init(appContext);
    }

    protected Notifier createNotifier() {
        return new Notifier();
    }

    public Notifier getNotifier() {
        return notifier;
    }

    public boolean hasForegroundActivies() {
        return activityList.size() != 0;
    }

    /**
     * 设置用户属性提供者
     *
     * @param userProvider
     */
    public void setUserProfileProvider(UserProfileProvider userProvider) {
        this.userProvider = userProvider;
    }

    private AccountPrivoder mAccountPrivoder;

    public void setAccountProvider(AccountPrivoder accountProvider) {
        this.mAccountPrivoder = accountProvider;
    }

    public AccountPrivoder getAccountPrivoder() {
        return mAccountPrivoder;
    }

    public interface AccountPrivoder {

        void tryLoginAccount(@NonNull TextView tvLoginStateTips, @NonNull ChatEaseTitleBar chatEaseTitleBar, @NonNull MessageList messageList);
    }

    /**
     * 获取用户属性提供者
     *
     * @return
     */
    public UserProfileProvider getUserProfileProvider() {
        return userProvider;
    }

    public void setSettingsProvider(SettingsProvider settingsProvider) {
        this.settingsProvider = settingsProvider;
    }

    public SettingsProvider getSettingsProvider() {
        return settingsProvider;
    }

    public OnLoginCallback getOnLoginCallback() {
        return mOnLoginCallback;
    }

    public void setOnLoginCallback(OnLoginCallback onLoginCallback) {
        mOnLoginCallback = onLoginCallback;
    }

    public interface OnLoginCallback {

        void onLoginSuccess();

        void onLoginFailed();
    }

    public interface UserProfileProvider {
        void setNickAndAvatar(@NonNull Context context, @NonNull Message message, ImageView userAvatarView, TextView usernickView);
    }

    /**
     * 表情信息提供者
     */
    public interface EmojiconInfoProvider {
        /**
         * 根据唯一识别号返回此表情内容
         *
         * @param emojiconIdentityCode
         * @return
         */
        Emojicon getEmojiconInfo(String emojiconIdentityCode);

        /**
         * 获取文字表情的映射Map,map的key为表情的emoji文本内容，value为对应的图片资源id或者本地路径(不能为网络地址)
         *
         * @return
         */
        Map<String, Object> getTextEmojiconMapping();
    }

    private EmojiconInfoProvider emojiconInfoProvider;

    /**
     * 获取表情提供者
     *
     * @return
     */
    public EmojiconInfoProvider getEmojiconInfoProvider() {
        return emojiconInfoProvider;
    }

    /**
     * 设置表情信息提供者
     *
     * @param emojiconInfoProvider
     */
    public void setEmojiconInfoProvider(EmojiconInfoProvider emojiconInfoProvider) {
        this.emojiconInfoProvider = emojiconInfoProvider;
    }

    /**
     * 新消息提示设置的提供者
     */
    public interface SettingsProvider {
        boolean isMsgNotifyAllowed(Message message);

        boolean isMsgSoundAllowed(Message message);

        boolean isMsgVibrateAllowed(Message message);

        boolean isSpeakerOpened();
    }

    /**
     * default settings provider
     */
    protected class DefaultSettingsProvider implements SettingsProvider {

        @Override
        public boolean isMsgNotifyAllowed(Message message) {
            return true;
        }

        @Override
        public boolean isMsgSoundAllowed(Message message) {
            return true;
        }

        @Override
        public boolean isMsgVibrateAllowed(Message message) {
            return true;
        }

        @Override
        public boolean isSpeakerOpened() {
            return true;
        }


    }


}