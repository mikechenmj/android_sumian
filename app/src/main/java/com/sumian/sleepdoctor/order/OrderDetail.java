package com.sumian.sleepdoctor.order;

/**
 * Created by sm
 * on 2018/1/24.
 * desc:
 */

public class OrderDetail {

    public int id;
    public int user_id;
    public int package_id;//套餐id
    public int group_id;//群 id
    public String charge_id;//支付平台查询订单号
    public String order_no;//app支付订单号
    public String currency;//币种（cny）
    public float unit_price;//单价，单位元
    public int quantity;//数量
    public float amount;//付款金额，单位元
    public String channel;//支付平台(WeChat alipay)
    public int time_paid;//完成支付时间
    public int time_expire;//订单过期时间
    public int final_a;//是否是最终结果
    public int status;//订单状态 0：待支付，1：支付成功，2：支付失败
    public int created_at;//订单创建时间
    public int updated_at;//订单更新时间

    @Override
    public String toString() {
        return "OrderDetail{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", package_id=" + package_id +
                ", group_id=" + group_id +
                ", charge_id='" + charge_id + '\'' +
                ", order_no='" + order_no + '\'' +
                ", currency='" + currency + '\'' +
                ", unit_price=" + unit_price +
                ", quantity=" + quantity +
                ", amount=" + amount +
                ", channel='" + channel + '\'' +
                ", time_paid=" + time_paid +
                ", time_expire=" + time_expire +
                ", final_a=" + final_a +
                ", status=" + status +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}
