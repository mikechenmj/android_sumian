package com.sumian.sd.account.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.sumian.hw.common.util.TextUtil;
import com.sumian.sd.R;
import com.sumian.sd.app.App;
import com.sumian.sd.doctor.bean.Doctor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jzz
 * on 2018/1/17.
 * desc:
 */

@SuppressWarnings("WeakerAccess")
public class UserInfo implements Parcelable, Serializable, Cloneable {

    public int id;
    public String mobile;
    public String nickname;
    public String name;
    public String avatar;
    public String area;
    public String gender;
    public String birthday;
    public Integer age;
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
    public String im_id;
    public String im_password;
    public List<Social> socialites;//绑定信息
    public Doctor doctor;//绑定的医生信息
    public int role; // add by zxz for passing compile
    public Answers answers;
    @SerializedName("set_password")
    public boolean hasPassword;

    protected UserInfo(Parcel in) {
        id = in.readInt();
        mobile = in.readString();
        nickname = in.readString();
        name = in.readString();
        avatar = in.readString();
        area = in.readString();
        gender = in.readString();
        birthday = in.readString();
        if (in.readByte() == 0) {
            age = null;
        } else {
            age = in.readInt();
        }
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
        im_id = in.readString();
        im_password = in.readString();
        socialites = in.createTypedArrayList(Social.CREATOR);
        doctor = in.readParcelable(Doctor.class.getClassLoader());
        role = in.readInt();
        hasPassword = in.readByte() != 0;
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public boolean isBindDoctor() {
        return bound_at > 0 && doctor_id > 0 && doctor != null;
    }

    public boolean isSameDoctor(int otherDoctorId) {
        return isBindDoctor() && otherDoctorId == doctor_id && otherDoctorId == doctor.getId();
    }

    public String formatGander() {

        if (TextUtils.isEmpty(gender)) {
            return App.Companion.getAppContext().getString(R.string.none_edit);
        }

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

    public boolean isHaveAnswers() {
        return answers != null;
    }

    public boolean isHaveFullUserInfo() {
        return !TextUtils.isEmpty(bmi) && (!TextUtils.isEmpty(gender) && !"secrecy".equals(gender)) && (!TextUtils.isEmpty(birthday) && !"未设置".equals(birthday));
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
        if (age == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(age);
        }
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
        dest.writeString(im_id);
        dest.writeString(im_password);
        dest.writeTypedList(socialites);
        dest.writeParcelable(doctor, flags);
        dest.writeInt(role);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public String getLeancloud_id() {
        return leancloud_id;
    }

    public void setLeancloud_id(String leancloud_id) {
        this.leancloud_id = leancloud_id;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }

    public int getBound_at() {
        return bound_at;
    }

    public void setBound_at(int bound_at) {
        this.bound_at = bound_at;
    }

    public String getLast_login_at() {
        return last_login_at;
    }

    public void setLast_login_at(String last_login_at) {
        this.last_login_at = last_login_at;
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getMonitor_sn() {
        return monitor_sn;
    }

    public void setMonitor_sn(String monitor_sn) {
        this.monitor_sn = monitor_sn;
    }

    public String getSleeper_sn() {
        return sleeper_sn;
    }

    public void setSleeper_sn(String sleeper_sn) {
        this.sleeper_sn = sleeper_sn;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getIm_id() {
        return im_id;
    }

    public void setIm_id(String im_id) {
        this.im_id = im_id;
    }

    public String getIm_password() {
        return im_password;
    }

    public void setIm_password(String im_password) {
        this.im_password = im_password;
    }

    public List<Social> getSocialites() {
        return socialites;
    }

    public void setSocialites(List<Social> socialites) {
        this.socialites = socialites;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public Answers getAnswers() {
        return answers;
    }

    public void setAnswers(Answers answers) {
        this.answers = answers;
    }

    @Override
    public UserInfo clone() throws CloneNotSupportedException {
        return (UserInfo) super.clone();//只对string 和基本数据类型进行 clone  不需要对新的引用类型进行拷贝  所以是相对来说的深拷贝,如果需要完全深拷贝需要为其引用对象也实现 clone, 并调用拷贝
    }

    public String[] getAddressArray() {
        String[] addressArray = new String[3];
        if (TextUtils.isEmpty(area)) {
            return addressArray;
        }
        String[] split = area.split("/");
        System.arraycopy(split, 0, addressArray, 0, split.length);
        return addressArray;
    }

    public boolean isHaveUserInfoAndSleepBarrierTest() {
        return isHaveAnswers() && isHaveFullUserInfo();
    }

    @Override
    public String toString() {
        return "UserInfo{" +
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
                ", im_id='" + im_id + '\'' +
                ", im_password='" + im_password + '\'' +
                ", socialites=" + socialites +
                ", doctor=" + doctor +
                ", role=" + role +
                ", answers=" + answers +
                '}';
    }

    public boolean hasDevice() {
        return !TextUtils.isEmpty(monitor_sn);
    }
}
