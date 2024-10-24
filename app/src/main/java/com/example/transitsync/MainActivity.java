package com.example.transitsync;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button busRouteDetails, profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        busRouteDetails = findViewById(R.id.bus_route_details);
        profile = findViewById(R.id.profile);

        /*busLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BusLocation.class);
                startActivity(intent);
            }
        });*/

        busRouteDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BusDetails.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileDetails.class);
                startActivity(intent);
            }
        });
    }

    /** @noinspection deprecation*/
    @Override
    public void onBackPressed() {
        // Exit the app instead of going back to the launcher
        super.onBackPressed();
        finishAffinity(); // Close all activities and exit the app
    }
}