package org.sss.dquiz.model;

/**
 * Created by iam on 1/17/17.
 */

public class Contents {

    public static final String ID = "id";
    public static final String CONTENT_ID = "content_id";
    public static final String SLIDE_NUMBER = "slide_no";
    public static final String CONTENT_TYPE = "content_type";
    public static final String CONTENT_DESCRIPTION = "content_desc";
    public static final String CONTENTS_TABLE = "contents";


    private int contentId;
    private int slideNo;
    private String contentType;
    private String contentDescription;

    public Contents(int contentId, int slideNo, String contentType, String contentDescription) {
        this.contentId = contentId;
        this.slideNo = slideNo;
        this.contentType = contentType;
        this.contentDescription = contentDescription;
    }


    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public int getSlideNo() {
        return slideNo;
    }

    public void setSlideNo(int slideNo) {
        this.slideNo = slideNo;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentDescription() {
        return contentDescription;
    }

    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }


    public static String getContentsTableQuery(){
        return "CREATE TABLE "+CONTENTS_TABLE+"( " +
                ""+ID+" INTEGER PRIMARY KEY NOT NULL," +
                ""+CONTENT_ID+" INTEGER NOT NULL," +
                ""+Topics.TOPIC_ID+" INTEGER NOT NULL," +
                ""+SLIDE_NUMBER+" TEXT," +
                ""+CONTENT_TYPE+" TEXT," +
                ""+CONTENT_DESCRIPTION+" TEXT,"+
                "FOREIGN KEY("+Topics.TOPIC_ID+") REFERENCES "+Topics.TOPIC_TABLE+"("+Topics.TOPIC_ID+"));";
    }

    public static String getContentsDropQuery(){
        return "DROP TABLE IF EXISTS "+CONTENTS_TABLE;
    }

}
