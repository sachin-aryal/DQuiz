package org.sss.dquiz.model;

/**
 * Created by iam on 1/17/17.
 */

public class Answers {

    public static final String ANSWER_ID = "answer_id";
    public static final String ANSWER_VAL = "answer_val";
    public static final String IS_CORRECT = "is_correct";
    public static final String ANSWERS_TABLE = "answers";

    int answerId;
    String answerVal;
    boolean isCorrect;

    public Answers(int answerId, String answerVal, int isCorrect) {
        this.answerId = answerId;
        this.answerVal = answerVal;
        this.isCorrect = (isCorrect == 1);
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public String getAnswerVal() {
        return answerVal;
    }

    public void setAnswerVal(String answerVal) {
        this.answerVal = answerVal;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(int correct) {
        isCorrect = (correct == 1);
    }


    public static String getAnswersTableQuery(){
        return "CREATE TABLE "+ANSWERS_TABLE+"( " +
                ""+ANSWER_ID+" INTEGER PRIMARY KEY NOT NULL," +
                ""+Questions.QUESTION_ID+" INTEGER NOT NULL," +
                ""+ANSWER_VAL+" TEXT," +
                ""+IS_CORRECT+" INTEGER NOT NULL," +
                "FOREIGN KEY("+Questions.QUESTION_ID+") REFERENCES "+Questions.QUESTION_TABLE+"("+Questions.QUESTION_ID+"));";
    }

    public static String getAnswersDropQuery(){
        return "DROP TABLE IF EXISTS "+ANSWERS_TABLE;
    }

}
