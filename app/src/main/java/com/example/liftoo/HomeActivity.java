package com.example.liftoo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.Arrays;

public class HomeActivity extends Activity {
    private Button btn;
    FirebaseAuth auth;
    FirebaseUser user;
    TextView txt;

    LoginButton loginButton;

    CallbackManager callbackManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if(user == null){

            setContentView(R.layout.login);
            FacebookSdk.sdkInitialize(getApplicationContext());

            loginButton = findViewById(R.id.fblog);

            callbackManager = CallbackManager.Factory.create();
            loginButton.setReadPermissions(Arrays.asList("email"));

            txt = findViewById(R.id.txt2);

        }else{
            Intent myIntent = new Intent(HomeActivity.this, mapActivity.class);
            startActivity(myIntent);
        }





        btn =  findViewById(R.id.sign_up);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignUp();
            }
        });


    }

    public void btnClickLogFb(View v){
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(HomeActivity.this, "user cancel it", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser myuserobj = auth.getCurrentUser();
                            updateUI(myuserobj);

                        }else{
                            Toast.makeText(getApplicationContext(),"couldn't register to firebase",Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    public void onActivityResult(int requestCode,int resultCode, Intent data){
        callbackManager.onActivityResult(requestCode, resultCode,data);
        super.onActivityResult(requestCode,resultCode,data);
    }

    private void updateUI(FirebaseUser myuserobj) {

        txt.setText(myuserobj.getEmail());
    }

    public void openSignUp(){

        Intent intent = new Intent(this,signup.class);
        startActivity(intent);

    }
}