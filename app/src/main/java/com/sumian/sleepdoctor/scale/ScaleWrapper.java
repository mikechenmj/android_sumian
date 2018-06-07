package com.sumian.sleepdoctor.scale;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/7 9:44
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ScaleWrapper {
    /**
     * id : 339
     * doctor_id : 0
     * scale_result_id : 0
     * created_at : 1528265455
     * scale : {"id":2,"doctor_id":0,"score_type":1,"title":"Epworth嗜睡量表","description":"该量表是由澳大利亚墨尔本的Epworth医院设计的。临床应用结果表明，ESS是一种十分简便的患者自我评估白天嗜睡程度的问卷表，请根据最近几个月的一般状况进行选择。","final_words":"您已完成量表的填写"}
     * result : null
     * doctor : null
     */

    private int id;
    private int doctor_id;
    private int scale_result_id;
    private int created_at;
    private ScaleWrapper.Scale scale;
    private Object result;
    private Object doctor;

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

    public ScaleWrapper.Scale getScale() {
        return scale;
    }

    public void setScale(ScaleWrapper.Scale scale) {
        this.scale = scale;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Object getDoctor() {
        return doctor;
    }

    public void setDoctor(Object doctor) {
        this.doctor = doctor;
    }

    @Override
    public String toString() {
        return "ScaleWrapper{" +
                "id=" + id +
                ", doctor_id=" + doctor_id +
                ", scale_result_id=" + scale_result_id +
                ", created_at=" + created_at +
                ", scale=" + scale +
                ", result=" + result +
                ", doctor=" + doctor +
                '}';
    }

    public static class Scale {
        /**
         * id : 2
         * doctor_id : 0
         * score_type : 1
         * title : Epworth嗜睡量表
         * description : 该量表是由澳大利亚墨尔本的Epworth医院设计的。临床应用结果表明，ESS是一种十分简便的患者自我评估白天嗜睡程度的问卷表，请根据最近几个月的一般状况进行选择。
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

        @Override
        public String toString() {
            return "Scale{" +
                    "id=" + id +
                    ", doctor_id=" + doctor_id +
                    ", score_type=" + score_type +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", final_words='" + final_words + '\'' +
                    '}';
        }
    }
}
