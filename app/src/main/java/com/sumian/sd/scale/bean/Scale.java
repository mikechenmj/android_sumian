package com.sumian.sd.scale.bean;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/7 9:58
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class Scale {

    /**
     * id : 223
     * doctor_id : 1
     * scale_result_id : 27
     * created_at : 1527645962
     * scale : {"id":1,"doctor_id":0,"score_type":1,"title":"广泛性焦虑量表（GAD-7）","description":"在过去两个星期，有多少时候您会受到如下几个问题的困扰？","final_words":"您已完成量表的填写"}
     * result : {"id":27,"score":0,"result":"正常","comment":"您的情况正常。","created_at":1527645977,"updated_at":1527645977}
     * doctor : {"id":1,"name":"速眠医生"}
     */

    private int id;
    private int doctor_id;
    private int scale_result_id;
    private int created_at;
    private ScaleDetail scale;
    private ResultBean result;
    private DoctorBean doctor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }

    public int getScale_result_id() {
        return scale_result_id;
    }

    public void setScale_result_id(int scale_result_id) {
        this.scale_result_id = scale_result_id;
    }

    public int getCreated_at() {
        return created_at;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    public ScaleDetail getScale() {
        return scale;
    }

    public void setScale(ScaleDetail scale) {
        this.scale = scale;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public DoctorBean getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorBean doctor) {
        this.doctor = doctor;
    }

    public static class ScaleDetail {
        /**
         * id : 1
         * doctor_id : 0
         * score_type : 1
         * title : 广泛性焦虑量表（GAD-7）
         * description : 在过去两个星期，有多少时候您会受到如下几个问题的困扰？
         * final_words : 您已完成量表的填写
         */

        private int id;
        private int doctor_id;
        private int score_type;
        private String title;
        private String description;
        private String final_words;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getDoctor_id() {
            return doctor_id;
        }

        public void setDoctor_id(int doctor_id) {
            this.doctor_id = doctor_id;
        }

        public int getScore_type() {
            return score_type;
        }

        public void setScore_type(int score_type) {
            this.score_type = score_type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getFinal_words() {
            return final_words;
        }

        public void setFinal_words(String final_words) {
            this.final_words = final_words;
        }
    }

    public static class ResultBean {
        /**
         * id : 27
         * score : 0
         * result : 正常
         * comment : 您的情况正常。
         * created_at : 1527645977
         * updated_at : 1527645977
         */

        private int id;
        private int score;
        private String result;
        private String comment;
        private int created_at;
        private int updated_at;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public int getCreated_at() {
            return created_at;
        }

        public void setCreated_at(int created_at) {
            this.created_at = created_at;
        }

        public long getCreateAtInMillis() {
            return created_at * 1000L;
        }

        public int getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(int updated_at) {
            this.updated_at = updated_at;
        }
    }

    public static class DoctorBean {
        /**
         * id : 1
         * name : 速眠医生
         */

        private int id;
        private String name;

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
    }
}
