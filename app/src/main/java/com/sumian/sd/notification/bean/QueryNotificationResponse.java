package com.sumian.sd.notification.bean;

import java.util.List;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/6 15:29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class QueryNotificationResponse {

    /**
     * data : [{"id":"cce3a3b8-40cd-4003-b8f5-4cbdcf4c8d28","type":"HwApp\\Notifications\\OnlineReportGet","data":{"id":3,"title":"电子报告更新","content":"您的电子报告更新了，点击查看。","report_url":"https://sleep-doctor-imm-dev.oss-cn-shanghai.aliyuncs.com/doctors/online_report/1/fc9c9a81-4aa4-4d4e-afc0-f1da37d5f7c4.pdf","scheme":"sleepdoctor%3A%2F%2Fonline-reports%3Fid%3D3%26url%3Dhttps%3A%2F%2Fsleep-doctor-imm-dev.oss-cn-shanghai.aliyuncs.com%2Fdoctors%2Fonline_report%2F1%2Ffc9c9a81-4aa4-4d4e-afc0-f1da37d5f7c4.pdf%26notification_id%3Dcce3a3b8-40cd-4003-b8f5-4cbdcf4c8d28%26user_id%3D2102"},"read_at":1528187515,"created_at":1528185484},{"id":"9c9e16e4-3239-4fc2-a4ec-2f7420e42c2b","type":"HwApp\\Notifications\\OnlineReportGet","data":{"id":2,"title":"电子报告更新","content":"您的电子报告更新了，点击查看。","report_url":"https://sleep-doctor-imm-dev.oss-cn-shanghai.aliyuncs.com/doctors/online_report/1/31aafa00-f569-4e0f-85b0-34033bc1e3c9.pdf","scheme":"sleepdoctor%3A%2F%2Fonline-reports%3Fid%3D2%26url%3Dhttps%3A%2F%2Fsleep-doctor-imm-dev.oss-cn-shanghai.aliyuncs.com%2Fdoctors%2Fonline_report%2F1%2F31aafa00-f569-4e0f-85b0-34033bc1e3c9.pdf%26notification_id%3D9c9e16e4-3239-4fc2-a4ec-2f7420e42c2b%26user_id%3D2102"},"read_at":1528248367,"created_at":1528185456},{"id":"a8b8410a-8e4d-464c-a76f-18c3a958e6a6","type":"HwApp\\Notifications\\DiaryEvaluated","data":{"id":840,"date":1525449600,"tittle":"医生建议更新","title":"医生建议更新","content":"速眠医生医生对您5月5日的日记进行反馈，点击查看。","scheme":"sleepdoctor%3A%2F%2Fdiaries%3Fdate%3D1525449600%26notification_id%3Da8b8410a-8e4d-464c-a76f-18c3a958e6a6%26user_id%3D2102"},"read_at":1528194969,"created_at":1528184418},{"id":"08c87fa2-13e5-4aad-9cbf-3829d0bd7f9e","type":"HwApp\\Notifications\\ScaleDistribution","data":{"id":234,"title":"医生发送了新的量表","content":"速眠医生医生给您发送了广泛性焦虑量表（GAD-7）量表，点击去测评","scheme":"sleepdoctor%3A%2F%2Fscale-distributions%3Fid%3D234%26notification_id%3D08c87fa2-13e5-4aad-9cbf-3829d0bd7f9e%26user_id%3D2102"},"read_at":1528180526,"created_at":1528178359},{"id":"6a1b932e-fb04-4aee-b1e9-5632ba0e8dca","type":"HwApp\\Notifications\\DiaryEvaluated","data":{"id":838,"date":1527782400,"tittle":"医生建议更新","title":"医生建议更新","content":"速眠医生医生对您6月1日的日记进行反馈，点击查看。","scheme":"sleepdoctor%3A%2F%2Fdiaries%3Fdate%3D1527782400%26notification_id%3D6a1b932e-fb04-4aee-b1e9-5632ba0e8dca%26user_id%3D2102"},"read_at":1528180526,"created_at":1528178108},{"id":"50bd53d2-02d0-4e10-91ae-17a91eedd730","type":"HwApp\\Notifications\\DiaryEvaluated","data":{"id":843,"date":1527955200,"tittle":"医生建议更新","title":"医生建议更新","content":"速眠医生医生对您6月3日的日记进行反馈，点击查看。","scheme":"sleepdoctor%3A%2F%2Fdiaries%3Fdate%3D1527955200%26notification_id%3D50bd53d2-02d0-4e10-91ae-17a91eedd730%26user_id%3D2102"},"read_at":1528180526,"created_at":1528178011},{"id":"2bfceac5-4ad1-4eb1-8edd-75be146639fb","type":"HwApp\\Notifications\\ScaleDistribution","data":{"id":223,"title":"医生发送了新的量表","content":"速眠医生医生给您发送了广泛性焦虑量表（GAD-7）量表，点击去测     评","scheme":"sleepdoctor%3A%2F%2Fscale-distributions%3Fid%3D223%26notification_id%3D2bfceac5-4ad1-4eb1-8edd-75be146639fb%26user_id%3D103"},"read_at":1528180526,"created_at":1527645963}]
     * meta : {"pagination":{"total":7,"count":7,"per_page":"15","current_page":1,"total_pages":1,"links":{"previous":null,"next":null}},"unread_num":0}
     */

    private MetaBean meta;
    private List<Notification> data;

    public MetaBean getMeta() {
        return meta;
    }

    public void setMeta(MetaBean meta) {
        this.meta = meta;
    }

    public List<Notification> getData() {
        return data;
    }

    public void setData(List<Notification> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "QueryNotificationResponse{" +
                "meta=" + meta +
                ", data=" + data +
                '}';
    }

    public static class MetaBean {
        /**
         * pagination : {"total":7,"count":7,"per_page":"15","current_page":1,"total_pages":1,"links":{"previous":null,"next":null}}
         * unread_num : 0
         */

        private PaginationBean pagination;
        private int unread_num;

        public PaginationBean getPagination() {
            return pagination;
        }

        public void setPagination(PaginationBean pagination) {
            this.pagination = pagination;
        }

        public int getUnread_num() {
            return unread_num;
        }

        public void setUnread_num(int unread_num) {
            this.unread_num = unread_num;
        }

        public static class PaginationBean {
            /**
             * total : 7
             * count : 7
             * per_page : 15
             * current_page : 1
             * total_pages : 1
             * links : {"previous":null,"next":null}
             */

            private int total;
            private int count;
            private String per_page;
            private int current_page;
            private int total_pages;
            private LinksBean links;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public String getPer_page() {
                return per_page;
            }

            public void setPer_page(String per_page) {
                this.per_page = per_page;
            }

            public int getCurrent_page() {
                return current_page;
            }

            public void setCurrent_page(int current_page) {
                this.current_page = current_page;
            }

            public int getTotal_pages() {
                return total_pages;
            }

            public void setTotal_pages(int total_pages) {
                this.total_pages = total_pages;
            }

            public LinksBean getLinks() {
                return links;
            }

            public void setLinks(LinksBean links) {
                this.links = links;
            }

            public static class LinksBean {
                /**
                 * previous : null
                 * next : null
                 */

                private Object previous;
                private Object next;

                public Object getPrevious() {
                    return previous;
                }

                public void setPrevious(Object previous) {
                    this.previous = previous;
                }

                public Object getNext() {
                    return next;
                }

                public void setNext(Object next) {
                    this.next = next;
                }
            }
        }
    }
}
