package org.sss.dquiz.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

        boolean retry = true;
        int retryCount = 0;
        ArrayList<Topics> topicList = null;
        while (retry) {
            if (cursor != null) {
                cursor.moveToFirst();
                topicList = new ArrayList<Topics>();
                if (cursor.moveToFirst()) {
                    do {
                        int topicId = cursor.getInt(cursor.getColumnIndex(Topics.TOPIC_ID));
                        String topicVal = cursor.getString(cursor.getColumnIndex(Topics.TOPIC_VAL));
                        String superTopicVal = cursor.getString(cursor.getColumnIndex(Topics.SUPER_TOPIC_VAL));
                        String description = cursor.getString(cursor.getColumnIndex(Topics.DESCRIPTION));
                        Topics topics = new Topics(topicId, topicVal, superTopicVal, description);
                        System.out.println("Topic Added With Id "+topicId);
                        topicList.add(topics);
                        retry = false;
                    } while (cursor.moveToNext());
                }
                cursor.close();
                System.out.println("Retry False.....................");
            }
            if(retry){
                retryCount++;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(retryCount > 5){
                    retry = false;
                }
            }
        }
        return topicList;
    }

    public ArrayList<Topics> getUniqueBySuperVal(SQLiteDatabase sqLiteDatabase){
        String topicQuery = "SELECT distinct("+Topics.SUPER_TOPIC_VAL+") FROM "+Topics.TOPIC_TABLE;
        Cursor cursor = sqLiteDatabase.rawQuery(topicQuery, null);

        boolean retry = true;
        int retryCount = 0;
        ArrayList<Topics> topicList = null;
        while (retry) {
            if (cursor != null) {
                cursor.moveToFirst();
                topicList = new ArrayList<Topics>();
                if (cursor.moveToFirst()) {
                    do {
                        String superTopicVal = cursor.getString(cursor.getColumnIndex(Topics.SUPER_TOPIC_VAL));
                        Topics topics = new Topics(0, null, superTopicVal, null);
                        topicList.add(topics);
                        retry = false;
                    } while (cursor.moveToNext());
                }
                cursor.close();
                System.out.println("Retry False.....................");
            }
            if(retry){
                retryCount++;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(retryCount > 5){
                    retry = false;
                }
            }
        }
        return topicList;
    }
}
