package com.sumian.sleepdoctor.account.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.doctor.bean.Doctor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jzz
 * on 2018/1/17.
 * desc:
 */

@SuppressWarnings("WeakerAccess")
public class UserProfile implements Serializable, Parcelable {

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
    public int id;
    public String mobile;
    public String nickname;
    public String name;
    public String avatar;
    public String area;
    public String gender;
    public String birthday;
    public int age;
    public String height;
    public String weight;
    public String bmi;
    public String leancloud_id;
    public int doctor_id;//已经绑定的医生的 id
    public int bound_at;
    public String last_login_at;
    public String device_info;
    public String monitor_sn;
    public String sleeper_sn;
    public String career;
    public String education;
    public int created_at;//账号创建时间
    public int updated_at;//账号更新时间
    public String im_id;
    public String im_password;
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
        age = in.readInt();
        height = in.readString();
        weight = in.readString();
        bmi = in.readString();
        leancloud_id = in.readString();
        doctor_id = in.readInt();
        bound_at = in.readInt();
        last_login_at = in.readString();
        device_info = in.readString();
        monitor_sn = in.readString();
        sleeper_sn = in.readString();
        career = in.readString();
        education = in.readString();
        created_at = in.readInt();
        updated_at = in.readInt();
        im_id = in.readString();
        im_password = in.readString();
        socialites = in.createTypedArrayList(Social.CREATOR);
        doctor = in.readParcelable(Doctor.class.getClassLoader());
        role = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
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
        dest.writeInt(age);
        dest.writeString(height);
        dest.writeString(weight);
        dest.writeString(bmi);
        dest.writeString(leancloud_id);
        dest.writeInt(doctor_id);
        dest.writeInt(bound_at);
        dest.writeString(last_login_at);
        dest.writeString(device_info);
        dest.writeString(monitor_sn);
        dest.writeString(sleeper_sn);
        dest.writeString(career);
        dest.writeString(education);
        dest.writeInt(created_at);
        dest.writeInt(updated_at);
        dest.writeString(im_id);
        dest.writeString(im_password);
        dest.writeTypedList(socialites);
        dest.writeParcelable(doctor, flags);
        dest.writeInt(role);
    }

    public boolean isBindDoctor() {
        return bound_at > 0 && doctor_id > 0 && doctor != null;
    }

    public boolean isSameDoctor(int otherDoctorId) {
        return isBindDoctor() && otherDoctorId == doctor_id && otherDoctorId == doctor.getId();
    }

    public String formatGander() {
        switch (gender) {
            case "male":
                return App.Companion.getAppContext().getString(R.string.male);
            case "female":
                return App.Companion.getAppContext().getString(R.string.female);
            case "secrecy":
            default:
                return App.Companion.getAppContext().getString(R.string.none_edit);
        }
    }

    public String formatField(String field) {
        return TextUtils.isEmpty(field) ? App.Companion.getAppContext().getString(R.string.none_edit) : field;
    }

    public String formatWeight(String weight) {
        return App.Companion.getAppContext().getString(R.string.none_edit).equals(weight) ? weight : weight + "kg";
    }

    public String formatHeight(String height) {
        return App.Companion.getAppContext().getString(R.string.none_edit).equals(height) ? height : height + "cm";
    }

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
                ", age=" + age +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", bmi='" + bmi + '\'' +
                ", leancloud_id='" + leancloud_id + '\'' +
                ", doctor_id=" + doctor_id +
                ", bound_at=" + bound_at +
                ", last_login_at='" + last_login_at + '\'' +
                ", device_info='" + device_info + '\'' +
                ", monitor_sn='" + monitor_sn + '\'' +
                ", sleeper_sn='" + sleeper_sn + '\'' +
                ", career='" + career + '\'' +
                ", education='" + education + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", im_id='" + im_id + '\'' +
                ", im_password='" + im_password + '\'' +
                ", socialites=" + socialites +
                ", doctor=" + doctor +
                ", role=" + role +
                '}';
    }

    private int[] getBirthday(String birthdayStr) {
        int[] yearMonth = new int[2];
        if (TextUtils.isEmpty(birthdayStr)) {
            return yearMonth;
        }
        String[] split = birthdayStr.split("-");
        try {
            if (split.length == 2) {
                yearMonth[0] = Integer.valueOf(split[0]);
                yearMonth[1] = Integer.valueOf(split[1]);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return yearMonth;
    }

    public int getBirthdayYear() {
        return getBirthday(birthday)[0];
    }

    public int getBirthdayMonth() {
        return getBirthday(birthday)[1];
    }

    public float getHeightValue() {
        return formatFloat(height);
    }

    public float getWeightValue() {
        return formatFloat(weight);
    }

    private float formatFloat(String floatStr) {
        if (TextUtils.isEmpty(floatStr)) {
            return 0f;
        }
        float value = 0f;
        try {
            value = Float.valueOf(floatStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public boolean isMale() {
        return "male".equals(gender);
    }

    public String getNameOrNickname() {
        return !TextUtils.isEmpty(name) ? name : nickname;
    }
}
