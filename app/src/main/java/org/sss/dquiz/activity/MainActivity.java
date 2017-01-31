package org.sss.dquiz.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.sss.dquiz.Constants.DquizConstants;
import org.sss.dquiz.R;
import org.sss.dquiz.adapter.SuperTopicsAdapter;
import org.sss.dquiz.database.DbObject;
import org.sss.dquiz.helper.HelperService;
import org.sss.dquiz.model.Topics;
import org.sss.dquiz.service.TopicService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences = null;
    DbObject mydb;
    TopicService topicService = null;
    private static Context mainContext = null;
    ProgressBar progressBar = null;
    RelativeLayout mainLayout = null;
    ListView listView = null;
    SQLiteDatabase dbObject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mydb = new DbObject(this);

        mainLayout = (RelativeLayout) findViewById(R.id.activity_main);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);

        RelativeLayout.LayoutParams progressParams = new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.setLayoutParams(progressParams);
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.addView(progressBar);

        sharedPreferences = getSharedPreferences(DquizConstants.MYPREFERENCES, Context.MODE_PRIVATE);
        mainContext = this;
        dbObject = mydb.getWritableDatabase();

        topicService = new TopicService();
        listView = (ListView) findViewById(R.id.topicList);
        fetchData();

    }


    public void fetchData(){
        final int[] retryCount = {0};
        final boolean[] retry = {true};

        Thread customThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(retry[0]) {
                    ArrayList topicList = topicService.getUniqueBySuperVal(dbObject);
                    if (topicList.size() != 0) {
                        final SuperTopicsAdapter superTopicsAdapter = new SuperTopicsAdapter(topicList, getApplicationContext());
                        runOnUiThread("showData",superTopicsAdapter);
                        runOnUiThread("hideProgress",null);
                        retry[0] = false;
                    } else {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        retryCount[0]++;
                        if(retryCount[0] >= 5){
                            retry[0] = false;
                        }
                    }
                    if(retryCount[0] >= 5){
                        runOnUiThread("hideProgress",null);
                        runOnUiThread("noServerConnection",null);
                    }
                }
            }
        });
        customThread.start();
    }

    public static void viewDescription(String superTopicVal){
        Intent intent = new Intent(mainContext,TopicsActivity.class);
        intent.putExtra("superTopicVal",superTopicVal);
        mainContext.startActivity(intent);
    }

    public void runOnUiThread(final String actionName, final SuperTopicsAdapter superTopicsAdapter){
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(actionName.equalsIgnoreCase("hideProgress")){
                    mainLayout.removeView(progressBar);
                }else if(actionName.equalsIgnoreCase("noServerConnection")){
                    HelperService.makeAlertBox("No Data Received.", "No Data Received From Server. Restart App After Some Time.", MainActivity.this);
                }else if(actionName.equalsIgnoreCase("showData")){
                    listView.setAdapter(superTopicsAdapter);
                    listView.setDividerHeight(4);
                    mainLayout.setGravity(Gravity.NO_GRAVITY);
                }
            }
        });
    }
}
