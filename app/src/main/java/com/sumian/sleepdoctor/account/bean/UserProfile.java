package com.sumian.sleepdoctor.account.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.sumian.sleepdoctor.improve.doctor.bean.Doctor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jzz
 * on 2018/1/17.
 * desc:
 */

public class UserProfile implements Serializable, Parcelable {

    public int id;
    public String mobile;
    public String nickname;
    public String name;
    public String avatar;
    public String area;
    public String gender;
    public String birthday;
    public String height;
    public String weight;
    public String leancloud_id;
    public int bound_at;
    public String last_login_at;
    public int created_at;//账号创建时间
    public int updated_at;//账号更新时间
    public int doctor_id;//已经绑定的医生的 id
    public List<Social> socialites;//绑定信息
    public Doctor doctor;//绑定的医生信息
    public int role; // add by zxz for passing compile

    public UserProfile() {
    }


    protected UserProfile(Parcel in) {
        id = in.readInt();
        mobile = in.readString();
        nickname = in.readString();
        name = in.readString();
        avatar = in.readString();
        area = in.readString();
        gender = in.readString();
        birthday = in.readString();
        height = in.readString();
        weight = in.readString();
        leancloud_id = in.readString();
        bound_at = in.readInt();
        last_login_at = in.readString();
        created_at = in.readInt();
        updated_at = in.readInt();
        doctor_id = in.readInt();
        socialites = in.createTypedArrayList(Social.CREATOR);
        doctor = in.readParcelable(Doctor.class.getClassLoader());
        role = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(mobile);
        dest.writeString(nickname);
        dest.writeString(name);
        dest.writeString(avatar);
        dest.writeString(area);
        dest.writeString(gender);
        dest.writeString(birthday);
        dest.writeString(height);
        dest.writeString(weight);
        dest.writeString(leancloud_id);
        dest.writeInt(bound_at);
        dest.writeString(last_login_at);
        dest.writeInt(created_at);
        dest.writeInt(updated_at);
        dest.writeInt(doctor_id);
        dest.writeTypedList(socialites);
        dest.writeParcelable(doctor, flags);
        dest.writeInt(role);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserProfile> CREATOR = new Creator<UserProfile>() {
        @Override
        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }

        @Override
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", mobile='" + mobile + '\'' +
                ", nickname='" + nickname + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", area='" + area + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", leancloud_id='" + leancloud_id + '\'' +
                ", bound_at=" + bound_at +
                ", last_login_at='" + last_login_at + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", doctor_id=" + doctor_id +
                ", socialites=" + socialites +
                ", doctor=" + doctor +
                ", role=" + role +
                '}';
    }

    public boolean isBindDoctor() {
        return bound_at > 0 && doctor_id > 0 && doctor != null;
    }

    public boolean isSameDoctor(int otherDoctorId) {
        return isBindDoctor() && otherDoctorId == doctor_id && otherDoctorId == doctor.getId();
    }
}
