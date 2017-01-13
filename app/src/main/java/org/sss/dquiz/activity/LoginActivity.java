package org.sss.dquiz.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.sss.dquiz.R;
import org.sss.dquiz.helper.HelperService;

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager = null;
    GoogleApiClient mGoogleApiClient = null;
    private static final int RC_SIGN_IN = 007;
    SignInButton btnSignIn = null;
    LoginButton mFacebookLoginButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_login);

        LoginActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                fbSignInManager();
                gmailSignInManager();
            }
        });
    }

    /*
    * Manage Facebook oAuth Process.
    * */

    private void fbSignInManager(){
        callbackManager = CallbackManager.Factory.create();
        mFacebookLoginButton = (LoginButton)findViewById(R.id.fb_login_button);
        mFacebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mFacebookLoginButton.setVisibility(View.INVISIBLE);
                btnSignIn.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(LoginActivity.this,SplashActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    /*
    * Manage Gmail oAuth Process.
    * */

    public void gmailSignInManager(){

        btnSignIn = (SignInButton) findViewById(R.id.gmail_login_button);
        try {

            int versionCode = getPackageManager().getPackageInfo("com.google.android.gms", 0 ).versionCode;
            if(HelperService.checkGPSVersion(versionCode)){
                mGoogleApiClient = HelperService.getGoogleApiClient(LoginActivity.this);
                btnSignIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signIn();
                    }
                });
            }else{
                HelperService.makeAlertBox("Google Play Service Version Mismatch.",
                        "Please update your Google Play Service to Login with Gmail",LoginActivity.this);
                btnSignIn.setEnabled(false);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
    * Start oAuth process for gmail.
    * */

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    /*
    * Dispatch incoming result to correct handler.
    * */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*
    * Method that handles the gmails sign in result.
    * */

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            Intent intent = new Intent(LoginActivity.this,SplashActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(LoginActivity.this,"Authentication Failed.",Toast.LENGTH_LONG).show();
        }
    }

}
