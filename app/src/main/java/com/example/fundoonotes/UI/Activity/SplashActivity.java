package com.example.fundoonotes.UI.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import com.example.fundoonotes.DashBoard.Activity.HomeActivity;
import com.example.fundoonotes.R;

public class SplashActivity extends AppCompatActivity {

    private static final String SPLASH_TAG = "SplashActivity";
    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams
                .FLAG_FULLSCREEN);

        new Handler().postDelayed(() -> {
            isLoggedIn = sharedPreferenceHelper.getLoggedIN();
            Log.d(SPLASH_TAG, "isLoggedIn Value" + isLoggedIn);
            Intent intent;
            if(isLoggedIn){
                intent = new Intent(SplashActivity.this, HomeActivity.class);
            }else{
                intent = new Intent(SplashActivity.this, LoginRegisterActivity.class);
            }
            startActivity(intent);
            finish();
        },3000);
    }
}