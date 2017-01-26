package org.sss.dquiz.service;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sss.dquiz.model.Answers;
import org.sss.dquiz.model.Questions;

import java.util.Iterator;

/**
 * Created by iam on 1/26/17.
 */

public class AnswerService {
    public static void insertAnswers(JSONObject answers, SQLiteDatabase sqLiteDatabase) throws JSONException {
        Iterator<String> keys = answers.keys();
        while (keys.hasNext()){
            String key = keys.next();
            JSONArray questionAnswers = answers.getJSONArray(key);
            for (int i=0;i<questionAnswers.length();i++){
                JSONObject questionAnswer = questionAnswers.getJSONObject(i);
                String insertAnswerQuery = "INSERT INTO "+ Answers.ANSWERS_TABLE+" ("
                        +Answers.ANSWER_ID+", "+ Questions.QUESTION_ID+", "
                        +Answers.ANSWER_VAL+", "+Answers.IS_CORRECT+") VALUES ("
                        +questionAnswer.getInt(Answers.ANSWER_ID)+", "+questionAnswer.getInt(Questions.QUESTION_ID)+", '"
                        +questionAnswer.getString(Answers.ANSWER_VAL)+"', '"+questionAnswer.getInt(Answers.IS_CORRECT)+"' )";
                sqLiteDatabase.execSQL(insertAnswerQuery);
            }
        }
    }
}
