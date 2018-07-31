package com.sumian.hw.network.response;

import android.text.TextUtils;

import com.sumian.hw.account.bean.Answer;
import com.sumian.hw.log.LogManager;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

@SuppressWarnings("unused")
public class HwUserInfo implements Serializable, Cloneable {

    private long id;
    private String mobile;//getLeanCloudId
    private String nickname;//昵称
    private String avatar;//头像 uil
    private String area;//地区
    private String gender;//male 男  female 女 secrecy 保密
    private String birthday;//出生年月
    private String height;//身高  null  表示未设置
    private String weight;//体重  null  表示未设置
    private String bmi;//bmi，null 表示height或weight未设置
    private int socialite_id;
    private String created_at;//创建时间
    private String updated_at;//更新时间
    private String leancloud_id;//leancloud_id  用于leancloud 登录
    private String age;//年龄  null,表示为未设置
    private String monitor_sn;//用户当前绑定的监测仪sn
    private String sleeper_sn;//用户当前绑定的助眠仪sn
    private String last_login_at;//
    private String career;//职业
    private String im_id;//IM ID，环信客服用这个来登录
    private String im_password;//IM 密码，环信客服用这个来登录
    private Answer answers;//睡眠障碍评估表 null：未填
    private List<Social> socialites;//第三方账号绑定信息，为空表示没绑定第三方账号

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public int getGenderType() {
        int genderType;
        switch (gender) {
            case "male":
                genderType = 0x00;
                break;
            case "female":
                genderType = 0x01;
                break;
            case "secrecy":
            default:
                genderType = 0xff;
                break;
        }
        return genderType;
    }

    public int getFormatAge() {
        if (TextUtils.isEmpty(age)) {
            return 0xff;
        } else {
            return Integer.parseInt(age, 10);
        }
    }

    public int getFormatBmi() {
        if (TextUtils.isEmpty(bmi)) {
            return 0xff;
        } else {
            return (int) (Double.parseDouble(bmi) * 5.0f);
        }
    }

    public int getInsomnia() {
        if (answers == null) {
            return 0xff;
        } else {
            return answers.level;
        }
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
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

    public Answer getAnswers() {
        return answers;
    }

    public void setAnswers(Answer answers) {
        this.answers = answers;
    }

    public List<Social> getSocialites() {
        return socialites;
    }

    public void setSocialites(List<Social> socialites) {
        this.socialites = socialites;
    }

    public boolean isHaveUserInfoAndSleepBarrierTest() {
        return isHaveAnswers() && isHaveFullUserInfo();
    }

    public boolean isHaveAnswers() {
        return answers != null;
    }

    public boolean isHaveFullUserInfo() {
        return !TextUtils.isEmpty(bmi) && (!TextUtils.isEmpty(gender) && !"secrecy".equals(gender)) && (!TextUtils.isEmpty(birthday) && !"未设置".equals(birthday));
    }

    public int getSocialite_id() {
        return socialite_id;
    }

    public void setSocialite_id(int socialite_id) {
        this.socialite_id = socialite_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getLast_login_at() {
        return last_login_at;
    }

    public void setLast_login_at(String last_login_at) {
        this.last_login_at = last_login_at;
    }


    @SuppressWarnings("deprecation")
    public boolean isAccountCreateDate(long createDate) {
        try {
            Date parseDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(created_at);
            Calendar tmpCalendar = Calendar.getInstance();
            tmpCalendar.set(parseDate.getYear(), parseDate.getMonth(), parseDate.getDate(), 0, 0, 0);
            return tmpCalendar.getTimeInMillis() / 1000L <= createDate;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        return "HwUserInfo{" +
            "id=" + id +
            ", mobile='" + mobile + '\'' +
            ", nickname='" + nickname + '\'' +
            ", avatar='" + avatar + '\'' +
            ", area='" + area + '\'' +
            ", gender='" + gender + '\'' +
            ", birthday='" + birthday + '\'' +
            ", height='" + height + '\'' +
            ", weight='" + weight + '\'' +
            ", bmi='" + bmi + '\'' +
            ", socialite_id=" + socialite_id +
            ", created_at='" + created_at + '\'' +
            ", updated_at='" + updated_at + '\'' +
            ", leancloud_id='" + leancloud_id + '\'' +
            ", age='" + age + '\'' +
            ", monitor_sn='" + monitor_sn + '\'' +
            ", sleeper_sn='" + sleeper_sn + '\'' +
            ", last_login_at='" + last_login_at + '\'' +
            ", career='" + career + '\'' +
            ", im_id='" + im_id + '\'' +
            ", im_password='" + im_password + '\'' +
            ", answers=" + answers +
            ", socialites=" + socialites +
            '}';
    }

    public static class Social implements Serializable {

        private int id;//第三方平台账号绑定 ID
        private int type;//第三方平台类型，0：微信
        private String open_id;
        private String union_id;
        private String nickname;//

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getOpen_id() {
            return open_id;
        }

        public void setOpen_id(String open_id) {
            this.open_id = open_id;
        }

        public String getUnion_id() {
            return union_id;
        }

        public void setUnion_id(String union_id) {
            this.union_id = union_id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        @Override
        public String toString() {
            return "Social{" +
                "id=" + id +
                ", type=" + type +
                ", open_id='" + open_id + '\'' +
                ", union_id='" + union_id + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
        }
    }

    public int getBirthdayYear() {
        return getBirthday(getBirthday())[0];
    }

    public int getBirthdayMonth() {
        return getBirthday(getBirthday())[1];
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
            LogManager.appendPhoneLog(e.getMessage());
        }
        return value;
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
            LogManager.appendPhoneLog(e.getMessage());
        }
        return yearMonth;
    }

    @Override
    public HwUserInfo clone() throws CloneNotSupportedException {
        return (HwUserInfo) super.clone();//只对string 和基本数据类型进行 clone  不需要对新的引用类型进行拷贝  所以是相对来说的深拷贝,如果需要完全深拷贝需要为其引用对象也实现 clone, 并调用拷贝
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

}
