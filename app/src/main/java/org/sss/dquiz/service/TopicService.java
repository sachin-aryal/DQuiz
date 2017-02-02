package org.sss.dquiz.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sss.dquiz.model.Contents;
import org.sss.dquiz.model.Topics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iam on 1/24/17.
 */

public class TopicService {

    public ArrayList<Topics> getTopicList(SQLiteDatabase sqLiteDatabase){
        String topicQuery = "SELECT *FROM "+Topics.TOPIC_TABLE;
        Cursor cursor = sqLiteDatabase.rawQuery(topicQuery, null);

        ArrayList<Topics> topicList = new ArrayList<Topics>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int topicId = cursor.getInt(cursor.getColumnIndex(Topics.TOPIC_ID));
                    String topicVal = cursor.getString(cursor.getColumnIndex(Topics.TOPIC_VAL));
                    String superTopicVal = cursor.getString(cursor.getColumnIndex(Topics.SUPER_TOPIC_VAL));
                    String description = cursor.getString(cursor.getColumnIndex(Topics.DESCRIPTION));
                    Topics topics = new Topics(topicId, topicVal, superTopicVal, description);
                    System.out.println("Topic Added With Id "+topicId);
                    topicList.add(topics);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return topicList;
    }

    public ArrayList<Topics> getUniqueBySuperVal(SQLiteDatabase sqLiteDatabase){
        String topicQuery = "SELECT distinct "+Topics.SUPER_TOPIC_VAL+" FROM "+Topics.TOPIC_TABLE
                +" ORDER BY "+Topics.TOPIC_ID+" ASC";
        System.out.println(topicQuery);
        Cursor cursor = sqLiteDatabase.rawQuery(topicQuery, null);

        ArrayList<Topics> topicList = new ArrayList<Topics>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String superTopicVal = cursor.getString(cursor.getColumnIndex(Topics.SUPER_TOPIC_VAL));
                    Topics topics = new Topics(0, null, superTopicVal, null);
                    topicList.add(topics);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return topicList;
    }


    public static void insertTopics(JSONArray contents,SQLiteDatabase sqLiteDatabase) throws JSONException {
        //System.out.println("contents length ----"+contents.length());
        for (int i = 0; i < contents.length(); i++) {
            JSONObject topicRow = contents.getJSONObject(i);
            String insertTopicQuery = "INSERT INTO "+Topics.TOPIC_TABLE+" ("
                    +Topics.TOPIC_ID+", "+Topics.TOPIC_VAL+", "
                    +Topics.SUPER_TOPIC_VAL+", "+Topics.DESCRIPTION+" ) VALUES ("
                    +topicRow.getInt(Topics.TOPIC_ID)+",'"+topicRow.getString(Topics.TOPIC_VAL)+"', '"
                    +topicRow.getString(Topics.SUPER_TOPIC_VAL)+"', '"+topicRow.getString(Topics.DESCRIPTION)+"' )";
            sqLiteDatabase.execSQL(insertTopicQuery);
        }
    }

    public ArrayList<Topics> getTopicListBySuperTopic(String superTopicVal,SQLiteDatabase sqLiteDatabase){
        ArrayList<Topics> topicList = null;
        String topicBySuperTopic = "SELECT "+Topics.TOPIC_ID+","+Topics.TOPIC_VAL+","+Topics.DESCRIPTION+" FROM "+Topics.TOPIC_TABLE+" WHERE "
                +Topics.SUPER_TOPIC_VAL+"='"+superTopicVal+"'";
        Cursor cursor = sqLiteDatabase.rawQuery(topicBySuperTopic,null);

        if (cursor != null) {
            topicList = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    int topicId = cursor.getInt(cursor.getColumnIndex(Topics.TOPIC_ID));
                    String topicVal = cursor.getString(cursor.getColumnIndex(Topics.TOPIC_VAL));
                    String description = cursor.getString(cursor.getColumnIndex(Topics.DESCRIPTION));
                    Topics topics = new Topics(topicId,topicVal,null,description);
                    topicList.add(topics);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return topicList;
    }
}
