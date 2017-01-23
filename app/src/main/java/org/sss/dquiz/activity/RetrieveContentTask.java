package org.sss.dquiz.activity;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.sss.dquiz.service.UserService;

/**
 * Created by iam on 1/22/17.
 */

public class RetrieveContentTask extends AsyncTask<String, Void, String> {
    private SharedPreferences sharedPreferences;
    private String actionType;
    private SQLiteDatabase sqLiteDatabase;

    public RetrieveContentTask(SharedPreferences sharedPreferences, String actionType, SQLiteDatabase sqLiteDatabase){
        this.sharedPreferences = sharedPreferences;
        this.actionType = actionType;
        this.sqLiteDatabase = sqLiteDatabase;
    }

    @Override
    protected String doInBackground(String... strings) {
        if(actionType.equals(UserService.REGISTER_ACTION)){
            UserService.registerNewUser(sharedPreferences,sqLiteDatabase);
        }
        return "";
    }
}
