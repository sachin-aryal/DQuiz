package org.sss.dquiz.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.sss.dquiz.model.Answers;
import org.sss.dquiz.model.Contents;
import org.sss.dquiz.model.Questions;
import org.sss.dquiz.model.Topics;

/**
 * Created by iam on 1/14/17.
 */

public class DbObject extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "dquiz";
    public static final int DATABASE_VERSION = 3;

    public DbObject(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        System.out.println("Creating New Tables --------------------------");
        sqLiteDatabase.execSQL(Topics.getTopicTableQuery());
        sqLiteDatabase.execSQL(Questions.getQuestionTableQuery());
        sqLiteDatabase.execSQL(Contents.getContentsTableQuery());
        sqLiteDatabase.execSQL(Answers.getAnswersTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        System.out.println("Database Version Changed ------> Droping Old Tables.");
        sqLiteDatabase.execSQL(Answers.getAnswersDropQuery());
        sqLiteDatabase.execSQL(Contents.getContentsDropQuery());
        sqLiteDatabase.execSQL(Questions.getQuestionsDropQuery());
        sqLiteDatabase.execSQL(Topics.getTopicDropQuery());
        onCreate(sqLiteDatabase);
    }
}
