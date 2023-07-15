package com.cinerikuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class Informative extends AppCompatActivity {

    private Button btnHow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informative);
        btnHow = findViewById(R.id.btn_how);
        btnHow.setOnClickListener(view -> {
            Intent intent = new Intent(this, HowUse.class);
            startActivity(intent);
            finish();
        });
    }
}