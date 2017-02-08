package org.sss.dquiz.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.sss.dquiz.Constants.DquizConstants;
import org.sss.dquiz.R;
import org.sss.dquiz.adapter.TopicsAdapter;
import org.sss.dquiz.database.DbObject;
import org.sss.dquiz.model.Topics;
import org.sss.dquiz.model.User;
import org.sss.dquiz.service.TopicService;

import java.util.ArrayList;

public class TopicsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    TopicService topicService = null;
    SQLiteDatabase dbObject = null;
    private static Context mainContext = null;
    NavigationView navigationView = null;
    DrawerLayout layout_drawer = null;
    SharedPreferences sharedPreferences = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);

        mainContext = this;
        topicService = new TopicService();
        dbObject = new DbObject(this.getApplicationContext()).getWritableDatabase();
        layout_drawer = (DrawerLayout) findViewById(R.id.layout_drawer);
        sharedPreferences = getSharedPreferences(DquizConstants.MYPREFERENCES, Context.MODE_PRIVATE);

        Intent intent = getIntent();
        String superTopicVal = intent.getStringExtra("superTopicVal");
        showTopicList(superTopicVal);
        navigationDrawerSetUp();
    }

    public void showTopicList(final String superTopicVal){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Topics> topicList = topicService.getTopicListBySuperTopic(superTopicVal,dbObject);
                TopicsAdapter topicsAdapter = new TopicsAdapter(topicList,getApplicationContext());
                showTopicList(topicsAdapter);
            }
        }).start();
    }

    public void showTopicList(final TopicsAdapter topicsAdapter){
        TopicsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView topicInfoList = (ListView) findViewById(R.id.topicInfoList);
                topicInfoList.setAdapter(topicsAdapter);
                topicInfoList.setDividerHeight(4);
            }
        });
    }

    public static void viewContent(final int topicId, final String topicVal){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(mainContext,ContentActivity.class);
                intent.putExtra("topicId",topicId);
                intent.putExtra("topicVal",topicVal);
                mainContext.startActivity(intent);
            }
        }).start();
    }

    public void navigationDrawerSetUp(){
        TopicsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                layout_drawer = (DrawerLayout) findViewById(R.id.layout_drawer);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        TopicsActivity.this, layout_drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                layout_drawer.setDrawerListener(toggle);
                layout_drawer.requestLayout();
                toggle.syncState();

                navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(TopicsActivity.this);
                navigationView.bringToFront();

                ArrayList<Topics> superTopicList = topicService.getUniqueBySuperVal(dbObject);
                if(superTopicList.size() >0) {
                    final Menu menu = navigationView.getMenu();
                    for (Topics topic : superTopicList) {
                        menu.add(topic.getSuperTopicVal()).setTitle(topic.getSuperTopicVal()).setEnabled(true);
                    }
                }
                displayUserProfile();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.layout_drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            System.out.println("Drawer End");
            drawer.closeDrawer(GravityCompat.START);
        } else {
            System.out.println("Drawer End.");
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.layout_drawer);
        drawer.closeDrawer(GravityCompat.START);
        showTopicList(item.getTitle()+"");
        return true;
    }

    public void displayUserProfile(){

        View headerLayout = navigationView.getHeaderView(0);

        TextView userName = (TextView) headerLayout.findViewById(R.id.userName);
        userName.setText(sharedPreferences.getString(User.NAME,""));
        TextView email = (TextView)headerLayout.findViewById(R.id.userEmail);
        email.setText(sharedPreferences.getString(User.EMAIL,""));
    }
}
