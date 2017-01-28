package org.sss.dquiz.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sss.dquiz.model.Questions;
import org.sss.dquiz.model.Topics;

/**
 * Created by iam on 1/26/17.
 */

public class QuestionService {

    public static void insertQuestion(JSONArray questions, SQLiteDatabase sqLiteDatabase) throws JSONException {
        for(int i=0;i<questions.length();i++){
            JSONObject questionRow = questions.getJSONObject(i);
            String insertQuestionQuery = "INSERT INTO "+ Questions.QUESTION_TABLE+" ("
                    +Questions.QUESTION_ID+", "+ Topics.TOPIC_ID+", "
                    +Questions.QUESTION_VAL+", "+Questions.QUESTION_AUGMENT+", "
                    +Questions.HINT+", "+Questions.DIFFICULTY+" ) VALUES ("
                    +questionRow.getInt(Questions.QUESTION_ID)+", "+questionRow.getInt(Topics.TOPIC_ID)+", '"
                    +questionRow.getString(Questions.QUESTION_VAL)+"', '"+questionRow.getString(Questions.QUESTION_AUGMENT)+"', '"
                    +questionRow.getString(Questions.HINT)+"', '"+Questions.DIFFICULTY+"' )";
            sqLiteDatabase.execSQL(insertQuestionQuery);
        }
    }

    public Questions getQuestion(int questionId,SQLiteDatabase sqLiteDatabase){
        String questionQuery = "SELECT *FROM "+Questions.QUESTION_TABLE+" WHERE "+Questions.QUESTION_ID+"="+questionId;
        Cursor cursor = sqLiteDatabase.rawQuery(questionQuery,null);
        Questions questions = null;

        if(cursor != null){
            if(cursor.moveToFirst()){
                do {
                    String  questionVal = cursor.getString(cursor.getColumnIndex(Questions.QUESTION_VAL));
                    String  questionAugment = cursor.getString(cursor.getColumnIndex(Questions.QUESTION_AUGMENT));
                    String  hint = cursor.getString(cursor.getColumnIndex(Questions.HINT));
                    String  difficulty = cursor.getString(cursor.getColumnIndex(Questions.DIFFICULTY));
                    questions = new Questions(questionId,questionVal,questionAugment,hint,difficulty);
                }while (cursor.moveToNext());
            }
        }

        return questions;
    }
}
