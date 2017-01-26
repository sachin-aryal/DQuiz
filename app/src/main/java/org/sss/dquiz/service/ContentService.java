package org.sss.dquiz.service;

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
}
