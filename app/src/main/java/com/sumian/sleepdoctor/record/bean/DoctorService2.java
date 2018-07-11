package com.sumian.sleepdoctor.record.bean;

import java.util.List;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/5 9:37
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DoctorService2 {
    /**
     * id : 3
     * name : 图文咨询服务
     * description : 提供您与医生线上交流机会，请在提问中详尽您的病情描述，医生将尽快予以回复。
     * 服务内容包括但不限于：
     * 1.解读相关睡眠报告；
     * 2.分析睡眠现状；
     * 3.提出改善睡眠质量建议；
     * 4. 症状、药物使用咨询
     * picture : https://sleep-doctor.oss-cn-shenzhen.aliyuncs.com/doctors/service/3/7fe107b2-21be-40ba-98c4-a80a7072aecc.jpg
     * icon : https://sleep-doctor.oss-cn-shenzhen.aliyuncs.com/doctors/service/3/045d8de7-adab-4ade-8ea5-04b0914abe3c.jpg
     * not_buy_description : 专业医生为您提供个性化咨询建议
     * bought_description : 已购买，请尽快开始提问
     * type : 1
     * day_last : 0
     * expired_at : 0
     * packages : [{"id":13,"days":0,"unit_price":1,"description":"<p><span style=\"color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, \"Helvetica Neue\", \"lucida grande\", PingFangHK-Light, STHeiti, \"Heiti SC\", \"Hiragino Sans GB\", \"Microsoft JhengHei\", \"Microsoft Yahei\", SimHei, \"WenQuanYi Micro Hei\", \"Droid Sans\", Roboto, Helvetica, Tahoma, Arial, sans-serif;\">提供您与医生线上交流机会，请在提问中详尽您的病情描述，医生将尽快予以回复。 服务内容包括但不限于： <\/span><\/p>\r\n<p><span style=\"color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, \"Helvetica Neue\", \"lucida grande\", PingFangHK-Light, STHeiti, \"Heiti SC\", \"Hiragino Sans GB\", \"Microsoft JhengHei\", \"Microsoft Yahei\", SimHei, \"WenQuanYi Micro Hei\", \"Droid Sans\", Roboto, Helvetica, Tahoma, Arial, sans-serif;\">1.解读相关睡眠报告； <\/span><\/p>\r\n<p><span style=\"color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, \"Helvetica Neue\", \"lucida grande\", PingFangHK-Light, STHeiti, \"Heiti SC\", \"Hiragino Sans GB\", \"Microsoft JhengHei\", \"Microsoft Yahei\", SimHei, \"WenQuanYi Micro Hei\", \"Droid Sans\", Roboto, Helvetica, Tahoma, Arial, sans-serif;\">2. 分析睡眠现状； <\/span><\/p>\r\n<p><span style=\"color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, \"Helvetica Neue\", \"lucida grande\", PingFangHK-Light, STHeiti, \"Heiti SC\", \"Hiragino Sans GB\", \"Microsoft JhengHei\", \"Microsoft Yahei\", SimHei, \"WenQuanYi Micro Hei\", \"Droid Sans\", Roboto, Helvetica, Tahoma, Arial, sans-serif;\">3.提出改善睡眠质量建议； <\/span><\/p>\r\n<p><span style=\"color: rgb(183,183,183);background-color: rgb(255,255,255);font-size: 14px;font-family: AvenirNext-Regular, \"Helvetica Neue\", \"lucida grande\", PingFangHK-Light, STHeiti, \"Heiti SC\", \"Hiragino Sans GB\", \"Microsoft JhengHei\", \"Microsoft Yahei\", SimHei, \"WenQuanYi Micro Hei\", \"Droid Sans\", Roboto, Helvetica, Tahoma, Arial, sans-serif;\">4. 症状、药物使用咨询<\/span><\/p>","not_buy_description":"未购买","bought_description":"已购买","unit":1,"service_length":1,"price_text":"0.01元/1次"}]
     * last_count : 3
     * remaining_description : 已购买，请尽快开始提问，剩余3次
     */

    private int id;
    private String name;
    private String description;
    private String picture;
    private String icon;
    private String not_buy_description;
    private String bought_description;
    private int type; //0：睡眠日记，1：图文咨询
    private int day_last;
    private int expired_at;
    private int last_count; // 服务剩余数量
    private String remaining_description;
    private List<DoctorServicePackage> packages;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDay_last() {
        return day_last;
    }

    public void setDay_last(int day_last) {
        this.day_last = day_last;
    }

    public int getExpired_at() {
        return expired_at;
    }

    public void setExpired_at(int expired_at) {
        this.expired_at = expired_at;
    }

    public int getLast_count() {
        return last_count;
    }

    public void setLast_count(int last_count) {
        this.last_count = last_count;
    }

    public String getRemaining_description() {
        return remaining_description;
    }

    public void setRemaining_description(String remaining_description) {
        this.remaining_description = remaining_description;
    }

    public List<DoctorServicePackage> getPackages() {
        return packages;
    }

    public void setPackages(List<DoctorServicePackage> packages) {
        this.packages = packages;
    }
}
