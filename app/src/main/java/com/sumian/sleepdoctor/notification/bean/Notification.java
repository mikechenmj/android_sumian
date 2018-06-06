package com.sumian.sleepdoctor.notification.bean;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/5 14:02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class Notification {

    /**
     * id : 6a1b932e-fb04-4aee-b1e9-5632ba0e8dca
     * type : App\Notifications\DiaryEvaluated
     * data : {"id":838,"date":1527782400,"tittle":"医生建议更新","title":"医生建议更新","content":"速眠医生医生对您6月1日的日记进行反馈，点击查看。","scheme":"sleepdoctor%3A%2F%2Fdiaries%3Fdate%3D1527782400%26notification_id%3D6a1b932e-fb04-4aee-b1e9-5632ba0e8dca%26user_id%3D2102"}
     * read_at : null
     * created_at : 1528178108
     */
    public static final String TYPE_DIARY_EVALUATION = "App\\Notifications\\DiaryEvaluated";
    public static final String TYPE_SCALE_DISTRIBUTION = "App\\Notifications\\ScaleDistribution";
    public static final String TYPE_ONLINE_REPORT = "App\\Notifications\\OnlineReportGet";

    private String id;
    private String type;    //医生反馈：App\Notifications\DiaryEvaluated，电子报告：App\Notifications\DiaryEvaluated）
    private DataBean data;
    private int read_at;
    private int created_at;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public Object getRead_at() {
        return read_at;
    }

    public void setRead_at(int read_at) {
        this.read_at = read_at;
    }

    public int getCreated_at() {
        return created_at;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    public long getReadAtInMillis() {
        return read_at * 1000L;
    }

    public long getCreateAtInMillis() {
        return created_at * 1000L;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", data=" + data +
                ", read_at=" + read_at +
                ", created_at=" + created_at +
                '}';
    }

    public static class DataBean {
        /**
         * id : 838
         * date : 1527782400
         * tittle : 医生建议更新
         * title : 医生建议更新
         * content : 速眠医生医生对您6月1日的日记进行反馈，点击查看。
         * scheme : sleepdoctor%3A%2F%2Fdiaries%3Fdate%3D1527782400%26notification_id%3D6a1b932e-fb04-4aee-b1e9-5632ba0e8dca%26user_id%3D2102
         */

        private int id;
        private int date;
        private String title;
        private String content;
        private String scheme;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getDate() {
            return date;
        }

        public void setDate(int date) {
            this.date = date;
        }

        public long getDateInMillis() {
            return date * 1000L;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getScheme() {
            return scheme;
        }

        public void setScheme(String scheme) {
            this.scheme = scheme;
        }
    }
}
