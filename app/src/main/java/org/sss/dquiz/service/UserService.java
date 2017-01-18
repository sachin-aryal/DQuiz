package org.sss.dquiz.service;

import android.content.SharedPreferences;

import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.sss.dquiz.Constants.DquizConstants;
import org.sss.dquiz.helper.HelperService;
import org.sss.dquiz.model.User;

/**
 * Created by iam on 1/18/17.
 */

public class UserService {

    public static final String REGISTER_ACTION = "register";

    public static void registerNewUser(SharedPreferences sharedPreferences){
        RequestParams requestParams = new RequestParams();
        User user = HelperService.getUser(sharedPreferences);
        requestParams.put(User.USERID,user.getUserId());
        requestParams.put(User.NAME,user.getName());
        requestParams.put(User.LOGINTYPE,sharedPreferences.getString(User.LOGINTYPE,null));

        try {
            JSONObject registerResult = RestServiceCaller.buildRestCall(requestParams,REGISTER_ACTION);
            System.out.println("Register Result......................."+registerResult);
            if(registerResult.getBoolean("success")){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(DquizConstants.ISREGISTER,true);
                editor.apply();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
