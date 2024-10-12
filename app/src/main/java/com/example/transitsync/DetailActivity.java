package com.example.transitsync;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get data from the intent
        int imageResId = getIntent().getIntExtra("imageResId", 0);
        String routeName = getIntent().getStringExtra("routeName");
        String busNumber = getIntent().getStringExtra("busNumber");
        String busTimings = getIntent().getStringExtra("busTimings");
        String distanceCovered = getIntent().getStringExtra("distanceCovered");
        String nearbyStops = getIntent().getStringExtra("nearbyStops");

        // Pass data to the fragment
        DetailFragment detailFragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt("imageResId", imageResId);
        args.putString("routeName", routeName);
        args.putString("busNumber", busNumber);
        args.putString("busTimings", busTimings);
        args.putString("distanceCovered", distanceCovered);
        args.putString("nearbyStops", nearbyStops);
        detailFragment.setArguments(args);

        // Load the fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .commit();
    }
}
