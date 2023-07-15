package com.cinerikuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.cinerikuy.presenter.Login;

public class HowUse extends AppCompatActivity {
    private Button btnHowUse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_use);
        btnHowUse = findViewById(R.id.btn_howuse);
        btnHowUse.setOnClickListener(view -> {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        });
    }
}