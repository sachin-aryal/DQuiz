package org.sss.dquiz.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
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
import android.widget.Toast;

import org.sss.dquiz.Constants.DquizConstants;
import org.sss.dquiz.R;
import org.sss.dquiz.adapter.SuperTopicsAdapter;
import org.sss.dquiz.database.DbObject;
import org.sss.dquiz.helper.HelperService;
import org.sss.dquiz.model.Topics;
import org.sss.dquiz.model.User;
import org.sss.dquiz.service.TopicService;

import java.lang.reflect.Field;
import java.util.ArrayList;

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

        navigationDrawerSetUp();
        fetchData();
    }


    public void fetchData(){
        final int[] retryCount = {0};
        final boolean[] retry = {true};

        Thread customThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(retry[0]) {
                    ArrayList<Topics> superTopicList = topicService.getUniqueBySuperVal(dbObject);
                    if (superTopicList.size() != 0) {
                        final SuperTopicsAdapter superTopicsAdapter = new SuperTopicsAdapter(superTopicList, getApplicationContext());
                        runOnUiThread("showData",superTopicsAdapter);
                        runOnUiThread("hideProgress",null);
                        retry[0] = false;
                    } else {
                        try {
                            Thread.sleep(4000);
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

    public static void viewDescription(final String superTopicVal){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(mainContext,TopicsActivity.class);
                intent.putExtra("superTopicVal",superTopicVal);
                mainContext.startActivity(intent);
            }
        }).start();

    }

    public static void viewNotAllowedToast(){
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mainContext, "Please complete previous Topic!", Toast.LENGTH_SHORT).show();
                    }
                },2000
        );
    }

    public void navigationDrawerSetUp(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, layout_drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        layout_drawer.setDrawerListener(toggle);
        layout_drawer.requestLayout();
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
        displayUserProfile();
        HelperService.closeDrag(layout_drawer);
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
    @Override
    public void onBackPressed() {
        if (layout_drawer.isDrawerOpen(GravityCompat.START)) {
            layout_drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        System.out.println("Item Title:"+item.getTitle());
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
