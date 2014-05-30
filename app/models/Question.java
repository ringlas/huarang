package models;

import play.db.ebean.Model;

import javax.persistence.*;

/**
 * Created by victor on 14-5-30.
 */
@Entity
public class Question extends Model {

    @Id
    private int id;

    @Column(name="question", length = 255)
    private String question;

    @Column(name="answer_a", length = 255)
    private String answer_a;

    @Column(name="answer_b", length = 255)
    private String answer_b;

    @Column(name="answer_c", length = 255)
    private String answer_c;

    @Column(name="answer_d", length = 255)
    private String answer_d;

    @Column(name="correct_answer")
    private int correct_answer;

    @ManyToOne(cascade= CascadeType.ALL)
    private Test test;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer_a() {
        return answer_a;
    }

    public void setAnswer_a(String answer_a) {
        this.answer_a = answer_a;
    }

    public String getAnswer_b() {
        return answer_b;
    }

    public void setAnswer_b(String answer_b) {
        this.answer_b = answer_b;
    }

    public String getAnswer_c() {
        return answer_c;
    }

    public void setAnswer_c(String answer_c) {
        this.answer_c = answer_c;
    }

    public String getAnswer_d() {
        return answer_d;
    }

    public void setAnswer_d(String answer_d) {
        this.answer_d = answer_d;
    }

    public int getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(int correct_answer) {
        this.correct_answer = correct_answer;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public static Finder<Integer,Question> find = new Finder<Integer,Question>(
            Integer.class, Question.class
    );
}
