package org.sss.dquiz.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.sss.dquiz.R;
import org.sss.dquiz.database.DbObject;
import org.sss.dquiz.helper.OnSwipeTouchListener;
import org.sss.dquiz.model.Contents;
import org.sss.dquiz.service.ContentService;

public class ContentActivity extends AppCompatActivity {

    ContentService contentService = null;
    SQLiteDatabase sqLiteDatabase = null;
    String topicTitle = "";
    int topicId = 0,initialSlideNumber = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        System.out.println("Initiating Contents Activity.");
        Intent intent = getIntent();
        topicId = intent.getIntExtra("topicId",0);
        topicTitle = intent.getStringExtra("topicVal");
        sqLiteDatabase = new DbObject(this.getApplicationContext()).getWritableDatabase();
        contentService = new ContentService();
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_content);
        relativeLayout.setOnTouchListener(new OnSwipeTouchListener(ContentActivity.this){
            public void onSwipeRight() {
                initialSlideNumber--;
                if(initialSlideNumber > 0) {
                    fetchContentsOnUiThread();
                }
            }
            public void onSwipeLeft() {
                initialSlideNumber++;
                fetchContentsOnUiThread();
            }
        });
        fetchContentsOnUiThread();
    }

    public void fetchContentsOnUiThread(){
        ContentActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Contents contents = contentService.getContents(topicId, initialSlideNumber, sqLiteDatabase);
                TextView topicVal = (TextView) findViewById(R.id.topicVal);
                TextView topicDescription = (TextView) findViewById(R.id.topicDescription);
                if (contents != null) {
                    topicVal.setText(topicTitle);
                    topicDescription.setText(contents.getContentDescription());
                }
            }
        });
    }
}
