package com.nowinski.kamil.flighttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChoiceModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_mode);
    }

    public void openWindowMode(View view){
        Intent intent = new Intent(ChoiceModeActivity.this, StaticScreenModeActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void openMapMode(View view){
        Intent intent = new Intent(ChoiceModeActivity.this, AirCraftNavigateActivity.class);
        startActivity(intent);
        this.finish();
    }
}
