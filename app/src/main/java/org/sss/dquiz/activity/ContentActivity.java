package org.sss.dquiz.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.sss.dquiz.R;
import org.sss.dquiz.database.DbObject;
import org.sss.dquiz.helper.HelperService;
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
    Button submitAnswer = null;
    EditText answerTextView = null;
    RadioGroup radioGroup = null;

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
                LinearLayout questionsLayout = (LinearLayout) findViewById(R.id.questionLayout);
                if (contents != null) {
                    topicVal.setText(topicTitle);
                    if(contents.getContentType().equalsIgnoreCase("question")){
                        questionsLayout.setVisibility(View.VISIBLE);
                        topicDescription.setVisibility(View.GONE);
                        questionsContent(contents);
                    }else{
                        topicDescription.setVisibility(View.VISIBLE);
                        questionsLayout.setVisibility(View.GONE);
                        topicDescription.setText(contents.getContentDescription());
                    }
                }
            }
        });
    }

    public void questionsContent(Contents contents){

        int questionId = Integer.valueOf(contents.getContentDescription());
        Questions questions = questionService.getQuestion(questionId,sqLiteDatabase);

        if(questions != null){
            final List<Answers> answersList = answerService.getAnswers(questionId,sqLiteDatabase);
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
}
