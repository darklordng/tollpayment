package com.example.tollpayment.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.tollpayment.R;

public class SplashActivity extends AppCompatActivity {

    public int splash_timer = 3000;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(context, LoginActivity.class));
                finish();
            }
        }, splash_timer);
    }
}
