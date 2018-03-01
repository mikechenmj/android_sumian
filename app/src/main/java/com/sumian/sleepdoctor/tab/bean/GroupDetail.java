package com.sumian.sleepdoctor.tab.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jzz
 * on 2018/1/19.
 * desc:
 */

public class GroupDetail<Doctor, User> implements Serializable {

    public int id;//群自增id (未加群，未登录，患者，医团)
    public int group_no;//群id (未加群，未登录，患者，医团)
    public String name;//群名 (未加群，未登录，患者，医团)
    public String description;//群简介 (未加群，未登录，患者，医团)
    public String conversation_id;//会话id (未加群，未登录，患者，医团)
    public double monthly_price;//30天价格 (未加群，未登录，患者，医团)
    public String avatar;//群头像 (未加群，未登录，患者，医团)
    public int created_at;//群创建时间 (未加群，未登录，患者，医团)
    public int updated_at;//群更新时间 (未加群，未登录，患者，医团)
    public int user_count;//群人数 (医团)
    public int role;//本人在群里身份 0：患者，1：运营人员，2：医生助理，3：医生(患者,医团)
    public String code_url;//二维码url (医团)
    public int expired_at;//过期时间 (患者)
    public int day_last;//剩余时间，0表示已过期 (患者)
    public Doctor doctor;//医生信息 (未加群，未登录，患者，医团)
    public List<User> users;//群成员信息 include=users 时显示 (医团)
    public List<Packages> packages;//群成员信息 include=packages 时显示 (未加群，未登录，患者，医团)

    @Override
    public String toString() {
        return "GroupDetail{" +
                "id=" + id +
                ", group_no=" + group_no +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", conversation_id='" + conversation_id + '\'' +
                ", monthly_price=" + monthly_price +
                ", avatar='" + avatar + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", user_count=" + user_count +
                ", role=" + role +
                ", code_url='" + code_url + '\'' +
                ", expired_at=" + expired_at +
                ", day_last=" + day_last +
                ", doctor=" + doctor +
                ", users=" + users +
                ", packages=" + packages +
                '}';
    }
}
