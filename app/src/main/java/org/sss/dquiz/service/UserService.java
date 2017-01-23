package org.sss.dquiz.service;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sss.dquiz.Constants.DquizConstants;
import org.sss.dquiz.helper.HelperService;
import org.sss.dquiz.model.Answers;
import org.sss.dquiz.model.Contents;
import org.sss.dquiz.model.Questions;
import org.sss.dquiz.model.Topics;
import org.sss.dquiz.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by iam on 1/18/17.
 */

public class UserService {

    public static final String REGISTER_ACTION = "REGISTER";
    private static SQLiteDatabase sqLiteDatabase = null;

    public static void registerNewUser(SharedPreferences sharedPreferences, SQLiteDatabase sqLiteDatabase){
        UserService.sqLiteDatabase = sqLiteDatabase;
        RequestParams requestParams = new RequestParams();
        User user = HelperService.getUser(sharedPreferences);
        requestParams.put(User.USERID,user.getUserId());
        requestParams.put(User.NAME,user.getName());
        requestParams.put(User.LOGINTYPE,sharedPreferences.getString(User.LOGINTYPE,null));

        JSONObject registerResult = RestServiceCaller.buildRestCall(requestParams,REGISTER_ACTION);
        try {
            if(registerResult != null) {
                if (registerResult.getBoolean("success")) {
                    registerResult.remove("success");
                    Iterator<String> iter = registerResult.keys();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        try {
                            JSONObject insideValue = (JSONObject) registerResult.get(key);
                            Iterator<String> topicIds = insideValue.keys();
                            while (topicIds.hasNext()){
                                String topicId = topicIds.next();
                                JSONObject allData = (JSONObject) insideValue.get(topicId);

                                JSONArray topics = (JSONArray) allData.get("topics");
                                insertTopics(topics);
                                JSONArray contents = (JSONArray) allData.get("contents");
                                insertContent(contents);
                                JSONArray questions = (JSONArray) allData.get("questions");
                                insertQuestion(questions);
                                JSONObject answers = (JSONObject) allData.get("answers");
                                insertAnswers(answers);

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(DquizConstants.ISREGISTER,true);
                                editor.apply();
                                System.out.println("Successfully Registered......");
                            }
                        } catch (JSONException e) {
                            System.out.println("Error Occurred"+e.getMessage());
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void insertTopics(JSONArray contents) throws JSONException {
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

    public static void insertContent(JSONArray contents) throws JSONException {
        for(int i=0;i<contents.length();i++){
            JSONObject contentRow = contents.getJSONObject(i);
            String insertContentQuery = "INSERT INTO "+ Contents.CONTENTS_TABLE + " ("
                    +Contents.ID+", "+Contents.CONTENT_ID+", "
                    +Topics.TOPIC_ID+", "+Contents.SLIDE_NUMBER+", "
                    +Contents.CONTENT_TYPE+", "+Contents.CONTENT_DESCRIPTION+" ) VALUES ("
                    +contentRow.getInt(Contents.ID)+", "+contentRow.getInt(Contents.CONTENT_ID)+", "
                    +contentRow.getInt(Topics.TOPIC_ID)+", "+contentRow.getInt(Contents.SLIDE_NUMBER)+", '"
                    +contentRow.getString(Contents.CONTENT_TYPE)+"', '"+contentRow.getString(Contents.CONTENT_DESCRIPTION)+"' )";
            sqLiteDatabase.execSQL(insertContentQuery);
        }
    }

    public static void insertQuestion(JSONArray questions) throws JSONException {
        for(int i=0;i<questions.length();i++){
            JSONObject questionRow = questions.getJSONObject(i);
            String insertQuestionQuery = "INSERT INTO "+ Questions.QUESTION_TABLE+" ("
                    +Questions.QUESTION_ID+", "+Topics.TOPIC_ID+", "
                    +Questions.QUESTION_VAL+", "+Questions.QUESTION_AUGMENT+", "
                    +Questions.HINT+", "+Questions.DIFFICULTY+" ) VALUES ("
                    +questionRow.getInt(Questions.QUESTION_ID)+", "+questionRow.getInt(Topics.TOPIC_ID)+", '"
                    +questionRow.getString(Questions.QUESTION_VAL)+"', '"+questionRow.getString(Questions.QUESTION_AUGMENT)+"', '"
                    +questionRow.getString(Questions.HINT)+"', '"+Questions.DIFFICULTY+"' )";
            sqLiteDatabase.execSQL(insertQuestionQuery);
        }
    }

    public static void insertAnswers(JSONObject answers) throws JSONException {
        Iterator<String> keys = answers.keys();
        while (keys.hasNext()){
            String key = keys.next();
            JSONArray questionAnswers = answers.getJSONArray(key);
            for (int i=0;i<questionAnswers.length();i++){
                JSONObject questionAnswer = questionAnswers.getJSONObject(i);
                String insertAnswerQuery = "INSERT INTO "+ Answers.ANSWERS_TABLE+" ("
                        +Answers.ANSWER_ID+", "+Questions.QUESTION_ID+", "
                        +Answers.ANSWER_VAL+", "+Answers.IS_CORRECT+") VALUES ("
                        +questionAnswer.getInt(Answers.ANSWER_ID)+", "+questionAnswer.getInt(Questions.QUESTION_ID)+", '"
                        +questionAnswer.getString(Answers.ANSWER_VAL)+"', '"+questionAnswer.getInt(Answers.IS_CORRECT)+"' )";
                sqLiteDatabase.execSQL(insertAnswerQuery);
            }
        }
    }
}
