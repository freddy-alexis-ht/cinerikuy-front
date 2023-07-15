package com.cinerikuy.presenter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

import com.cinerikuy.Informative;
import com.cinerikuy.R;

public class MainActivity extends AppCompatActivity{
    private ImageView btnIntro;
    private SharedPreferences sharedPreferences;
    private boolean isLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnIntro = findViewById(R.id.btnIntro);
        btnIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                isLogin = sharedPreferences.getBoolean("isLogin", false);
                if (isLogin) {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(MainActivity.this, Informative.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}