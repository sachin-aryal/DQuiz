package org.sss.dquiz.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sss.dquiz.model.Contents;
import org.sss.dquiz.model.Topics;

/**
 * Created by iam on 1/26/17.
 */

public class ContentService {
    public static void insertContent(JSONArray contents, SQLiteDatabase sqLiteDatabase) throws JSONException {
        for(int i=0;i<contents.length();i++){
            JSONObject contentRow = contents.getJSONObject(i);
            String insertContentQuery = "INSERT INTO "+ Contents.CONTENTS_TABLE + " ("
                    +Contents.ID+", "+Contents.CONTENT_ID+", "
                    + Topics.TOPIC_ID+", "+Contents.SLIDE_NUMBER+", "
                    +Contents.CONTENT_TYPE+", "+Contents.CONTENT_DESCRIPTION+" ) VALUES ("
                    +contentRow.getInt(Contents.ID)+", "+contentRow.getInt(Contents.CONTENT_ID)+", "
                    +contentRow.getInt(Topics.TOPIC_ID)+", "+contentRow.getInt(Contents.SLIDE_NUMBER)+", '"
                    +contentRow.getString(Contents.CONTENT_TYPE)+"', '"+contentRow.getString(Contents.CONTENT_DESCRIPTION)+"' )";
            sqLiteDatabase.execSQL(insertContentQuery);
        }
    }

    public Contents getContents(int topicId,int slideNumber,SQLiteDatabase sqLiteDatabase){
        Contents contents = null;

        String contentQuery = "SELECT *FROM "+Contents.CONTENTS_TABLE+" WHERE "+Topics.TOPIC_ID+"="+topicId+" AND "
                +Contents.SLIDE_NUMBER+"="+slideNumber;

        Cursor cursor = sqLiteDatabase.rawQuery(contentQuery,null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String contentType = cursor.getString(cursor.getColumnIndex(Contents.CONTENT_TYPE));
                    String contentDescription = cursor.getString(cursor.getColumnIndex(Contents.CONTENT_DESCRIPTION));
                    contents = new Contents(0,slideNumber,contentType,contentDescription);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return contents;
    }

    public int getMaxSlideNumber(int topicId,SQLiteDatabase sqLiteDatabase){
        String maxSlideQuery = "SELECT MAX("+Contents.SLIDE_NUMBER+") FROM "+Contents.CONTENTS_TABLE+" WHERE "+
                Topics.TOPIC_ID+" = "+topicId;
        int maxSlideNo = 0;
        Cursor cursor = sqLiteDatabase.rawQuery(maxSlideQuery,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    maxSlideNo = cursor.getInt(cursor.getColumnIndex(Contents.SLIDE_NUMBER));
                    return maxSlideNo;
                }while (cursor.moveToNext());
            }
        }
        return maxSlideNo;
    }
}
