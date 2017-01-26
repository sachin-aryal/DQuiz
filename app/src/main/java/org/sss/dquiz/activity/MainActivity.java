package org.sss.dquiz.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.sss.dquiz.Constants.DquizConstants;
import org.sss.dquiz.R;
import org.sss.dquiz.adapter.TopicAdapter;
import org.sss.dquiz.database.DbObject;
import org.sss.dquiz.helper.HelperService;
import org.sss.dquiz.model.User;
import org.sss.dquiz.service.TopicService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences = null;
    DbObject mydb;
    TopicService topicService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mydb = new DbObject(this);
        //TODO: Add Progress Bar
        sharedPreferences = getSharedPreferences(DquizConstants.MYPREFERENCES, Context.MODE_PRIVATE);

        final SQLiteDatabase dbObject = new DbObject(this.getApplicationContext()).getWritableDatabase();
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                topicService = new TopicService();
                ListView listView = (ListView) findViewById(R.id.topicList);
                ArrayList topicList = topicService.getUniqueBySuperVal(dbObject);
                //TODO: Hide Progress Bar
                if(topicList.size() != 0) {
                    TopicAdapter topicAdapter = new TopicAdapter(topicList, getApplicationContext());
                    listView.setAdapter(topicAdapter);
                    listView.setDividerHeight(4);
                }else{
                    HelperService.makeAlertBox("No Data Received.","No Data Received From Server. Reconnecting....",MainActivity.this);
                    Intent intent = new Intent(MainActivity.this,SplashActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
