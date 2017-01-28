package org.sss.dquiz.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.sss.dquiz.R;
import org.sss.dquiz.database.DbObject;
import org.sss.dquiz.helper.OnSwipeTouchListener;
import org.sss.dquiz.model.Answers;
import org.sss.dquiz.model.Contents;
import org.sss.dquiz.model.Questions;
import org.sss.dquiz.service.AnswerService;
import org.sss.dquiz.service.ContentService;
import org.sss.dquiz.service.QuestionService;

import java.util.List;

public class ContentActivity extends AppCompatActivity {

    ContentService contentService = null;
    QuestionService questionService = null;
    AnswerService answerService = null;
    SQLiteDatabase sqLiteDatabase = null;
    String topicTitle = "";
    int topicId = 0,initialSlideNumber = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        Intent intent = getIntent();
        topicId = intent.getIntExtra("topicId",0);
        topicTitle = intent.getStringExtra("topicVal");

        sqLiteDatabase = new DbObject(this.getApplicationContext()).getWritableDatabase();
        contentService = new ContentService();
        questionService = new QuestionService();
        answerService = new AnswerService();

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
                RelativeLayout questionsLayout = (RelativeLayout) findViewById(R.id.questionLayout);
                if (contents != null) {
                    topicVal.setText(topicTitle);
                    if(contents.getContentType().equalsIgnoreCase("question")){
                        questionsLayout.setVisibility(View.VISIBLE);
                        int questionId = Integer.valueOf(contents.getContentDescription());
                        Questions questions = questionService.getQuestion(questionId,sqLiteDatabase);
                        if(questions != null){
                            List<Answers> answersList = answerService.getAnswers(questionId,sqLiteDatabase);
                            int answerSize = answersList.size();
                            if(answerSize>0){
                                TextView questionVal = (TextView) findViewById(R.id.questionVal);
                                RelativeLayout questionLayout = (RelativeLayout) findViewById(R.id.questionLayout);
                                System.out.println("Question Layout-----------"+questionLayout);
                                if(answerSize == 1){
                                    EditText answerTextView = new EditText(ContentActivity.this);
                                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                                            (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                                    params.addRule(RelativeLayout.BELOW,questionVal.getId());
                                    answerTextView.setLayoutParams(params);
                                    questionLayout.addView(answerTextView);
                                }else{
                                    RadioGroup radioGroup = new RadioGroup(ContentActivity.this);
                                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                                            (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    params.addRule(RelativeLayout.BELOW,questionVal.getId());
                                    radioGroup.setLayoutParams(params);
                                    questionLayout.addView(radioGroup);
                                    int index = 0;
                                    RadioButton previousRadio = null;
                                    for(Answers answers:answersList){

                                        RadioButton answerOption = new RadioButton(ContentActivity.this);
                                        RelativeLayout.LayoutParams radioParams = new RelativeLayout.LayoutParams
                                                (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                                        if(index == 0){
                                            radioParams.addRule(RelativeLayout.BELOW,questionVal.getId());
                                        }else{
                                            radioParams.addRule(RelativeLayout.BELOW,previousRadio.getId());
                                        }
                                        answerOption.setText(answers.getAnswerVal());
                                        answerOption.setLayoutParams(radioParams);
                                        questionLayout.addView(answerOption);
                                        index++;
                                        previousRadio = answerOption;
                                    }
                                }
                            }
                        }
                    }else{
                        questionsLayout.setVisibility(View.GONE);
                        topicDescription.setText(contents.getContentDescription());
                    }
                }
            }
        });
    }
}
