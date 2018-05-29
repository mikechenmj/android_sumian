package com.sumian.sleepdoctor.account.model;

import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;
import com.google.gson.reflect.TypeToken;
import com.sumian.sleepdoctor.utils.JsonUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/28 16:09
 *     desc   : 管理用户账号信息
 *     version: 1.0
 * </pre>
 */
public class AccountManager {
    static final int SOCIAL_TYPE_WECHAT = 0;

    @IntDef({SOCIAL_TYPE_WECHAT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SocialType {
    }

    @SuppressWarnings("FieldCanBeLocal")
    private static String SOCIAL_INFO_SP_KEY_PREFIX = "social_info_";
    private SPUtils mSPUtils = SPUtils.getInstance(getClass().getName());

    private AccountManager() {
    }

    private static class InstanceHolder {
        private static final AccountManager sInstance = new AccountManager();
    }

    public static AccountManager getInstance() {
        return InstanceHolder.sInstance;
    }


    private String getSocialInfoSpKey(int socialType) {
        return SOCIAL_INFO_SP_KEY_PREFIX + socialType;
    }

    private void setSocialInfo(@SocialType int socialType, String wechatInfo) {
        String key = getSocialInfoSpKey(socialType);
        mSPUtils.put(key, wechatInfo);
    }

    private String getSocialInfo(@SocialType int socialType) {
        return mSPUtils.getString(getSocialInfoSpKey(socialType));
    }

    /**
     * {
     * "country":"中国",
     * "unionid":"oQT0F0rlH7sbHKmpfWFNg-0xYB34",
     * "gender":"男",
     * "city":"深圳",
     * "openid":"ouV7E1SWfPLo3pzHtflGSXpS3Xl4",
     * "language":"zh_CN",
     * "profile_image_url":"[图片]http://thirdwx.qlogo.cn/mmopen/vi_32/rBdyicTIUVrNUC8ov8AkYTrcmJtLyrZ4l3ade0QcZy9mFZ7THfYj6ZoSTnxxJtibFHBaGAMpgribQ9j5ZN3xojsvg/132",
     * "accessToken":"10_guvcdl7JIG2he-MdCJn0g5OoWCj48uRvCwTn8EbJUsXa8tf-mxdO7oVeJlUQaJUg4pqVh_tIZ4E74t5hSGOZWQ",
     * "access_token":"10_guvcdl7JIG2he-MdCJn0g5OoWCj48uRvCwTn8EbJUsXa8tf-mxdO7oVeJlUQaJUg4pqVh_tIZ4E74t5hSGOZWQ",
     * "uid":"oQT0F0rlH7sbHKmpfWFNg-0xYB34",
     * "province":"广东",
     * "screen_name":"詹徐照",
     * "name":"詹徐照",
     * "iconurl":"[图片]http://thirdwx.qlogo.cn/mmopen/vi_32/rBdyicTIUVrNUC8ov8AkYTrcmJtLyrZ4l3ade0QcZy9mFZ7THfYj6ZoSTnxxJtibFHBaGAMpgribQ9j5ZN3xojsvg/132",
     * "nickname":"詹徐照",
     * "expiration":"1527522430658",
     * "expires_in":"1527522430658",
     * "refreshToken":"10_2tAwf9idGaMdfpI4Fm0JMhMqQhp6_EAHBpFGD_sWM3FQuY9HOjA0Fy_7TKKSJqqMswxnXjkv5jif93QGsAwxYQ"
     * }
     */
    public void setWechatInfo(String wechatInfo) {
        setSocialInfo(SOCIAL_TYPE_WECHAT, wechatInfo);
    }

    public String getWechatInfo() {
        return getSocialInfo(SOCIAL_TYPE_WECHAT);
    }

    public String getWechatNickname() {
        String wechatInfo = getWechatInfo();
        if (TextUtils.isEmpty(wechatInfo)) {
            return null;
        }
        Map<String, String> map = JsonUtil.fromJson(wechatInfo, new TypeToken<Map<String, String>>() {
        }.getType());
        if (map == null) {
            return null;
        }
        return map.get("name");
    }

}
