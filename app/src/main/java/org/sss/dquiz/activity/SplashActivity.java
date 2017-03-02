package org.sss.dquiz.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import org.sss.dquiz.Constants.DquizConstants;
import org.sss.dquiz.R;
import org.sss.dquiz.database.DbObject;
import org.sss.dquiz.helper.HelperService;
import org.sss.dquiz.service.UserService;

public class SplashActivity extends AppCompatActivity {
    ProgressBar mProgressBar;
    private AccessTokenTracker accessTokenTracker = null;
    private static int SPLASH_TIME_OUT = 3000;
    SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_splash);

        final DbObject dbObject = new DbObject(getApplicationContext());
//        HelperService.sqlLiteInfo(dbObject.getWritableDatabase());
       /* ImageView img = (ImageView)findViewById(R.id.imageview);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_down);
        img.setAnimation(anim);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar2);

        ObjectAnimator anim1 = ObjectAnimator.ofInt(mProgressBar,"progress",0,100);
        anim1.setDuration(4000);
        anim1.setInterpolator(new DecelerateInterpolator());
        anim.start();
*/
        sharedPreferences = getSharedPreferences(DquizConstants.MYPREFERENCES, Context.MODE_PRIVATE);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                if(HelperService.isFBLoggedIn()){

                    Profile profile = Profile.getCurrentProfile();
                    if(profile == null){
                        Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }
                    if(profile == null){
                        Intent intent = new Intent(SplashActivity.this,SplashActivity.class);
                        startActivity(intent);
                    }else {
                        HelperService.setCurrentUserInfo(profile, sharedPreferences);
                        if (!sharedPreferences.contains(DquizConstants.ISREGISTER)) {
                            new RetrieveContentTask(sharedPreferences, UserService.REGISTER_ACTION, dbObject.getWritableDatabase()).execute();
                        }
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                }else if(HelperService.isGmailLoggedIn(SplashActivity.this)){
                    GoogleSignInResult googleSignInResult = HelperService.getGoogleSignInResult();
                    if (googleSignInResult.isSuccess()) {
                        GoogleSignInAccount acct = googleSignInResult.getSignInAccount();
                        if(acct == null){
                            Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                            startActivity(intent);
                        }
                        HelperService.setCurrentUserInfo(acct,sharedPreferences);
                        if(!sharedPreferences.contains(DquizConstants.ISREGISTER)){
                            new RetrieveContentTask(sharedPreferences,UserService.REGISTER_ACTION,dbObject.getWritableDatabase()).execute();
                        }
                        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SplashActivity.this,"Authentication Failed.",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                }

                finish();
            }
        }, SPLASH_TIME_OUT);


        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                AccessToken.setCurrentAccessToken(currentAccessToken);
            }
        };
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

}
