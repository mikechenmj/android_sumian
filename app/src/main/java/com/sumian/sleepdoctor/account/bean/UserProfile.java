package com.sumian.sleepdoctor.account.bean;

/**
 * Created by jzz
 * on 2018/1/17.
 * desc:
 */

public class UserProfile {

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
                '}';
    }
}
