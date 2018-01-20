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
    public String last_login_at;//最后登录时间
    public int role;//角色扮演  0：患者，1：运营人员，2：医生助理，3：医生(医团)
    public int created_at;//账号创建时间 (医团)
    public int updated_at;//账号更新时间 (医团)

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
                ", last_login_at='" + last_login_at + '\'' +
                ", role=" + role +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}
