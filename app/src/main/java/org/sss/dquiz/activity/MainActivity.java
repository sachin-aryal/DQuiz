package org.sss.dquiz.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.sss.dquiz.Constants.DquizConstants;
import org.sss.dquiz.R;
import org.sss.dquiz.database.DbObject;
import org.sss.dquiz.helper.HelperService;
import org.sss.dquiz.model.User;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences = null;
    DbObject mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mydb = new DbObject(this);

        sharedPreferences = getSharedPreferences(DquizConstants.MYPREFERENCES, Context.MODE_PRIVATE);
        TextView textView = (TextView) findViewById(R.id.uName);

        User user = HelperService.getUser(sharedPreferences);
        textView.setText("Welcome "+user.getName());

    }
}
