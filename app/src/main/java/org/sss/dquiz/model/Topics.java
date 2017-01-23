package org.sss.dquiz.model;

/**
 * Created by iam on 1/17/17.
 */

public class Topics {

    public static final String TOPIC_ID = "topic_id";
    public static final String TOPIC_VAL = "topic_val";
    public static final String SUPER_TOPIC_VAL = "super_topic_val";
    public static final String DESCRIPTION = "description";
    public static final String TOPIC_TABLE = "topic";

    int topicId;
    String topicVal;
    String superTopicVal;

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public String getTopic_val() {
        return topicVal;
    }

    public void setTopic_val(String topicVal) {
        this.topicVal = topicVal;
    }

    public String getSuperTopicVal() {
        return superTopicVal;
    }

    public void setSuperTopicVal(String superTopicVal) {
        this.superTopicVal = superTopicVal;
    }

    public Topics(int topicId, String topicVal, String superTopicVal) {
        this.topicId = topicId;
        this.topicVal = topicVal;
        this.superTopicVal = superTopicVal;
    }

    public static String getTopicTableQuery(){
        return "CREATE TABLE "+TOPIC_TABLE+"( " +
                ""+TOPIC_ID+" INTEGER PRIMARY KEY NOT NULL," +
                ""+TOPIC_VAL+" TEXT," +
                ""+SUPER_TOPIC_VAL+" TEXT," +
                ""+DESCRIPTION+" TEXT);";
    }

    public static String getTopicDropQuery(){
        return "DROP TABLE IF EXISTS "+TOPIC_TABLE;
    }
}
