package com.sumian.sleepdoctor.pager.bean;

import java.util.List;

/**
 * Created by sm
 * on 2018/1/24.
 * desc:
 */

public class Order {

    public String id;// 由 Ping++ 生成的支付对象 ID， 27 位字符串。
    public String object;//"charge",  值为 "charge"。
    public int created;//支付创建时的 Unix 时间戳。
    public boolean livemode;//是否处于  live 模式。
    public boolean paid;//是否已付款。
    public boolean refunded;//  是否存在退款信息，无论退款是否成功
    public boolean reversed; // 订单是否撤销。
    public String app;//"app_1Gqj58ynP0mHeX1q",  支付使用的  app 对象的  id ，expandable 可展开，
    public String channel;//"alipay",  支付使用的第三方支付渠道
    public String order_no;// "123456789",   商户订单号(自定义，时间戳+随机字符串)
    public String client_ip;// "127.0.0.1",  发起支付请求客户端的 IP 地址，格式为 IPv4 整型
    public float amount;// 100,  订单总金额（必须大于 0），单位为对应币种的最小货币单位，人民币为分。
    public float amount_settle;// 100,  清算金额，单位为对应币种的最小货币单位，人民币为分。
    public String currency;// "cny",  3 位 ISO 货币代码，人民币为  cny 。
    public String subject;// "Your Subject",  商品标题
    public String body;// "Your Body",  商品描述信息
    public Extra extra;//特定渠道发起交易时需要的额外参数，以及部分渠道支付成功返回的额外参数
    public String time_paid;// null,  订单支付完成时的 Unix 时间戳。
    public int time_expire;// 1410838127,  订单失效时间的 Unix 时间戳。
    public String time_settle;// null,  订单清算时间，用 Unix 时间戳表示。（暂不生效）
    public String transaction_no;// null,  支付渠道返回的交易流水号。
    public Refunds refunds;//退款详情列表
    public float amount_refunded;// 0,  已退款总金额，单位为对应币种的最小货币单位，例如：人民币为分。
    public String failure_code;// null,  订单的错误码
    public String failure_msg;//null,  订单的错误消息的描述
    public MetaData metaData;//元数据
    public Credential credential;//支付凭证，用于客户端发起支付
    public String description;// null  订单附加说明

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", object='" + object + '\'' +
                ", created=" + created +
                ", livemode=" + livemode +
                ", paid=" + paid +
                ", refunded=" + refunded +
                ", reversed=" + reversed +
                ", app='" + app + '\'' +
                ", channel='" + channel + '\'' +
                ", order_no='" + order_no + '\'' +
                ", client_ip='" + client_ip + '\'' +
                ", amount=" + amount +
                ", amount_settle=" + amount_settle +
                ", currency='" + currency + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", extra=" + extra +
                ", time_paid='" + time_paid + '\'' +
                ", time_expire=" + time_expire +
                ", time_settle='" + time_settle + '\'' +
                ", transaction_no='" + transaction_no + '\'' +
                ", refunds=" + refunds +
                ", amount_refunded=" + amount_refunded +
                ", failure_code='" + failure_code + '\'' +
                ", failure_msg='" + failure_msg + '\'' +
                ", metaData=" + metaData +
                ", credential=" + credential +
                ", description='" + description + '\'' +
                '}';
    }

    public class Extra {

    }

    public class Refunds {
        public String object;//list
        public String url;//   /v1/charges/ch_L8qn10mLmr1GS8e5OODmHaL4/refunds"
        public boolean has_more;
        public List<Object> data;

        @Override
        public String toString() {
            return "Refunds{" +
                    "object='" + object + '\'' +
                    ", url='" + url + '\'' +
                    ", has_more=" + has_more +
                    ", data=" + data +
                    '}';
        }
    }

    public static class MetaData {
        public int package_id;//套餐 id
        public int quantity;//购买数量

        @Override
        public String toString() {
            return "MetaData{" +
                    "package_id=" + package_id +
                    ", quantity=" + quantity +
                    '}';
        }
    }

    public class Credential {
        public String object;// "credential"
        public Upacp upacp;//

        public class Upacp {

            public String tn;//201409161028470000000
            public String mode;//01

            @Override
            public String toString() {
                return "Upacp{" +
                        "tn='" + tn + '\'' +
                        ", mode='" + mode + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "Credential{" +
                    "object='" + object + '\'' +
                    ", upacp=" + upacp +
                    '}';
        }
    }


}
