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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.sss.dquiz.Constants.DquizConstants;
import org.sss.dquiz.R;
import org.sss.dquiz.database.DbObject;
import org.sss.dquiz.helper.HelperService;
import org.sss.dquiz.helper.OnSwipeTouchListener;
import org.sss.dquiz.model.Answers;
import org.sss.dquiz.model.Contents;
import org.sss.dquiz.model.Questions;
import org.sss.dquiz.model.Topics;
import org.sss.dquiz.model.User;
import org.sss.dquiz.service.AnswerService;
import org.sss.dquiz.service.ContentService;
import org.sss.dquiz.service.QuestionService;
import org.sss.dquiz.service.TopicService;

import java.util.ArrayList;
import java.util.List;

public class ContentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ContentService contentService = null;
    QuestionService questionService = null;
    AnswerService answerService = null;
    SQLiteDatabase dbObject = null;
    String topicTitle = "";
    int topicId = 0,slideNumber = 1;
    Button submitAnswer = null;
    EditText answerTextView = null;
    RadioGroup radioGroup = null;
    TextView contentType = null;
    SharedPreferences sharedPreferences = null;
    NavigationView navigationView = null;
    DrawerLayout layout_drawer = null;
    TopicService topicService = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        Intent intent = getIntent();
        topicId = intent.getIntExtra("topicId",0);
        topicTitle = intent.getStringExtra("topicVal");

        dbObject = new DbObject(this.getApplicationContext()).getWritableDatabase();
        contentService = new ContentService();
        questionService = new QuestionService();
        answerService = new AnswerService();
        topicService = new TopicService();
        sharedPreferences = getSharedPreferences(DquizConstants.MYPREFERENCES, Context.MODE_PRIVATE);
        navigationDrawerSetUp();

        layout_drawer.setOnTouchListener(new OnSwipeTouchListener(ContentActivity.this){
            public void onSwipeRight() {
                slideNumber--;
                if(slideNumber > 0) {
                    fetchContentsThread();
                }
            }
            public void onSwipeLeft() {
                if(contentType.getText().equals("question")){
                    if(checkTopicAndSlideNumber()){
                        slideNumber++;
                        fetchContentsThread();
                    }else{
                        HelperService.makeToast(ContentActivity.this,"Solve the question.", Toast.LENGTH_SHORT);
                    }
                }
            }
        });
        fetchContentsThread();
    }

    public void fetchContentsThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Contents contents = contentService.getContents(topicId, slideNumber, dbObject);
                displayContentsOnUiThread(contents);
            }
        }).start();
    }

    public void displayContentsOnUiThread(final Contents contents){
        ContentActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView topicVal = (TextView) findViewById(R.id.topicVal);
                TextView contentDescription = (TextView) findViewById(R.id.contentDescription);
                contentType  = (TextView) findViewById(R.id.contentType);
                LinearLayout questionsLayout = (LinearLayout) findViewById(R.id.questionLayout);
                if (contents != null) {
                    topicVal.setText(topicTitle);
                    contentType.setText(contents.getContentType());
                    if(contents.getContentType().equalsIgnoreCase("question")){
                        questionsLayout.setVisibility(View.VISIBLE);
                        contentDescription.setVisibility(View.GONE);
                        questionsContent(contents);
                    }else{
                        contentDescription.setVisibility(View.VISIBLE);
                        questionsLayout.setVisibility(View.GONE);
                        contentDescription.setText(contents.getContentDescription());
                    }
                    if(slideNumber > sharedPreferences.getInt("slideNo",0) && topicId >= sharedPreferences.getInt("topicId",0)){
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("topicId",topicId);
                        editor.putInt("slideNo",slideNumber);
                        editor.apply();
                    }
                }
            }
        });
    }

    public void questionsContent(Contents contents){

        int questionId = Integer.valueOf(contents.getContentDescription());
        Questions questions = questionService.getQuestion(questionId,dbObject);

        if(questions != null){
            final List<Answers> answersList = answerService.getAnswers(questionId,dbObject);
            final int answerSize = answersList.size();

            if(answerSize>0){
                TextView questionVal = (TextView) findViewById(R.id.questionVal);
                questionVal.setText("Question. "+questions.getQuestionVal());
                LinearLayout questionLayout = (LinearLayout) findViewById(R.id.questionLayout);
                questionLayout.removeView(radioGroup);
                questionLayout.removeView(submitAnswer);

                if(submitAnswer == null){
                    submitAnswer = new Button(ContentActivity.this);
                    submitAnswer.setText("Submit");
                }
                if(answerSize == 1){
                    if(answerTextView == null){
                        answerTextView = new EditText(ContentActivity.this);
                    }
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    answerTextView.setLayoutParams(params);
                    questionLayout.addView(answerTextView);
                    submitAnswer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String userAnswer = answerTextView.getText()+"";
                            String rightAnswer = answersList.get(0).getAnswerVal();
                            checkAnswerTextFeild(rightAnswer,userAnswer);
                        }
                    });
                }else{
                    if(radioGroup == null){
                        radioGroup = new RadioGroup(ContentActivity.this);
                    }else{
                        radioGroup.removeAllViews();
                    }
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    radioGroup.setLayoutParams(params);
                    String rightAnswer = "";
                    for(Answers answers:answersList){
                        if(answers.isCorrect()){
                            rightAnswer = answers.getAnswerVal();
                        }
                        RadioButton answerOption = new RadioButton(ContentActivity.this);
                        LinearLayout.LayoutParams radioParams = new LinearLayout.LayoutParams
                                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        answerOption.setText(answers.getAnswerVal());
                        answerOption.setLayoutParams(radioParams);
                        answerOption.setId(answers.getAnswerId());
                        radioGroup.addView(answerOption);
                    }
                    questionLayout.addView(radioGroup);

                    final String finalRightAnswer = rightAnswer;
                    submitAnswer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int selectedId = radioGroup.getCheckedRadioButtonId();
                            String userAnswer = "";
                            for(Answers answers:answersList){
                                if(answers.getAnswerId() == selectedId){
                                    userAnswer = answers.getAnswerVal();
                                    break;
                                }
                            }
                            checkAnswerMultiple(finalRightAnswer,userAnswer);
                        }
                    });

                }
                questionLayout.addView(submitAnswer);
            }
        }
    }

    public void checkAnswerMultiple(String correctAnswer,String selectedAnswer){
        if(correctAnswer.equalsIgnoreCase(selectedAnswer)){
            HelperService.makeAlertBox("Correct","Right Answer",ContentActivity.this);
            slideNumber++;
            fetchContentsThread();
        }else{
            HelperService.makeAlertBox("Wrong","Wrong Answer",ContentActivity.this);
        }
    }

    public void checkAnswerTextFeild(String correctAnswer,String selectedAnswer){
        if(correctAnswer.equalsIgnoreCase(selectedAnswer)) {
            HelperService.makeAlertBox("Correct", "Right Answer", ContentActivity.this);
        }else{
            HelperService.makeAlertBox("Wrong","Wrong Answer",ContentActivity.this);
        }
    }

    public boolean checkTopicAndSlideNumber(){

        int topicIdS = sharedPreferences.getInt("topicId",0);
        int slideNoS = sharedPreferences.getInt("slideNo",1);

        if(topicIdS >= this.topicId && slideNoS > this.slideNumber){
            return true;
        }
        return false;
    }


    public void navigationDrawerSetUp(){
        ContentActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                layout_drawer = (DrawerLayout) findViewById(R.id.layout_drawer);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        ContentActivity.this, layout_drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                layout_drawer.setDrawerListener(toggle);
                layout_drawer.requestLayout();
                toggle.syncState();

                navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(ContentActivity.this);
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
        if (layout_drawer.isDrawerOpen(GravityCompat.START)) {
            layout_drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        layout_drawer.closeDrawer(GravityCompat.START);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ContentActivity.this,TopicsActivity.class);
                intent.putExtra("superTopicVal",item.getTitle()+"");
                startActivity(intent);
            }
        }).start();
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
