package com.nowinski.kamil.flighttracker;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class FlightTrackerMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_tracker_main);
    }

    public void loadMainMenu(View view){
        Intent intent = new Intent(FlightTrackerMain.this, ChoiceModeActivity.class);
        startActivity(intent);
        this.finish();
    }
}
