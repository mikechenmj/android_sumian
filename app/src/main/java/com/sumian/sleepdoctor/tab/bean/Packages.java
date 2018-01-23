package com.sumian.sleepdoctor.tab.bean;

/**
 * Created by sm
 * on 2018/1/23.
 * desc:群成员信息 include=packages 时显示 (未加群，未登录，患者，医团)
 */

public class Packages {
    public int id;//群套餐id (未加群，未登录，患者，医团)
    public int days;//天 (未加群，未登录，患者，医团)
    public String description;//群套餐描述 (未加群，未登录，患者，医团)
    public int unit_price;//群套餐价格 (未加群，未登录，患者，医团)

    @Override
    public String toString() {
        return "Packages{" +
                "id=" + id +
                ", days=" + days +
                ", description='" + description + '\'' +
                ", unit_price=" + unit_price +
                '}';
    }
}
