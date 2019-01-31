package com.sumian.sd.buz.account.bean;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/1 15:40
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class Answers {

    /**
     * id : 49
     * answers : 23:00,0,07:00,07:00,0,0,0
     * score : 1
     * level : 0
     * created_at : 1525651696
     */

    public int id;
    public String answers;
    public int score;
    public int level;
    public int created_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCreated_at() {
        return created_at;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Answers{" +
                "id=" + id +
                ", answers='" + answers + '\'' +
                ", score=" + score +
                ", level=" + level +
                ", created_at=" + created_at +
                '}';
    }
}
