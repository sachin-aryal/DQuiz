package org.sss.dquiz.activity;

import android.app.ProgressDialog;
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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.sss.dquiz.Constants.DquizConstants;
import org.sss.dquiz.R;
import org.sss.dquiz.adapter.SuperTopicsAdapter;
import org.sss.dquiz.database.DbObject;
import org.sss.dquiz.helper.HelperService;
import org.sss.dquiz.model.Topics;
import org.sss.dquiz.model.User;
import org.sss.dquiz.service.TopicService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    SharedPreferences sharedPreferences = null;
    DbObject mydb;
    TopicService topicService = null;
    private static Context mainContext = null;
    ProgressBar progressBar = null;
    RelativeLayout mainLayout = null;
    ListView listView = null;
    SQLiteDatabase dbObject = null;
    NavigationView navigationView = null;
    DrawerLayout layout_drawer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        layout_drawer = (DrawerLayout) findViewById(R.id.layout_drawer);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);

        RelativeLayout.LayoutParams progressParams = new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.setLayoutParams(progressParams);
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.addView(progressBar);

        sharedPreferences = getSharedPreferences(DquizConstants.MYPREFERENCES, Context.MODE_PRIVATE);
        mainContext = this;
        mydb = new DbObject(this);
        dbObject = mydb.getWritableDatabase();
        topicService = new TopicService();
        listView = (ListView) findViewById(R.id.topicList);
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                navigationDrawerSetUp();
                displayUserProfile();
                fetchData();
            }
        });

    }


    public void fetchData(){
        final int[] retryCount = {0};
        final boolean[] retry = {true};

        Thread customThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(retry[0]) {
                    ArrayList<Topics> topicList = topicService.getUniqueBySuperVal(dbObject);
                    if (topicList.size() != 0) {
                        final SuperTopicsAdapter superTopicsAdapter = new SuperTopicsAdapter(topicList, getApplicationContext());
                        runOnUiThread("showData",superTopicsAdapter,topicList);
                        runOnUiThread("hideProgress",null,null);
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
                        runOnUiThread("hideProgress",null,null);
                        runOnUiThread("noServerConnection",null,null);
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

    public void navigationDrawerSetUp(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.layout_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void runOnUiThread(final String actionName, final SuperTopicsAdapter superTopicsAdapter,final List<Topics> topicList){
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

                    final Menu menu = navigationView.getMenu();
                    for(Topics topic:topicList) {
                        menu.add("").setTitle(topic.getSuperTopicVal()).setEnabled(true);
                    }
                }
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        System.out.println(id);
        System.out.println(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.layout_drawer);
        drawer.closeDrawer(GravityCompat.START);
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
