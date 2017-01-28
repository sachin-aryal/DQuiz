package org.sss.dquiz.activity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import org.sss.dquiz.R;
import org.sss.dquiz.adapter.TopicsAdapter;
import org.sss.dquiz.database.DbObject;
import org.sss.dquiz.model.Topics;
import org.sss.dquiz.service.TopicService;

import java.util.ArrayList;

public class TopicsActivity extends AppCompatActivity {

    TopicService topicService = null;
    SQLiteDatabase sqLiteDatabase = null;
    private static Context mainContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);

        mainContext = this;
        topicService = new TopicService();
        sqLiteDatabase = new DbObject(this.getApplicationContext()).getWritableDatabase();
        Intent intent = getIntent();
        final String superTopicVal = intent.getStringExtra("superTopicVal");
        TopicsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Topics> topicList = topicService.getTopicListBySuperTopic(superTopicVal,sqLiteDatabase);
                TopicsAdapter topicsAdapter = new TopicsAdapter(topicList,getApplicationContext());
                ListView topicInfoList = (ListView) findViewById(R.id.topicInfoList);
                topicInfoList.setAdapter(topicsAdapter);
                topicInfoList.setDividerHeight(4);
            }
        });
    }

    public static void viewContent(int topicId,String topicVal){
        Intent intent = new Intent(mainContext,ContentActivity.class);
        intent.putExtra("topicId",topicId);
        intent.putExtra("topicVal",topicVal);
        mainContext.startActivity(intent);
    }
}
