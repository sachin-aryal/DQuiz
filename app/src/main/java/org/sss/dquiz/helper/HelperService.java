package org.sss.dquiz.helper;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import org.sss.dquiz.model.User;

/**
 * Created by iam on 1/12/17.
 */

public class HelperService {

    private final static int requiredGPS = 9256000;
    private static GoogleSignInResult googleSignInResult = null;
    private static SharedPreferences sharedPreferences = null;
    public static boolean checkGPSVersion(int versonCode){

        System.out.println("Google Play Service Version: "+versonCode);
        if(versonCode >= requiredGPS){
            return true;
        }
        return false;
    }

    public static GoogleApiClient getGoogleApiClient(final AppCompatActivity activity){
        return new GoogleApiClient.Builder(activity.getApplicationContext())
                .enableAutoManage(activity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(activity,"Connection Failed. May be you are not connected to internet.",Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, getGoogleSignOptions())
                .build();

    }

    public static GoogleSignInOptions getGoogleSignOptions(){
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
    }


    public static void makeAlertBox(String title,String message,Activity activity){
        new AlertDialog.Builder(activity).setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }


    public static boolean isFBLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public static boolean isGmailLoggedIn(AppCompatActivity activity){
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(getGoogleApiClient(activity));
        if (opr.isDone()) {
            googleSignInResult = opr.get();
            return true;
        }
        return false;
    }

    public static GoogleSignInResult getGoogleSignInResult(){
        return googleSignInResult;
    }

    public static void setCurrentUserInfo(Profile profile, SharedPreferences sharedPreferences){
        HelperService.sharedPreferences = sharedPreferences;
        setCurrentUserInfo("facebook",profile.getId(),profile.getName());
    }

    public static void setCurrentUserInfo(GoogleSignInAccount account,SharedPreferences sharedPreferences){
        HelperService.sharedPreferences = sharedPreferences;
        setCurrentUserInfo("gmail",account.getId(),account.getDisplayName());
    }

    public static void setCurrentUserInfo(String loginType,String userId,String userName){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(User.LOGINTYPE, loginType);
        editor.putString(User.USERID, userId);
        editor.putString(User.NAME, userName);
        editor.apply();
    }

    public static User getUser(SharedPreferences sharedPreferences){
        return new User(sharedPreferences.getString(User.USERID,null), sharedPreferences.getString(User.NAME,null));

    }


    public static void sqlLiteInfo(SQLiteDatabase database){
        Cursor c = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                String tableName  = c.getString( c.getColumnIndex("name"));
                System.out.println("Table Name=> "+tableName);
                Cursor dbCursor = database.query(tableName, null, null, null, null, null, null);
                String[] columnNames = dbCursor.getColumnNames();
                System.out.println("All Columns in "+tableName);
                for(String col:columnNames){
                    System.out.println("Column: "+col);
                }
                c.moveToNext();
            }
        }
        c.close();
    }



}
