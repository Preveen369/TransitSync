package com.example.transitsync;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BusDetails extends AppCompatActivity {

    private RecyclerView busListRecyclerView;
    private BusAdapter busAdapter;
    private ArrayList<Bus> busArrayList;
    private DatabaseReference routesRef;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_details);

        // Initialize the RecyclerView
        busListRecyclerView = findViewById(R.id.bus_list);
        busListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the ArrayList and Adapter
        busArrayList = new ArrayList<>();
        busAdapter = new BusAdapter(busArrayList, BusDetails.this);
        busListRecyclerView.setAdapter(busAdapter);

        // Fetch data from Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        routesRef = database.getReference("routes");

        // Fetching routes data from Firebase
        valueEventListener = routesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                busArrayList.clear(); // Clear the list before adding new data

                for (DataSnapshot routeSnapshot : dataSnapshot.getChildren()) {
                    String routeName = routeSnapshot.child("name").getValue(String.class);
                    routeName = routeName != null ? routeName : "Unknown Route";

                    String distance = routeSnapshot.child("distance").getValue(String.class);
                    distance = distance != null ? distance : "Unknown Distance";

                    ArrayList<String> stopsList = (ArrayList<String>) routeSnapshot.child("stops").getValue();
                    String stops = stopsList != null ? String.join(", ", stopsList) : "No Stops Available";

                    // Fetching bus details
                    for (DataSnapshot busSnapshot : routeSnapshot.child("buses").getChildren()) {
                        String busNumber = busSnapshot.child("license_plate").getValue(String.class);
                        busNumber = busNumber != null ? busNumber : "Unknown Bus";

                        String startTime = busSnapshot.child("schedule").child("start_time").getValue(String.class);
                        startTime = startTime != null ? startTime : "Unknown Start Time";

                        String endTime = busSnapshot.child("schedule").child("end_time").getValue(String.class);
                        endTime = endTime != null ? endTime : "Unknown End Time";

                        String busTimings = startTime + " - " + endTime;

                        // Create a new Bus object and add it to the list
                        busArrayList.add(new Bus(R.drawable.bus_icon, routeName, busNumber, busTimings, distance, stops));
                    }
                }

                // Notify the adapter that data has changed
                busAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log the error message
                String errorMessage = databaseError.getMessage();
                Log.e("BusDetails", "Error fetching data: " + errorMessage);

                // Show a Toast message to inform the user
                Toast.makeText(BusDetails.this, "Failed to load bus details. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the Firebase listener when the activity is destroyed to prevent memory leaks
        if (routesRef != null && valueEventListener != null) {
            routesRef.removeEventListener(valueEventListener);
        }
    }
}