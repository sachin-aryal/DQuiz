package org.sss.dquiz.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by iam on 1/14/17.
 */

public class DbObject extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "dquiz";
    public static final int DATABASE_VERSION = 1;

    public DbObject(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
