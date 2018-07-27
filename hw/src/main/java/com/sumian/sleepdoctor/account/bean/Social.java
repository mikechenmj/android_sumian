package com.sumian.sleepdoctor.account.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by sm
 * on 2018/2/9.
 * desc:
 */

@SuppressWarnings("WeakerAccess")
public class Social implements Parcelable, Serializable {
    public static final int SOCIAL_TYPE_WECHAT = 0;
    public static final Creator<Social> CREATOR = new Creator<Social>() {
        @Override
        public Social createFromParcel(Parcel in) {
            return new Social(in);
        }

        @Override
        public Social[] newArray(int size) {
            return new Social[size];
        }
    };
    /**
     * type : 0
     * open_id : ouV7E1SWfPLo3pzHtflGSXpS3Xl4
     * union_id : oQT0F0rlH7sbHKmpfWFNg-0xYB34
     * nickname : 詹徐照
     * id : 87
     */

    public int id;//第三方绑定id
    @SocialType
    public int type;//类型 0:微信
    public String open_id;//第三方open id
    public String union_id;//第三方union id
    public String nickname;//第三方昵称
    public int user_id;//用户id
    public int created_at;//第三方绑定时间
    public int updated_at;//第三方最后更新时间

    @SuppressWarnings("unused")
    public Social() {
    }

    protected Social(Parcel in) {
        id = in.readInt();
        type = in.readInt();
        user_id = in.readInt();
        open_id = in.readString();
        union_id = in.readString();
        nickname = in.readString();
        created_at = in.readInt();
        updated_at = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(type);
        dest.writeInt(user_id);
        dest.writeString(open_id);
        dest.writeString(union_id);
        dest.writeString(nickname);
        dest.writeInt(created_at);
        dest.writeInt(updated_at);
    }

    @Override
    public String toString() {
        return "Social{" +
                "id=" + id +
                ", type=" + type +
                ", user_id=" + user_id +
                ", open_id='" + open_id + '\'' +
                ", union_id='" + union_id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }

    @IntDef({SOCIAL_TYPE_WECHAT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SocialType {
    }

}
