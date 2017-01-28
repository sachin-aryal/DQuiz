package org.sss.dquiz.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.sss.dquiz.Constants.DquizConstants;
import org.sss.dquiz.R;
import org.sss.dquiz.adapter.SuperTopicsAdapter;
import org.sss.dquiz.database.DbObject;
import org.sss.dquiz.helper.HelperService;
import org.sss.dquiz.service.TopicService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences = null;
    DbObject mydb;
    TopicService topicService = null;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mydb = new DbObject(this);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.VISIBLE);
        sharedPreferences = getSharedPreferences(DquizConstants.MYPREFERENCES, Context.MODE_PRIVATE);

        final SQLiteDatabase dbObject = new DbObject(this.getApplicationContext()).getWritableDatabase();
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                topicService = new TopicService();
                ListView listView = (ListView) findViewById(R.id.topicList);
                ArrayList topicList = topicService.getUniqueBySuperVal(dbObject);
                progress.setVisibility(View.INVISIBLE);                                                                                                                                                                                                                                                                         
                // TODO: Deactivate all the topic that are not unlocked.
                if(topicList.size() != 0) {
                    final SuperTopicsAdapter superTopicsAdapter = new SuperTopicsAdapter(topicList, getApplicationContext());
                    listView.setAdapter(superTopicsAdapter);
                    listView.setDividerHeight(4);
                }else{
                    HelperService.makeAlertBox("No Data Received.","No Data Received From Server. Reconnecting....",MainActivity.this);
                    Intent intent = new Intent(MainActivity.this,SplashActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void viewDescription(String superTopicVal){
        Intent intent = new Intent(MainActivity.this,TopicsActivity.class);
        intent.putExtra("superTopicVal",superTopicVal);
        startActivity(intent);
    }
}
