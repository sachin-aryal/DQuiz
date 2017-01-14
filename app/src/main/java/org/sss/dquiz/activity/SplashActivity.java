package org.sss.dquiz.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import org.sss.dquiz.Constants.DquizConstants;
import org.sss.dquiz.R;
import org.sss.dquiz.helper.HelperService;

public class SplashActivity extends AppCompatActivity {
    private AccessTokenTracker accessTokenTracker = null;
    private static int SPLASH_TIME_OUT = 3000;
    SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_splash);
        sharedPreferences = getSharedPreferences(DquizConstants.MYPREFERENCES, Context.MODE_PRIVATE);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                if(HelperService.isFBLoggedIn()){

                    Profile profile = Profile.getCurrentProfile();
                    HelperService.setCurrentUserInfo(profile,sharedPreferences);
                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);

                }else if(HelperService.isGmailLoggedIn(SplashActivity.this)){
                    GoogleSignInResult googleSignInResult = HelperService.getGoogleSignInResult();
                    if (googleSignInResult.isSuccess()) {
                        GoogleSignInAccount acct = googleSignInResult.getSignInAccount();
                        HelperService.setCurrentUserInfo(acct,sharedPreferences);

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