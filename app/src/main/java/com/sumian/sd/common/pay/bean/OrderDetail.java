package com.sumian.sd.common.pay.bean;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/11 19:11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class OrderDetail {

    /**
     * id : 327
     * user_id : 2102
     * package_id : 13
     * group_id : 0
     * charge_id : ch_P484e5bXHG4GabPWb1qvDGmH
     * order_no : 1806111528707709566
     * currency : cny
     * unit_price : 1
     * quantity : 1
     * amount : 1
     * channel : wx
     * time_paid : 1528707713
     * time_expire : 1528714909
     * refund_id : re_4yL0809G8O8SazrrTKuDuPOG
     * refunded_at : 1528707791
     * refund_way : 原路退回
     * refund_reason : 图文咨询服务，医生超过24小时未回复
     * status : 4  订单状态  0：待支付，1：支付成功，2：支付失败 3：退款中 4：退款成功 5：退款失败
     * snapshot : {"id":13,"group_id":0,"days":0,"description":"<p><span style=\"color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, \"Helvetica Neue\", \"lucida grande\", PingFangHK-Light, STHeiti, \"Heiti SC\", \"Hiragino Sans GB\", \"Microsoft JhengHei\", \"Microsoft Yahei\", SimHei, \"WenQuanYi Micro Hei\", \"Droid Sans\", Roboto, Helvetica, Tahoma, Arial, sans-serif;\">提供您与医生线上交流机会，请在提问中详尽您的病情描述，医生将尽快予以回复。 服务内容包括但不限于： <\/span><\/p>\r\n<p><span style=\"color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, \"Helvetica Neue\", \"lucida grande\", PingFangHK-Light, STHeiti, \"Heiti SC\", \"Hiragino Sans GB\", \"Microsoft JhengHei\", \"Microsoft Yahei\", SimHei, \"WenQuanYi Micro Hei\", \"Droid Sans\", Roboto, Helvetica, Tahoma, Arial, sans-serif;\">1.解读相关睡眠报告； <\/span><\/p>\r\n<p><span style=\"color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, \"Helvetica Neue\", \"lucida grande\", PingFangHK-Light, STHeiti, \"Heiti SC\", \"Hiragino Sans GB\", \"Microsoft JhengHei\", \"Microsoft Yahei\", SimHei, \"WenQuanYi Micro Hei\", \"Droid Sans\", Roboto, Helvetica, Tahoma, Arial, sans-serif;\">2. 分析睡眠现状； <\/span><\/p>\r\n<p><span style=\"color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, \"Helvetica Neue\", \"lucida grande\", PingFangHK-Light, STHeiti, \"Heiti SC\", \"Hiragino Sans GB\", \"Microsoft JhengHei\", \"Microsoft Yahei\", SimHei, \"WenQuanYi Micro Hei\", \"Droid Sans\", Roboto, Helvetica, Tahoma, Arial, sans-serif;\">3.提出改善睡眠质量建议； <\/span><\/p>\r\n<p><span style=\"color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, \"Helvetica Neue\", \"lucida grande\", PingFangHK-Light, STHeiti, \"Heiti SC\", \"Hiragino Sans GB\", \"Microsoft JhengHei\", \"Microsoft Yahei\", SimHei, \"WenQuanYi Micro Hei\", \"Droid Sans\", Roboto, Helvetica, Tahoma, Arial, sans-serif;\">4. 症状、药物使用咨询<\/span><\/p>","unit_price":1,"service_id":3,"unit":1,"service_length":1,"doctor_id":1,"enable":1,"deleted_at":null,"created_at":1526383945,"updated_at":1526518733,"not_buy_description":"未购买","bought_description":"已购买","price_text":"0.01元/1次","service":{"id":3,"type":1,"name":"图文咨询服务","description":"提供您与医生线上交流机会，请在提问中详尽您的病情描述，医生将尽快予以回复。\r\n服务内容包括但不限于：\r\n1.解读相关睡眠报告；\r\n2.分析睡眠现状；\r\n3.提出改善睡眠质量建议；\r\n4. 症状、药物使用咨询","picture":"https://sleep-doctor.oss-cn-shenzhen.aliyuncs.com/doctors/service/3/7fe107b2-21be-40ba-98c4-a80a7072aecc.jpg","icon":"https://sleep-doctor.oss-cn-shenzhen.aliyuncs.com/doctors/service/3/045d8de7-adab-4ade-8ea5-04b0914abe3c.jpg","created_at":1526383912,"updated_at":1526455732,"not_buy_description":"专业医生为您提供个性化咨询建议","bought_description":"已购买，请尽快开始提问"}}
     * created_at : 1528707710
     * updated_at : 1528707792
     */

    private int id;
    private int user_id;
    private int package_id;
    private int group_id;
    private String charge_id;
    private String order_no;
    private String currency;
    private int unit_price;
    private int quantity;
    private int amount;
    private String channel;
    private int time_paid;
    private int time_expire;
    private String refund_id;
    private int refunded_at;
    private String refund_way;
    private String refund_reason;
    private int status;
    private SnapshotBean snapshot;
    private int created_at;
    private int updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getPackage_id() {
        return package_id;
    }

    public void setPackage_id(int package_id) {
        this.package_id = package_id;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getCharge_id() {
        return charge_id;
    }

    public void setCharge_id(String charge_id) {
        this.charge_id = charge_id;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(int unit_price) {
        this.unit_price = unit_price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getTime_paid() {
        return time_paid;
    }

    public void setTime_paid(int time_paid) {
        this.time_paid = time_paid;
    }

    public int getTime_expire() {
        return time_expire;
    }

    public void setTime_expire(int time_expire) {
        this.time_expire = time_expire;
    }

    public String getRefund_id() {
        return refund_id;
    }

    public void setRefund_id(String refund_id) {
        this.refund_id = refund_id;
    }

    public int getRefunded_at() {
        return refunded_at;
    }

    public void setRefunded_at(int refunded_at) {
        this.refunded_at = refunded_at;
    }

    public long getRefundedAtInMillis() {
        return refunded_at * 1000L;
    }

    public String getRefund_way() {
        return refund_way;
    }

    public void setRefund_way(String refund_way) {
        this.refund_way = refund_way;
    }

    public String getRefund_reason() {
        return refund_reason;
    }

    public void setRefund_reason(String refund_reason) {
        this.refund_reason = refund_reason;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public SnapshotBean getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(SnapshotBean snapshot) {
        this.snapshot = snapshot;
    }

    public int getCreated_at() {
        return created_at;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    public int getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(int updated_at) {
        this.updated_at = updated_at;
    }

    public static class SnapshotBean {
        /**
         * id : 13
         * group_id : 0
         * days : 0
         * description : <p><span style="color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, "Helvetica Neue", "lucida grande", PingFangHK-Light, STHeiti, "Heiti SC", "Hiragino Sans GB", "Microsoft JhengHei", "Microsoft Yahei", SimHei, "WenQuanYi Micro Hei", "Droid Sans", Roboto, Helvetica, Tahoma, Arial, sans-serif;">提供您与医生线上交流机会，请在提问中详尽您的病情描述，医生将尽快予以回复。 服务内容包括但不限于： </span></p>
         * <p><span style="color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, "Helvetica Neue", "lucida grande", PingFangHK-Light, STHeiti, "Heiti SC", "Hiragino Sans GB", "Microsoft JhengHei", "Microsoft Yahei", SimHei, "WenQuanYi Micro Hei", "Droid Sans", Roboto, Helvetica, Tahoma, Arial, sans-serif;">1.解读相关睡眠报告； </span></p>
         * <p><span style="color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, "Helvetica Neue", "lucida grande", PingFangHK-Light, STHeiti, "Heiti SC", "Hiragino Sans GB", "Microsoft JhengHei", "Microsoft Yahei", SimHei, "WenQuanYi Micro Hei", "Droid Sans", Roboto, Helvetica, Tahoma, Arial, sans-serif;">2. 分析睡眠现状； </span></p>
         * <p><span style="color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, "Helvetica Neue", "lucida grande", PingFangHK-Light, STHeiti, "Heiti SC", "Hiragino Sans GB", "Microsoft JhengHei", "Microsoft Yahei", SimHei, "WenQuanYi Micro Hei", "Droid Sans", Roboto, Helvetica, Tahoma, Arial, sans-serif;">3.提出改善睡眠质量建议； </span></p>
         * <p><span style="color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, "Helvetica Neue", "lucida grande", PingFangHK-Light, STHeiti, "Heiti SC", "Hiragino Sans GB", "Microsoft JhengHei", "Microsoft Yahei", SimHei, "WenQuanYi Micro Hei", "Droid Sans", Roboto, Helvetica, Tahoma, Arial, sans-serif;">4. 症状、药物使用咨询</span></p>
         * unit_price : 1
         * service_id : 3
         * unit : 1
         * service_length : 1
         * doctor_id : 1
         * enable : 1
         * deleted_at : null
         * created_at : 1526383945
         * updated_at : 1526518733
         * not_buy_description : 未购买
         * bought_description : 已购买
         * price_text : 0.01元/1次
         * service : {"id":3,"type":1,"name":"图文咨询服务","description":"提供您与医生线上交流机会，请在提问中详尽您的病情描述，医生将尽快予以回复。\r\n服务内容包括但不限于：\r\n1.解读相关睡眠报告；\r\n2.分析睡眠现状；\r\n3.提出改善睡眠质量建议；\r\n4. 症状、药物使用咨询","picture":"https://sleep-doctor.oss-cn-shenzhen.aliyuncs.com/doctors/service/3/7fe107b2-21be-40ba-98c4-a80a7072aecc.jpg","icon":"https://sleep-doctor.oss-cn-shenzhen.aliyuncs.com/doctors/service/3/045d8de7-adab-4ade-8ea5-04b0914abe3c.jpg","created_at":1526383912,"updated_at":1526455732,"not_buy_description":"专业医生为您提供个性化咨询建议","bought_description":"已购买，请尽快开始提问"}
         */

        private int id;
        private int group_id;
        private int days;
        private String description;
        private int unit_price;
        private int service_id;
        private int unit;
        private int service_length;
        private int doctor_id;
        private int enable;
        private Object deleted_at;
        private int created_at;
        private int updated_at;
        private String not_buy_description;
        private String bought_description;
        private String price_text;
        private ServiceBean service;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getGroup_id() {
            return group_id;
        }

        public void setGroup_id(int group_id) {
            this.group_id = group_id;
        }

        public int getDays() {
            return days;
        }

        public void setDays(int days) {
            this.days = days;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getUnit_price() {
            return unit_price;
        }

        public void setUnit_price(int unit_price) {
            this.unit_price = unit_price;
        }

        public int getService_id() {
            return service_id;
        }

        public void setService_id(int service_id) {
            this.service_id = service_id;
        }

        public int getUnit() {
            return unit;
        }

        public void setUnit(int unit) {
            this.unit = unit;
        }

        public int getService_length() {
            return service_length;
        }

        public void setService_length(int service_length) {
            this.service_length = service_length;
        }

        public int getDoctor_id() {
            return doctor_id;
        }

        public void setDoctor_id(int doctor_id) {
            this.doctor_id = doctor_id;
        }

        public int getEnable() {
            return enable;
        }

        public void setEnable(int enable) {
            this.enable = enable;
        }

        public Object getDeleted_at() {
            return deleted_at;
        }

        public void setDeleted_at(Object deleted_at) {
            this.deleted_at = deleted_at;
        }

        public int getCreated_at() {
            return created_at;
        }

        public void setCreated_at(int created_at) {
            this.created_at = created_at;
        }

        public int getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(int updated_at) {
            this.updated_at = updated_at;
        }

        public String getNot_buy_description() {
            return not_buy_description;
        }

        public void setNot_buy_description(String not_buy_description) {
            this.not_buy_description = not_buy_description;
        }

        public String getBought_description() {
            return bought_description;
        }

        public void setBought_description(String bought_description) {
            this.bought_description = bought_description;
        }

        public String getPrice_text() {
            return price_text;
        }

        public void setPrice_text(String price_text) {
            this.price_text = price_text;
        }

        public ServiceBean getService() {
            return service;
        }

        public void setService(ServiceBean service) {
            this.service = service;
        }

        public static class ServiceBean {
            /**
             * id : 3
             * type : 1
             * name : 图文咨询服务
             * description : 提供您与医生线上交流机会，请在提问中详尽您的病情描述，医生将尽快予以回复。
             * 服务内容包括但不限于：
             * 1.解读相关睡眠报告；
             * 2.分析睡眠现状；
             * 3.提出改善睡眠质量建议；
             * 4. 症状、药物使用咨询
             * picture : https://sleep-doctor.oss-cn-shenzhen.aliyuncs.com/doctors/service/3/7fe107b2-21be-40ba-98c4-a80a7072aecc.jpg
             * icon : https://sleep-doctor.oss-cn-shenzhen.aliyuncs.com/doctors/service/3/045d8de7-adab-4ade-8ea5-04b0914abe3c.jpg
             * created_at : 1526383912
             * updated_at : 1526455732
             * not_buy_description : 专业医生为您提供个性化咨询建议
             * bought_description : 已购买，请尽快开始提问
             */

            private int id;
            private int type;
            private String name;
            private String description;
            private String picture;
            private String icon;
            private int created_at;
            private int updated_at;
            private String not_buy_description;
            private String bought_description;

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

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getPicture() {
                return picture;
            }

            public void setPicture(String picture) {
                this.picture = picture;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public int getCreated_at() {
                return created_at;
            }

            public void setCreated_at(int created_at) {
                this.created_at = created_at;
            }

            public int getUpdated_at() {
                return updated_at;
            }

            public void setUpdated_at(int updated_at) {
                this.updated_at = updated_at;
            }

            public String getNot_buy_description() {
                return not_buy_description;
            }

            public void setNot_buy_description(String not_buy_description) {
                this.not_buy_description = not_buy_description;
            }

            public String getBought_description() {
                return bought_description;
            }

            public void setBought_description(String bought_description) {
                this.bought_description = bought_description;
            }
        }
    }
}
