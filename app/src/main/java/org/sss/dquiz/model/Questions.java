package org.sss.dquiz.model;

/**
 * Created by iam on 1/17/17.
 */

public class Questions{

    public static final String QUESTION_ID = "question_id";
    public static final String QUESTION_VAL = "question_val";
    public static final String QUESTION_AUGMENT = "question_augment";
    public static final String HINT = "hint";
    public static final String DIFFICULTY = "difficulty";
    public static final String QUESTION_TABLE = "questions";

    int questionId;
    String questionVal;
    String questionAugument;
    String hint;
    String difficulty;

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionVal() {
        return questionVal;
    }

    public void setQuestionVal(String questionVal) {
        this.questionVal = questionVal;
    }

    public String getQuestionArgument() {
        return questionAugument;
    }

    public void setQuestionArgument(String questionAugument) {
        this.questionAugument = questionAugument;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Questions(int questionId, String questionVal, String questionAugument, String hint, String difficulty) {
        this.questionId = questionId;
        this.questionVal = questionVal;
        this.questionAugument = questionAugument;
        this.hint = hint;
        this.difficulty = difficulty;
    }

    public static String getQuestionTableQuery(){
        return "CREATE TABLE "+QUESTION_TABLE+"( " +
                ""+QUESTION_ID+" INTEGER PRIMARY KEY NOT NULL," +
                ""+Topics.TOPIC_ID+" INTEGER NOT NULL," +
                ""+QUESTION_VAL+" TEXT," +
                ""+QUESTION_AUGMENT+" TEXT," +
                ""+HINT+" TEXT," +
                ""+DIFFICULTY+" TEXT,"+
                "FOREIGN KEY ("+Topics.TOPIC_ID+") REFERENCES "+Topics.TOPIC_TABLE+"("+Topics.TOPIC_ID+"));";
    }

    public static String getQuestionsDropQuery(){
        return "DROP TABLE IF EXISTS "+QUESTION_TABLE;
    }
}
