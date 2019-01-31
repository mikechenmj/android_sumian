package com.sumian.sd.buz.diary.sleeprecord.bean;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/5 9:38
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DoctorServicePackage {
    /**
     * id : 13
     * days : 0
     * unit_price : 1
     * description : <p><span style="color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, "Helvetica Neue", "lucida grande", PingFangHK-Light, STHeiti, "Heiti SC", "Hiragino Sans GB", "Microsoft JhengHei", "Microsoft Yahei", SimHei, "WenQuanYi Micro Hei", "Droid Sans", Roboto, Helvetica, Tahoma, Arial, sans-serif;">提供您与医生线上交流机会，请在提问中详尽您的病情描述，医生将尽快予以回复。 服务内容包括但不限于： </span></p>
     * <p><span style="color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, "Helvetica Neue", "lucida grande", PingFangHK-Light, STHeiti, "Heiti SC", "Hiragino Sans GB", "Microsoft JhengHei", "Microsoft Yahei", SimHei, "WenQuanYi Micro Hei", "Droid Sans", Roboto, Helvetica, Tahoma, Arial, sans-serif;">1.解读相关睡眠报告； </span></p>
     * <p><span style="color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, "Helvetica Neue", "lucida grande", PingFangHK-Light, STHeiti, "Heiti SC", "Hiragino Sans GB", "Microsoft JhengHei", "Microsoft Yahei", SimHei, "WenQuanYi Micro Hei", "Droid Sans", Roboto, Helvetica, Tahoma, Arial, sans-serif;">2. 分析睡眠现状； </span></p>
     * <p><span style="color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, "Helvetica Neue", "lucida grande", PingFangHK-Light, STHeiti, "Heiti SC", "Hiragino Sans GB", "Microsoft JhengHei", "Microsoft Yahei", SimHei, "WenQuanYi Micro Hei", "Droid Sans", Roboto, Helvetica, Tahoma, Arial, sans-serif;">3.提出改善睡眠质量建议； </span></p>
     * <p><span style="color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, "Helvetica Neue", "lucida grande", PingFangHK-Light, STHeiti, "Heiti SC", "Hiragino Sans GB", "Microsoft JhengHei", "Microsoft Yahei", SimHei, "WenQuanYi Micro Hei", "Droid Sans", Roboto, Helvetica, Tahoma, Arial, sans-serif;">4. 症状、药物使用咨询</span></p>
     * not_buy_description : 未购买
     * bought_description : 已购买
     * unit : 1
     * service_length : 1
     * price_text : 0.01元/1次
     */

    private int id;
    private int days;
    private int unit_price;
    private String description;
    private String not_buy_description;
    private String bought_description;
    private int unit;
    private int service_length;
    private String price_text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(int unit_price) {
        this.unit_price = unit_price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getPrice_text() {
        return price_text;
    }

    public void setPrice_text(String price_text) {
        this.price_text = price_text;
    }
}
