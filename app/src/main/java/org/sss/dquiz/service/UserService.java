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
                if (registerResult.has("success")) {
                    if (registerResult.getBoolean("success")) {
                        JSONArray topics = (JSONArray) registerResult.get("allTopics");
                        TopicService.insertTopics(topics, sqLiteDatabase);
                        registerResult.remove("success");
                        registerResult.remove("allTopics");

                        JSONObject userStatus = (JSONObject) registerResult.get("userStatus");

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(DquizConstants.ISREGISTER, true);
                        editor.putInt("slideNo",userStatus.getInt("slideNo"));
                        editor.putInt("topicId",userStatus.getInt("topicId"));
                        editor.apply();

                        Iterator<String> iter = registerResult.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            try {
                                JSONObject insideValue = (JSONObject) registerResult.get(key);
                                Iterator<String> topicIds = insideValue.keys();
                                while (topicIds.hasNext()) {
                                    String topicId = topicIds.next();
                                    JSONObject allData = null;
                                    try {
                                        allData = (JSONObject) insideValue.get(topicId);
                                    }catch (Exception ex){
                                        System.out.println("No Data Found.");
                                    }
                                    try {
                                        JSONArray contents = (JSONArray) allData.get("contents");
                                        ContentService.insertContent(contents, sqLiteDatabase);
                                    }catch (Exception ex){
                                        System.out.println("No Contents Data.");
                                    }
                                    try {
                                        JSONArray questions = (JSONArray) allData.get("questions");
                                        QuestionService.insertQuestion(questions, sqLiteDatabase);
                                    }catch (Exception ex){
                                        System.out.println("No Question Data.");
                                    }
                                    try {
                                        JSONObject answers = (JSONObject) allData.get("answers");
                                        AnswerService.insertAnswers(answers, sqLiteDatabase);
                                    }catch (Exception ex){
                                        System.out.println("No Answer Data.");
                                    }
                                }
                            } catch (JSONException e) {
                                System.out.println("Error Occurred" + e.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
